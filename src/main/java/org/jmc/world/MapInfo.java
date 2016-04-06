package org.jmc.world;

import java.nio.file.Path;

/**
 * @author adrian.
 */
public class MapInfo {
	public Path path;
	public Dimension dimension;

	public MapInfo(Path path, Dimension dimension) {
		this.dimension = dimension;
		this.path = path;
	}
}
