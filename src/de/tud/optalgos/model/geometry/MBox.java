package de.tud.optalgos.model.geometry;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class representing a rectangles contained in the boxes
 *
 */
public class MBox extends Rectangle implements Cloneable{
	
	private static final long serialVersionUID = 1L;
	public static final int SMOOTH_DEGREE = 1;
	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;
	private final int boxLength;
	private int gridStep;
	private int maxRange;
	private ArrayList<MRectangle> mRectangles;
	private double freeArea = -1;
	private double fillArea = -1;
	private double fillRate = -1;
	
	
	public MBox(int boxLength, ArrayList<MRectangle> mRectangles) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = mRectangles;
		this.gridStep = this.boxLength/SMOOTH_DEGREE;
		this.maxRange = this.boxLength / this.gridStep;
	}
	
	public MBox(int boxLength, ArrayList<MRectangle> mRectangles, int gridStep) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = mRectangles;
		this.gridStep = gridStep;
		this.maxRange = this.boxLength / this.gridStep;
	}
	
	public MBox(int boxLength) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = new ArrayList<MRectangle>();
		this.gridStep = this.boxLength/SMOOTH_DEGREE;
		this.maxRange = this.boxLength / this.gridStep;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getBoxLength() {
		return boxLength;
	}
	
	/**
	 * Insert the given rectangle into this box at an optimal position.
	 * @param m	The given rectangle
	 * @return	True if inserted, False otherwise
	 */
	public boolean optimalInsert(MRectangle m) {
		
		if(this.getFreeArea() < m.getArea()) 
			return false;
		
		// Try to insert with/without rotating at different position
		for (int i = 0; i < maxRange; i++){
			for (int j = 0; j < maxRange; j++) {
				if(this.insert(m, i*gridStep, j*gridStep, false)) 
					return true;
				else if(this.insert(m, i*gridStep, j*gridStep, true)) 
						return true;
			}
		}
		return false;
	}
	
	/**
	 * Insert at the given location with or without rotation
	 * @param rect
	 * @param x
	 * @param y
	 * @param rotated
	 * @return
	 */
	public boolean insert(MRectangle rect, int x, int y, boolean rotated) {
		//check overlapping
		if(rotated) 
			rect = rect.rotate();
		else 
			rect = rect.clone();
		
		rect.setLocation(x, y);
		if(!this.contains(rect)) 
			return false;
		
		for (MRectangle internalRect : this.mRectangles) 
		    if(internalRect.intersects(rect)) 
		    	return false;

		// Update grid step
		if(rect.getMinSize() < gridStep*SMOOTH_DEGREE) 
			this.gridStep = rect.getMinSize()/SMOOTH_DEGREE;
		if(this.gridStep == 0) 
			this.gridStep = 1;
		
		//optimization: move to edge (move)		
		if (new Random().nextBoolean()){
			this.push(rect, VERTICAL, this.gridStep);
			this.push(rect, HORIZONTAL, this.gridStep);
			this.push(rect, VERTICAL, this.gridStep);
			this.push(rect, HORIZONTAL, this.gridStep);
		}
		else {
			this.push(rect, HORIZONTAL, this.gridStep);
			this.push(rect, VERTICAL, this.gridStep);
			this.push(rect, HORIZONTAL, this.gridStep);
			this.push(rect, VERTICAL, this.gridStep);
		}

		//insert this Rectangle into Box
		this.mRectangles.add(rect);
		this.freeArea = this.getFreeArea() - rect.getArea();
		this.fillArea = this.getFillArea() + rect.getArea();
		return true;
	}
	
	/**
	 * TODO comment me
	 */
	public void optimalSort() {
		for (MRectangle mRectangle : this.mRectangles) {
			this.push(mRectangle, VERTICAL, this.gridStep);
			this.push(mRectangle, HORIZONTAL, this.gridStep);
		}
	}
	
	/**
	 * Push the given rectangle step by step in the given direction with the given initial
	 * step length until the step length is reduced to 0. The rectangle must always fit 
	 * the current state of this box. 
	 * 
	 * @param m				The rectangle to move
	 * @param direction		The direction to move the rectangle
	 * @param stepLength	The length of the moving step 
	 */
	private void push(MRectangle m, int direction, int stepLength) {
	
		if (stepLength == 0) 
			return;
		int tempX = (int) m.getX();
		int tempY = (int) m.getY();
		
		switch (direction) {
		case VERTICAL:{
			if(this.fit(m, tempX+stepLength, tempY, false)) {
				m.setLocation(tempX+stepLength, tempY);
				this.push(m, direction, stepLength);
			}
			else 
				this.push(m, direction, stepLength/2);
			break;
		}
		case HORIZONTAL:{
			if(this.fit(m, tempX, tempY+stepLength, false)) {
				m.setLocation(tempX, tempY+stepLength);
				this.push(m, direction, stepLength);
			}
			else 
				this.push(m, direction, stepLength/2);
			break;
		}
		default:
			throw new RuntimeException("Unknown moving direction: " + direction);
		}
	}
	
	/**
	 * Check if the given rectangle can be inserted into this box at the given position
	 * 
	 * @param m	The given rectangle
	 * @param x	x-coordinate of the inserting position to be checked
	 * @param y	y-coordinate of the inserting position to be checked
	 * @param rotated	If the rectangle is rotated before checking
	 * @return	True if fit, False otherwise
	 */
	public boolean fit(MRectangle m, int x, int y, boolean rotated) {
		MRectangle clonedM;
		if(rotated) 
			clonedM = m.rotate();
		else 
			clonedM = m.clone();
		clonedM.setLocation(x, y);
		
		// Correctness: the given rectangle must be inside the box
		if(!this.contains(clonedM)) 
			return false;
		
		for (MRectangle internalM : this.mRectangles) 
		    if(!m.equals(internalM) && internalM.intersects(clonedM)) 
		    	return false;
		return true;
	}
	
	/**
	 * Select a rectangle from this box. The probability that a rectangle is selected is
	 * proportional to its area.
	 * 
	 * @return	The index of the selected rectangle
	 */
	public int getRandomRectProportionally(){
		double thisArea = this.width * this.height;
		double random = this.getFillArea()*Math.random();
		int index = 0;
		for (MRectangle m : this.mRectangles){
			random -= thisArea - m.getArea();
			if(random<=0) 
				return index;
			index++;
		}
		return 0;
	}

	/**
	 * Get the total area that is still free in this box
	 * @return	The free area
	 */
	public double getFreeArea() {
		if (this.freeArea == -1) 
			this.freeArea = this.boxLength*this.boxLength - this.getFillArea(); 
		return this.freeArea;
	}
	
	/**
	 * Get the total area that is filled in this box
	 * @return	The filled area
	 */
	public double getFillArea() {
		if (this.fillArea == -1) {
			this.fillArea = 0;
			for (MRectangle internalM : this.mRectangles) 
				this.fillArea += internalM.getArea();
		}
		return this.fillArea;
	}
	
	/**
	 * Get the rate of filled area over the total area of this box 
	 * @return	The fill rate
	 */
	public double getFillRate() {
		if (this.fillRate == -1)
			this.fillRate = this.getFillArea() / (this.boxLength*this.boxLength);
		return  this.fillRate;
	}

	/**
	 * Remove the given rectangle from this box
	 * @param rect
	 */
	public void removeRect(MRectangle rect) {
		this.mRectangles.remove(rect);
		this.fillArea = this.getFillArea() - rect.getArea();
		this.freeArea = this.getFreeArea() + rect.getArea();
	}
	
	/**
	 * Get all the rectangles currently contained in this box
	 * @return	The HashSet of all rectangles
	 */
	public ArrayList<MRectangle> getMRectangles() {
		return mRectangles;
	}
	
	@Override
	public String toString() {
		return "((" + getLocation().getX() + ", " + getLocation().getY() + 
				"), " + this.boxLength + ")";
	}
	
	@Override
	public MBox clone(){
		ArrayList<MRectangle> clonedRectangles = new ArrayList<MRectangle>();
		for (MRectangle mRectangle : this.mRectangles) {
			clonedRectangles.add(mRectangle.clone());
		}
		MBox newBox = new MBox(this.boxLength,clonedRectangles,this.gridStep);
		return newBox;
	};
}
