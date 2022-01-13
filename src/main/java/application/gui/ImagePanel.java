package application.gui;

import application.util.FeedbackType;
import application.util.ScreenType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ImagePanel extends BorderPane implements Panel {
	private final String imageName;
	private final FeedbackType feedbackType;
	private final ScreenType type;
	private final ImageView imageScreen;
	private final Text text;

	public ImagePanel(String imageName, String text, Image image, FeedbackType feedbackType) {
		this.imageName = imageName;
		this.feedbackType = feedbackType;
		this.type = ScreenType.Image;
		this.text = new Text(text);
		imageScreen = new ImageView(image);
		
		this.setCenter(imageScreen);
		this.setBottom(this.text);
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

		// configure the Bottom section to align top (of the Bottom) and center
		ImagePanel.setAlignment(text, Pos.TOP_CENTER);
		ImagePanel.setMargin(text, new Insets(30));
	}

	public String getImageName() {
		return imageName;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	@Override
	public ScreenType getType() {
		return type;
	}
}
