package ics4u;

/*
 * File:			ImageLoader.java
 * Date Created:	2019-06-06
 * Last Modified:	2019-06-11
 * Authors:			Simon Ou, Daniel Qu, Justin Zhou
 * 
 * Course:			ICS4U1-03
 * Teacher:			Mr. Anandarajan
 * 
 * Description:		Utility class that stores and loads some image resources from file.
 */

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageLoader {
	/*
	 * Declaration of all image objects
	 */
	public static BufferedImage GAME_BACKGROUND;

	public static ImageIcon[] LETTER_DICE;
	
	public static ImageIcon ANSWER_BUTTON;
	public static ImageIcon LETTER_DICE_GLOW;
	
	public static ImageIcon MENU_BUTTON;
	public static ImageIcon PASS_BUTTON;
	
	public static ImageIcon RESUME_BUTTON;
	public static ImageIcon RESTART_BUTTON;
	public static ImageIcon QUIT_BUTTON;

	/**
	 * Loads a buffered image from file
	 * 
	 * @param name
	 *            name of the file, excluding extension
	 * @return BufferedImage object
	 */
	private static BufferedImage loadBufferedImage(String name) {
		try {
			return ImageIO.read(MyBoggle.class.getResource("/" + name + ".png"));
		} catch (IOException e) {
			System.err.println(name + ".png could not be loaded from file as a buffered image!");
		}
		return null;
	}

	/**
	 * Loads an image icon from file
	 * 
	 * @param name
	 *            name of the file, excluding extension
	 * @return ImageIcon object
	 */
	private static ImageIcon loadImageIcon(String name) {
		return new ImageIcon(MyBoggle.class.getResource("/" + name + ".png"));
	}

	/**
	 * Loads all images from file, call at start of program
	 */
	public static void loadImages() {
		GAME_BACKGROUND = loadBufferedImage("game_background");

		LETTER_DICE = new ImageIcon[5];
		for (int i = 0; i < 5; i++)
			LETTER_DICE[i] = loadImageIcon("letter_dice_" + i);
		
		ANSWER_BUTTON = loadImageIcon("answer_button");
		LETTER_DICE_GLOW = loadImageIcon("letter_dice_glow");
		
		MENU_BUTTON = loadImageIcon("menu_button");
		PASS_BUTTON = loadImageIcon("pass_button");
		
		RESUME_BUTTON = loadImageIcon("Resume");
		RESTART_BUTTON = loadImageIcon("Restart");
		QUIT_BUTTON = loadImageIcon("Quit");
	}
}
