package application.util;

import java.util.Random;

import application.game.ImageWrapper;
import application.gui.BlankPanel;
import application.gui.CirclesPanel;
import application.gui.CirclesPanelContainer;
import application.gui.CrossPanel;
import application.gui.ImagePanel;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ScreenGenerator {
	private final double width;
	private final double height;

	public ScreenGenerator(double screenWidth, double screenHeight) {
		this.width = screenWidth;
		this.height = screenHeight;
	}

	/*
	 * Returns a circles screen. Circle numbers will be generated in the range of
	 * [min, max] based on the difficulty level
	 */
	public CirclesPanelContainer createCirclesScreen(int min, int max, Color color, int difficultyLvl) {
		var rand = new Random();

		/*
		 * random number for circles. The number for each panel depends on the other
		 * panel and the difficulty level. Difficulty level starts at 5. The number for
		 * each panel must be between [min, max], while the difference between both
		 * panels must be equal to difficulty level. For example for min = 20, max = 30,
		 * difficultyLvl = 5: if left panel has 20 circles the right panel must have 25
		 * circles
		 */
		int random;
		do {
			random = rand.nextInt(max + 1 - min) + min;
		} while (random - difficultyLvl < min && random + difficultyLvl > max);

		int inverse;
		if (random - difficultyLvl >= min)
			inverse = random - difficultyLvl;
		else
			inverse = random + difficultyLvl;

		int circlesOnLeft, circlesOnRight;
		var tmp = rand.nextInt(2);
		if (tmp > 0) {
			circlesOnLeft = random;
			circlesOnRight = inverse;
		} else {
			circlesOnLeft = inverse;
			circlesOnRight = random;
		}

		// init 2 sub Panels, left and right		
//		var inner = new GridPane();
//		inner.setAlignment(Pos.CENTER);
//		inner.setPadding(new Insets(3));
//		inner.setHgap(2);
//		inner.setVgap(2);

		var radius = width / 90; // radius of each circle
		var left = new CirclesPanel(circlesOnLeft, radius, color, Sides.Left, width / 2, height * 0.9);
		var right = new CirclesPanel(circlesOnRight, radius, color, Sides.Right, width / 2, height * 0.9);

		// (Pane, col, row)
//		inner.add(left, 0, 0);
//		inner.add(right, 1, 0);
//
//		// wrapper panel
//		var containerPanel = new CirclesPanelContainer(inner, ScreenType.Circles, width, height);
		var containerPanel = new CirclesPanelContainer(new HBox(left, right), ScreenType.Circles, width, height);
		containerPanel.addToPanel();

		return containerPanel;
	}

	/*
	 * Returns an ImagePane consists of an image and a text below it.
	 */
	public ImagePanel createImagesScreen(ImageWrapper image, String str) {
		var imageStream = this.getClass().getClassLoader().getResourceAsStream(image.getName());
		
		if(imageStream == null)
			throw new NullPointerException("Image " + image.getName() + " couldn't be found");
		var img = new Image(imageStream);
		var imagePanel = new ImagePanel(image.getName(), str, img, image.getType());
		imagePanel.fitImageToScreen(width, height);
		imagePanel.styleText();

		return imagePanel;
	}

	/*
	 * Creates a screen with a cross in the middle of it
	 */
	public CrossPanel createCrossScreen(Color color, int proportion, int lineWidth) {
		return new CrossPanel(color, proportion, lineWidth, width, height);
	}

	public BlankPanel createBlankPanel() {
		return new BlankPanel();
	}
}
