/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.threading;

import org.jmc.gui.MapPreview;
import org.jmc.world.Chunk;
import org.jmc.world.Dimension;
import org.jmc.world.MapInfo;
import org.jmc.world.Region;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Chunk loader that loads only the chunks visible on the screen and
 * removes the chunks that go off screen. 
 * @author danijel
 *
 */
public class ViewChunkLoaderThread implements ChunkLoaderThread {
	/**
	 * Used by isRunning and stopRunning methods.
	 */
	private boolean running;

	/**
	 * Reference to preview panel so we can change the preview.
	 */
	private MapPreview preview;

	/**
	 * World save information.
	 */
	private MapInfo mapInfo;

	/**
	 * Collection of chunk images from the preview panel.
	 */
	private Vector<MapPreview.ChunkImage> chunk_images;

	/**
	 * Frequency of repainting in ms.
	 */
	private final int REPAINT_FREQUENCY=100;

	/**
	 * Maximum number of chunks loaded.
	 */
	public final int MAX_CHUNK_NUM=32768;

	/**
	 * A collection of loaded chunk IDs.
	 */
	Set<Integer> loaded_chunks;

	/**
	 * Variables defining the Y-axis boundaries of the current preview. 
	 */
	private int floor, ceiling;
	private boolean y_bounds_changed;
	private Object y_bounds_sync;

	/**
	 * Main constructor.
	 * @param preview reference to the preview panel
	 */
	public ViewChunkLoaderThread(MapPreview preview, MapInfo mapInfo) {
		this.preview=preview;
		this.mapInfo = mapInfo;

		chunk_images=preview.getChunkImages();

		loaded_chunks=new HashSet<>();
		floor=0;
		ceiling=Integer.MAX_VALUE;
		y_bounds_changed=false;
		y_bounds_sync=new Object();
	}

	/**
	 * Main thread method.
	 */
	@Override
	public void run() {
		running=true;

		Region region;
		Chunk chunk;

		Rectangle prev_bounds=new Rectangle();

		long last_time=System.currentTimeMillis(),this_time;

		loaded_chunks.clear();
		while(running)
		{
			Rectangle bounds=preview.getChunkBounds();
			int floor,ceiling;
			synchronized (y_bounds_sync) {
				floor=this.floor;
				ceiling=this.ceiling;
			}

			boolean stop_iter=false;
			if(!bounds.equals(prev_bounds) || y_bounds_changed)
			{
				int cxs=bounds.x;
				int czs=bounds.y;
				int cxe=bounds.x+bounds.width;
				int cze=bounds.y+bounds.height;

				if(y_bounds_changed)
				{
					y_bounds_changed=false;
					loaded_chunks.clear();
					chunk_images.clear();
				}

				Iterator<MapPreview.ChunkImage> iter=chunk_images.iterator();
				while(iter.hasNext())
				{
					MapPreview.ChunkImage chunk_image=iter.next();

					int cx=chunk_image.x/64;
					int cz=chunk_image.y/64;

					if(cx<cxs || cx>cxe || cz<czs || cz>cze)
					{
						loaded_chunks.remove(cx*MAX_CHUNK_NUM+cz);
						iter.remove();
					}

					Rectangle new_bounds=preview.getChunkBounds();
					if(!bounds.equals(new_bounds) || y_bounds_changed)
					{
						stop_iter=true;
						break;
					}

					if(!running) return;
				}


				for(int cx=cxs; cx<=cxe && !stop_iter; cx++)
				{
					for(int cz=czs; cz<=cze && !stop_iter; cz++)
					{
						if(loaded_chunks.contains(cx*MAX_CHUNK_NUM+cz)) continue;

						try {
							// TODO: if the region is missing we should not iterate over all the chunks in the region...
							region=Region.findRegion(mapInfo, cx, cz);
							chunk=region.getChunk(cx, cz);
						} catch (Exception e) {
							continue;
						}

						if(chunk==null) continue;


						int ix=chunk.getPosX();
						int iy=chunk.getPosZ();

						chunk.renderImages(floor, ceiling);
						BufferedImage height_img=null;
						BufferedImage img=chunk.getBlockImage();

						preview.addImage(img, height_img, ix*64, iy*64);
						loaded_chunks.add(cx*MAX_CHUNK_NUM+cz);


						this_time=System.currentTimeMillis();
						if(this_time-last_time>REPAINT_FREQUENCY)
						{
							preview.repaint();
							last_time=this_time;
						}

						Rectangle new_bounds=preview.getChunkBounds();
						if(!bounds.equals(new_bounds) || y_bounds_changed) {
							stop_iter = true;
						}

						if(!running) return;
					}
				}
				preview.redraw();
				preview.repaint();

			}
			prev_bounds=bounds;

			if(!stop_iter)
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
		}

	}

	/**
	 * Interface override.
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	/**
	 * Interface override.
	 */
	@Override
	public void stopRunning() {
		running=false;

	}

	/**
	 * Change the Y-axis bounds needed for drawing. 
	 * @param floor
	 * @param ceiling
	 */
	public void setYBounds(int floor, int ceiling)
	{
		synchronized (y_bounds_sync) 
		{
			this.floor=floor;
			this.ceiling=ceiling;	
			y_bounds_changed=true;
		}		
	}

}
