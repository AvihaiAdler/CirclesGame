package application.gui;

import application.util.ScreenType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Screen extends Scene {
	private final ScreenType type;

	public Screen(Parent parent, ScreenType type, double width, double height, Color color) {
		super(parent, width, height, color);
		((Pane)this.getRoot()).setBackground(new Background(new BackgroundFill(null, null, null)));
		this.type = type;
	}

	public ScreenType getType() {
		return type;
	}
}
