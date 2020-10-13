package io.github.idgibbo;

import io.github.idgibbo.utils.JavaUtils;
import io.github.idgibbo.windows.PanelConsole;
import io.github.idgibbo.windows.PanelMain;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

public class Main {

	public static File FILE_MAIN = new File(Paths.get(".").toAbsolutePath().normalize().toString());
	public static File FILE_CODE = new File(FILE_MAIN, "code");
	public static File FILE_DATA = new File(FILE_MAIN, "data");
	public static File FILE_DATA_TEMP = new File(FILE_DATA, "temp");
	public static File FILE_DATA_MAPPINGS = new File(FILE_DATA, "mappings");
	public static File FILE_DATA_MAPPINGS_STABLE = new File(FILE_DATA_MAPPINGS, "stable");
	public static File FILE_DATA_MAPPINGS_SNAPSHOT = new File(FILE_DATA_MAPPINGS, "snapshot");

	// TODO Fork
	public static final String PROGRAM_NAME = "Forge CSV Switcher";
	public static final String PROGRAM_VERSION = "1.0.0 Development";
	
	public static PanelConsole console =  new PanelConsole();
	
	public static void main(String[] args) {

		// Gives the default Windows theme TODO Fork
		try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); }
		catch (Exception e) { e.printStackTrace(); }
		
		// Folder stuff
		FILE_CODE.mkdir();
		
		FILE_DATA.mkdir();
		FILE_DATA_TEMP.mkdir();
		FILE_DATA_MAPPINGS.mkdir();
		FILE_DATA_MAPPINGS_STABLE.mkdir();
		FILE_DATA_MAPPINGS_SNAPSHOT.mkdir();
		
		// JFrame GUI Stuff
		JFrame frame = new JFrame(PROGRAM_NAME + " " + PROGRAM_VERSION);
		JMenuBar menubar = new JMenuBar();
		
		frame.add(new PanelMain());
        frame.setJMenuBar(menubar);
		frame.setSize(400, 300);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenu menu1 = new JMenu("Program");

        JMenuItem menuItemAbout = new JMenuItem("About");
        menuItemAbout.setToolTipText("About application");
        menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JavaUtils.dialog(
						PROGRAM_NAME + " " + PROGRAM_VERSION + "\n" +
				"By: Eric Golde & Isaac Gibson",
						frame, "About", JavaUtils.MessageLogo.NONE);
			}
        	
        });
        menu1.add(menuItemAbout);
        
        JMenuItem menuItemOptions = new JMenuItem("Toggle Console");
        menuItemOptions.setToolTipText("Show/Hide output console");
        menuItemOptions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				console.toggle();
			}
        	
        });
        menu1.add(menuItemOptions);
        
        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setToolTipText("Exit application");
        menuItemExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				quit();
			}
        	
        });
        menu1.add(menuItemExit);
        
        menubar.add(menu1);
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                quit();
            }
        });
        
        frame.setVisible(true);
	}
	
	static void quit() {
		System.exit(0);
	}
}
