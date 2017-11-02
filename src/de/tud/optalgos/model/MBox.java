package de.tud.optalgos.model;

import java.awt.Rectangle;

public class MBox extends Rectangle{
	
	private static final long serialVersionUID = 1L;
	public final int boxLength;
	
	public MBox(int boxLength) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
	}

}
