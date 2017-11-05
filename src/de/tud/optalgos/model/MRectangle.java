package de.tud.optalgos.model;

import java.awt.Rectangle;

public class MRectangle extends Rectangle {
	
	private static final long serialVersionUID = 1L;
	private int boxLength;
	private MBox mBox;
	private final int maxSize;
	private final int minSize;
	
	public MRectangle(int x, int y, int width, int height, int boxLength) {
		super.setRect(x, y, width, height);
		this.boxLength = boxLength;
		this.maxSize = Math.max(width, height);
		this.minSize = Math.min(width, height);
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

	public int getMaxSize() {
		return maxSize;
	}

	public int getMinSize() {
		return minSize;
	}
	
	@Override
	public String toString(){
		return "((" + getLocation().getX() + ", " + getLocation().getY() + "), " + 
					"(" + getWidth() + ", " + getHeight() + "))";
	};
}


