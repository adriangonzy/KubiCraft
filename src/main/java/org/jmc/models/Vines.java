package org.jmc.models;

import org.jmc.config.BlockInfo;
import org.jmc.config.BlockTypes;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for vines.
 */
public class Vines extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		short topId = chunks.getBlockID(x, y+1, z);
		
		boolean top = topId != 0 && BlockTypes.get(topId).getOcclusion() == BlockInfo.Occlusion.FULL;
		boolean s = (data & 1) != 0;
		boolean w = (data & 2) != 0;
		boolean n = (data & 4) != 0;
		boolean e = (data & 8) != 0;

		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.47f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.47f);
		vertices[2] = new Vertex( 0.5f,  0.5f, -0.47f);			
		vertices[3] = new Vertex(-0.5f,  0.5f, -0.47f);

		Transform rot = new Transform();
		Transform trans = new Transform();

		// rotate quad UVs
		UV[] rotatedUVs = new UV[] {
				new UV(0,1),
				new UV(1,1),
				new UV(1,0),
				new UV(0,0)
		};


		if (n)
		{
			trans.translate(x, y, z);
			obj.addFace(vertices, null, trans, materials.get(data,biome)[0]);
		}
		if (s)
		{
			rot.rotate(0, 180, 0);
			trans.translate(x, y, z);
			trans = trans.multiply(rot);
			obj.addFace(vertices, null, trans, materials.get(data,biome)[0]);
		}
		if (e)
		{
			rot.rotate(0, 90, 0);
			trans.translate(x, y, z);
			trans = trans.multiply(rot);
			obj.addFace(vertices, null, trans, materials.get(data,biome)[0]);
		}
		if (w)
		{
			rot.rotate(0, -90, 0);
			trans.translate(x, y, z);
			trans = trans.multiply(rot);
			obj.addFace(vertices, null, trans, materials.get(data,biome)[0]);
		}
		if (top)
		{
			rot.rotate(90, 0, 0);
			trans.translate(x, y, z);
			trans = trans.multiply(rot);
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data,biome)[0]);
		}
	}

}
