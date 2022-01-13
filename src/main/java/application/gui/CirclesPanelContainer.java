package application.gui;

import application.util.ScreenType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
//import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CirclesPanelContainer extends StackPane implements Panel {
	private double width;
	private double height;
	private HBox inner;
	private Line divider;
	private final ScreenType type;

	public CirclesPanelContainer(HBox inner, ScreenType type, double width, double height) {
		this.inner = inner;
		this.width = width;
		this.height = height;
		this.type = ScreenType.Circles;
		
		divider = new Line(this.width / 2, 0, this.width / 2, this.height);
		divider.setStrokeWidth(5);
		divider.setStroke(Color.WHITE);
	}

	public void clearPanel() {
		getChildren().clear();
	}

	public HBox getInner() {
		return inner;
	}

	public void setInner(HBox inner) {
		this.inner = inner;
	}

	public void addToPanel() {
		setProperties();
		if (divider != null)
			getChildren().add(divider);
		getChildren().add(inner);
	}

	public void setProperties() {
//		setAlignment(Pos.CENTER);
//		setPadding(new Insets(1));
//		inner.setHgap(2);
//		inner.setVgap(2);
		inner.setAlignment(Pos.CENTER);
		inner.setPadding(new Insets(10));
	}

	@Override
	public ScreenType getType() {
		return type;
	}
}
