package org.jmc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


/**
 * Holds the global options for the program.
 * 
 * Some options are only used in GUI mode or command line mode, but most apply to both.
 */
public class Options
{

	public enum OffsetType
	{
		NONE,
		CENTER,
		CUSTOM
	}

	/**
	 * Output directory.
	 */
	public static File outputDir = new File(".");

	/**
	 * Lower bound of the volume to export.
	 */
	public static int minX=-32, minY=0, minZ=-32;

	/**
	 * Upper bound of the volume to export.
	 */
	public static int maxX=32, maxY=256, maxZ=32;

	/**
	 * How to scale the exported geometry.
	 */
	public static float scale = 1.0f;

	/**
	 * How to offset the coordinates of the exported geometry.
	 */
	public static OffsetType offsetType = OffsetType.NONE;

	/**
	 * Custom offset.
	 */
	public static int offsetX=0, offsetZ=0;

	/**
	 * List of block ids to exclude.
	 */
	public static Set<Short> excludeBlocks = new HashSet<>();

	/**
	 * Name of .MTL file to export.
	 */
	public static String mtlFileName = "minecraft.mtl";

	/**
	 * True if debug mode
	 */
	public static boolean debug = false;
	
	/**
	 * How many threads to use when exporting.
	 */
	public static int exportThreads = Runtime.getRuntime().availableProcessors();

	/**
	 * How many triangle are allowed before display a warning message
	 */
	public static final long MAX_WARNING_TRIANGLE = 2000000;

	/**
	 * How many triangle are allowed
	 */
	public static final long MAX_ALLOWED_TRIANGLE = 4000000;

	/**
	 * Maximum size of a file for export
	 */
	public static final long MAX_SIZE = 200 * 1024 * 1024;

}
