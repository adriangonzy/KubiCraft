package org.jmc.gui;

import org.jmc.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author paul
 */
public class ErrorMessage {

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
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0), null));
		errorText = new JTextArea();
		errorText.setColumns(0);
		errorText.setEditable(false);
		errorText.setLineWrap(true);
		errorText.setRows(0);
		errorText.setWrapStyleWord(true);
		mainPanel.add(errorText, BorderLayout.CENTER);
		closeButton = new JButton();
		closeButton.setHorizontalAlignment(4);
		closeButton.setHorizontalTextPosition(4);
		closeButton.setText("X");
		closeButton.setVerticalAlignment(0);
		closeButton.setVerticalTextPosition(0);
		mainPanel.add(closeButton, BorderLayout.EAST);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}

	public interface CloseHandler {
		void onClose();
	}

	private CloseHandler closeHandler;

	public JPanel mainPanel;
	private JTextArea errorText;
	private JButton closeButton;

	public ErrorMessage(String str, Throwable e) {
		initComponents(str);
		if (e != null) {
			Log.error(str, e);
		}
	}

	public ErrorMessage(String str) {
		this(str, null);
	}

	public void setCloseHandler(CloseHandler closeHandler) {
		this.closeHandler = closeHandler;
	}

	private void initComponents(String error) {
		mainPanel.setBackground(CustomPalette.MAGENTA);
		mainPanel.setForeground(CustomPalette.FORE_GROUND);

		errorText.setForeground(Color.WHITE);
		errorText.setBackground(CustomPalette.MAGENTA);
		setFontSize(errorText, error);
		errorText.setText(error);

		closeButton.setBackground(CustomPalette.MAGENTA);
		closeButton.setForeground(Color.WHITE);
		closeButton.setFocusPainted(false);
		closeButton.setBorderPainted(false);
		closeButton.setFont(CustomFont.minecraft);

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeHandler.onClose();
			}
		});
	}

	private static void setFontSize(JTextArea fontSize, String message) {
		if (message.length() > 50) {
			fontSize.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 15));
		} else {
			fontSize.setFont(new Font(CustomFont.minecraft.getName(), Font.PLAIN, 25));
		}
	}

}
