package application.gui;

import application.util.ScreenType;
import javafx.scene.layout.BorderPane;

public class BlankPanel extends BorderPane implements Panel {
	private final ScreenType type;

	public BlankPanel() {
		this.type = ScreenType.Blank;
	}

	@Override
	public ScreenType getType() {
		return type;
	}
}
