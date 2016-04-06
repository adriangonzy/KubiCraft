package org.jmc.util;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author adrian.
 */
public class Resources {

	static final String TEX_FOLDER = "conf/textures/tex";

	public static InputStream load(String resourceName) {
		InputStream res = Resources.class.getResourceAsStream(resourceName);
		if (res == null) {
			Log.error("Failed to load resource " + resourceName + ". Make sure to use an absolute path.", null);
		}
		return res;
	}

	public static InputStream safeLoad(String resourceName) {
		InputStream res = load(resourceName);
		if (res == null) {
			throw new RuntimeException(resourceName + " must be loaded for exporter to work properly.");
		}
		return res;
	}

	public static Iterator<Map.Entry<String, InputStream>> loadTextures() throws IOException {

		// load texture paths by name
		final Map<String, String> textures = new HashMap<>();

		final File jarFile = new File(Resources.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		if (jarFile.isFile()) {
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while(entries.hasMoreElements()) {
				String test = entries.nextElement().getName();
				if (test.startsWith(TEX_FOLDER) && !test.equals(TEX_FOLDER + "/")) { //filter according to the path
					String[] segments = test.split("/");
					textures.put(segments[segments.length - 1], "/" + test);
				}
			}
			jar.close();
		} else {
			BufferedReader in=new BufferedReader(new InputStreamReader(load("/" + TEX_FOLDER)));
			String texName;
			while((texName=in.readLine())!=null) {
				textures.put(texName, "/" + TEX_FOLDER + "/" + texName);
			}
		}
		final Iterator<Map.Entry<String, String>> iterator = textures.entrySet().iterator();

		return new Iterator<Map.Entry<String, InputStream>>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Map.Entry<String, InputStream> next() {
				Map.Entry<String, String> entry = iterator.next();
				return new AbstractMap.SimpleEntry<>(entry.getKey(), load(entry.getValue()));
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
}
