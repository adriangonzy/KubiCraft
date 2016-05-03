package org.jmc.export;

import org.jmc.Options;
import org.jmc.Options.OffsetType;
import org.jmc.config.Materials;
import org.jmc.threading.ChunkDataBuffer;
import org.jmc.threading.ReaderRunnable;
import org.jmc.threading.ThreadOutputQueue;
import org.jmc.threading.WriterRunnable;
import org.jmc.util.*;
import org.jmc.world.Chunk;
import org.jmc.world.LevelDat;
import org.jmc.world.MapInfo;
import org.jmc.world.Region;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles the export of Minecraft world geometry to an .OBJ file (with matching
 * .MTL file)
 */
public class ObjExporter {

	public static void export(MapInfo mapInfo, ProgressCallback progress, KubityExporter.ErrorCallback errorCallback) {

		// hack for mac bundled .app
		String userDir = System.getProperty("user.dir");
		if (userDir != null && userDir.endsWith(".app")) {
			Options.outputDir = new File(userDir.substring(0, userDir.length() - "kubicraft.app".length()));
		}

		Path exportsDir = Paths.get(Options.outputDir.getAbsolutePath(), Options.exportsFolder);
		exportsDir.toFile().mkdir();
		String outName = mapInfo.path.toFile().getName() + ".zip";
		if (mapInfo.texturePath != null) {
			String[] segments = mapInfo.texturePath.split(Pattern.quote(File.separator));
			outName = mapInfo.path.toFile().getName() + "-" + segments[segments.length - 1] + ".zip";
		}

		File exportFile = new File(exportsDir.toFile().getAbsolutePath(), outName);
		ZipOutputStream exportOutputStream;

		try {
			exportOutputStream = new ZipOutputStream(new FileOutputStream(exportFile));
		} catch (FileNotFoundException e) {
			Log.error("Cannot create archive file : " + exportFile.getAbsolutePath(), e);
			errorCallback.handleError("Cannot create archive file : " + exportFile.getAbsolutePath());
			return;
		}

		try {
			long nbTriangle = addOBJToZip(mapInfo, exportOutputStream, progress);

			if (nbTriangle > Options.MAX_WARNING_TRIANGLE && nbTriangle < Options.MAX_ALLOWED_TRIANGLE) {
				errorCallback.handleWarning("Warning ! Your export has over 2 Millions triangles (currently " + nbTriangle +
						"). This might take a while. Check the FAQ for more info.");
			}

			if (nbTriangle > Options.MAX_ALLOWED_TRIANGLE) {
				errorCallback.handleError("Your export exceeds the allowed 4 Millions triangles limit (currently " + nbTriangle +
						"). This might change with future releases. Check the FAQ for more info.");
				deleteFiles(exportFile);
				return;
			}

			addTexturesToZip(mapInfo, exportOutputStream, progress);
			addMTLToZip(exportOutputStream);
			exportOutputStream.close();

			if (exportFile.length() > Options.MAX_SIZE){
				errorCallback.handleError("This export is too big (maximum 200Mb). Try making a smaller one.");
				deleteFiles(exportFile);
				return;
			}

			if (progress != null) {
				progress.setStatus(ProgressCallback.Status.UPLOADING);
			}

			KubityExporter.export(exportFile.toPath(), progress, errorCallback);

			Log.info("Done!");
		} catch (Exception e) {
			Log.error("Error while exporting OBJ:", e);
			errorCallback.handleError("Error while exporting OBJ: " + e.getMessage());
			deleteFiles(exportFile);
		}
	}

