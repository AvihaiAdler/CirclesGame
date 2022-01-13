package application.util;

public enum ScreenType {
	Cross("cross"),
	Circles("circles"),
	Image("image"),
	Blank("blank");
	
	public final String type;
	private ScreenType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
