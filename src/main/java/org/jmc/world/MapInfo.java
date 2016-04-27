package org.jmc.world;

import java.nio.file.Path;

/**
 * @author adrian.
 */
public class MapInfo {
	public Path path;
	public Dimension dimension;
	public SelectionBounds bounds;
	public String texturePath;

	public MapInfo(Path path, Dimension dimension, String texturePath) {
		this.dimension = dimension;
		this.path = path;
		this.texturePath = texturePath;
		this.bounds = new SelectionBounds();
	}

	public MapInfo(Path path, Dimension dimension) {
		this(path, dimension, null);
	}

	public static class SelectionBounds {
		/**
		 * Lower bound of the volume to export.
		 */
		public int minX=-32, minY=0, minZ=-32;

		/**
		 * Upper bound of the volume to export.
		 */
		public int maxX=32, maxY=256, maxZ=32;

		public SelectionBounds() {
			// default values
		}

		public SelectionBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}

		@Override
		public String toString() {
			return String.format("min [ x:%d z:%d ] max [ x:%d z:%d ]", minX, minZ, maxX, maxZ);
		}
	}
}
