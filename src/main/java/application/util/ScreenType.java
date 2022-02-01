package application.util;

public enum ScreenType {
  Welcome("welcome"),
	Cross("cross"),
	Circles("circles"),
	Image("image"),
	Blank("blank");
	
	public String type;
	private ScreenType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
