package de.tud.optalgos.model;

import java.util.ArrayList;

public class MInstance extends Instance{
	
	private ArrayList<MRectangle> rechtangles;
	private ArrayList<MBox> boxes;
	private final int boxLength;
	
	public MInstance(
			int boxLength, ArrayList<MRectangle> rectangles, ArrayList<MBox> boxes) {
		this.boxLength = boxLength;
		this.rechtangles = rectangles;
		this.boxes = boxes;
	}

	public ArrayList<MRectangle> getRechtangles() {
		return rechtangles;
	}

	public ArrayList<MBox> getBoxes() {
		return boxes;
	}

	public int getBoxLength() {
		return boxLength;
	}
}
