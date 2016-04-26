package org.jmc.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author adrian.
 */
public class AboutPanel {
	private JPanel aboutPanel;
	private JLabel title;
	private JScrollPane scroll;
	private JPanel sections;

	AboutPanel() {
		$$$setupUI$$$();
		init();
	}

	private void init() {
		title.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 25));

		sections.setBackground(CustomPalette.BACK_GROUND);
		sections.setForeground(CustomPalette.FORE_GROUND);

		scroll.getVerticalScrollBar().setUI(new CustomScrollBar());
		scroll.getVerticalScrollBar().setUnitIncrement(12);
		scroll.setBorder(null);

		sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
		for (int i = ContentUtil.CONTENT_ABOUT.length - 1; i >= 0; i--) {
			JTextArea section = new JTextArea(ContentUtil.CONTENT_ABOUT[i]);
			section.setEditable(false);
			section.setLineWrap(true);
			section.setWrapStyleWord(true);
			section.setBackground(CustomPalette.BACK_GROUND);
			section.setForeground(CustomPalette.FORE_GROUND);
			section.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 16));
			section.setAlignmentX(Component.LEFT_ALIGNMENT);

			JTextArea sectionTitle = new JTextArea(ContentUtil.CONTENT_ABOUT[--i]);
			sectionTitle.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 20));
			sectionTitle.setEditable(false);
			sectionTitle.setLineWrap(true);
			sectionTitle.setWrapStyleWord(true);
			sectionTitle.setBackground(CustomPalette.BACK_GROUND);
			sectionTitle.setForeground(CustomPalette.FLASHY_GREEN);
			sectionTitle.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));
			sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

			sections.add(section, 0);
			sections.add(sectionTitle, 0);
		}

		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMinimum());
	}

	public JPanel getPanel() {
		return aboutPanel;
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		aboutPanel = new JPanel();
		aboutPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		title = new JLabel();
		title.setHorizontalAlignment(0);
		title.setHorizontalTextPosition(0);
		title.setText("About");
		aboutPanel.add(title, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(31);
		aboutPanel.add(scroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(900, -1), null, 0, false));
		sections = new JPanel();
		sections.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		scroll.setViewportView(sections);
		sections.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25), null));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return aboutPanel;
	}
}