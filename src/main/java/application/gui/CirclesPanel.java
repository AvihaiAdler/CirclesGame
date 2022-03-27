package application.gui;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import application.util.Sides;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class CirclesPanel extends Pane {
	private final int numOfCircles;
	private final double width;
	private final double height;
	private final double radius;
	private final List<Sphere> lst;
	private final Color color;
	private final Sides side;

	public CirclesPanel(int numOfCircles, double radius, Color color, Sides side, double width, double height) {
		this.numOfCircles = numOfCircles;
		this.width = width;
		this.height = height;
		this.radius = radius;
		this.side = side;
		this.color = color;

		lst = generateSpheres();
		addToPanel();
		setProperties();
	}

	private List<Sphere> generateSpheres() {
		return IntStream.range(0, numOfCircles)
				.mapToObj(i -> {
					var phong = new PhongMaterial(color);
					phong.setSpecularColor(Color.SILVER);
					return phong;
				})
				.map(phong -> {
					var sphere = new Sphere(radius);
					sphere.setMaterial(phong);
					return sphere;
				})
				.toList();
	}

	public Sides getSide() {
		return side;
	}

	public int getSpheresCount() {
		return numOfCircles;
	}

	public void addToPanel() {
		generateCoord();
		getChildren().addAll(lst);
	}

	private void generateCoord() {
		var rand = new Random();
		// (rand() * (max - 2*radius - 2*radius)) + 2*radius - to generate a number between (max-2*radius, 2*radius]
		lst.forEach(sphere -> {
			sphere.setTranslateX((rand.nextDouble() * (width - 4 * radius)) + 2 * radius);
			sphere.setTranslateY((rand.nextDouble() * (height - 4 * radius)) + 2 * radius);
		});

		// better distribution of circles
		for (var i = 0; i < lst.size() - 1; i++) {
			for (var j = i + 1; j < lst.size(); j++) {
				// if collide with another circle
				if (Math.abs(lst.get(i).getTranslateX() - lst.get(j).getTranslateX()) < 4.2 * radius
						&& Math.abs(lst.get(i).getTranslateY() - lst.get(j).getTranslateY()) < 4.2 * radius) {
					// throw it vertically (it's X will stay the same). note: this algorithm only
					// works for a relatively small number of circles with a small radius
					lst.get(j).setTranslateY((rand.nextDouble() * (height - 4 * radius)) + 2 * radius);
				}
			}
		}
	}

	public void setProperties() {
		setPadding(new Insets(2));
		setPrefSize(width, height);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
	}

	public CirclesPanel greaterThan(CirclesPanel other) {
		if (other == null)
			return this;
		return this.getSpheresCount() > other.getSpheresCount() ? this : other;
	}
}
