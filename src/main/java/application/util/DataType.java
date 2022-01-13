package application.util;

public enum DataType {
	Title("title"),
	Data("data");
	
	private final String type;
	
	private DataType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type;
	}
}
