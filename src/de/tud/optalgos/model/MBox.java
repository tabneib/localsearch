package de.tud.optalgos.model;

import java.awt.Rectangle;
import java.util.HashSet;

public class MBox extends Rectangle{
	
	private static final long serialVersionUID = 1L;
	private final int boxLength;
	private HashSet<MRectangle> mRectangles;
	
	public MBox(int boxLength, HashSet<MRectangle> mRectangles) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = mRectangles;
	}
	
	public MBox(int boxLength) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = new HashSet<MRectangle>();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getBoxLength() {
		return boxLength;
	}
	
	
	/*
	 * automatic insert
	 */
	public boolean insert(MRectangle m) {
		//TODO 
		
		return true;
	}
	
	/*
	 * insert in definite location with or without rotation
	 */
	public boolean insert(MRectangle m, int x, int y, boolean rotated) {
		if(rotated) {
			m.rotate();
		}
		m.setLocation(x, y);
		if(!this.contains(m)) {
			return false;
		}
		for (MRectangle internM : this.mRectangles) {
		    if(internM.intersects(m)) {
		    	return false;
		    }
		}
		mRectangles.add(m);
		return true;
	}
	
	/*
	 * check if this box can insert a rectangle in definite location with or without rotation
	 */
	public boolean fit(MRectangle m, int x, int y, boolean rotated) {
		if(rotated) {
			m.rotate();
		}
		m.setLocation(x, y);
		if(!this.contains(m)) {
			return false;
		}
		for (MRectangle internM : this.mRectangles) {
		    if(internM.intersects(m)) {
		    	return false;
		    }
		}
		
		return true;
	}
	
	/*
	 * insert in definite location with or without rotation
	 */
	

	public HashSet<MRectangle> getMRectangles() {
		return mRectangles;
	}
	
	@Override
	public String toString() {
		return "((" + getLocation().getX() + ", " + getLocation().getY() + "), " + this.boxLength + ")";
	}
}
