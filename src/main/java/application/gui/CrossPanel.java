package application.gui;

import application.util.ScreenType;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CrossPanel extends StackPane implements Panel {
	private double width;
	private double height;
	private int lineWidth;
	private Line horizontal;
	private Line vertical;
	private final ScreenType type;

	// represents a factor to determine how much the line will 'extends' to the
	// sides
	private int proportion;

	private final Color color;

	public CrossPanel(Color color, int proportion, int lineWidth, double width, double height) {
		super();
		this.width = width;
		this.height = height;
		this.proportion = proportion;
		this.lineWidth = lineWidth;
		this.color = color;
		this.type = ScreenType.Cross;

		setProperties();
		constructCross();
		addToPanel();
	}

	private void constructCross() {
		horizontal = new Line(width / 2 - width / proportion, height / 2, width / 2 + width / proportion, height / 2);
		horizontal.setStrokeWidth(lineWidth);
		horizontal.setStroke(color);

		vertical = new Line(width / 2, height / 2 - height / proportion, width / 2, height / 2 + height / proportion);
		vertical.setStrokeWidth(lineWidth);
		vertical.setStroke(color);
	}

	public void addToPanel() {
		getChildren().addAll(vertical, horizontal);
	}

	public void setProperties() {
		setPadding(new Insets(3));
	}

	@Override
	public ScreenType getType() {
		return type;
	}
}
