package org.jmc.export;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jmc.Options;
import org.jmc.pusher.Pack;
import org.jmc.pusher.PusherService;
import org.jmc.util.Log;
import org.jmc.util.ProgressHttpEntityWrapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

/**
 * Created by pitton on 2016-02-17.
 */
public class KubityExporter {
	public static String PACKER_URL = Options.ENVIRONMENT.protocol + "://" +
										Options.ENVIRONMENT.packer_id + ".qrvr.io:" +
										Options.ENVIRONMENT.port + "/api/pack";

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
		PusherService pusherService = new PusherService(progress, errorCallback);
		pusherService.start(pack.channelId);
		progress.setStatus(ProgressCallback.Status.PROCESSING);
	}

	private static Pack upload(Path inputFile, final ProgressCallback progressCallback, ErrorCallback errorCallback) {
		try (CloseableHttpClient httpClient = createClient()) {
			String packerURL = PACKER_URL;
			Log.info("Uploading to " + packerURL);
			HttpPost post = new HttpPost(packerURL);
			post.setHeader("user-agent", "kubicraft/" + VERSION);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("email", "empty@mail.fr");
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
}
