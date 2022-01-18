package ics4u;

/*
 * File:			GameInstance.java
 * Date Created:	2019-06-05
 * Last Modified:	2019-06-11
 * Authors:			Simon Ou, Daniel Qu, Justin Zhou
 * 
 * Course:			ICS4U1-03
 * Teacher:			Mr. Anandarajan
 * 
 * Description:		Class that contains the main game components. A new instance of GameInstance
 * 					should be instantiated every time a new instance of the Boggle game begins.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GameInstance extends JFrame {
	public static final int WINDOW_WIDTH = 960;
	public static final int WINDOW_HEIGHT = 570;

	private static final long serialVersionUID = 1L; // stops Eclipse from prompting warnings :)
	private final Insets insets; // insets used for component layout

	private static GameInstance instance; // instance object

	public static final int BOARD_SIZE = 5; // width and length in tiles

	public final LetterDice[][] gameBoard; // stores the dice on the game board

	/*
	 * Declare JFrame components
	 */
	public final JLabel[][] diceLabels;

	public final JTextField answerField; // region for player to type word
	public final JButton answerButton; // button to press to check word validity

	public final ActionListener answerButtonListener; // action listener for answer button

	public final JButton menuButton; // allows one to pause the game
	public final JButton passButton; // allows one to pass, in case no words can be found

	public final ActionListener menuButtonListener;
	public final ActionListener passButtonListener;

	public final List<String> enteredWords; // already entered words after previous scramble
	public final JLabel alreadyUsedLabel; // "Already Used Words" label
	public final JTextArea enteredWordsLabel; // displays all entered words

	/*
	 * Prompts on screen (e.g. "Player 1's Turn!")
	 */
	public final JLabel promptLabel;

	/*
	 * Declare JLabels to display score related information
	 */
	public final JLabel p1ScoreHeader, p2ScoreHeader, p1ScoreLabel, p2ScoreLabel;

	/*
	 * Keeps track of the time left for each turn
	 */
	public final JLabel timerLabel;

	private Thread timerThread; // thread that keeps timer running without interfering with GUI
	private boolean timerRunning = false;
	private boolean timerPaused = false;

	/*
	 * Pause menu components
	 */
	private JDialog pauseDialog; // dialog object
	private final JLabel pauseLabel; // title of pause screen

	private final JButton resumeButton;
	private final JButton restartButton;
	private final JButton quitButton;

	/*
	 * Keeps track of the current score of both players
	 */
	public int p1Score = 0;
	public int p2Score = 0;
	public boolean isP1Turn = true;

	private int passCount = 0; // number of consecutive passes, 4 indicates scramble board

	private Font letterFont = new Font("Algerian", Font.BOLD, 48); // font used for block letters

	private ArrayList<String> dictionary = new ArrayList<String>();

	private ArrayList<String> threeLetters = new ArrayList<String>();
	private ArrayList<String> fourLetters = new ArrayList<String>();
	private ArrayList<String> fiveLetters = new ArrayList<String>();

	/**
	 * Constructs a new instance of Boggle
	 */
	public GameInstance() {
		super(MyBoggle.WINDOW_NAME);
		instance = this;

		insets = this.getContentPane().getInsets(); // get insets, for layout management

		gameBoard = new LetterDice[BOARD_SIZE][BOARD_SIZE];

		try { // initialize the dictionary
			createDictionary(dictionary);
		} catch (Exception e) {
			System.err.println("Could not find text file to load in dictionary!");
			System.exit(-1);
		}

		/*
		 * Initialize JFrame
		 */
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setFocusable(true);
		this.setLayout(null);

		this.setVisible(true);
		this.requestFocus();
		
		this.setLocationRelativeTo(null);

		/*
		 * Initialize the game board with dice
		 */
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++)
				gameBoard[i][j] = new LetterDice(LetterDice.DICE_CONFIG[i][j].toCharArray(), i);
		}

		/*
		 * Set JFrame background
		 */
		setContentPane(new JComponent() {
			private static final long serialVersionUID = 2L; // serialization code (here to remove warning) // from
																// file

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(ImageLoader.GAME_BACKGROUND, 0, 0, this); // draw background image to screen
			}
		});
		this.revalidate();

		/*
		 * Initialize JFrame components
		 */
		diceLabels = new JLabel[BOARD_SIZE][BOARD_SIZE]; // dice labels added to GUI in updateDice()

		answerField = new JTextField();
		answerField.setBounds(547 + insets.left, 387 + insets.top, 387, 39);
		answerField.setFont(new Font("Ubuntu", Font.PLAIN, 24));
		answerField.setHorizontalAlignment(SwingConstants.CENTER);
		answerField.setBorder(BorderFactory.createEmptyBorder());
		answerField.setOpaque(false);
		answerField.setVisible(false);
		this.add(answerField);

		answerButton = new JButton(ImageLoader.ANSWER_BUTTON);
		answerButton.setBounds(840 + insets.left, 447 + insets.top, 95, 52);
		answerButton.setOpaque(false);
		answerButton.setBorderPainted(false);
		answerButton.setContentAreaFilled(false);
		answerButton.setFocusPainted(false);
		this.add(answerButton);

		answerButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameInstance.instance.answerField.setVisible(false);
				GameInstance.instance.answerButton.removeActionListener(this);
				GameInstance.instance.menuButton.removeActionListener(menuButtonListener);
				GameInstance.instance.passButton.removeActionListener(passButtonListener);

				Thread t = new Thread() {
					@Override
					public void run() {
						GameInstance.instance.submitWord(); // submits the word in the text field
					}
				};
				t.start();
			}
		};
		answerField.addActionListener(answerButtonListener);

		menuButton = new JButton(ImageLoader.MENU_BUTTON);
		menuButton.setBounds(545 + insets.left, 447 + insets.top, 153, 52);
		menuButton.setOpaque(false);
		menuButton.setBorderPainted(false);
		menuButton.setContentAreaFilled(false);
		menuButton.setFocusPainted(false);
		this.add(menuButton);

		menuButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPauseScreen(); // pause the game
			}
		};

		passButton = new JButton(ImageLoader.PASS_BUTTON);
		passButton.setBounds(692 + insets.left, 447 + insets.top, 153, 52);
		passButton.setOpaque(false);
		passButton.setBorderPainted(false);
		passButton.setContentAreaFilled(false);
		passButton.setFocusPainted(false);
		this.add(passButton);

		passButtonListener = new ActionListener() { // user chooses to pass their turn
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					@Override
					public void run() {
						passCount++;

						answerButton.removeActionListener(answerButtonListener);
						menuButton.removeActionListener(menuButtonListener);
						passButton.removeActionListener(passButtonListener);

						timerRunning = false;

						alreadyUsedLabel.setVisible(false);
						enteredWordsLabel.setVisible(false);

						answerField.setVisible(false);

						promptLabel.setText((MyBoggle.isPVP ? ("Player " + (isP1Turn ? "1 " : "2 "))
								: (isP1Turn ? "Player 1 " : "Computer ")) + " passed the turn!");
						promptLabel.setVisible(true);

						if (passCount >= 4) { // each player passed 2 times
							passCount = 0;

							delay(1);
							promptLabel.setText("Scrambling board...");
							delay(1);
							scrambleBoard();
						}

						delay(1);
						switchTurn();
					}
				};
				t.start();
			}
		};

		promptLabel = new JLabel();
		promptLabel.setBounds(547 + insets.left, 188 + insets.top, 387, 176);
		promptLabel.setFont(new Font("Cambria", Font.PLAIN, 22));
		promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(promptLabel);

		alreadyUsedLabel = new JLabel("Already Used Words:");
		alreadyUsedLabel.setBounds(557 + insets.left, 198 + insets.top, 367, 156);
		alreadyUsedLabel.setFont(new Font("Cambria", Font.PLAIN, 20));
		alreadyUsedLabel.setVerticalAlignment(SwingConstants.TOP);
		alreadyUsedLabel.setVisible(false);
		this.add(alreadyUsedLabel);

		enteredWords = new ArrayList<String>();
		enteredWordsLabel = new JTextArea();
		enteredWordsLabel.setFont(new Font("Cambria", Font.PLAIN, 17));
		enteredWordsLabel.setLineWrap(true);
		enteredWordsLabel.setWrapStyleWord(true);
		enteredWordsLabel.setOpaque(false);
		enteredWordsLabel.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(enteredWordsLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(557 + insets.left, 228 + insets.top, 367, 126);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.add(scrollPane);

		p1ScoreHeader = new JLabel("Player 1");
		p1ScoreHeader.setBounds(588 + insets.left, 114 + insets.top, 108, 26);
		p1ScoreHeader.setFont(new Font("Georgia", Font.BOLD, 14));
		p1ScoreHeader.setHorizontalAlignment(SwingConstants.CENTER);
		p1ScoreHeader.setForeground(Color.BLACK);
		this.add(p1ScoreHeader);

		p2ScoreHeader = new JLabel(MyBoggle.isPVP ? "Player 2" : "Computer");
		p2ScoreHeader.setBounds(791 + insets.left, 114 + insets.top, 108, 26);
		p2ScoreHeader.setFont(new Font("Georgia", Font.BOLD, 14));
		p2ScoreHeader.setHorizontalAlignment(SwingConstants.CENTER);
		p2ScoreHeader.setForeground(Color.BLACK);
		this.add(p2ScoreHeader);

		p1ScoreLabel = new JLabel("0");
		p1ScoreLabel.setBounds(588 + insets.left, 128 + insets.top, 108, 71);
		p1ScoreLabel.setFont(new Font("Arial Black", Font.PLAIN, 36));
		p1ScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		p1ScoreLabel.setVerticalAlignment(SwingConstants.TOP);
		this.add(p1ScoreLabel);

		p2ScoreLabel = new JLabel("0");
		p2ScoreLabel.setBounds(791 + insets.left, 128 + insets.top, 108, 71);
		p2ScoreLabel.setFont(new Font("Arial Black", Font.PLAIN, 36));
		p2ScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		p2ScoreLabel.setVerticalAlignment(SwingConstants.TOP);
		this.add(p2ScoreLabel);

		timerLabel = new JLabel("0:15");
		timerLabel.setBounds(547 + insets.left, 56 + insets.top, 387, 42);
		timerLabel.setFont(new Font("Castellar", Font.BOLD, 42));
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(timerLabel);

		/*
		 * Pause menu components
		 */
		pauseLabel = new JLabel();
		pauseLabel.setFont(new Font("Britannic Bold", Font.BOLD, 28));
		pauseLabel.setForeground(new Color(222, 200, 166));
		pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pauseLabel.setVerticalAlignment(SwingConstants.TOP);

		resumeButton = new JButton(ImageLoader.RESUME_BUTTON);
		resumeButton.setOpaque(false);
		resumeButton.setBorderPainted(false);
		resumeButton.setContentAreaFilled(false);
		resumeButton.setFocusPainted(false);
		resumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hidePauseScreen();
			}
		});

		restartButton = new JButton(ImageLoader.RESTART_BUTTON);
		restartButton.setOpaque(false);
		restartButton.setBorderPainted(false);
		restartButton.setContentAreaFilled(false);
		restartButton.setFocusPainted(false);
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameInstance.instance.dispose();
				GameInstance.instance.pauseDialog.dispose();

				new GameInstance();
			}
		});

		quitButton = new JButton(ImageLoader.QUIT_BUTTON);
		quitButton.setOpaque(false);
		quitButton.setBorderPainted(false);
		quitButton.setContentAreaFilled(false);
		quitButton.setFocusPainted(false);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		this.repaint();

		Thread t = new Thread() {
			@Override
			public void run() {
				GameInstance.instance.initRound(); // start the game
			}
		};
		t.start();
	}

	/**
	 * Displays the pause screen
	 */
	private void showPauseScreen() {
		timerPaused = true;

		answerField.setVisible(false);

		answerButton.removeActionListener(answerButtonListener);
		menuButton.removeActionListener(menuButtonListener);
		passButton.removeActionListener(passButtonListener);

		/*
		 * Initialize and display pause dialog
		 */
		pauseDialog = new JDialog(this, MyBoggle.WINDOW_NAME);
		pauseDialog.setSize(300, 400);
		pauseDialog.setResizable(false);
		pauseDialog.setFocusable(true);
		pauseDialog.getContentPane().setBackground(new Color(60, 60, 60));
		pauseDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pauseDialog.setVisible(true);

		GridLayout layout = new GridLayout(4, 1, 5, 5);
		pauseDialog.setLayout(layout);

		pauseLabel.setText("PAUSED");
		pauseDialog.add(pauseLabel);

		pauseDialog.add(resumeButton);
		pauseDialog.add(restartButton);
		pauseDialog.add(quitButton);

		pauseDialog.pack();
	}

	/**
	 * Removes the pause screen (resume game)
	 */
	private void hidePauseScreen() {
		timerPaused = false;

		pauseDialog.dispose(); // close pause dialog

		answerField.setVisible(true);

		answerButton.addActionListener(answerButtonListener);
		menuButton.addActionListener(menuButtonListener);
		passButton.addActionListener(passButtonListener);
	}

	/**
	 * Initializes the Boggle round
	 */
	private void initRound() {
		scrambleBoard();

		/*
		 * Roll to determine who goes first
		 */
		promptLabel.setText("Rolling for first turn...");
		this.revalidate();
		delay(2);

		int firstPlayer = (int) (Math.random() * 2) + 1; // first player, 1 for p1, 2 for p2
		if (firstPlayer == 1)
			isP1Turn = true;
		else
			isP1Turn = false;

		promptLabel.setText(
				((MyBoggle.isPVP ? "Player " + firstPlayer : (isP1Turn ? "Player 1" : "Computer")) + " goes first!"));
		delay(2);

		if (!MyBoggle.isPVP && isP1Turn == false) // computer goes first
			computerTurn();
		else
			startTurn(true);
	}

	/**
	 * Makes the prompt label and answer field visible, makes the answer button
	 * clickable
	 * 
	 * @param resetTimer
	 *            if the timer should be reset
	 */
	private void startTurn(boolean resetTimer) {
		promptLabel.setVisible(false);
		answerField.setVisible(true);

		/*
		 * Make the previous words list visible
		 */
		String enteredWordsText = "";
		for (int i = 0; i < enteredWords.size(); i++) {
			if (i != 0)
				enteredWordsText += ", ";
			enteredWordsText += enteredWords.get(i);
		}
		enteredWordsLabel.setText(enteredWordsText);
		enteredWordsLabel.setVisible(true);
		alreadyUsedLabel.setVisible(true);

		answerButton.addActionListener(answerButtonListener);
		menuButton.addActionListener(menuButtonListener);
		passButton.addActionListener(passButtonListener);
		setPromptText(); // now that text field is visible, add hint text

		/*
		 * Check if either player has won the game
		 */
		if (p1Score >= MyBoggle.endPoints)
			endGame(1);
		else if (p2Score >= MyBoggle.endPoints)
			endGame(2);

		if (resetTimer) {
			timerRunning = true;

			timerThread = new Thread() {
				private long secondsLeft; // number of seconds remaining
				private long msCounter; // counts milliseconds

				@Override
				public void run() {
					secondsLeft = 15;
					msCounter = 0;

					timerLabel.setText("0:15");
					timerLabel.setForeground(Color.BLACK);

					while (timerRunning) {
						delay(0.05);
						msCounter += 50;

						if (secondsLeft > 5) {
							if (msCounter >= 1000) { // 1 second past
								if (!timerPaused)
									secondsLeft--;
								timerLabel.setText("0:" + (secondsLeft >= 10 ? secondsLeft : "0" + secondsLeft));
								msCounter = 0;
							}
						} else if (secondsLeft >= 0) {
							if (msCounter >= 500) { // 0.5 seconds past
								if (!timerPaused) {
									timerLabel.setText("0:0" + secondsLeft);

									if (timerLabel.getForeground() == Color.BLACK) {
										timerLabel.setForeground(Color.RED);
										secondsLeft--;
									} else
										timerLabel.setForeground(Color.BLACK);
								}

								msCounter = 0;
							}
						} else { // time is up
							answerButton.removeActionListener(answerButtonListener);
							menuButton.removeActionListener(menuButtonListener);
							passButton.removeActionListener(passButtonListener);

							timerRunning = false;

							alreadyUsedLabel.setVisible(false);
							enteredWordsLabel.setVisible(false);

							answerField.setVisible(false);

							promptLabel.setText("Time's up!");
							promptLabel.setVisible(true);

							secondsLeft = 15; // set to 15 to prevent relooping

							Thread t = new Thread() {
								@Override
								public void run() {
									delay(1.5);
									GameInstance.instance.switchTurn();
								}
							};
							t.start();
						}
					}
				}
			};
			timerThread.start(); // restarts the thread
		}
	}

	/**
	 * Switches the turn from p1 to p2 or vice versa Will auto-execute computer
	 * moves if necessary
	 */
	private void switchTurn() {
		isP1Turn = !isP1Turn;

		/*
		 * Display whose turn it is
		 */
		promptLabel.setText("It is now "
				+ (MyBoggle.isPVP ? ("Player " + (isP1Turn ? "1" : "2")) : (isP1Turn ? "Player 1" : "the Computer"))
				+ "'s turn");
		delay(2);

		if (MyBoggle.isPVP) { // allow other player to begin their turn
			startTurn(true);
		} else if (isP1Turn) { // run player 1 turn
			startTurn(true);
		} else { // run computer turn
			computerTurn();
		}
	}

	/**
	 * Computer takes a turn
	 */
	private void computerTurn() {
		/*
		 * Check if either player has won the game
		 */
		if (p1Score >= MyBoggle.endPoints)
			endGame(1);
		else if (p2Score >= MyBoggle.endPoints)
			endGame(2);

		int rand = (int) (Math.random() * 32); // used as randomness factor to determine cpu answer

		switch (MyBoggle.diff) { // rotate through difficulties
		case 1: // easy
			if (rand < 16) { // 50% chance to pass
				promptLabel.setText("Computer has passed.");

				delay(1);
				switchTurn();
			} else { // 50% chance to guess 3 letter word
				answerField.setText(threeLetters.get((int) (Math.random() * threeLetters.size())));

				submitWord();
			}
			break;
		case 2: // medium
			if (rand < 8) { // 25% chance to pass
				promptLabel.setText("Computer has passed.");

				delay(1);
				switchTurn();
			} else if (rand < 16) { // 25% chance to get 4 letters
				answerField.setText(fourLetters.get((int) (Math.random() * fourLetters.size())));

				submitWord();
			} else { // 50% chance to guess 3 letter word
				answerField.setText(threeLetters.get((int) (Math.random() * threeLetters.size())));

				submitWord();
			}
			break;
		case 3: // hard // medium
			if (rand < 8) { // 25% chance to get 5 letters
				answerField.setText(fiveLetters.get((int) (Math.random() * fiveLetters.size())));

				submitWord();
			} else if (rand < 16) { // 25% chance to get 4 letters
				answerField.setText(fourLetters.get((int) (Math.random() * fourLetters.size())));

				submitWord();
			} else { // 50% chance to guess 3 letter word
				answerField.setText(threeLetters.get((int) (Math.random() * threeLetters.size())));

				submitWord();
			}
			break;
		}
	}

	/**
	 * Finds all 3, 4, and 5 letter words for the AI
	 */
	private void findWords() {
		threeLetters.clear();
		fourLetters.clear();
		fiveLetters.clear();

		findWords("", null);
	}

	/**
	 * Finds all 3, 4, and 5 letter words recursively
	 * 
	 * @param s
	 *            String built so far
	 * @param posList
	 *            list of all locations of tiles used
	 */
	private void findWords(String s, List<Integer> posList) {
		if (s.length() == 3) {
			if (matchDictionary(s, 0, dictionary.size() - 1)) {
				threeLetters.add(s);
			}
		}
		if (s.length() == 4) {
			if (matchDictionary(s, 0, dictionary.size() - 1))
				fourLetters.add(s);
		}
		if (s.length() == 5) {
			if (matchDictionary(s, 0, dictionary.size() - 1))
				fiveLetters.add(s);
			return;
		}

		if (s.equals("")) {
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					List<Integer> newList = new ArrayList<Integer>();
					newList.add(i * BOARD_SIZE + j);
					findWords(String.valueOf(gameBoard[i][j].getActiveLetter()).toLowerCase(), newList);
				}
			}
		} else {
			int lastX = posList.get(posList.size() - 1) / BOARD_SIZE;
			int lastY = posList.get(posList.size() - 1) % BOARD_SIZE;

			for (int relX = -1; relX <= 1; relX++) {
				for (int relY = -1; relY <= 1; relY++) {
					/*
					 * Continue on invalid coordinates (off the board, or same position)
					 */
					if (relX == 0 && relY == 0)
						continue;
					if (lastX + relX < 0 || lastX + relX >= BOARD_SIZE)
						continue;
					if (lastY + relY < 0 || lastY + relY >= BOARD_SIZE)
						continue;
					if (posList.contains((lastX + relX) * BOARD_SIZE + (lastY + relY)))
						continue;

					s += String.valueOf(gameBoard[lastX + relX][lastY + relY].getActiveLetter()).toLowerCase();
					posList.add((lastX + relX) * BOARD_SIZE + (lastY + relY));
					findWords(s, posList);

					s = s.substring(0, s.length() - 1); // remove letter last added
					posList.remove(posList.size() - 1); // remove last position just added
				}
			}
		}
	}

	/**
	 * Called when submit answer button is pressed Checks the validity of the word
	 * entered and responds appropriately
	 */
	private void submitWord() {
		String word = answerField.getText().toUpperCase();

		if (word.length() == 0)
			return; // do not allow empty submissions

		passCount = 0; // reset pass count, as this player did not pass

		for (int i = 0; i < word.length(); i++) { // check if the submission contains non-letters
			if (!Character.isLetter(word.charAt(i))) {
				answerButton.removeActionListener(answerButtonListener);
				menuButton.removeActionListener(menuButtonListener);
				passButton.removeActionListener(passButtonListener);

				timerPaused = true;

				alreadyUsedLabel.setVisible(false);
				enteredWordsLabel.setVisible(false);

				promptLabel.setText("Only letters allowed!");
				promptLabel.setVisible(true);

				delay(2);
				timerPaused = false;

				startTurn(false);
				return;
			}
		}

		answerField.setText("");
		alreadyUsedLabel.setVisible(false);
		enteredWordsLabel.setVisible(false);
		timerRunning = false;
		this.revalidate();

		if (word.length() < MyBoggle.minLength) { // word is not long enough
			promptLabel.setText("That word is not long enough! (Min. " + MyBoggle.minLength + " letters");
		} else if (!matchDictionary(word.toLowerCase(), 0, dictionary.size() - 1)) { // word does not exist in the
																						// dictionary
			promptLabel.setText("That's not a real English word!");
		} else if (enteredWords.contains(word.toLowerCase())) { // word already exists
			promptLabel.setText("That word has already been used!");
		} else {
			/*
			 * Determine if the word is on the board
			 */
			List<Integer> letterList = wordOnBoard(word, new ArrayList<Integer>()); // find letters on board
			if (letterList == null) {
				promptLabel.setText("That word isn't on the board!");
			} else {
				int pointValue = calcPoints(word);

				enteredWords.add(word.toLowerCase()); // add new word to list of already found words

				/*
				 * Add points to player who just found the word
				 */
				if (isP1Turn)
					p1Score += pointValue;
				else
					p2Score += pointValue;

				/*
				 * Update the scoreboard
				 */
				p1ScoreLabel.setText(String.valueOf(p1Score));
				p2ScoreLabel.setText(String.valueOf(p2Score));

				promptLabel.setText((MyBoggle.isPVP ? ("Player " + (isP1Turn ? "1 " : "2 "))
						: (isP1Turn ? "Player 1 " : "Computer ")) + "earned " + pointValue + " points!");
				promptLabel.setVisible(true);
				this.revalidate();

				for (int i = 0; i < letterList.size(); i++) { // loop through all letters
					/*
					 * Create glowing effect on board to show where the letters are
					 */
					int xPos = letterList.get(i) / BOARD_SIZE;
					int yPos = letterList.get(i) % BOARD_SIZE;

					this.diceLabels[xPos][yPos].setVisible(false);
					this.validate();

					JLabel glowLabel = new JLabel(ImageLoader.LETTER_DICE_GLOW);

					/*
					 * Define glow label characteristics
					 */
					glowLabel.setText(String.valueOf(gameBoard[xPos][yPos].getActiveLetter()));
					glowLabel.setHorizontalTextPosition(SwingConstants.CENTER);
					glowLabel.setFont(letterFont);
					glowLabel.setForeground(Color.BLACK);

					/*
					 * Add content pane
					 */
					glowLabel.setBounds(90 + insets.left + yPos * 80, 70 + insets.top + xPos * 80,
							(int) glowLabel.getPreferredSize().getWidth(),
							(int) glowLabel.getPreferredSize().getHeight());
					this.add(glowLabel);
					this.setVisible(true);
					this.revalidate();
					this.repaint();

					delay(0.3);
					this.remove(glowLabel);

					diceLabels[xPos][yPos].setVisible(true);
				}
			}
		}

		promptLabel.setVisible(true);

		delay(2);
		switchTurn();
	}

	/**
	 * Determines if the given word is present on the Boggle board
	 * 
	 * @param word
	 *            the word to find
	 * @param posList
	 *            the list of tile positions containing the correct letters
	 * @return a list the same length as the word, showing the positions of the
	 *         respective letters on the board (will be null or empty if word does
	 *         not exist)
	 */
	private List<Integer> wordOnBoard(String word, List<Integer> posList) {
		if (posList.isEmpty()) { // no letters yet
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					if (gameBoard[i][j].getActiveLetter() == word.charAt(0)) {
						List<Integer> listToUse = new ArrayList<Integer>(); // list to pass in recursively
						listToUse.add(i * BOARD_SIZE + j);
						List<Integer> newList = wordOnBoard(word, listToUse); // result list from recursive call
						if (newList != null && !newList.isEmpty())
							return newList;
					}
				}
			}
			return null; // all tiles tried as starting locations, no word found
		} else if (posList.size() == word.length()) // all letters have been found
			return posList;
		else {
			int lastX = posList.get(posList.size() - 1) / BOARD_SIZE;
			int lastY = posList.get(posList.size() - 1) % BOARD_SIZE;

			for (int relX = -1; relX <= 1; relX++) {
				for (int relY = -1; relY <= 1; relY++) {
					/*
					 * Continue on invalid coordinates (off the board, or same position)
					 */
					if (relX == 0 && relY == 0)
						continue;
					if (lastX + relX < 0 || lastX + relX >= BOARD_SIZE)
						continue;
					if (lastY + relY < 0 || lastY + relY >= BOARD_SIZE)
						continue;

					/*
					 * If the letter is matching and the tile has not yet been used on the board
					 */
					if (gameBoard[lastX + relX][lastY + relY].getActiveLetter() == word.charAt(posList.size())
							&& !posList.contains((lastX + relX) * BOARD_SIZE + (lastY + relY))) {
						posList.add((lastX + relX) * BOARD_SIZE + (lastY + relY));
						List<Integer> newList = wordOnBoard(word, posList);
						if (newList != null && !newList.isEmpty())
							return newList;
						else
							posList.remove(posList.size() - 1); // remove the most recent letter
					}
				}
			}
		}
		return null;
	}

	/**
	 * Updates and displays current dice data
	 */
	private void updateDice() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {

				if (diceLabels[i][j] != null) // remove existing die display
					this.getContentPane().remove(diceLabels[i][j]);

				/*
				 * Set colour and text of die
				 */
				diceLabels[i][j] = new JLabel(ImageLoader.LETTER_DICE[gameBoard[i][j].getDisplayColour()]);

				diceLabels[i][j].setText(String.valueOf(gameBoard[i][j].getActiveLetter()));
				diceLabels[i][j].setHorizontalTextPosition(SwingConstants.CENTER);
				diceLabels[i][j].setFont(letterFont);
				diceLabels[i][j].setForeground(Color.BLACK);

				/*
				 * Set location of dice
				 */
				this.getContentPane().add(diceLabels[i][j]);
				diceLabels[i][j].setBounds(90 + insets.left + j * 80, 70 + insets.top + i * 80,
						(int) diceLabels[i][j].getPreferredSize().getWidth(),
						(int) diceLabels[i][j].getPreferredSize().getHeight());
			}
		}

		this.revalidate(); // update screen
	}

	/**
	 * Scrambles the board, called when both players have given up
	 */
	private void scrambleBoard() {
		Random rand = new Random();

		/*
		 * Create a copy of the game board
		 */
		LetterDice[][] oldGameBoard = new LetterDice[BOARD_SIZE][BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				oldGameBoard[i][j] = gameBoard[i][j];
			}
		}

		/*
		 * Initializes an array to keep track of the spots on the board that have been
		 * assigned a die, used to scramble the positions of the dice on the board
		 */
		boolean[] used = new boolean[25];
		for (int z = 0; z < used.length; z++) {
			used[z] = false;
		}

		boolean[] dicePosition = new boolean[25];
		for (int z = 0; z < dicePosition.length; z++) {
			dicePosition[z] = false;
		}

		int counter = 0;

		/*
		 * Scrambles the positions of the dice on the board
		 */
		while (counter < 25) {
			int column;
			int row;

			int n = rand.nextInt(25); // origin location
			int p = rand.nextInt(25); // destination location

			if (used[n] == false && dicePosition[p] == false) {

				column = p % 5;
				row = p / 5;

				gameBoard[row][column] = oldGameBoard[n / 5][n % 5];

				used[n] = true;
				dicePosition[p] = true;
				counter++;
			}
		}

		/*
		 * Changes the side facing up for each die
		 */
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				gameBoard[i][j].rollDie();
			}
		}

		updateDice();

		if (!MyBoggle.isPVP)
			findWords(); // find computer words
	}

	/**
	 * Sets prompt text on answer field
	 */
	private void setPromptText() {
		answerField.setText("Type a word here");
		answerField.setForeground(new Color(100, 100, 100));
		answerField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				answerField.setText("");
				answerField.setForeground(Color.BLACK);
				answerField.removeMouseListener(this);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				answerField.setText("");
				answerField.setForeground(Color.BLACK);
				answerField.removeMouseListener(this);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				answerField.setText("");
				answerField.setForeground(Color.BLACK);
				answerField.removeMouseListener(this);
			}
		});
	}

	/**
	 * Attempts to wait a certain amount of time
	 * 
	 * @param seconds
	 *            number of seconds to wait
	 */
	private void delay(double seconds) {
		try {
			Thread.sleep((long) (seconds * 1000));
		} catch (InterruptedException e) {
			// No action required, program can proceed as normal
		}
	}

	/**
	 * Ends this instance of Boggle
	 * 
	 * @param winner
	 *            the winner of the game (1 or 2)
	 */
	private void endGame(int winner) {
		timerPaused = true;

		answerField.setVisible(false);

		answerButton.removeActionListener(answerButtonListener);
		menuButton.removeActionListener(menuButtonListener);
		passButton.removeActionListener(passButtonListener);

		/*
		 * Initialize and display pause dialog
		 */
		pauseDialog = new JDialog(this, MyBoggle.WINDOW_NAME);
		pauseDialog.setSize(300, 400);
		pauseDialog.setResizable(false);
		pauseDialog.setFocusable(true);
		pauseDialog.getContentPane().setBackground(new Color(60, 60, 60));
		pauseDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pauseDialog.setVisible(true);

		GridLayout layout = new GridLayout(4, 1, 5, 5);
		pauseDialog.setLayout(layout);

		pauseLabel.setText(
				MyBoggle.isPVP ? "PLAYER " + winner + " WINS!" : (winner == 1 ? "PLAYER 1 WINS!" : "COMPUTER WINS!"));
		pauseDialog.add(pauseLabel);

		pauseDialog.add(restartButton);
		pauseDialog.add(quitButton);

		pauseDialog.pack();
	}

	/**
	 * Initializes the dictionary by text file
	 * 
	 * @param dictionary
	 *            array list to initialize
	 * @throws Exception
	 */
	private void createDictionary(ArrayList<String> dictionary) throws Exception {
		File wordlist = new File("src/wordlist.txt"); // The file location should be wherever the
														// wordlist.txt location is on your computer

		Scanner input = new Scanner(wordlist);
		String line;

		while (input.hasNext()) {
			line = input.nextLine();
			dictionary.add(line);
		}
		input.close();
	}

	/**
	 * Checks if the word is in the dictionary, using recursive binary search
	 * 
	 * @param word
	 *            word to find in the dictionary
	 * @param left
	 *            left bound of checking region
	 * @param right
	 *            right bound of checking region
	 */
	private boolean matchDictionary(String word, int left, int right) {
		if (right >= left) {
			int mid = left + (right - left) / 2;

			if (dictionary.get(mid).equals(word)) // word has been found
				return true;
			else if (dictionary.get(mid).compareTo(word) > 0) // mid point is greater than word
				return matchDictionary(word, left, mid - 1);
			else
				return matchDictionary(word, mid + 1, right); // mid point is less than word
		} else
			return false;
	}

	/**
	 * Calculates the number of points a word is worth Revamped formula to increase
	 * the reward for high letter plays
	 * 
	 * @param word
	 *            the word to calculate point value of
	 * @return the point value
	 */
	private int calcPoints(String word) {
		return (int) (0.75 * (Math.pow(word.length(), 1.5)));
	}
}
