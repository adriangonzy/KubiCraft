package org.jmc;

import java.io.File;
import java.nio.file.Paths;
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

	public static String exportsFolder = "kubicraft-exports";

	/**
	 * Output directory.
	 */
	public static String outputDir = System.getProperty("user.dir");

	static {
		// hack for mac bundled .app
		String userDir = System.getProperty("user.dir");
		if (userDir != null && userDir.endsWith(".app")) {
			outputDir = userDir.substring(0, userDir.length() - "kubicraft.app".length());
		}
	}

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
