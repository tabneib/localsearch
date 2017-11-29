package de.tud.optalgos.model;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Random;

public class MBox extends Rectangle implements Cloneable{
	
	private static final long serialVersionUID = 1L;
	public static final int SMOOTH_DEGREE = 10;
	public static final int ATTEMPTS =1000000;
	private final int boxLength;
	private int gridStep;
	private HashSet<MRectangle> mRectangles;
	
	public MBox(int boxLength, HashSet<MRectangle> mRectangles) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = mRectangles;
		this.gridStep = this.boxLength/SMOOTH_DEGREE;
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
	 * automatic insert in random position
	 */
	public boolean insert(MRectangle m) {
		System.out.println("insert");
		Random r = new Random();
		int maxRange = this.boxLength/this.gridStep;
		MRectangle cloneM = m.clone();
		
		if(this.getFreeArea() < m.getArea()) {
			return false;
		}
		int attempt = 0;
		while(attempt < ATTEMPTS) {
			if(this.insert(cloneM, r.nextInt(maxRange)*gridStep, r.nextInt(maxRange)*gridStep, r.nextBoolean())) {
				return true;
			}
			attempt++;
		}
		
		return false;
	}
	
	/*
	 * insert in definite location with or without rotation
	 */
	public boolean insert(MRectangle m, int x, int y, boolean rotated) {
		//check overlapping
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
		//insert this Rectangle into Box
		mRectangles.add(m);
		//update grid step
		if(m.getMinSize() < gridStep*SMOOTH_DEGREE) {
			this.gridStep = m.getMinSize()/SMOOTH_DEGREE;
		}
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
	 * get free area
	 */
	public double getFreeArea() {
		double coveredArea = 0;
		for (MRectangle internM : this.mRectangles) {
			coveredArea += internM.getArea();
		}
		return this.boxLength*this.boxLength - coveredArea;
	}
	
	/*
	 * get fill grade
	 */
	public double getFillGrade() {
		double coveredArea = 0;
		for (MRectangle internM : this.mRectangles) {
			coveredArea += internM.getArea();
		}
		return (coveredArea / this.boxLength*this.boxLength);
	}

	public HashSet<MRectangle> getMRectangles() {
		return mRectangles;
	}
	
	@Override
	public String toString() {
		return "((" + getLocation().getX() + ", " + getLocation().getY() + "), " + this.boxLength + ")";
	}
	
	@Override
	public MBox clone(){
		HashSet<MRectangle> clonedRectangles = new HashSet<MRectangle>();
		for (MRectangle mRectangle : this.mRectangles) {
			clonedRectangles.add(mRectangle.clone());
		}
		MBox newBox = new MBox(this.boxLength,clonedRectangles);
		return newBox;
	};
}
