package ics4u;

/*
 * File:			MyBoggle.java
 * Date Created:	2019-06-05
 * Last Modified:	2019-06-11
 * Authors:			Simon Ou, Daniel Qu, Justin Zhou
 * 
 * Course:			ICS4U1-03
 * Teacher:			Mr. Anandarajan
 * 
 * Description:	An application that allows the user to play the popular board game
 * 				"Boggle" with a friend or against the computer. Players take turns earning
 * 				points by finding 3+ letter words in a 5x5 grid of scrambled letters.
 * 				The first player to reach a certain number of points wins the game. Enjoy!
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyBoggle extends JFrame {
	private static final long serialVersionUID = 0L;

	// Window settings
	public static final int WINDOW_WIDTH = 360;
	public static final int WINDOW_HEIGHT = 270;
	public static final String WINDOW_NAME = "Boggle";

	// Main menu
	static final JFrame menu = new JFrame(WINDOW_NAME);
	static final JLabel back = new JLabel();

	// User settings for the game
	public static int endPoints; // Points to end the game
	public static int diff = 4; // The difficulty of the AI
	public static int minLength; // The minimum length of the word
	public static boolean isPVP; // If the game is PVP or PVE

	public static int menuID = 1; // The menu the user is on

	// Warning tool used to check if code runs in a certain block
	static void warn() {
		System.out.println("Works");
	}

	// Clears a JPanel to a blank state
	static void clear(JPanel menu) {
		menu.removeAll();
		menu.revalidate();
		menu.repaint();
	}

	// Repaints a JPanel, so the new components show
	static void refresh(JPanel menu) {
		menu.revalidate();
		menu.repaint();
	}

	// Disposes the menu and ends the program
	static void close(JFrame menu) {
		menu.dispose();
		System.exit(0);
	}
	
	// Thread.sleep simplifier
	static void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

	// Main menu
	static void menu1(JPanel pane) {
		menuID = 0;

		GridBagConstraints c = new GridBagConstraints();

		// Title icon;
		JLabel title = new JLabel(new ImageIcon(MyBoggle.class.getResource("/Title.png")));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 20;
		c.gridx = 2;
		c.gridy = 1;
		pane.add(title, c);

		// PVE Button
		JButton PVE = new JButton(new ImageIcon(MyBoggle.class.getResource("/PVE.png")));
		PVE.setPreferredSize(new Dimension(240, 30));
		PVE.setBorderPainted(false);
		PVE.setContentAreaFilled(false);
		PVE.setFocusPainted(false);
		PVE.setOpaque(false);
		PVE.addActionListener(new ActionListener() { // Opens the difficulty selection menu
			public void actionPerformed(ActionEvent e) {
				isPVP = false; // Is not PVP
				clear(pane);
				menu2(pane);
				refresh(pane);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;
		c.gridx = 2;
		c.gridy = 2;
		pane.add(PVE, c);

		// PVP Button
		JButton PVP = new JButton(new ImageIcon(MyBoggle.class.getResource("/PVP.png")));
		PVP.setPreferredSize(new Dimension(240, 30));
		PVP.setBorderPainted(false);
		PVP.setContentAreaFilled(false);
		PVP.setFocusPainted(false);
		PVP.setOpaque(false);
		PVP.addActionListener(new ActionListener() { // Skips the difficulty menu, goes to point selection menu
			public void actionPerformed(ActionEvent e) {
				isPVP = true; // Is PVP
				clear(pane);
				menu3(pane);
				refresh(pane);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;
		c.gridx = 2;
		c.gridy = 4;
		pane.add(PVP, c);

		// How to Play Button
		JButton rules = new JButton(new ImageIcon(MyBoggle.class.getResource("/Rules.png")));
		rules.setPreferredSize(new Dimension(240, 30));
		rules.setBorderPainted(false);
		rules.setContentAreaFilled(false);
		rules.setFocusPainted(false);
		rules.setOpaque(false);
		rules.addActionListener(new ActionListener() { // Opens the how to play menu
			public void actionPerformed(ActionEvent e) {
				clear(pane);
				HtP(pane);
				refresh(pane);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;
		c.gridx = 2;
		c.gridy = 6;
		pane.add(rules, c);

		// Exit Button
		JButton exit = new JButton(new ImageIcon(MyBoggle.class.getResource("/Exit.png")));
		exit.setPreferredSize(new Dimension(150, 15));
		exit.setBorderPainted(false);
		exit.setContentAreaFilled(false);
		exit.setFocusPainted(false);
		exit.setOpaque(false);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close(menu);
			}
		});
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = 8;
		pane.add(exit, c);
	}

	// How to Play Menu
	static void HtP(JPanel pane) {
		menuID = 3;

		GridBagConstraints c = new GridBagConstraints();
		JLabel space;

		// Rules
		JLabel rules = new JLabel(new ImageIcon(MyBoggle.class.getResource("/HtP.png")));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		c.ipadx = 0;
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = 2;
		pane.add(rules, c);

		// Back Button
		JButton back = new JButton(new ImageIcon(MyBoggle.class.getResource("/Back.png")));
		back.setPreferredSize(new Dimension(240, 30));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setOpaque(false);
		back.addActionListener(new ActionListener() { // Clears and repaints the main menu
			public void actionPerformed(ActionEvent e) {
				clear(pane);
				menu1(pane);
				refresh(pane);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.01;
		c.ipady = -10;
		c.ipadx = -30;
		c.gridx = 1;
		c.gridy = 0;
		pane.add(back, c);

		// Spacers (To center the buttons)
		space = new JLabel(" ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = 10;
		c.ipadx = 50;
		c.gridx = 3;
		c.gridy = 0;
		pane.add(space, c);

		space = new JLabel(" ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = 10;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 3;
		pane.add(space, c);
	}

	// Difficulty Menu
	static void menu2(JPanel pane) {
		menuID = 1;

		GridBagConstraints c = new GridBagConstraints();
		JLabel space;

		JButton easy = new JButton(new ImageIcon(MyBoggle.class.getResource("/Easy.png")));
		JButton med = new JButton(new ImageIcon(MyBoggle.class.getResource("/Medium.png")));
		JButton hard = new JButton(new ImageIcon(MyBoggle.class.getResource("/Hard.png")));

		// Easy Button
		easy.setPreferredSize(new Dimension(240, 25));
		easy.setBorderPainted(false);
		easy.setContentAreaFilled(false);
		easy.setFocusPainted(false);
		easy.setOpaque(false);
		easy.addActionListener(new ActionListener() { // Allows the user to see the selected difficulty
			public void actionPerformed(ActionEvent e) {
				hard.setIcon(new ImageIcon(MyBoggle.class.getResource("/Hard.png")));
				med.setIcon(new ImageIcon(MyBoggle.class.getResource("/Medium.png")));
				easy.setIcon(new ImageIcon(MyBoggle.class.getResource("/EasySelect.png")));
				refresh(pane);
				diff = 1;
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipadx = -30;
		c.ipady = -5;
		c.gridx = 2;
		c.gridy = 3;
		pane.add(easy, c);

		// Medium Button
		med.setPreferredSize(new Dimension(240, 25));
		med.setBorderPainted(false);
		med.setContentAreaFilled(false);
		med.setFocusPainted(false);
		med.setOpaque(false);
		med.addActionListener(new ActionListener() { // Allows the user to see the selected difficulty
			public void actionPerformed(ActionEvent e) {
				hard.setIcon(new ImageIcon(MyBoggle.class.getResource("/Hard.png")));
				easy.setIcon(new ImageIcon(MyBoggle.class.getResource("/Easy.png")));
				med.setIcon(new ImageIcon(MyBoggle.class.getResource("/MediumSelect.png")));
				refresh(pane);
				diff = 2;
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipadx = -30;
		c.ipady = -5;
		c.gridx = 2;
		c.gridy = 4;
		pane.add(med, c);

		// Hard Button
		hard.setPreferredSize(new Dimension(240, 25));
		hard.setBorderPainted(false);
		hard.setContentAreaFilled(false);
		hard.setFocusPainted(false);
		hard.setOpaque(false);
		hard.addActionListener(new ActionListener() { // Allows the user to see the selected difficulty
			public void actionPerformed(ActionEvent e) {
				easy.setIcon(new ImageIcon(MyBoggle.class.getResource("/Easy.png")));
				med.setIcon(new ImageIcon(MyBoggle.class.getResource("/Medium.png")));
				hard.setIcon(new ImageIcon(MyBoggle.class.getResource("/HardSelect.png")));
				refresh(pane);
				diff = 3;
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipadx = -30;
		c.ipady = -5;
		c.gridx = 2;
		c.gridy = 5;
		pane.add(hard, c);

		// Next Button
		JButton next = new JButton(new ImageIcon(MyBoggle.class.getResource("/Next.png")));
		next.setPreferredSize(new Dimension(240, 25));
		next.setBorderPainted(false);
		next.setContentAreaFilled(false);
		next.setFocusPainted(false);
		next.setOpaque(false);
		next.addActionListener(new ActionListener() { // Moves to the next menu: selecting the number of points to win
			public void actionPerformed(ActionEvent e) {
				if ((6 % diff) == 0) {
					clear(pane);
					menu3(pane);
					refresh(pane);
				}
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipadx = -30;
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = 7;
		pane.add(next, c);

		// Back Button
		JButton back = new JButton(new ImageIcon(MyBoggle.class.getResource("/Back.png")));
		back.setPreferredSize(new Dimension(240, 25));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setOpaque(false);
		back.addActionListener(new ActionListener() { // Returns to the main menu
			public void actionPerformed(ActionEvent e) {
				clear(pane);
				menu1(pane);
				refresh(pane);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.01;
		c.weighty = 1;
		c.ipady = 30;
		c.ipadx = -35;
		c.gridx = 1;
		c.gridy = 1;
		pane.add(back, c);

		// Spacers (To center the buttons)
		space = new JLabel(" ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 10;
		c.ipady = 10;
		c.ipadx = 50;
		c.gridx = 3;
		c.gridy = 1;
		pane.add(space, c);

		space = new JLabel(" ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = 0;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 8;
		pane.add(space, c);

		space = new JLabel(" ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = -5;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 2;
		pane.add(space, c);

		space = new JLabel(" ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = 5;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 10;
		pane.add(space, c);
	}

	// Point selection menu
	static void menu3(JPanel pane) {
		menuID = 2;

		GridBagConstraints c = new GridBagConstraints();
		JLabel space;

		// Options for the number of points to win
		String[] optNumbers = {"20", "50", "75", "100", "150", "200", "250"};
		String[] optLetters = {"3", "4", "5" };

		// Instructions for number of points
		JLabel ins1 = new JLabel("Number of points to play until");
		ins1.setSize(new Dimension(120, 50));
		ins1.setFont(new Font("Agency FB", Font.BOLD, 20));
		ins1.setForeground(new Color(255, 255, 255));
		c.gridx = 2;
		c.gridy = 3;
		pane.add(ins1, c);

		// Options panel for number of points
		JComboBox<String> opt1 = new JComboBox<String>(optNumbers);
		opt1.setFont(new Font("Castellar", Font.PLAIN, 16));
		c.gridx = 2;
		c.gridy = 4;
		pane.add(opt1, c);

		// Instructions for letters in a word
		JLabel ins2 = new JLabel("Minimum letters per word");
		ins2.setSize(new Dimension(120, 50));
		ins2.setFont(new Font("Agency FB", Font.BOLD, 20));
		ins2.setForeground(new Color(255, 255, 255));
		c.gridx = 2;
		c.gridy = 6;
		pane.add(ins2, c);

		// Options panel for letters in a word
		JComboBox<String> opt2 = new JComboBox<String>(optLetters);
		opt2.setFont(new Font("Castellar", Font.PLAIN, 16));
		c.gridx = 2;
		c.gridy = 7;
		pane.add(opt2, c);

		// Play button
		JButton next = new JButton(new ImageIcon(MyBoggle.class.getResource("/Play.png")));
		next.setPreferredSize(new Dimension(240, 25));
		next.setBorderPainted(false);
		next.setContentAreaFilled(false);
		next.setFocusPainted(false);
		next.setOpaque(false);
		next.addActionListener(new ActionListener() { // Opens a new window that loads the game
			public void actionPerformed(ActionEvent e) {
				endPoints = Integer.valueOf((String) opt1.getSelectedItem());
				minLength = Integer.valueOf((String) opt2.getSelectedItem());
				
				// A separate class file called GameInstance.java
				menu.dispose();
				new GameInstance();
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipadx = 0;
		c.ipady = 0;
		c.gridx = 2;
		c.gridy = 9;
		pane.add(next, c);

		// Back button
		JButton back = new JButton(new ImageIcon(MyBoggle.class.getResource("/Back.png")));
		back.setPreferredSize(new Dimension(240, 25));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setOpaque(false);
		back.addActionListener(new ActionListener() { // Clears and repaints the main menu
			public void actionPerformed(ActionEvent e) {
				clear(pane);
				if (isPVP == false) {
					menu2(pane);
				} else {
					menu1(pane);
				}
				refresh(pane);
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.01;
		c.weighty = 1;
		c.ipady = 30;
		c.ipadx = -30;
		c.gridx = 1;
		c.gridy = 1;
		pane.add(back, c);

		// Spacers (To center the buttons)
		space = new JLabel(" ");
		c.weightx = 0.5;
		c.ipady = 20;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 2;
		pane.add(space, c);

		space = new JLabel(" ");
		c.weightx = 0.5;
		c.ipady = 20;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 5;
		pane.add(space, c);
		
		space = new JLabel(" ");
		c.weightx = 0.5;
		c.ipady = 30;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 8;
		pane.add(space, c);

		space = new JLabel(" ");
		c.weightx = 0.5;
		c.ipady = 30;
		c.ipadx = 95;
		c.gridx = 3;
		c.gridy = 1;
		pane.add(space, c);
	}

	// Main method
	public static void main(String[] args) {
		// Main Windows
		menu.setDefaultCloseOperation(EXIT_ON_CLOSE);
		menu.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		menu.setResizable(false);
		menu.setVisible(true);
		
		menu.setLocationRelativeTo(null);
		
		// Loads in some image resource files
		ImageLoader.loadImages();

		// Main Panel
		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		pane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		pane.setBackground(new Color(0, 0, 0, 64));
		pane.setBounds(0, 0, pane.getPreferredSize().width, pane.getPreferredSize().height);

		// Adds JLabel back as the background
		back.setBounds(0, 0, pane.getPreferredSize().width, pane.getPreferredSize().height);
		menu.add(back);

		// Adds the panel to the JFrame
		menu.add(pane);

		// Thread to load background animation
		Thread thread = new Thread() {
			@Override
			public void run() {
				// Loads all the images into an array
				ImageIcon[] bkgs = { new ImageIcon(MyBoggle.class.getResource("/frame1.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame2.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame3.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame4.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame5.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame6.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame7.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame8.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame9.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame10.png")),
						new ImageIcon(MyBoggle.class.getResource("/frame11.png")), };

				//Presets the background to remove the chance of error
				back.setIcon(bkgs[0]);
				delay(1);
				menu.remove(pane);
				menu.add(pane);
				delay(1);
				
				//Loads the images as an animation in the background at 120ms per frame
				while (true) {
					for (int x = 0; x < 11; x++) {
						back.setIcon(bkgs[x]);
						delay(1);
						refresh(pane);
						delay(119);
					}
				}
			}
		};
		thread.start();

		// Draws the first menu screen
		menu1(pane);
		pane.revalidate();
		pane.repaint();
	}
}