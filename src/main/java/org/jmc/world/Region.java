/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.world;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.jmc.Options;
import org.jmc.util.Log;

/**
 * Region file class.
 * This file contains individual chunks. It can be either the old MCRegion format or
 * the new Anvil format.
 * @author danijel
 *
 */
public class Region implements Iterable<Chunk> {

	public static String REGION_FILE_REGEX = "r\\.[[-]0-9]+\\.[[-]0-9]+\\.mca";

	private static final Map<String, Region> regionsCache = new HashMap<>();

	private final Map<Integer, Chunk> chunkCache = new HashMap<>();

	public static void clearCache() {
		regionsCache.clear();
	}

	/**
	 * Path to the file.
	 */
	private final File region_file;
	/**
	 * Buffer of offsets of individual chunks.
	 */
	private final ByteBuffer offset;
	/**
	 * Buffer of timestamps of individual chunks.
	 */
	private final ByteBuffer timestamp;
	/**
	 * Is the file in anvil or old mcregion format.
	 */
	final boolean is_anvil;

	/**
	 * Main constructor.
	 * @param file path to file
	 * @throws IOException if error occurs
	 */
	private Region(File file) throws IOException
	{
		if(!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());
		
		if(file.getName().endsWith("mcr"))
			is_anvil=false;
		else if(file.getName().endsWith("mca"))
			is_anvil=true;
		else
			throw new IOException("Unknown file extension! Only .mcr or .mca supported!");

		region_file=file;

		FileInputStream fis=new FileInputStream(region_file);

		byte [] offset_array=new byte[4096];
		fis.read(offset_array);
		offset=ByteBuffer.wrap(offset_array);

		byte [] timestamp_array=new byte[4096];
		fis.read(timestamp_array);
		timestamp=ByteBuffer.wrap(timestamp_array);

		fis.close();
	}	
	
	/**
	 * Find a region file containing the chunk with given coordinates.
	 * @param mapInfo world save information
	 * @param chunk_x x coordinate of chunk
	 * @param chunk_z z coordinate of chunk
	 * @return region file object
	 * @throws IOException of error occurs
	 */
	public static Region findRegion(MapInfo mapInfo, int chunk_x, int chunk_z) throws IOException
	{
		int rx,rz;
		//equivalent to dividing by 32
		rx = chunk_x >> 5;
		rz = chunk_z >> 5;

		String mcaFileName = "r." + rx + "." + rz + ".mca";
		String regCacheKey = mapInfo.path.toAbsolutePath() + "_" + mapInfo.dimension.getName() + "_" + mcaFileName;

		Region region = regionsCache.get(regCacheKey);
		if (region != null) {
			return region;
		}


		File regionFile;
		if(mapInfo.dimension == Dimension.OVERWORLD)
			regionFile = Paths.get(mapInfo.path.toAbsolutePath().toString(), "region", mcaFileName).toFile();
		else
			regionFile = Paths.get(mapInfo.path.toAbsolutePath().toString(), "DIM" + mapInfo.dimension.getIndex(), "region", mcaFileName).toFile();

		region = new Region(regionFile);
		regionsCache.put(regCacheKey, region);

		return region;
	}

	/**
	 * Get the list of all regions in the given save.
	 * @param mapInfo world save information
	 * @return collection of region file objects
	 * @throws IOException if error occurs
	 */
	public static Vector<Region> loadAllRegions(MapInfo mapInfo) throws IOException
	{
		List<Region> ret=new ArrayList<>();
		File dir;
		if (mapInfo.dimension == Dimension.OVERWORLD) {
			dir = Paths.get(mapInfo.path.toString(), "region").toFile();
		} else {
			dir = Paths.get(mapInfo.path.toString(), "DIM" + mapInfo.dimension.getIndex(), "region").toFile();
		}
		if(!dir.exists() || !dir.isDirectory())
			throw new FileNotFoundException(dir.getAbsolutePath());

		File[] files=dir.listFiles();
		for(File f:files)
		{
			String name=f.getName();
			if(name.matches(REGION_FILE_REGEX))
			{
				Region region = new Region(f);
				regionsCache.put(mapInfo.path.toAbsolutePath().toString()+mapInfo.dimension.getName()+f.getName(), region);
				ret.add(region);
			}
		}

		return new Vector<>(ret);
	}
	
