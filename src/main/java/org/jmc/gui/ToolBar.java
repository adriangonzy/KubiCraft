package org.jmc.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jmc.export.TextureExporter;
import org.jmc.util.Filesystem;
import org.jmc.util.Messages;
import org.jmc.world.MapInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * @author paul
 */
public class ToolBar {

	private final ToolbarHandler delegate;
	private final MainWindow mainWindow;
	private JPanel toolbarPanel;
	private JButton bBack;
	private JButton bExport;
	private JPanel selectorDimension;
	private JLabel nameLabel;
	private JPanel centerPanel;
	private JButton textureDefaultButton;


	private static File selectionTextureFolder;
	private final MapInfo mapInfo;

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
		bBack = new JButton();
		bBack.setText("Back");
		toolbarPanel.add(bBack, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		bExport = new JButton();
		this.$$$loadButtonText$$$(bExport, ResourceBundle.getBundle("messages").getString("MainPanel.EXPORT_BUTTON"));
		toolbarPanel.add(bExport, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(0, 0));
		toolbarPanel.add(centerPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		selectorDimension = new JPanel();
		selectorDimension.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		centerPanel.add(selectorDimension, BorderLayout.EAST);
		nameLabel = new JLabel();
		nameLabel.setText("Title");
		centerPanel.add(nameLabel, BorderLayout.WEST);
		textureDefaultButton = new JButton();
		textureDefaultButton.setText("Texture: default");
		toolbarPanel.add(textureDefaultButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadButtonText$$$(AbstractButton component, String text) {
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return toolbarPanel;
	}

	public interface ToolbarHandler {
		void showError(String error);

		void back();

		void export(MapInfo mapInfo);

		void changeDimension(MapInfo mapInfo);
	}

	public ToolBar(MainWindow mainWindow, MapInfo mapInfo, ToolbarHandler delegate) {
		this.mainWindow = mainWindow;
		this.delegate = delegate;
		this.mapInfo = mapInfo;
		initComponent();
	}

	private void initComponent() {

		selectorDimension.setFont(CustomFont.minecraft);
		selectorDimension.setMinimumSize(new Dimension(100, 50));
		selectorDimension.setLayout(new BoxLayout(selectorDimension, BoxLayout.LINE_AXIS));
		selectorDimension.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		nameLabel.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 25));

		bBack.setFont(CustomFont.minecraft);
		bBack.setOpaque(true);
		bBack.setBorderPainted(false);
		bBack.setFocusPainted(false);

		bExport.setFont(CustomFont.minecraft);
		bExport.setOpaque(true);
		bExport.setFocusPainted(false);
		bExport.setBorderPainted(false);
		bExport.setForeground(CustomPalette.MAGENTA);
		bExport.setToolTipText(Messages.getString("PreviewWindow.EXPORTING_DISABLED_WHILE_NO_SELECTION"));

		textureDefaultButton.setFont(CustomFont.minecraft);
		textureDefaultButton.setOpaque(true);
		textureDefaultButton.setFocusPainted(false);
		textureDefaultButton.setBorderPainted(false);

		disableBExport(PreviewWindow.isExporting);

		bBack.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				delegate.back();
			}
		});
		textureDefaultButton.addActionListener(new ActionListener() {
			@SuppressWarnings("Duplicates")
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser() {

					@Override
					public void approveSelection() {
						File selectedFile = getSelectedFile();
						if (selectedFile.isFile()) {
							return;
						}
						super.approveSelection();
					}

				};
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				if (selectionTextureFolder != null) {
					fileChooser.setCurrentDirectory(selectionTextureFolder);
				}

				if (fileChooser.showDialog(ToolBar.this.getToolbarPanel(),
						Messages.getString("MainPanel.CHOOSE_FOLDER")) == JFileChooser.APPROVE_OPTION) {

					File selectedTextureFolder = fileChooser.getSelectedFile();
					if (Filesystem.isTextureRepertory(selectedTextureFolder)) {
						mapInfo.texturePath = selectedTextureFolder.getAbsolutePath();
						textureDefaultButton.setText("Texture: " + Paths.get(mapInfo.texturePath).getFileName().toString());
					} else {
						delegate.showError(Messages.getString("MainPanel.ENTER_VALID_TEXTURE"));
					}
				}
			}
		});
	}

	public void disableBExport(boolean isExporting) {
		bExport.setForeground(CustomPalette.FLASHY_GREEN);
		for (int i = 0; i < bExport.getActionListeners().length; i++) {
			bExport.removeActionListener(bExport.getActionListeners()[i]);
		}

		if (isExporting) {
			processState();
		} else {
			bExport.setToolTipText(Messages.getString("PreviewWindow.EXPORTING_DISABLED_WHILE_NO_SELECTION"));
		}
	}

	public void enableBExport() {
		/* Reset ActionListener */
		for (int i = 0; i < bExport.getActionListeners().length; i++) {
			bExport.removeActionListener(bExport.getActionListeners()[i]);
		}

		bExport.setText("Export");
		bExport.setForeground(CustomPalette.FORE_GROUND);
		bExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				delegate.export(mapInfo);
			}
		});
	}


	public void initDimensionSelector(final MapInfo mapInfo) {
		//Initialize the dimension selector for the current map
		selectorDimension.removeAll();
		java.util.List<org.jmc.world.Dimension> dimensions = Filesystem.getMinecraftDimension(mapInfo.path);
		for (final org.jmc.world.Dimension dim : dimensions) {

			JButton dimButton = new JButton(dim.getName());
			dimButton.setFont(CustomFont.minecraft);
			dimButton.setOpaque(true);
			dimButton.setBorderPainted(false);
			if (mapInfo.dimension == dim) {
				dimButton.setEnabled(false);
			}

			final boolean enabled = mapInfo.dimension != dim;
			dimButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (enabled) {
						initDimensionSelector(new MapInfo(mapInfo.path, dim));
						delegate.changeDimension(new MapInfo(mapInfo.path, dim));
					}
				}
			});

			selectorDimension.add(dimButton);
		}
		selectorDimension.validate();
		selectorDimension.repaint();
	}

	public JPanel getToolbarPanel() {
		return toolbarPanel;
	}

	public JLabel getNameLabel() {
		return nameLabel;
	}

	public void updateProgressState(int value) {
		String status = bExport.getText().split(" ")[0].trim();
		if (!status.equals("Export")) {
			bExport.setText(status + " " + value + "%");
		}
	}

	public void exportState() {
		bExport.setText("Exporting 0%");
		bExport.setToolTipText(Messages.getString("PreviewWindow.EXPORTING_DISABLED_WHILE_EXPORTING"));
		bExport.setForeground(CustomPalette.LIGHT_GREEN);
	}

	public void uploadState() {
		bExport.setText("Uploading 0%");
		bExport.setToolTipText(Messages.getString("PreviewWindow.EXPORTING_DISABLED_WHILE_EXPORTING"));
		bExport.setForeground(CustomPalette.LIGHT_GREEN);
	}

	public void processState() {
		bExport.setText("Processing might take a few minutes...");
		bExport.setToolTipText(Messages.getString("PreviewWindow.EXPORTING_DISABLED_WHILE_EXPORTING"));
		bExport.setForeground(CustomPalette.LIGHT_GREEN);
	}

}
