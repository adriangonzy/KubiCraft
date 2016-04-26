package org.jmc.export;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jmc.Options;
import org.jmc.util.Log;
import org.jmc.util.ProgressHttpEntityWrapper;
import org.jmc.world.LevelDat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

/**
 * Created by pitton on 2016-02-17.
 */
public class KubityExporter {

	public static String VERSION = ResourceBundle.getBundle("version").getString("version");

	public interface ErrorCallback {
		void handleError(String message);
		void handleWarning(String message);
	}

	public static void export(Path inputFile, final ProgressCallback progress, final ErrorCallback errorCallback) throws IOException, InterruptedException {
		final Pack pack = upload(inputFile, progress, errorCallback);
		if (pack == null) {
			if (progress != null) {
				progress.setStatus(ProgressCallback.Status.FINISHED);
			}
			return;
		}

		String url = getLoadingURL(pack.id, inputFile);
		if (!Desktop.isDesktopSupported()) {
			Log.info("You can find your project here : " + url);
			return;
		}
		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported(Desktop.Action.BROWSE)) {
			Log.info("You can find your project here : " + url);
			if (errorCallback != null) {
				errorCallback.handleError("Couldn't open your browser. You can find your project here: " + url);
			}
			return;
		}
		desktop.browse(URI.create(url));

		if (progress != null) {
			progress.setStatus(ProgressCallback.Status.PROCESSING);
		}

		monitorProcessing(pack, progress);
	}

	private static Pack upload(Path inputFile, final ProgressCallback progressCallback, ErrorCallback errorCallback) {
		try (CloseableHttpClient httpClient = createClient()) {
			String packerURL = getPackerURL();
			Log.info("Uploading to " + packerURL);
			HttpPost post = new HttpPost(packerURL);
			post.setHeader("user-agent", "kubicraft/" + VERSION);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("rawFile", inputFile.toFile(), ContentType.create("application/zip"), inputFile.getFileName().toString());

			final long exportSize = inputFile.toFile().length();

			post.setEntity(new ProgressHttpEntityWrapper(builder.build(), new ProgressHttpEntityWrapper.ProgressListener() {
				@Override
				public void transferred(long transferedBytes) {
					if (progressCallback != null) {
						progressCallback.setProgress((float)transferedBytes/(float)exportSize);
					}
				}
			}));

			try (CloseableHttpResponse execute = httpClient.execute(post)) {
				if (execute.getStatusLine().getStatusCode() != 200) {
					return null;
				}
				try (ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
					execute.getEntity().writeTo(outstream);
					if (progressCallback != null) {
						progressCallback.setProgress(1);
					}
					return new Gson().fromJson(new String(outstream.toByteArray()), Pack.class);
				}
			}
		} catch (IOException e) {
			Log.error("Unable to upload file " + inputFile.getFileName().toString() + " " + e , e);
			errorCallback.handleError("Unable to upload file " + inputFile.getFileName().toString() + ". Server  not found.");
		}
		return null;
	}

	private static CloseableHttpClient createClient() {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
			public X509Certificate[] getAcceptedIssuers(){return null;}
			public void checkClientTrusted(X509Certificate[] certs, String authType){}
			public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, trustAllCerts, new SecureRandom());
			SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			return HttpClientBuilder.create().setSSLSocketFactory(scsf).build();
		} catch (Exception e) {
			Log.error("Can't upload the zip", e);
			return HttpClientBuilder.create().build();
		}
	}

	private static String getPackerURL() {
		return "https://packer-preprod.kubity.com:443/api/pack";
	}

	private static String getLoadingURL(String packId, Path path) throws IOException {
		String url = "%s://%s/loading/%s?filename=%s&filesize=%s&pluginVersion=%s&softwareVersion=%s";
		String filename = URLEncoder.encode(path.getFileName().toString(), "UTF-8");
		String mcVersion = new LevelDat(path.toFile()).getVersion();
		return String.format(url, "https", "preprod.kubity.com", packId, filename, Files.size(path), KubityExporter.VERSION, mcVersion);
	}

	private static class Pack {
		public String id;
		public String status;
	}

	private static void monitorProcessing(final Pack pack, final ProgressCallback progress) {
		(new Thread(new Runnable() {
			@Override
			public void run() {
				Pack currentPack = pack;
				while (!currentPack.status.equals("FINISH")) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {}
					currentPack = getPack(currentPack.id);
					if (currentPack == null) {
						break;
					}
				}
				if (progress != null) {
					progress.setStatus(ProgressCallback.Status.FINISHED);
				}
			}
		})).start();
	}

	private static Pack getPack(String id) {
		try (CloseableHttpClient httpClient = createClient()) {
			HttpGet get = new HttpGet(getPackerURL() + "/" + id);
			get.setHeader("user-agent", "kubicraft/" + VERSION);

			try (CloseableHttpResponse execute = httpClient.execute(get)) {
				if (execute.getStatusLine().getStatusCode() != 200) {
					return null;
				}
				try (ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
					execute.getEntity().writeTo(outstream);
					String json = new String(outstream.toByteArray());
					if (Options.debug) {
						System.out.println(json);
					}
					return new Gson().fromJson(json, Pack.class);
				}
			}
		} catch (IOException e) {
			return null;
		}
	}
}
