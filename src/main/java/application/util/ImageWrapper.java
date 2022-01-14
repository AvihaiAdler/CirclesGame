package application.util;

import application.dao.FeedbackType;

public class ImageWrapper {
	private final String name;
	private final FeedbackType type;


	public ImageWrapper(String name, FeedbackType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public FeedbackType getType() {
		return type;
	}
}
