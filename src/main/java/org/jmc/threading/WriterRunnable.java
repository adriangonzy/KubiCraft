package org.jmc.threading;

import org.jmc.Options;
import org.jmc.export.ProgressCallback;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ThreadOutputQueue.ChunkOutput;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.zip.ZipOutputStream;

public class WriterRunnable implements Runnable {

	private List<Vertex> exportVertices;
	private List<UV> exportTexCoords;
	private List<Vertex> exportNormals;
	private List<FaceUtils.OBJFace> exportFaces;
	private Map<Vertex, Integer> vertexMap;
	private Map<UV, Integer> texCoordMap;
	private Map<Vertex, Integer> normalsMap;
	private int vertex_counter, tex_counter, norm_counter;
	public long nbTrianglesCount = 0L;

	private long obj_idx_count;


	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	private float x_offset, y_offset, z_offset;

	private float file_scale;

	private ThreadOutputQueue outputQueue;

	private ZipOutputStream zos;

	private ProgressCallback progress;
	private int chunksToDo;

	public WriterRunnable(ThreadOutputQueue queue, ZipOutputStream zos, ProgressCallback progress, int chunksToDo) {
		super();

		outputQueue = queue;
		this.progress = progress;
		this.chunksToDo = chunksToDo;
		this.zos = zos;

		x_offset = 0;
		y_offset = 0;
		z_offset = 0;
		file_scale = Options.scale;

		obj_idx_count = -1;
		vertexMap = new HashMap<>();
		vertex_counter = 1;
		texCoordMap = new HashMap<>();
		tex_counter = 1;
		normalsMap = new HashMap<>();
		norm_counter = 1;

		exportVertices = new ArrayList<>();
		exportTexCoords = new ArrayList<>();
		exportNormals = new ArrayList<>();
		exportFaces = new ArrayList<>();
	}

