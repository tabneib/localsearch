package de.tud.optalgos.model;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Random;

public class MBox extends Rectangle implements Cloneable{
	
	private static final long serialVersionUID = 1L;
	public static final int SMOOTH_DEGREE = 10;
	public static final int ATTEMPTS =10000;
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
		
		Random r = new Random();
		int maxRange = this.boxLength/this.gridStep;
		
		if(this.getFreeArea() < m.getArea()) {
			return false;
		}
		
		int attempt = 0;
		while(attempt < ATTEMPTS) {
			if(this.insert(m, r.nextInt(maxRange)*gridStep, r.nextInt(maxRange)*gridStep, r.nextBoolean())) {
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
			m = m.rotate();
		}else {
			m = m.clone();
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
		
		//update grid step
		if(m.getMinSize() < gridStep*SMOOTH_DEGREE) {
			this.gridStep = m.getMinSize()/SMOOTH_DEGREE;
		}
		//optimization: move to edge (move)
		
		this.optimalMove(m, 1,this.gridStep);
		this.optimalMove(m, 2,this.gridStep);
		
		//insert this Rectangle into Box
		this.mRectangles.add(m);
		return true;
	}
	
	public void optimalSort() {
		HashSet<MRectangle> oldSet = this.mRectangles;
		for (MRectangle mRectangle : oldSet) {
			this.optimalMove(mRectangle, 1,this.gridStep);
			this.optimalMove(mRectangle, 2,this.gridStep);
		}
		
	}
	private void optimalMove(MRectangle m, int direction, int step) {
	
		if (step==0) {
			return;
		}
		int tempX = (int)m.getX();
		int tempY = (int)m.getY();
		switch (direction) {
		case 1:{
			
			
			if(this.fit(m, tempX-step, tempY, false)) {
				
				m.setLocation(tempX-step, tempY);
				this.optimalMove(m, direction, step);
			}else {
				
				this.optimalMove(m, direction, step/2);
			}
			break;
		}
		case 2:{
			if(this.fit(m, tempX, tempY-step, false)) {
				
				m.setLocation(tempX, tempY-step);
				this.optimalMove(m, direction, step);
			}else {
				
				this.optimalMove(m, direction, step/2);
			}
			break;
		}
			
		default:
			break;
		}

	}
	
	
	
	
	/*
	 * check if this box can insert a rectangle in definite location with or without rotation
	 */
	public boolean fit(MRectangle m, int x, int y, boolean rotated) {
		MRectangle clonedM;
		if(rotated) {
			clonedM = m.rotate();
		}else {
			clonedM = m.clone();
		}
		clonedM.setLocation(x, y);
		if(!this.contains(clonedM)) {
			
			return false;
		}
		for (MRectangle internM : this.mRectangles) {
		    if(internM.intersects(clonedM) && !m.equals(internM)) {
		    	
		    	return false;
		    }
		}
		return true;
	}
	
	public int getRandomRectangleIndexForMove(){
		double thisArea = this.width * this.height;
		double total = 0;
		for (MRectangle m : this.mRectangles) 
		{
			total += thisArea- m.getArea();
		}
		double random = total*Math.random();
		int index = 0;
		for (MRectangle m : this.mRectangles) 
		{
			random -= thisArea- m.getArea();
			if(random<=0) {
				return index;
			}
			index++;
		}
		return 0;
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
		return coveredArea/(this.boxLength*this.boxLength);
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
