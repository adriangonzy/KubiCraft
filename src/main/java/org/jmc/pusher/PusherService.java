package org.jmc.pusher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import org.jmc.Options;
import org.jmc.export.KubityExporter;
import org.jmc.export.ProgressCallback;
import org.jmc.util.Log;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Paul on 23/06/2016.
 */
public class PusherService {
	private static String PUSHER_EVENT_PACKER = "packer";
	private static String PUSHER_EVENT_MODEL = "model";

	private final KubityExporter.ErrorCallback errorCallback;
	private final ProgressCallback progress;
	private Pusher pusher = new Pusher(Options.ENVIRONMENT.pusher_key);

	public PusherService(ProgressCallback progress, KubityExporter.ErrorCallback errorCallback) {
		this.progress = progress;
		this.errorCallback = errorCallback;
	}


	private void connect() {
		pusher.connect(new ConnectionEventListener() {
			@Override
			public void onConnectionStateChange(ConnectionStateChange change) {
				System.out.printf("previous %s : current %s\n", change.getPreviousState(), change.getCurrentState());
				progress.setStatus(ProgressCallback.Status.PROCESSING);
			}

			@Override
			public void onError(String s, String s1, Exception e) {
				errorCallback.handleError("Couldn't pack your map.");
			}
		}, ConnectionState.ALL);
	}

	private void subscribe(String name) {
		Channel channel = pusher.subscribe(name);

		channel.bind(PUSHER_EVENT_PACKER, new SubscriptionEventListener() {
			@Override
			public void onEvent(String channel, String event, String data) {
				System.out.printf("channel %s event %s\n", channel, event);

				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				Pack pack = gson.fromJson(data, Pack.class);

				System.out.printf("data %s\n", gson.toJson(pack));

				if (pack.status.equals("FINISH")) {
					progress.setStatus(ProgressCallback.Status.FINISHED);
					disconnect();
				} else if (pack.status.equals("FAIL")) {
					errorCallback.handleError("Processing failed");
					disconnect();
				} else {
					progress.setStatus(ProgressCallback.Status.PROCESSING);
				}
			}
		});

		channel.bind(PUSHER_EVENT_MODEL, new SubscriptionEventListener() {
			@Override
			public void onEvent(String channel, String event, String data) {
				Model model = new Gson().fromJson(data, Model.class);
				openUrlInBrowser("http://" + Options.ENVIRONMENT.id + ".qrvr.io/p/" + model.id);
			}
		});
	}

	private void openUrlInBrowser(String receiptPath) {
		if (!Desktop.isDesktopSupported()) {
			Log.info("You can find your project here : " + receiptPath);
			return;
		}
		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported(Desktop.Action.BROWSE)) {
			Log.info("You can find your project here : " + receiptPath);
			if (errorCallback != null) {
				errorCallback.handleError("Couldn't open your browser. You can find your project here: " + receiptPath);
			}
			return;
		}
		try {
			desktop.browse(URI.create(receiptPath));
		} catch (IOException e) {
			errorCallback.handleError("Couldn't open your browser. You can find your project here: " + receiptPath);
		}
	}

	private void disconnect() {
		pusher.disconnect();
	}

	public void start(String channelId) {
		this.connect();
		this.subscribe(channelId);
	}
}
