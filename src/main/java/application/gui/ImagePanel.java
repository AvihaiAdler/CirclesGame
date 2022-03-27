package application.gui;

import application.dal.FeedbackType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ImagePanel extends AnchorPane {
	private final String imageName;
	private final FeedbackType feedbackType;
	private final ImageView imageScreen;
	private final Text text;

	public ImagePanel(String imageName, String text, Image image, FeedbackType feedbackType) {
		this.imageName = imageName;
		this.feedbackType = feedbackType;
		this.text = new Text(text);
		imageScreen = new ImageView(image);

		this.getChildren().addAll(imageScreen, this.text);
	}
	
	public void fitImageToScreen(double width, double height) {
		double imgHeight = imageScreen.getImage().getHeight();
		double imgWidth = imageScreen.getImage().getWidth();
		if (imgHeight >= height * 0.6)
			imgHeight = imgHeight * 0.75;
		if(imgWidth >= height * 0.6) 
			imgWidth = imgWidth * 0.75;
		
		imageScreen.setFitWidth(imgWidth);
		imageScreen.setFitHeight(imgHeight);
	}
	
	public void styleText() {
		text.setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.REGULAR, 80));
		text.setFill(Color.rgb(220, 220, 220));
		text.setTextAlignment(TextAlignment.CENTER);
	}
	
	public void alignToCenter(double screenWidth, double screenHeight) {
    AnchorPane.setTopAnchor(imageScreen, screenHeight / 10);
    AnchorPane.setLeftAnchor(imageScreen, screenWidth / 2 - imageScreen.getFitWidth() / 2);
    AnchorPane.setRightAnchor(imageScreen, screenWidth / 2 - imageScreen.getFitWidth() / 2);
    AnchorPane.setTopAnchor(text, screenHeight / 10 + imageScreen.getFitHeight());
    AnchorPane.setLeftAnchor(text, screenWidth / 2 - imageScreen.getFitWidth() / 5);
    AnchorPane.setRightAnchor(text, screenWidth / 2 - imageScreen.getFitWidth() / 5);
	}

	public String getImageName() {
		return imageName;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}
}
