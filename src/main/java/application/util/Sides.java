package application.util;

public enum Sides {
	Left("left"),
	Right("right");
	
	private final String side;
	
	private Sides(String side) {
		this.side = side;
	}
	
	public String toString() {
		return side;
	}
}
