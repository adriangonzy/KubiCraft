package org.jmc.util;

import org.jmc.gui.MainWindow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Logging methods
 */
public class Log
{

	private static final Logger log = Logger.getLogger(Log.class.getName());

	/**
	 * Logs a debug message to stdout.
	 *
	 * @param msg string to be logged
	 */
	public static void debug(String msg)
	{
		log.config(msg);
	}

	/**
	 * Logs an informational message to stdout.
	 * If the GUI is up, it will also be shown in the messages area of the main window.
	 *
	 * @param msg string to be logged
	 */
	public static void info(String msg)
	{
		log.info(msg);
		MainWindow.log(msg, false);
	}

	/**
	 * Logs an error message to stderr.
	 * If the GUI is up, it will also be shown in a popup window, and the stack trace 
	 * written to the messages area of the main window.
	 *
	 * @param msg string to be logged
	 * @param ex (optional) exception that caused the error
	 * @param popup pop a message
	 */
	public static void error(String msg, Throwable ex, boolean popup)
	{
		log.log(Level.SEVERE, msg, ex);
		MainWindow.log("ERROR: "+msg, true);
		if (ex != null)
		{
			// write the full stack trace to the message area
			final StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			MainWindow.log(sw.toString(), true);
		}
	}

	/**
	 * Version that automatically pops a message.
	 * @param msg
	 * @param ex
	 */
	public static void error(String msg, Throwable ex)
	{
		error(msg,ex,false);
	}

}
