package de.tud.optalgos.model;

import java.awt.Rectangle;

public class MRectangle extends Rectangle {
	
	private static final long serialVersionUID = 1L;
	public int boxLength;
	
	public MRectangle(int x, int y, int width, int height, int boxLength) {
		super.setRect(x, y, width, height);
		this.boxLength = boxLength;
	}

}

