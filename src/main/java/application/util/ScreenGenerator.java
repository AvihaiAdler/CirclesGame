package application.util;

import java.util.Random;
import application.gui.CirclesPanel;
import application.gui.Screen;
import application.gui.CrossPanel;
import application.gui.ImagePanel;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ScreenGenerator {
	private final double width;
	private final double height;
	private final Color backgroundColor;

	public ScreenGenerator(double screenWidth, double screenHeight, Color backgroundColor) {
		this.width = screenWidth;
		this.height = screenHeight;
		this.backgroundColor = backgroundColor;
	}

	/*
	 * Returns a circles screen. Circle numbers will be generated in the range of
	 * [min, max] based on the difficulty level
	 */
	public Screen createCirclesScreen(int min, int max, int difficultyLvl) {
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
		var radius = width / 150; // radius of each circle
		var left = new CirclesPanel(circlesOnLeft, radius, Color.rgb(220, 220, 220), Sides.Left, width/4, height * 0.8);
		var right = new CirclesPanel(circlesOnRight, radius, Color.rgb(220, 220, 220), Sides.Right, width/4, height * 0.8);
		
		// wrapper panel
		var separator = new Separator(Orientation.VERTICAL);
		separator.setStyle("-fx-border-style: solid; -fx-border-width: 0 0 0 2; -fx-border-color: #c0c0c0");
		
		HBox.setHgrow(left, Priority.ALWAYS);
		HBox.setHgrow(right, Priority.ALWAYS);
		var box = new HBox(left, separator, right);
		box.setAlignment(Pos.CENTER);
		box.setSpacing(50);
		
		return new Screen(box, ScreenType.Circles, width, height, this.backgroundColor);
	}

	/*
	 * Returns an ImagePane consists of an image and a text below it.
	 */
	public Screen createImagesScreen(ImageWrapper image, String str) {
		var imageStream = this.getClass().getClassLoader().getResourceAsStream(image.name());
		
		if(imageStream == null)
			throw new NullPointerException("Image " + image.name() + " couldn't be found");
		var img = new Image(imageStream);
		var imagePanel = new ImagePanel(image.name(), str, img, image.type());
		imagePanel.fitImageToScreen(width, height);
		imagePanel.styleText();
		
		return new Screen(imagePanel, ScreenType.Image, width, height, backgroundColor);
	}

	/*
	 * Creates a screen with a cross in the middle of it
	 */
	public Screen createCrossScreen(int proportion, int lineWidth) {
		var screen = new CrossPanel(Color.rgb(220, 220, 220), proportion, lineWidth, height, height);
		return new Screen(screen, ScreenType.Cross, width, height, backgroundColor);
	}

	public Screen createBlankPanel() {
		return new Screen(new StackPane(), ScreenType.Blank, width, height, backgroundColor);
	}
}
