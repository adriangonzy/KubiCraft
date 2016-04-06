package org.jmc.util;

import org.jmc.world.Dimension;
import org.jmc.world.Region;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * File and directory related methods.
 */
public class Filesystem {

	public static final FileNameExtensionFilter zipFilter = new FileNameExtensionFilter(null, "zip");

	/**
	 * Gets the directory that Minecraft keeps its save files in. It works on
	 * all systems that Minecraft 1.2 works in.
	 * 
	 * @return path to the Minecraft dir
	 */
	public static File getMinecraftDir() {
		String minecraft = "minecraft";
		String osname = System.getProperty("os.name").toLowerCase();
		String default_home = System.getProperty("user.home", ".");

		if (osname.contains("solaris") || osname.contains("sunos") || osname.contains("linux")
				|| osname.contains("unix")) {
			return new File(default_home, "." + minecraft);
		}

		if (osname.contains("win")) {
			String win_home = System.getenv("APPDATA");
			if (win_home != null)
				return new File(win_home, "." + minecraft);
			else
				return new File(default_home, "." + minecraft);
		}

		if (osname.contains("mac")) {
			return new File(default_home, "Library/Application Support/" + minecraft);
		}

		return null;
	}

	/**
	 * Retrieves the list of minecraft map in the default folder.
	 * @return
	 */
	public static List<Path> getMinecraftMaps() {
		File minecraftDir = getMinecraftDir();
		if (minecraftDir == null) {
			return Collections.emptyList();
		}
		Path saves = Paths.get(minecraftDir.toPath().toString(), "saves");
		if (!Files.exists(saves)) {
			return Collections.emptyList();
		}
		List<Path> fileList = new ArrayList<>();
		for (File file : saves.toFile().listFiles()) {
			if (!hasValidRegionFolder(file)) {
				continue;
			}
			fileList.add(file.toPath());
		}
		return fileList;
	}

	/**
	 * Retrieves the list of enable dimension for the map.
	 *
	 * @param map
	 * @return Array of enable dimensions
	 */
	public static List<Dimension> getMinecraftDimension(Path map) {
		List<Dimension> dimension = new ArrayList();
		if (!Files.exists(map)) {
			return null;
		}

		//always add Overworld
		dimension.add(Dimension.OVERWORLD);
		//check nether
		if(isValidRegionFolder(Paths.get(map.toString(), "DIM-1", "region").toFile())){
			dimension.add(Dimension.NETHER);
		}
		//check the end
		if(isValidRegionFolder(Paths.get(map.toString(), "DIM1", "region").toFile())){
			dimension.add(Dimension.END);
		}
		return dimension;
	}

	public static boolean hasValidRegionFolder(File dir) {
		if (dir == null) {
			throw new RuntimeException("Invadid dir parameter! Must be non null");
		}
		if (!dir.exists() || !dir.isDirectory()) {
			return false;
		}
		File[] regionFolders = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					return false;
				}
				return pathname.getName().equals("region") && isValidRegionFolder(pathname);
			}
		});

		return !(regionFolders.length > 1 || regionFolders.length <= 0);
	}

	public static boolean isValidRegionFolder(File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			return false;
		}
		return dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(Region.REGION_FILE_REGEX);
			}
		}).length > 0;
	}

	public static Path unzip(Path file) throws IOException {
		Path destination = Files.createTempDirectory("minecraft_zip");
		Path returnDestFolder = destination;

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file.toFile()))) {
			ZipEntry entry = zis.getNextEntry();
			if (entry.isDirectory()) {
				returnDestFolder = Paths.get(destination.toString(), entry.getName());
			}
			while (entry != null) {
				Path outputPath = Paths.get(destination.toString(), entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(outputPath);
				} else {
					Files.copy(zis, outputPath);
				}
				entry = zis.getNextEntry();
			}
			zis.closeEntry();
		}

		if (!returnDestFolder.getParent().equals(destination)) {
			returnDestFolder = returnDestFolder.getParent();
		}
		return returnDestFolder;
	}
}
