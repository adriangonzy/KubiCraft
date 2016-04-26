package org.jmc.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author adrian.
 */
public class Resources {

	public static InputStream load(String resourceName) {
		InputStream res = Resources.class.getResourceAsStream(resourceName);
		if (res == null) {
			Log.error("Failed to load resource " + resourceName + ". Make sure to use an absolute path.", null);
		}
		return res;
	}

	public static InputStream loadCustom(String resourceName) {
		InputStream res;
		try {
			res = new FileInputStream(resourceName);
		} catch (FileNotFoundException e) {
			res = load(resourceName);
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

}
