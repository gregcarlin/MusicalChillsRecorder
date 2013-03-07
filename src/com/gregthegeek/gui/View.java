package com.gregthegeek.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class View extends JFrame {
	private static final long serialVersionUID = -5534178449760746433L;
	private static final int WIDTH = 700;
	private static final int HEIGHT = 500;
	private static final int LINE_HEIGHT = 25;
	
	private final JTextField nameField = new JTextField();
	private final JTable musicTable;
	private File[] songFiles;
	private Player songPlayer;

	public View() {
		super("Musical Chills Recorder");
		setBounds(getCentered(WIDTH, HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		
		// populate songFiles
		File folder = new File("songs/");
		if(folder.exists()) {
		    songFiles = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    int len = name.length();
                    return len > 4 && name.substring(len - 4).equalsIgnoreCase(".mp3");
                }});
		} else {
		    folder.mkdir();
		    songFiles = new File[0];
		}
		
		// begin left side
		
		// restart button TODO
		JButton restartButton = new JButton("Restart");
		restartButton.setBounds(45, 50, 120, LINE_HEIGHT);
		add(restartButton);
		
		// play/pause button for previews TODO
		final JButton previewButton = new JButton("Start Preview");
		previewButton.setBounds(restartButton.getWidth() + restartButton.getX() + 30, restartButton.getY(), restartButton.getWidth(), restartButton.getHeight());
		previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(songPlayer == null) {
                    int row = musicTable.getSelectedRow();
                    if(row < 0) {
                        // TODO Error: no song selected
                    } else {
                        try {
                            songPlayer = new Player(new BufferedInputStream(new FileInputStream(songFiles[row])));
                            songPlayer.play();
                            previewButton.setText("Stop Preview");
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (JavaLayerException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    songPlayer.close();
                    songPlayer = null;
                    previewButton.setText("Start Preview");
                }
            }
        });
		add(previewButton);
		
		// progress bar to show song progress
		JProgressBar songProgress = new JProgressBar();
		songProgress.setBounds(15, restartButton.getY() + restartButton.getHeight() + 10, previewButton.getX() + previewButton.getWidth(), LINE_HEIGHT);
		add(songProgress);
		
		// table holding song data
		DefaultTableModel model = new DefaultTableModel() {
            private static final long serialVersionUID = 7859729145724385346L;

            @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		model.addColumn("Songs", songFiles);
		musicTable = new JTable(model);
		musicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane musicPane = new JScrollPane(musicTable);
		musicPane.setBounds(10, HEIGHT / 4, WIDTH / 2 - 20, HEIGHT / 2 + 70);
		add(musicPane);
		
		// begin right side
		
		// label for name field
		JLabel nameLbl = new JLabel("Name:");
		nameLbl.setBounds(WIDTH / 2 + 10, HEIGHT / 4, 50, LINE_HEIGHT);
		nameLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
		add(nameLbl);
		
		// name field
		nameField.setBounds(nameLbl.getX() + nameLbl.getWidth() + 10, nameLbl.getY(), WIDTH / 3, LINE_HEIGHT);
		add(nameField);
		
		// button that clears name field
		JButton nameClear = new JButton("X");
		int buttonHeight = (int) (LINE_HEIGHT * 0.8);
		nameClear.setBounds(nameField.getX() + nameField.getWidth() + 10, nameField.getY() + ((LINE_HEIGHT - buttonHeight) / 2), buttonHeight, buttonHeight);
		nameClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                nameField.setText("");
            }});
		add(nameClear);
		
		// begin recording button TODO
		JButton beginRecording = new JButton("Begin Recording");
		beginRecording.setBounds(WIDTH / 2 + 15, HEIGHT / 2, nameLbl.getWidth() + nameField.getWidth() + buttonHeight, LINE_HEIGHT * 2);
		beginRecording.setFont(new Font("SansSerif", Font.PLAIN, 20));
		add(beginRecording);
		
		// save data button TODO
		JButton saveData = new JButton("Save Data");
		saveData.setBounds(beginRecording.getX() + 10, beginRecording.getY() + beginRecording.getHeight() + 40, beginRecording.getWidth() - 20, beginRecording.getHeight());
		saveData.setFont(new Font("SansSerif", Font.PLAIN, 18));
		add(saveData);
		
		setVisible(true);
	}
	
	public String getUserName() {
	    return nameField.getText();
	}
	
	private static Rectangle getCentered(int width, int height) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		return new Rectangle(d.width / 2 - width / 2, d.height / 2 - height / 2, width, height);
	}
}
