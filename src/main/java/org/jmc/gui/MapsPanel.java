package org.jmc.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jmc.Options;
import org.jmc.threading.ThumbnailChunkLoaderThread;
import org.jmc.util.Filesystem;
import org.jmc.util.Messages;
import org.jmc.world.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author paul
 */
public class MapsPanel {
	/**
	 * Thumbnail height need to be divisible by chunk_size / zoom
	 * chunk_size = 64 and zoom = 0.1f -> 6
	 */
	private static final int HEIGHT_MAPITEM = 90;
	private static final int WIDTH_MAPITEM_TITLE = 165;


	private MainWindow mainWindow;

	//GUI
	private JButton hereButton;
	private JLabel titleLabel1;
	private JLabel titleLabel2;
	private JPanel mapsPanel;
	private JPanel mapsContainer;
	private JScrollPane scrollMap;
	private JPanel maps;
	private JPanel helpFooter;

	private static File selectionMapsFolder;
	private static Component[] mapsSave = new Component[0];

	public MapsPanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;

		$$$setupUI$$$();
		init();
		mapsPanel.addComponentListener(new resizeListener());
		//Add maps
		if (mapsSave.length == 0) {
			addMaps();
			mapsSave = maps.getComponents();
		} else {
			for (Component c : mapsSave) {
				maps.add(c);
			}
			maps.repaint();
			maps.validate();
		}
	}

	private void createUIComponents() {
		maps = new JPanel(new GridLayout(0, 3, 0, 0));
		titleLabel1 = new JLabel();
		titleLabel2 = new JLabel();
	}

	public MapsPanel init() {
		//Titles and text
		titleLabel1.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 20));
		titleLabel2.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 20));

		hereButton.setText("Here");
		hereButton.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 20));
		hereButton.setForeground(CustomPalette.GREEN);

		//Panels
		mapsPanel.setBackground(CustomPalette.DARK_BLUE);
		Border dash = BorderFactory.createDashedBorder(CustomPalette.GREEN, 2, 2);
		mapsPanel.setBorder(dash);

		//Scroll
		scrollMap.getVerticalScrollBar().setUI(new CustomScrollBar());
		scrollMap.getHorizontalScrollBar().setUI(new CustomScrollBar());
		scrollMap.setBackground(CustomPalette.DARK_BLUE.darker());
		scrollMap.setBorder(null);
		scrollMap.getVerticalScrollBar().setUnitIncrement(12);
		scrollMap.getHorizontalScrollBar().setUnitIncrement(12);

		//ActionsListeners
		hereButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser() {

					@Override
					public void approveSelection() {
						File selectedFile = getSelectedFile();
						if (selectedFile.isFile() && !Filesystem.zipFilter.accept(selectedFile)) {
							return;
						}
						super.approveSelection();
					}

				};
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				if (selectionMapsFolder != null) {
					fileChooser.setCurrentDirectory(selectionMapsFolder);
				}

				if (fileChooser.showDialog(MapsPanel.this.getPanel(),
						Messages.getString("MainPanel.CHOOSE_FOLDER")) == JFileChooser.APPROVE_OPTION) {

					File selectedMapFolder = fileChooser.getSelectedFile();
					selectionMapsFolder = selectedMapFolder.getParentFile();
					String path = selectedMapFolder.getAbsolutePath();

					// Check for the zip
					if (!Files.isDirectory(Paths.get(path)) && Filesystem.zipFilter.accept(Paths.get(path).toFile())) {
						try {
							File save = Filesystem.unzip(Paths.get(path)).toFile();
							if (Filesystem.hasValidRegionFolder(save)) {
								mainWindow.showPreview(save.toPath());
								return;
							}
						} catch (IOException ex) {
						}

						mainWindow.showError(Messages.getString("MainPanel.ENTER_VALID_ZIP"));
						return;
					}

					if (!Filesystem.hasValidRegionFolder(Paths.get(path).toFile())) {
						mainWindow.showError(Messages.getString("MainPanel.ERR_REGION_FOLDER"));
						return;
					}
					mainWindow.showPreview(Paths.get(path));
				}
			}
		});

		return this;
	}

	public JPanel getPanel() {
		return mapsPanel;
	}

	private void addMaps() {
		final java.util.List<Path> map = Filesystem.getMinecraftMaps();

		if (map.size() == 0) {
			this.titleLabel1.setText("- No saves founds -");
		}

		JButton titleButton;
		initNoSavesFoundFallback();

		for (final Path p : map) {
			JPanel mapItem = new JPanel();

			titleButton = new JButton(p.getFileName().toString());
			titleButton.setPreferredSize(new Dimension(WIDTH_MAPITEM_TITLE, HEIGHT_MAPITEM));
			titleButton.setMinimumSize(new Dimension(WIDTH_MAPITEM_TITLE, HEIGHT_MAPITEM));
			titleButton.setMaximumSize(new Dimension(WIDTH_MAPITEM_TITLE, HEIGHT_MAPITEM));
			titleButton.setFont(CustomFont.minecraft);
			titleButton.setFocusPainted(false);
			titleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainWindow.showPreview(p);
				}
			});

			//load empty preview
			MapThumbnail thumbnail = new MapThumbnail(HEIGHT_MAPITEM, HEIGHT_MAPITEM, p);
			thumbnail.setPreferredSize(new Dimension(HEIGHT_MAPITEM, HEIGHT_MAPITEM));
			thumbnail.setMinimumSize(new Dimension(HEIGHT_MAPITEM, HEIGHT_MAPITEM));
			thumbnail.setMaximumSize(new Dimension(HEIGHT_MAPITEM * 2, HEIGHT_MAPITEM * 2));
			mapItem.add(thumbnail);

			//load chunks
			final ThumbnailChunkLoaderThread thumbnailLoader = new ThumbnailChunkLoaderThread(thumbnail, new MapInfo(p, org.jmc.world.Dimension.OVERWORLD));
			(new Thread(thumbnailLoader)).start();

			mapItem.add(titleButton);
			maps.add(mapItem);
		}
	}

	public void initNoSavesFoundFallback() {

		JPanel mapDownloads = new JPanel();
		JLabel explain = new JLabel("Find cool maps in any of this websites");
		explain.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 20));
		explain.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		explain.setForeground(CustomPalette.FORE_GROUND);

		mapDownloads.setLayout(new BoxLayout(mapDownloads, BoxLayout.Y_AXIS));
		mapDownloads.add(explain);
		mapDownloads.add(Box.createRigidArea(new Dimension(30, 30)));

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createRigidArea(new Dimension(30, 30)));
		buttons.add(newDownloadLink("Planet Minecraft", "http://www.planetminecraft.com/resources/projects/any/?share=world_link"));
		buttons.add(Box.createRigidArea(new Dimension(30, 30)));
		buttons.add(newDownloadLink("Minecraft World Map", "http://www.minecraftworldmap.com/search"));
		buttons.add(Box.createRigidArea(new Dimension(30, 30)));
		buttons.add(newDownloadLink("Minecraft Maps", "http://www.minecraftmaps.com/"));
		buttons.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		mapDownloads.add(buttons);

		helpFooter.setLayout(new BoxLayout(helpFooter, BoxLayout.Y_AXIS));
		helpFooter.add(Box.createRigidArea(new Dimension(15, 15)), 0);
		helpFooter.add(mapDownloads, 1);
	}

	public JButton newDownloadLink(String text, final String url) {
		JButton button = new JButton(text);
		int buttonWidth = WIDTH_MAPITEM_TITLE + HEIGHT_MAPITEM;
		button.setPreferredSize(new Dimension(buttonWidth, HEIGHT_MAPITEM / 2));
		button.setMinimumSize(new Dimension(buttonWidth, HEIGHT_MAPITEM / 2));
		button.setMaximumSize(new Dimension(buttonWidth, HEIGHT_MAPITEM / 2));
		button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		button.setForeground(CustomPalette.GREEN);

		button.setFont(CustomFont.minecraft);
		button.setFocusPainted(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Desktop.isDesktopSupported()) {
					return;
				}
				Desktop desktop = Desktop.getDesktop();
				if (!desktop.isSupported(Desktop.Action.BROWSE)) {
					return;
				}
				try {
					desktop.browse(URI.create(url));
				} catch (IOException e1) {
					return;
				}
			}
		});
		return button;
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		createUIComponents();
		mapsPanel = new JPanel();
		mapsPanel.setLayout(new GridLayoutManager(3, 1, new Insets(15, 15, 15, 15), -1, -1));
		mapsPanel.setEnabled(true);
		titleLabel1 = new JLabel();
		titleLabel1.setHorizontalAlignment(0);
		titleLabel1.setHorizontalTextPosition(0);
		titleLabel1.setText("Choose one of your saves");
		mapsPanel.add(titleLabel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		mapsContainer = new JPanel();
		mapsContainer.setLayout(new BorderLayout(0, 0));
		mapsPanel.add(mapsContainer, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
		mapsContainer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), null));
		scrollMap = new JScrollPane();
		scrollMap.setForeground(new Color(-12828863));
		scrollMap.setHorizontalScrollBarPolicy(31);
		scrollMap.setVerticalScrollBarPolicy(20);
		mapsContainer.add(scrollMap, BorderLayout.CENTER);
		scrollMap.setViewportView(maps);
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		mapsPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		titleLabel2 = new JLabel();
		titleLabel2.setHorizontalAlignment(10);
		titleLabel2.setText("... Or look for your own maps ");
		panel1.add(titleLabel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(163, 16), null, 0, false));
		hereButton = new JButton();
		hereButton.setHorizontalAlignment(0);
		hereButton.setText("Here");
		panel1.add(hereButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mapsPanel;
	}

	class resizeListener extends ComponentAdapter {
		private int previousColumn = 3;
		private static final int MARGIN_WIDTH = 30;
		private static final int TOTAL_MAP_ITEM_WIDTH = WIDTH_MAPITEM_TITLE + HEIGHT_MAPITEM + MARGIN_WIDTH;

		public void componentResized(ComponentEvent e) {
			GridLayout layout = (GridLayout) maps.getLayout();
			int w = mapsContainer.getWidth();

			int newColumn = w / TOTAL_MAP_ITEM_WIDTH;

			if (previousColumn != newColumn) {
				Component[] components = maps.getComponents();
				maps.removeAll();

				layout.setColumns(newColumn);
				previousColumn = newColumn;

				for (int i = 0; i < components.length; i++) {
					maps.add(components[i]);
				}
			}
		}
	}
}
