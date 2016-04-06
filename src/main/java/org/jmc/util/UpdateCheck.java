package org.jmc.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jmc.export.KubityExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author adrian.
 */
public class UpdateCheck {

	public static String NEW_VERSION_DOWNLOAD_LINK = "https://github.com/adriangonzy/KubiCraft";
	public static String LATEST_VERSION_URL = "http://storage.googleapis.com/kubity-software-factory/minecraft/latest/version.txt";

	private static String fetchLatestVersion() {
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		     CloseableHttpResponse execute = httpClient.execute(new HttpGet(LATEST_VERSION_URL));
		     ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {

			if (execute.getStatusLine().getStatusCode() != 200) {
				return "0.0";
			}

			execute.getEntity().writeTo(outstream);
			
			return new String(outstream.toByteArray()).trim();
		} catch (IOException e) {
			return "0.0";
		}
	}

	public static boolean isNewVersionAvailable() {
		return KubityExporter.VERSION.compareTo(fetchLatestVersion()) < 0;
	}
}
