package org.jmc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author adrian.
 */
public class IOUtil {

	public static byte[] toByteArray(InputStream is) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
		{
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

			os.flush();

			return os.toByteArray();
		}
	}
}
