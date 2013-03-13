package com.gregthegeek.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
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
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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

import org.jaudiotagger.audio.AudioFileIO;

import com.gregthegeek.control.Controller;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class View extends JFrame {
	private static final long serialVersionUID = -5534178449760746433L;
	private static final int WIDTH = 700;
	private static final int HEIGHT = 500;
	private static final int LINE_HEIGHT = 25;
	
	private final JTextField nameField = new JTextField();
	private final JTable musicTable;
	private final JButton previewButton;
	private final JProgressBar songProgress;
	private File[] songFiles;
	private Player songPlayer;
	private int currentSong;
	private Timer progressUpdater;

	public View(final Controller controller) {
		super("Musical Chills Recorder");
		setBounds(getCentered(WIDTH, HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
		
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
		songProgress = new JProgressBar();
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
		
		// begin recording button
		JButton beginRecording = new JButton("Begin Recording");
		beginRecording.setFocusable(false);
		beginRecording.setBounds(WIDTH / 2 + 15, HEIGHT / 2, nameLbl.getWidth() + nameField.getWidth() + buttonHeight, LINE_HEIGHT * 2);
		beginRecording.setFont(new Font("SansSerif", Font.PLAIN, 20));
		beginRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                requestFocusInWindow();
                stopSong();
                playSong();
            }
		});
		add(beginRecording);
		
		// save data button
		JButton saveData = new JButton("Save Data");
		saveData.setFocusable(false);
		saveData.setBounds(beginRecording.getX() + 10, beginRecording.getY() + beginRecording.getHeight() + 40, beginRecording.getWidth() - 20, beginRecording.getHeight());
		saveData.setFont(new Font("SansSerif", Font.PLAIN, 18));
		saveData.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent arg0) {
		        requestFocusInWindow();
		        try {
		            int rt = JOptionPane.showOptionDialog(View.this, "Choose format.", "Save Data", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"CSV", "TXT"}, 0);
		            controller.saveData(Controller.SaveFormat.values()[rt]);
                } catch (IOException e) {
                    error("Data could not be saved.");
                    e.printStackTrace();
                }
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
                    } else if (songPlayer != null && !songPlayer.isComplete()) {
                        getContentPane().setBackground(Color.RED);
                        controller.onButtonPress(name, songPlayer.getPosition());
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    assert !nameField.getText().isEmpty();
                    controller.onButtonRelease(nameField.getText(), songFiles[currentSong].getName(), songPlayer.getPosition());
                    getContentPane().setBackground(new Color(237, 237, 237));
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
            songPlayer = new Player(new BufferedInputStream(new FileInputStream(songFiles[currentSong])));
            new Thread() {
                @Override
                public void run() {
                    try {
                        songPlayer.play();
                        stopSong();
                    } catch (JavaLayerException e) {
                        error("Error Reading Song.");
                        e.printStackTrace();
                    }
                }
            }.start();
            previewButton.setText("Stop Preview");
            int dur = AudioFileIO.read(songFiles[currentSong]).getAudioHeader().getTrackLength();
            //System.out.printf("dur is %d%n", dur);
            songProgress.setMaximum(dur);
            progressUpdater = new Timer();
            progressUpdater.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(songPlayer == null) {return;}
                    songProgress.setValue(songPlayer.getPosition() / 1000);
                    //System.out.printf("song position is %d%n", songPlayer.getPosition() / 1000);
                }}, 0, 1000);
        } catch (FileNotFoundException e) {
            error("Song Not Found.");
            e.printStackTrace();
        } catch (Exception e) {
            error("Error Reading Song.");
            e.printStackTrace();
        }
	}
	
	private void stopSong() {
	    if(songPlayer != null) {
	        progressUpdater.cancel();
	        progressUpdater = null;
	        
	        songPlayer.close();
            songPlayer = null;
            
            previewButton.setText("Start Preview");
            
            songProgress.setValue(songProgress.getMaximum());
	    }
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
