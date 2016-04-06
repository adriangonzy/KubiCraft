package org.jmc.threading;

import org.jmc.gui.MapThumbnail;
import org.jmc.world.*;
import org.jmc.world.Dimension;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

/**
 * @author adrian.
 */
public class ThumbnailChunkLoaderThread implements Runnable {

	/**
	 * Mapview where the thumbnail is rendered
	 */
	private MapThumbnail thumbnail;

	private MapInfo mapInfo;

	/**
	 * Checks wether the thumbnail is fully resized
	 */
	private boolean thumbnailExpanded;

	/**
	 * Frequency of repainting in ms.
	 */
	private final int REPAINT_FREQUENCY = 300;

	public ThumbnailChunkLoaderThread(final MapThumbnail thumbnail, MapInfo mapInfo) {
		this.mapInfo = mapInfo;
		this.thumbnail = thumbnail;
		this.thumbnailExpanded = false;

		thumbnail.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				thumbnailExpanded = true;
			}
		});
	}

	@Override
	public void run() {
		while (!thumbnailExpanded) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}

		long last_time = System.currentTimeMillis(), this_time;

		Rectangle bounds = thumbnail.getChunkBounds();
		int cxs = bounds.x;
		int czs = bounds.y;
		int cxe = bounds.x + bounds.width;
		int cze = bounds.y + bounds.height;
		Region region;
		Chunk chunk;
		for (int cx = cxs; cx <= cxe; cx++) {
			for (int cz = czs; cz <= cze; cz++) {

				try {
					region = Region.findRegion(mapInfo, cx, cz);
					chunk = region.getChunk(cx, cz);
				} catch (Exception e) {
					continue;
				}

				if (chunk == null) continue;

				chunk.renderImages(0, Integer.MAX_VALUE);
				thumbnail.addImage(chunk.getBlockImage(), null, chunk.getPosX() * 64, chunk.getPosZ() * 64);

				this_time = System.currentTimeMillis();
				if (this_time - last_time > REPAINT_FREQUENCY) {
					thumbnail.repaint();
					last_time = this_time;
				}
			}
		}
		thumbnail.repaint();
	}
}