	private static long addOBJToZip(MapInfo mapInfo, ZipOutputStream out, ProgressCallback progress) throws IOException, InterruptedException {
		if (progress != null) {
			progress.setStatus(ProgressCallback.Status.EXPORTING_OBJ);
		}

		// Add Obj file to the zip
		out.putNextEntry(new ZipEntry("export.obj"));

		if (mapInfo.bounds.maxX - mapInfo.bounds.minX == 0 || mapInfo.bounds.maxY - mapInfo.bounds.minY == 0 || mapInfo.bounds.maxZ - mapInfo.bounds.minZ == 0) {
			Log.error(Messages.getString("MainPanel.SEL_ERR"), null, true);
			return 0;
		}

		// Calculate the boundaries of the chunks selected by the user
		Point cs = Chunk.getChunkPos(mapInfo.bounds.minX, mapInfo.bounds.minZ);
		Point ce = Chunk.getChunkPos(mapInfo.bounds.maxX + 15, mapInfo.bounds.maxZ + 15);
		int oxs, oys, ozs;

		if (Options.offsetType == OffsetType.CENTER) {
			oxs = -(mapInfo.bounds.minX + (mapInfo.bounds.maxX - mapInfo.bounds.minX) / 2);
			oys = -mapInfo.bounds.minY;
			ozs = -(mapInfo.bounds.minZ + (mapInfo.bounds.maxZ - mapInfo.bounds.minZ) / 2);
			Log.info("Center offset: " + oxs + "/" + oys + "/" + ozs);
		} else if (Options.offsetType == OffsetType.CUSTOM) {
			oxs = Options.offsetX;
			oys = 0;
			ozs = Options.offsetZ;
			Log.info("Custom offset: " + oxs + "/" + oys + "/" + ozs);
		} else {
			oxs = 0;
			oys = 0;
			ozs = 0;
		}

		int chunksToDo = (ce.x - cs.x + 1) * (ce.y - cs.y + 1);

		ChunkDataBuffer chunk_buffer = new ChunkDataBuffer(mapInfo.bounds.minX, mapInfo.bounds.maxX, mapInfo.bounds.minY,
				mapInfo.bounds.maxY, mapInfo.bounds.minZ, mapInfo.bounds.maxZ);

		List<Point> inputPoints = new ArrayList<>(50000);
		ThreadOutputQueue outputQueue = new ThreadOutputQueue();

		WriterRunnable writeRunner = new WriterRunnable(outputQueue, out, progress, chunksToDo);
		writeRunner.setOffset(oxs, oys, ozs);

		out.write(("mtllib export.mtl\n\n").getBytes());

		long startExport = System.currentTimeMillis();

		long addingQueueTime = startExport;
		// loop through the chunks selected by the user
		for (int cx = cs.x; cx <= ce.x; cx++) {
			for (int cz = cs.y; cz <= ce.y; cz++) {
				if (chunkExists(mapInfo, cx, cz)){
					inputPoints.add(new Point(cx, cz));
				}
			}
		}

		Log.info("Adding to queue : " + (System.currentTimeMillis() - addingQueueTime) + " ms");

		Thread writeThread = new Thread(writeRunner);
		writeThread.setName("WriteThread");
		writeThread.start();

		Thread[] threads = new Thread[Options.exportThreads];
		Log.info("Processing will use " + Options.exportThreads + " thread(s).");
		for (int i = 0; i < Options.exportThreads; i++) {
			int start = (i * inputPoints.size() / Options.exportThreads);
			int end = ((i + 1) * inputPoints.size() / Options.exportThreads);

			threads[i] = new Thread(new ReaderRunnable(mapInfo, chunk_buffer, cs, ce, inputPoints, start, end, outputQueue));
			threads[i].setName("ReadThread-" + i);
			threads[i].setPriority(Thread.NORM_PRIORITY - 1);
			threads[i].start();
		}

		long readingTime = System.currentTimeMillis();

		for (Thread thread : threads){
			thread.join();
		}
		Log.info("Reading Chunks : " + (System.currentTimeMillis() - readingTime) + " ms");

		outputQueue.finish();
		writeThread.join();
		chunk_buffer.removeAllChunks();
		out.closeEntry();

		Log.info("Total : " + (System.currentTimeMillis() - startExport) + " ms");
		Log.info("Number of triangles : " + writeRunner.nbTrianglesCount + " triangles.");

		addMetadataToZip(mapInfo, out, writeRunner.nbTrianglesCount, 470, System.currentTimeMillis() - startExport);
		return writeRunner.nbTrianglesCount;
	}

	private static boolean chunkExists(MapInfo mapInfo, int x, int z) {
		try {
			Region region = Region.findRegion(mapInfo, x, z);
			if (region == null) {
				return false;
			}

			Chunk chunk = region.getChunk(x, z);
			if (chunk == null) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static void addTexturesToZip(MapInfo mapInfo, ZipOutputStream out, ProgressCallback progress) throws IOException {
		if (progress != null) {
			progress.setStatus(ProgressCallback.Status.EXPORTING_TEXTURES);
		}

		TextureExporter textureExporter = new TextureExporter(mapInfo.texturePath, progress);
		Iterator<Map.Entry<String, InputStream>> textureIterator = textureExporter.loadTextures();
		while (textureIterator.hasNext()) {
			Map.Entry<String, InputStream> tex = textureIterator.next();
			out.putNextEntry(new ZipEntry("tex/" + tex.getKey()));
			out.write(IOUtil.toByteArray(tex.getValue()));
			out.closeEntry();
		}
	}

	private static void addMTLToZip(ZipOutputStream out) throws IOException {
		out.putNextEntry(new ZipEntry("export.mtl"));
		byte[] fileContent = IOUtil.toByteArray(Resources.load(Materials.CONFIG_FILE));
		out.write(fileContent);
		out.closeEntry();
	}

	private static void addMetadataToZip(MapInfo mapInfo, ZipOutputStream out, long nbTriangles, int nbMaterials, long exportTime) throws IOException {
		out.putNextEntry(new ZipEntry("mcmetadata.json"));
		String metadataJSON = "{" +
				"\"exporterVersion\":\"" + KubityExporter.VERSION + "\"," +
				"\"mcVersion\":\"" + new LevelDat(mapInfo.path.toFile()).getVersion() + "\"," +
				"\"dimension\":\"" + mapInfo.dimension + "\"," +
				"\"nbTriangles\":" + nbTriangles + "," +
				"\"nbMaterials\":" + nbMaterials + "," +
				"\"exportTime\":" + exportTime +
				"}";
		out.write(metadataJSON.getBytes());
		out.closeEntry();
	}

	private static void deleteFiles(File... files) {
		for (File file : files) {
			try {
				Files.walkFileTree(file.toPath(), new DeletePathVisitor());
			} catch (IOException e) {}
		}
	}
}
