package application.util;

public enum ScreenType {
  Welcome("welcome"),
	Cross("cross"),
	Circles("circles"),
	Image("image"),
	Blank("blank");
	
	public final String type;
	ScreenType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
