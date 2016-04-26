package org.jmc.world;

import java.nio.file.Path;

/**
 * @author adrian.
 */
public class MapInfo {
	public Path path;
	public Dimension dimension;
	public String texturePath;

	public MapInfo(Path path, Dimension dimension, String texturePath) {
		this.dimension = dimension;
		this.path = path;
		this.texturePath = texturePath;
	}

	public MapInfo(Path path, Dimension dimension) {
		this(path, dimension, null);
	}
}
