package de.tud.optalgos.model;

import java.awt.Rectangle;

public class MRectangle extends Rectangle {
	
	private static final long serialVersionUID = 1L;
	private int boxLength;
	private MBox mBox;
	
	public MRectangle(int x, int y, int width, int height, int boxLength) {
		super.setRect(x, y, width, height);
		this.boxLength = boxLength;
	}

	public MBox getmBox() {
		return mBox;
	}

	public void setmBox(MBox mBox) {
		this.mBox = mBox;
	}

	public int getBoxLength() {
		return boxLength;
	}

}

