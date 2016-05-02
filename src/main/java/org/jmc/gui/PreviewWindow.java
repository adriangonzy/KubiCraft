/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_List;
import org.jmc.export.KubityExporter;
import org.jmc.export.ObjExporter;
import org.jmc.export.ProgressCallback;
import org.jmc.threading.ChunkLoaderThread;
import org.jmc.threading.ViewChunkLoaderThread;
import org.jmc.util.Filesystem;
import org.jmc.util.Messages;
import org.jmc.world.Dimension;
import org.jmc.world.LevelDat;
import org.jmc.world.MapInfo;
import org.jmc.world.Region;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;

/**
 * @author adrian
 */
public class PreviewWindow extends JPanel {
	/**
	 * Main map preview panel.
	 */
	private MapPreview preview;

	private ToolBar pToolbar;

	/**
	 * Thread object used for monitoring the state of the chunk loading thread.
	 * Necessary for restarting the thread when loading a new map.
	 */
	private ChunkLoaderThread chunk_loader = null;

	private MainWindow mainWindow;

	public static boolean isExporting;

	/**
	 * Panel constructor.
	 */
	public PreviewWindow(final MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		setLayout(new BorderLayout());

		initPreview();
		add(preview, BorderLayout.CENTER);
	}

	public void update(MapInfo mapInfo) {
		if (pToolbar != null) {
			remove(pToolbar.getToolbarPanel());
		}

		pToolbar = new ToolBar(mapInfo, new ToolBar.ToolbarHandler() {
			@Override
			public void back() {
				goBack();
			}

			@Override
			public void showError(String error) {
				mainWindow.showError(error);
			}

			@Override
			public void export(MapInfo mapInfo) {
				startExport(mapInfo);
			}

			@Override
			public void changeDimension(MapInfo mapInfo) {
				update(mapInfo);
			}
		});
		add(pToolbar.getToolbarPanel(), BorderLayout.NORTH);
		pToolbar.initDimensionSelector(mapInfo);

		reloadPreview(mapInfo);
		validate();
		repaint();
	}

	MapInfo.SelectionBounds bounds;

	private void initPreview() {
		preview = new MapPreview(new MapPreview.SelectionListener() {
			@Override
			public void onAreaSelected() {
				if (isExporting){
					return;
				}
				pToolbar.enableBExport();
			}

			@Override
			public void onAreaUnselected() {
				pToolbar.disableBExport(isExporting);
			}

			@Override
			public void onSelectionUpdate(MapInfo.SelectionBounds selectionBounds) {
				PreviewWindow.this.bounds = selectionBounds;
			}
		});
		preview.setBackground(CustomPalette.BACK_GROUND);
		preview.setAltitudes(0, 256);
	}

	public void reloadPreview(MapInfo mapInfo) {
		//Reset preview
		remove(preview);
		initPreview();
		add(preview, BorderLayout.CENTER);
		validate();
		repaint();

		preview.resetSelection();

		// clear caches
		if(chunk_loader != null) {
			chunk_loader.stopRunning();
		}
		Region.clearCache();
		preview.clearImages();
		preview.clearChunks();
		loadMap(mapInfo);
	}

	private void loadMap(MapInfo mapInfo) {

		if (mapInfo.path == null) {
			mainWindow.showError(Messages.getString("MainPanel.ENTER_CORRECT_DIR"));
			return;
		}
		if (!Files.exists(mapInfo.path)) {
			mainWindow.showError(Messages.getString("MainPanel.ENTER_CORRECT_DIR"));
			return;
		}
		if (!Filesystem.hasValidRegionFolder(mapInfo.path.toFile())) {
			mainWindow.showError(Messages.getString("MainPanel.ERR_REGION_FOLDER"));
			return;
		}

		int player_x = 0;
		int player_z = 0;
		Dimension dimension;

		LevelDat levelDat = new LevelDat(mapInfo.path.toFile());
		if (levelDat.open()) {
			//Log.info(levelDat.toString());
			TAG_List pos = levelDat.getPosition();
			if (pos != null) {
				player_x = (int) ((TAG_Double) pos.getElement(0)).value;
				player_z = (int) ((TAG_Double) pos.getElement(2)).value;
			}
			dimension = levelDat.getDimension();

			int spawn_x = levelDat.getSpawnX();
			int spawn_z = levelDat.getSpawnZ();

			preview.clearMarkers();
			if (mapInfo.dimension == dimension) {
				preview.addMarker(player_x, player_z, Color.red);
			}
			if (mapInfo.dimension == Dimension.OVERWORLD) {
				preview.addMarker(spawn_x, spawn_z, Color.green);
			}
		}
		preview.setPosition(player_x, player_z, MainWindow.getFrames()[0].getWidth(), MainWindow.getFrames()[0].getHeight());

		pToolbar.getNameLabel().setText(mapInfo.path.getFileName()+"");

		if (chunk_loader != null && chunk_loader.isRunning())
			chunk_loader.stopRunning();

		chunk_loader = new ViewChunkLoaderThread(preview, mapInfo);
		chunk_loader.setYBounds(0, 256);
		(new Thread(chunk_loader)).start();
	}

	public void goBack() {
		if (chunk_loader != null) {
			chunk_loader.stopRunning();
		}

		mainWindow.showMenu();
	}

	public void startExport(final MapInfo mapInfo) {
		isExporting = true;
		pToolbar.disableBExport(isExporting);

		mapInfo.bounds = this.bounds;

		Thread export = new Thread(new Runnable() {
			@Override
			public void run() {
				ObjExporter.export(mapInfo, new ProgressCallback() {
					@Override
					public void setProgress(float value) {
						pToolbar.updateProgressState((int) (value * 100f));
					}

					@Override
					public void setStatus(Status status) {
						if (status == Status.EXPORTING_OBJ) {
							pToolbar.exportState();
						}
						if (status == Status.EXPORTING_TEXTURES) {
							pToolbar.exportTextureState();
						}
						if (status == Status.UPLOADING) {
							pToolbar.uploadState();
						}
						if (status == Status.PROCESSING) {
							pToolbar.processState();
						}
						if (status == Status.FINISHED) {
							isExporting = false;
							pToolbar.enableBExport();
						}
					}
				}, new KubityExporter.ErrorCallback() {
					@Override
					public void handleError(String message) {
						mainWindow.showError(message);
						isExporting = false;
						pToolbar.enableBExport();
					}
					@Override
					public void handleWarning(String message) {
						mainWindow.showError(message);
					}
				});
			}
		});
		export.start();
	}
}
