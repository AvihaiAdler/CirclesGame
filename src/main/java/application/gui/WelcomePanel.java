package application.gui;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class WelcomePanel extends BorderPane {
  private final double width;
  private final Text welcome;
  private final Text instructions;
  private final Text last;
  
  public WelcomePanel(double width) {
    this.width = width;
    
    instructions = new Text();
    welcome = new Text();
    last = new Text();
    construct();
    
    var container =  new VBox();
    container.getChildren().addAll(welcome, instructions, last);
    container.setAlignment(Pos.CENTER);
    BorderPane.setAlignment(container, Pos.CENTER);
    this.setCenter(container);
  }
  
  private void construct() {
    style("Welcome!", welcome, 40, TextAlignment.CENTER);
    String text = """
            In this game you'll be presented with 2 screens for a short period of time. Each screen contains a number of circles. 
            Your task is to identify the side which contains the most circles and choose it.
            To choose a screen simply press the corresponding arrow on your keyboard, i.e. to choose the left screen - press the left arrow key.
            The choosing phase starts as soon as the screen turns black and you can't see the circles anymore.
            
            Good luck!
            """;
    style(text, instructions, 30, TextAlignment.JUSTIFY);
    style("Press any key to continue", last, 40, TextAlignment.CENTER);
  }
  
  private void style(String str, Text text, int fontSize, TextAlignment alignment) {
    text.setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.REGULAR, fontSize));
    text.setText(str);
    text.setWrappingWidth(width/2);
    text.setFill(Color.WHITE);
    text.setTextAlignment(alignment);
  }
}
