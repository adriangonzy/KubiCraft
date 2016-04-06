package org.jmc;

import org.jmc.gui.CustomPalette;
import org.jmc.gui.MainWindow;
import org.jmc.util.Log;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Main
{

	private static MainWindow mainWindow;
	/**
	 * Start of program.
	 *
	 * @param args program arguments
	 */
	public static void main(String[] args)
	{
		SplashScreen sp = startSplashscreen();
		try {
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					Log.error("Uncaught exception in thread: " + t.getName(), e);
					mainWindow.showError("An unexpected error occured: " + e.getClass().getSimpleName() + ". Please restart.");
				}
			});

			Locale.setDefault(Locale.ENGLISH);

			if (args.length == 0) {
				runGUI();
				return;
			}
			if (args.length == 1) {
				if (args[0].equals("--debug")) {
					Options.debug = true;
					runGUI();
					return;
				}
			}
		} finally {
			if (sp != null && sp.isVisible()) {
				sp.close();
			}
		}
	}

	private static SplashScreen startSplashscreen() {
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			return null;
		}
		Graphics2D g = splash.createGraphics();
		if (g == null) {
			return null;
		}
		return splash;
	}


	private static void runGUI()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.put("Panel.background", CustomPalette.BACK_GROUND);
			UIManager.put("TextField.background", CustomPalette.BACK_GROUND);
			UIManager.put("TextField.foreground", CustomPalette.FORE_GROUND);
			UIManager.put("Button.background", CustomPalette.BACK_GROUND);
			UIManager.put("Button.foreground", CustomPalette.FORE_GROUND);
			UIManager.put("Button.disabledText", CustomPalette.FLASHY_GREEN);
			UIManager.put("Label.foreground", CustomPalette.FORE_GROUND);
			UIManager.put("CheckBox.foreground", CustomPalette.FORE_GROUND);
			UIManager.put("ProgressBar.background", CustomPalette.BLUE_GRAY);
			UIManager.put("ProgressBar.foreground", CustomPalette.LIGHT_GREEN);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		mainWindow = new MainWindow();
	}
}