	@Override
	public void run() {
		ChunkOutput chunkOut;
		int chunksDone = 0;
		while (true) {
			//Check for chunks in queue
			try {
				chunkOut = outputQueue.getNext();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			//if none left, kill thread
			if (chunkOut == null) {
				break;
			}

			List<FaceUtils.Face> chunkFaces = chunkOut.getFaces();

			addOBJFaces(chunkFaces);

			// export the chunk to the OBJ
			try {
				appendTextures(zos);
				appendNormals(zos);
				appendVertices(zos);
				appendFaces(zos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearData();

			chunksDone++;
			if (progress != null) {
				float progValue = (float) chunksDone / (float) chunksToDo;
				progress.setProgress(progValue);
			}
		}
	}

	/**
	 * Write texture coordinates. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	private void appendTextures(ZipOutputStream out) throws IOException {
		for (UV uv : exportTexCoords)
		{
			BigDecimal uRound = new BigDecimal(uv.u).setScale(9, RoundingMode.HALF_UP);
			BigDecimal vRound = new BigDecimal(uv.v).setScale(9, RoundingMode.HALF_UP);
			out.write(("vt " + uRound.toPlainString() + " " + vRound.toPlainString() + "\n").getBytes());
		}
	}

	/**
	 * Write normals. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	private void appendNormals(ZipOutputStream out) throws IOException {
		for (Vertex norm : exportNormals)
		{
			BigDecimal xRound = new BigDecimal(norm.x).setScale(3, RoundingMode.HALF_UP);
			BigDecimal yRound = new BigDecimal(norm.y).setScale(3, RoundingMode.HALF_UP);
			BigDecimal zRound = new BigDecimal(norm.z).setScale(3, RoundingMode.HALF_UP);
			out.write(("vn " + xRound.toPlainString() + " " + yRound.toPlainString() + " " + zRound.toPlainString() + "\n").getBytes("UTF-8"));
		}
	}

	/**
	 * Appends vertices to the file.
	 * @param out
	 */
	private void appendVertices(ZipOutputStream out) throws IOException
	{
		for (Vertex vertex : exportVertices)
		{
			float x = (vertex.x + x_offset) * file_scale;
			float y = (vertex.y + y_offset) * file_scale;
			float z = (vertex.z + z_offset) * file_scale;
			BigDecimal xRound = new BigDecimal(x).setScale(3, RoundingMode.HALF_UP);
			BigDecimal yRound = new BigDecimal(y).setScale(3, RoundingMode.HALF_UP);
			BigDecimal zRound = new BigDecimal(z).setScale(3, RoundingMode.HALF_UP);
			out.write(("v " + xRound.toPlainString() + " " + yRound.toPlainString() + " " + zRound.toPlainString() + "\n").getBytes("UTF-8"));
		}
	}

	/**
	 * This method prints faces from the current buffer to an OBJ format.
	 *
	 * @param out file to append the data
	 */
	private void appendFaces(ZipOutputStream out) throws IOException
	{
		Collections.sort(exportFaces);
		String last_mtl=null;
		Long last_obj_idx=Long.valueOf(-1);
		nbTrianglesCount += (2 * exportFaces.size());
		for(FaceUtils.OBJFace f:exportFaces)
		{
			if(!f.mtl.equals(last_mtl))
			{
				out.write(("\nusemtl "+f.mtl + "\n").getBytes("UTF-8"));
				last_mtl=f.mtl;
			}

			if(!f.obj_idx.equals(last_obj_idx))
			{
				out.write(("g o"+f.obj_idx + '\n').getBytes("UTF-8"));
				last_obj_idx=f.obj_idx;
			}

			out.write("f".getBytes());
			for (int i = 0; i < f.vertices.length; i++)
			{

				if (f.normals != null && f.uv != null)
					out.write((" " + f.vertices[i] + "/" + f.uv[i] + "/" + f.normals[i]).getBytes("UTF-8"));
					//out.format((Locale)null, " %d/%d/%d", f.vertices[i], f.uv[i], f.normals[i]);
				else if (f.normals == null && f.uv != null)
					out.write((" " + f.vertices[i] + "/" + f.uv[i]).getBytes("UTF-8"));
					//out.format((Locale)null, " %d/%d", f.vertices[i], f.uv[i]);
				else if (f.normals != null && f.uv == null)
					out.write((" " + f.vertices[i] + "//" + f.normals[i]).getBytes("UTF-8"));
					//out.format((Locale)null, " %d//%d", f.vertices[i], f.normals[i]);
				else
					out.write((" " + f.vertices[i]).getBytes("UTF-8"));
				//out.format((Locale)null, " %d", f.vertices[i]);
			}
			out.write(("\n".getBytes("UTF-8")));
		}
	}

	private void addOBJFaces(List<FaceUtils.Face> chunkFaces)
	{
		int last_chunk_idx=-1;
		for (FaceUtils.Face f : chunkFaces) {
			Vertex[] verts = f.vertices;
			Vertex[] norms = f.norms;
			UV[] uv = f.uvs;
			String mtl = f.material;

			if(f.chunk_idx != last_chunk_idx)
			{
				obj_idx_count++;
				last_chunk_idx=f.chunk_idx;
			}

			FaceUtils.OBJFace face = new FaceUtils.OBJFace(verts.length);
			face.obj_idx=Long.valueOf(obj_idx_count);
			face.mtl = mtl;
			if (norms == null) face.normals = null;
			if (uv == null)
			{
				face.uv = null;
			}
//			else if(Options.useUVFile)
//			{
//				uv= UVRecalculate.recalculate(uv, mtl);
//			}

			for (int i = 0; i < verts.length; i++)
			{
				// add vertices
				Vertex vert;
				vert = verts[i];

				if (vertexMap.containsKey(vert))
				{
					face.vertices[i] = vertexMap.get(vert);
				}
				else
				{
					exportVertices.add(vert);
					vertexMap.put(vert, vertex_counter);
					face.vertices[i] = vertex_counter;
					vertex_counter++;
				}

				// add normals
				if (norms != null)
				{
					Vertex norm;
					norm = norms[i];

					if (normalsMap.containsKey(norm))
					{
						face.normals[i] = normalsMap.get(norm);
					}
					else
					{
						exportNormals.add(norm);
						normalsMap.put(norm, norm_counter);
						face.normals[i] = norm_counter;
						norm_counter++;
					}
				}

				// add texture coords
				if (uv != null)
				{
					if (texCoordMap.containsKey(uv[i]))
					{
						face.uv[i] = texCoordMap.get(uv[i]);
					}
					else
					{
						exportTexCoords.add(uv[i]);
						texCoordMap.put(uv[i], tex_counter);
						face.uv[i] = tex_counter;
						tex_counter++;
					}
				}
			}

			exportFaces.add(face);
		}
	}

	/**
	 * Offset all the vertices by these amounts.
	 * Used to position the chunk in its right location.
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void setOffset(float x, float y, float z)
	{
		x_offset=x;
		y_offset=y;
		z_offset=z;
	}

	private void clearData() {
		//keep edge vertices
		for(Vertex v:exportVertices)
			if((v.x-0.5)%16!=0 && (v.z-0.5)%16!=0 && (v.x+0.5)%16!=0 && (v.z+0.5)%16!=0)
				vertexMap.remove(v);
		exportVertices.clear();
		exportTexCoords.clear();
		exportNormals.clear();
		exportFaces.clear();
	}

}