	/**
	 * Retrieve the given chunk.
	 * @param x x coordinate of the chunk
	 * @param z z coordinate of the chunk
	 * @return chunk object
	 * @throws Exception if error occurs while reading the chunk
	 */
	public Chunk getChunk(int x, int z) throws Exception
	{			
		int cx=x%32;
		int cz=z%32;
		if(cz<0) 
			cz=32+cz;
		if(cx<0) 
			cx=32+cx;
		int loc = cx + cz * 32;		
		Chunk chunk = getChunk(loc);
		
		if(chunk!=null && (chunk.getPosX()!=x || chunk.getPosZ()!=z))
		{
			throw new Exception("Chunk coord don't match!");
		}
		 
		return chunk;
	}


	/**
	 * Get the Nth chunk in this file.
	 * @param idx index of the chunk
	 * @return chunk object
	 * @throws Exception if error occurs while reading the chunk
	 */
	public Chunk getChunk(int idx) throws Exception
	{
		Chunk chunk = chunkCache.get(idx);
		if (chunk != null) {
			return chunk;
		}
		int off = offset.getInt(idx*4);
		int sec = off >> 8;
		int len = off & 0xff;
		//int ts = THIS IS UNUSED - line below is to supress the warning 
		timestamp.getInt(idx*4);

		if(sec<2)
			return null;

		RandomAccessFile raf=new RandomAccessFile(region_file, "r");
		raf.seek(sec*4096);

		len=raf.readInt();
		int compression_type=raf.readByte();
		if(compression_type==1) //GZIP
		{
			byte[] buf = new byte[len - 1];
			raf.read(buf);
			raf.close();
			InputStream is=new GZIPInputStream(new ByteArrayInputStream(buf));
			chunk = new Chunk(is, is_anvil);
			chunkCache.put(idx, chunk);
			return chunk;
		}
		else if(compression_type==2)//Inflate
		{
			byte[] buf = new byte[len - 1];
			raf.read(buf);
			raf.close();
			InputStream is=new InflaterInputStream(new ByteArrayInputStream(buf));
			chunk = new Chunk(is, is_anvil);
			chunkCache.put(idx, chunk);
			return chunk;
		}
		else
		{
			raf.close();
			throw new Exception("Wrong compression type!");
		}
	}

	/**
	 * Prints a list of available chunks to the stnadard output.
	 */
	public void printAvailableChunks()
	{
		int i=0;
		for(int z=0; z<32; z++)
			for(int x=0; x<32; x++,i++)
			{
				int off=offset.getInt(4*i);
				if(off!=0)
				{
					System.out.println("{"+x+","+z+"} -> "+(off>>8)+"/"+(off&0xff));
				}
			}
	}

	/**
	 * Small internal class for iterating chunks in the region file.
	 * @author danijel
	 *
	 */
	class ChunkIterator implements Iterator<Chunk>
	{

		/**
		 * Reference to the region file.
		 */
		Region region;
		/**
		 * Position of the iterator within the file.
		 */
		int pos;

		/**
		 * Main constructor.
		 * @param region reference to the region file
		 */
		public ChunkIterator(Region region) {
			this.region=region;
			pos=0;
		}

		/**
		 * Interface override.
		 */
		@Override
		public boolean hasNext() {
			for(int x=pos+1;x<1024;x++)
				if(region.offset.getInt(x*4)!=0)
					return true;

			return false;
		}

		/**
		 * Interface override.
		 */
		@Override
		public Chunk next() {

			Chunk ret=null;

			while(offset.getInt(pos*4)==0)
			{				
				pos++;
			}

			try {
				ret=getChunk(pos);
			} catch (Exception e) {
				Log.error("Error getting chunk "+pos, e);
			}
			
			pos++;

			return ret;
		}

		/**
		 * Interface override. Not implemented!
		 */
		@Override
		public void remove() {

			//TODO: not yet supported
			
		}

	}

	/**
	 * Returns the chunk iterator for the given file.
	 * It allows to iterate the chunks within the file in the following manner:
	 * <pre>for(Chunk chunk:region) {} </pre>
	 */
	@Override
	public Iterator<Chunk> iterator() 
	{
		Iterator<Chunk> ret=new ChunkIterator(this);
		return ret;
	}
}
