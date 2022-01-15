package application.gui;

import application.util.ScreenType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class Screen extends Scene implements Panel {
	private ScreenType type;

	public Screen(Parent parent, ScreenType type, double width, double height, Color color) {
		super(parent, width, height, color);
		this.type = type;
	}

	@Override
	public ScreenType getType() {
		return type;
	}
}
