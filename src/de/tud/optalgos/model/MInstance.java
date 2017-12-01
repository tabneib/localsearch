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
	
	public ArrayList<MBox> getClonedBoxes() {
		ArrayList<MBox> newBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.boxes) {
			if(!mBox.getMRectangles().isEmpty()) {
				newBoxes.add(mBox.clone());
			}
			
		}
		return newBoxes;
	}

	public int getBoxLength() {
		return boxLength;
	}
}
