package com.gregthegeek.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class View extends JFrame {
	private static final long serialVersionUID = -5534178449760746433L;

	public View() {
		super("Musical Chills Recorder");
		setBounds(getCentered(700, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		
		
		setVisible(true);
	}
	
	private static Rectangle getCentered(int width, int height) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		return new Rectangle(d.width / 2 - width / 2, d.height / 2 - height / 2, width, height);
	}
}
