package org.jmc.threading;

import org.jmc.geom.FaceUtils.Face;
import org.jmc.threading.ThreadOutputQueue.ChunkOutput;
import org.jmc.world.Chunk;
import org.jmc.world.MapInfo;
import org.jmc.world.Region;

import java.awt.*;
import java.util.List;

public class ReaderRunnable implements Runnable {
	private ChunkDataBuffer chunkBuffer;
	private ThreadChunkDeligate chunkDeligate;
	private Point chunkStart;
	private Point chunkEnd;
	private ThreadOutputQueue outputQueue;
	private int start;
	private int end;
	private List<Point> inputPoints;
	private MapInfo mapInfo;

	public ReaderRunnable(MapInfo mapInfo, ChunkDataBuffer chunk_buffer, Point chunkStart, Point chunkEnd, List<Point> inputPoints, int start, int end,
	                      ThreadOutputQueue outQueue) {
		super();
		this.mapInfo = mapInfo;
		this.chunkBuffer = chunk_buffer;
		this.chunkDeligate = new ThreadChunkDeligate(chunk_buffer);
		this.chunkStart = chunkStart;
		this.chunkEnd = chunkEnd;
		this.inputPoints = inputPoints;
		this.start = start;
		this.end = end;
		this.outputQueue = outQueue;
	}

	@Override
	public void run() {
		Point chunkCoord;
		for (int i = start ; i < end ; i++) {
			chunkCoord = inputPoints.get(i);
			ChunkOutput output = exportChunk(chunkCoord);
			if (output == null){
				continue;
			}
			outputQueue.add(output);
		}
	}

	private ChunkOutput exportChunk(Point chunkCoord){
		int chunkX = chunkCoord.x;
		int chunkZ = chunkCoord.y;

		// load chunk being processed to the buffer
		if (!addChunkIfExists(mapInfo, chunkBuffer, chunkX, chunkZ))
			return null;

		// also load chunks from x-1 to x+1 and z-1 to z+1
		for (int lx = chunkX - 1; lx <= chunkX + 1; lx++) {
			for (int lz = chunkZ - 1; lz <= chunkZ + 1; lz++) {
				if (lx < chunkStart.x || lx > chunkEnd.x || lz < chunkStart.y || lz > chunkEnd.y)
					continue;

				if (lx == chunkX && lz == chunkZ)
					continue;

				addChunkIfExists(mapInfo, chunkBuffer, lx, lz);
			}
		}

		chunkDeligate.setCurrentChunk(new Point(chunkX, chunkZ));

		// export the chunk to the OBJ
		List<Face> faces = new ChunkProcessor().process(chunkDeligate, chunkX, chunkZ);

		/*** Commented for the moment because it causes bugs ***/
		/*** TODO: refactor the whole chunkbuffer and chunkdeligate using global cache ***/
		// remove the chunks we won't need anymore from the buffer
//		for (int lx = chunkX - 1; lx <= chunkX + 1; lx++) {
//			for (int lz = chunkZ - 1; lz <= chunkZ + 1; lz++) {
//				chunkBuffer.removeChunk(lx, lz);
//			}
//		}
		
		ChunkOutput output = new ChunkOutput(faces);
		return output;
	}
	
	private static boolean addChunkIfExists(MapInfo mapInfo, ChunkDataBuffer chunk_buffer, int x, int z) {
		if (chunk_buffer.hasChunk(x, z)) {
			return true;
		}

		try {
			Region region = Region.findRegion(mapInfo, x, z);
			if (region == null) {
				return false;
			}

			Chunk chunk = region.getChunk(x, z);
			if (chunk == null) {
				return false;
			}

			chunk_buffer.addChunk(chunk);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
