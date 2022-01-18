package ics4u;

/*
 * File:			LetterDice.java
 * Date Created:	2019-06-05
 * Last Modified:	2019-06-06
 * Authors:			Simon Ou, Daniel Qu, Justin Zhou
 * 
 * Course:			ICS4U1-03
 * Teacher:			Mr. Anandarajan
 * 
 * Description:		Letter dice object class, holds state variables for letters on each side of
 * 					the die, as well as the active side (facing up). Includes methods to
 * 					get active side and roll the die.
 */

public class LetterDice {
	/*
	 * Contains all dice layout presets
	 * 
	 * DICE_CONFIG[x-coordinate][y-coordinate] = 6-letter string containing side
	 * configurations Note: top left corner is considered (0, 0)
	 */
	public static final String[][] DICE_CONFIG = { { "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM" },
			{ "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW" }, { "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DHHLOR" },
			{ "DHHNOT", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU" },
			{ "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU" } };

	private char[] letters; // stores the letter on each side of the die

	private int activeIndex; // index of the letter currently facing up on the die

	private final int displayColour; // the colour of the tile, used to determine which texture to use

	/**
	 * Constructs a new letter die
	 * 
	 * @param letters
	 *            an array of size 6, containing the letters on the die
	 * @param displayColour
	 *            colour of wood displayed on screen
	 */
	public LetterDice(char[] letters, int displayColour) {
		this.letters = letters;
		this.displayColour = displayColour;

		rollDie();
	}

	/**
	 * 
	 * @return the letter on the side of the die facing up
	 */
	public char getActiveLetter() {
		return letters[activeIndex];
	}

	/**
	 * 
	 * @return the display colour of the tile on screen
	 */
	public int getDisplayColour() {
		return displayColour;
	}

	/**
	 * "Rolls" the die, a random letter will be facing up as a result
	 */
	public void rollDie() {
		activeIndex = (int) (Math.random() * 6);
	}
}
