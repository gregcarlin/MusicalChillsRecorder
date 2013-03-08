package com.gregthegeek.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.gregthegeek.control.Controller;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

public class View extends JFrame {
	private static final long serialVersionUID = -5534178449760746433L;
	private static final int WIDTH = 700;
	private static final int HEIGHT = 500;
	private static final int LINE_HEIGHT = 25;
	
	private final Controller controller;
	private final JTextField nameField = new JTextField();
	private final JTable musicTable;
	private final JButton previewButton;
	private File[] songFiles;
	private AdvancedPlayer songPlayer;
	private int currentSong;

	public View(final Controller controller) {
		super("Musical Chills Recorder");
		setBounds(getCentered(WIDTH, HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
		this.controller = controller;
		
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
		
		// restart button
		JButton restartButton = new JButton("Restart");
		restartButton.setFocusable(false);
		restartButton.setBounds(45, 50, 120, LINE_HEIGHT);
		restartButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent arg0) {
		        requestFocusInWindow();
		        if(songPlayer == null) {
		            error("No song is playing.");
		        } else {
		            stopSong();
		            playSong();
		        }
		    }
		});
		add(restartButton);
		
		// play/pause button for previews
		previewButton = new JButton("Start Preview");
		previewButton.setFocusable(false);
		previewButton.setBounds(restartButton.getWidth() + restartButton.getX() + 30, restartButton.getY(), restartButton.getWidth(), restartButton.getHeight());
		previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                requestFocusInWindow();
                if(songPlayer == null) {
                    playSong();
                } else {
                    stopSong();
                }
            }
        });
		add(previewButton);
		
		// progress bar to show song progress
		JProgressBar songProgress = new JProgressBar();
		songProgress.setFocusable(false);
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
		musicPane.setFocusable(false);
		musicPane.setBounds(10, HEIGHT / 4, WIDTH / 2 - 20, HEIGHT / 2 + 70);
		add(musicPane);
		
		// begin right side
		
		// label for name field
		JLabel nameLbl = new JLabel("Name:");
		nameLbl.setFocusable(false);
		nameLbl.setBounds(WIDTH / 2 + 10, HEIGHT / 4, 50, LINE_HEIGHT);
		nameLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
		add(nameLbl);
		
		// name field
		//nameField.setFocusable(false);
		nameField.setBounds(nameLbl.getX() + nameLbl.getWidth() + 10, nameLbl.getY(), WIDTH / 3, LINE_HEIGHT);
		add(nameField);
		
		// button that clears name field
		JButton nameClear = new JButton("X");
		nameClear.setFocusable(false);
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
		beginRecording.setFocusable(false);
		beginRecording.setBounds(WIDTH / 2 + 15, HEIGHT / 2, nameLbl.getWidth() + nameField.getWidth() + buttonHeight, LINE_HEIGHT * 2);
		beginRecording.setFont(new Font("SansSerif", Font.PLAIN, 20));
		beginRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                requestFocusInWindow();
                // TODO
            }
		});
		add(beginRecording);
		
		// save data button TODO
		JButton saveData = new JButton("Save Data");
		saveData.setFocusable(false);
		saveData.setBounds(beginRecording.getX() + 10, beginRecording.getY() + beginRecording.getHeight() + 40, beginRecording.getWidth() - 20, beginRecording.getHeight());
		saveData.setFont(new Font("SansSerif", Font.PLAIN, 18));
		saveData.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent arg0) {
		        // TODO
		        requestFocusInWindow();
		    }
		});
		add(saveData);
		
		addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    String name = nameField.getText();
                    if(name.isEmpty()) {
                        error("No name entered.");
                    } else {
                        setBackground(Color.RED);
                        controller.onButtonPress(name);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    assert !nameField.getText().isEmpty();
                    controller.onButtonRelease(nameField.getText());
                    setBackground(new Color(237, 237, 237));
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // do nothing
                //System.out.printf("key typed: %c%n", e.getKeyChar());
            }
		});
		
		setVisible(true);
	}
	
	private void playSong() {
	    try {
	        currentSong = musicTable.getSelectedRow();
	        if(currentSong < 0) {
	            error("No song selected.");
	            return;
	        }
            songPlayer = new AdvancedPlayer(new BufferedInputStream(new FileInputStream(songFiles[currentSong])));
            songPlayer.setPlayBackListener(new PlaybackListener() {
                
            });
            new Thread() {
                @Override
                public void run() {
                    try {
                        songPlayer.play();
                        //System.out.println("after play");
                        songPlayer = null;
                        previewButton.setText("Start Preview");
                    } catch (JavaLayerException e) {
                        error("Error Reading Song.");
                        e.printStackTrace();
                    }
                }
            }.start();
            previewButton.setText("Stop Preview");
        } catch (FileNotFoundException e) {
            error("Song Not Found.");
            e.printStackTrace();
        } catch (JavaLayerException e) {
            error("Error Reading Song.");
            e.printStackTrace();
        }
	}
	
	private void stopSong() {
	    songPlayer.stop();
        songPlayer = null;
        previewButton.setText("Start Preview");
	}
	
	private void error(String message) {
	    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public String getUserName() {
	    return nameField.getText();
	}
	
	private static Rectangle getCentered(int width, int height) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		return new Rectangle(d.width / 2 - width / 2, d.height / 2 - height / 2, width, height);
	}
}
