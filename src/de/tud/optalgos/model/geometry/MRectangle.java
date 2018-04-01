package de.tud.optalgos.model.geometry;

import java.awt.Rectangle;

public class MRectangle extends Rectangle {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * If overlapping of rectangles is permitted
	 */
	private static boolean overlap = false;
	
	/**
	 * The percentage of permitted overlapping area
	 */
	private static double overlapRate = 1.0;
	
	private int boxLength;
	private MBox mBox;
	private final int maxSize;
	private final int minSize;
	
	
	public MRectangle(int x, int y,int width, int height, int boxLength) {
		super.setRect(x, y, width, height);
		this.boxLength = boxLength;
		this.maxSize = Math.max(width, height);
		this.minSize = Math.min(width, height);
	}
	
	public MRectangle(int width, int height, int boxLength) {
		super.setRect(-1, -1, width, height);
		this.boxLength = boxLength;
		this.maxSize = Math.max(width, height);
		this.minSize = Math.min(width, height);
	}


	@Override
	public boolean intersects(Rectangle r) {
		if (!overlap)
			return super.intersects(r);
		else{
			// Case overlapping is permitted 
			if (!super.intersects(r))
				return false;
			else {
				Rectangle intersection = super.intersection(r);
				if (intersection.getWidth() * intersection.getHeight() / 
						Math.max(this.getArea(), ((MRectangle) r).getArea()) <= overlapRate) 
					return false;
				else
					return true;
			}
		}
	}
	
	public static void setOverlap(boolean ouverlap) {
		overlap = ouverlap;
	}
	
	public static boolean getOverlap() {
		return overlap;
	}
	
	public static void setOverlapRate(double rate) {
		overlapRate = rate;
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
	
	public double getArea() {
		return this.getWidth() * this.getHeight();
	}
	
	public MRectangle rotate() {
		MRectangle rotatedM = this.clone();
		rotatedM.setSize(this.height, this.width);
		return rotatedM;
	}
	
	@Override
	public String toString(){
		return "((" + getLocation().getX() + ", " + getLocation().getY() + "), " + 
					"(" + getWidth() + ", " + getHeight() + "))";
	};

	@Override
	public MRectangle clone(){
		MRectangle cloneM = new MRectangle(this.width, this.height, boxLength);
		cloneM.setLocation(this.getLocation());
		return cloneM;
	};
}
