/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import org.jmc.Options;
import org.jmc.config.BlockTypes;
import org.jmc.config.EntityTypes;
import org.jmc.config.Materials;
import org.jmc.export.KubityExporter;
import org.jmc.util.IOUtil;
import org.jmc.util.Log;
import org.jmc.util.Resources;
import org.jmc.world.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main program window.
 */
public class MainWindow extends JFrame implements KeyListener

{
	//Might want to up these later to support larger monitors...
	/**
	 * Maximum width of window.
	 */
	public static final int MAX_WIDTH=1920;
	/**
	 * Maximum height of window.
	 */
	public static final int MAX_HEIGHT=1080;

	private JPanel main;
	/**
	 * SubPanels
	 */
	private MenuPanel menu;
	private PreviewWindow preview;

	public static GUIConsoleLog consoleLog;

	/**
	 * Window contructor.
	 */
	public MainWindow()
	{
		super("Main Window");

		this.setFocusable(true);
		this.addKeyListener(this);

		try {
			Materials.initialize();
			BlockTypes.initialize();
			EntityTypes.initialize();
		}
		catch (Exception e) {
			Log.error("Error reading configuration file:", e);
		}

		setPreferredSize(new Dimension(1024,768));
		setMinimumSize(new Dimension(1024,768));
		setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("KubiCraft - " + KubityExporter.VERSION);
		setLocationRelativeTo(null);
		try {
			byte[] imageContent = IOUtil.toByteArray(Resources.load("/conf/ui/kubicraft.png"));
			Image image = new ImageIcon(imageContent).getImage();
			setIconImage(image);
		} catch (IOException e) {
			log("We can't load the favicon", false);
		}


		main = new JPanel(new BorderLayout());
		menu = new MenuPanel(this);
		preview = new PreviewWindow(this);

		main.add(menu.getPanel());
		add(main);
		
		setVisible(true);
	}

	public static void log(String message, boolean isError) {
		if (Options.debug) {
			consoleLog.log(message, isError);
		}
	}

	public void showMenu() {
		main.removeAll();
		main.add(menu.getPanel());
		main.validate();
		main.repaint();
	}

	public void showPreview(Path path) {
		preview.update(new MapInfo(path, org.jmc.world.Dimension.OVERWORLD));
		main.removeAll();
		main.add(preview);
		main.validate();
		main.repaint();
	}

	public void showError(String errorMessage) {
		final ErrorMessage error = new ErrorMessage(errorMessage);
		add(error.mainPanel, BorderLayout.SOUTH);
		validate();
		repaint();
		error.setCloseHandler(new ErrorMessage.CloseHandler() {
			@Override
			public void onClose() {
				remove(error.mainPanel);
				validate();
				repaint();
			}
		});
	}

	// Swing BUG <a href="http://bugs.java.com/view_bug.do?bug_id=6464548">issue</a>} => use this fix for forcing max size.
	@Override
	public void paint(Graphics g) {
		Dimension d = getSize();
		Dimension m = getMaximumSize();
		boolean resize = d.width > m.width || d.height > m.height;
		d.width = Math.min(m.width, d.width);
		d.height = Math.min(m.height, d.height);

		if (resize) {
			Point p = getLocation();
			setVisible(false);
			setSize(d);
			setLocation(p);
			setVisible(true);
		}
		super.paint(g);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.isMetaDown() || e.isControlDown()) && e.getKeyCode() == KeyEvent.VK_D) {
			if (!Options.debug) {
				if (consoleLog == null) {
					consoleLog = new GUIConsoleLog();
				}
				consoleLog.setVisible(true);
				Options.debug = true;
			} else {
				consoleLog.setVisible(false);
				Options.debug = false;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
