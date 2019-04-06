package problem.geometry;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representing a rectangle to be placed into a box.
 *
 */
public class MRectangle extends Rectangle {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * If overlapping of rectangles is permitted
	 */
	private static boolean overlap = false;
	
	/**
	 * The current percentage of permitted overlapping area.
	 * 
	 */
	private static double overlapRate;

	/**
	 * The maximal percentage of permitted overlapping area.
	 * This is the starting value of the reduction of overlapping percentage.
	 * This should not be equal to 1 because if so, two overlapped rectangles of the same
	 * size will be equal to each other This could mess up the program.
	 */
	public static final double MAX_OVERLAP_RATE = 0.9;

	/**
	 * The minimal percentage of permitted overlapping area.
	 * This is the lower bound for the reduction of overlapping percentage.
	 */
	public static final double MIN_OVERLAP_RATE = 0;
	
	
	/**
	 * The total area of this rectangle that overlap other rectangles
	 */
	private double overlapArea = 0;
	
	/**
	 * Map of all intersections of this rectangle to the respective rectangles.
	 * This intersections are not overlapped with each other.
	 */
	private HashMap<MRectangle, Rectangle> intersections = new HashMap<>();
	
	private int boxLength;
	private MBox box;
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
		if (!overlap || overlapRate <= 0)
			return super.intersects(r);
		else{
			// Case overlapping is permitted 
			if (!super.intersects(r))
				return false;
			else {
				Rectangle intersection = intersection(r);
				if (intersection.getWidth() * intersection.getHeight() / 
						Math.max(this.getArea(), ((MRectangle) r).getArea()) <= overlapRate) 
					return false;
				else
					return true;
			}
		}
	}
	
	/**
	 * Check if it is possible to place this rectangle into the set of the given existing
	 * rectangles. 
	 * In case overlapping is permitted, no 3-layer-overlapping is allowed.
	 * Note that the given list of rectangles might already include this rectangle.
	 * @param existingRects
	 * @return
	 */
	public boolean checkPlaceable(ArrayList<MRectangle> existingRects) {
		if (overlap) {
			ArrayList<Rectangle> intersektions = new ArrayList<>();
			for (MRectangle r : existingRects) {
				if (!this.equals(r) && !this.intersects(r) && !this.intersection(r).isEmpty())
					// If there exists valid overlapping
					intersektions.add(this.intersection(r));
				else if (this.intersects(r))
					return false;
			}
			
			// No 3-layer-overlapping is allowed
			if (intersektions.size() >= 2) {
				for (int i = 0; i < intersektions.size(); i++)
					for (int j = i + 1; j < intersektions.size(); j++) 
						if (intersektions.get(i).intersects(intersektions.get(j)))
								return false;
			}
			// No overlapping at all or only one overlap (and therefore 2-layer-overlapping)
			return true;
		}
		else {
			for (MRectangle r : existingRects) 
			    if(!this.equals(r) && this.intersects(r)) 
			    	return false;
			return true;
		}
	}

	/**
	 * Add the given intersection with the given rectangle.
	 * If the given intersection already exists, a runtime exception is thrown.
	 * @param rect
	 * @param intersection
	 * @return the difference of total overlapping area
	 */
	public double addIntersection(MRectangle rect, Rectangle intersection) {
		if (this.intersections.containsKey(rect) &&
				this.intersections.get(rect).equals(intersection))
			throw new RuntimeException("Invalid insertion of already existing intersection!");
		
		this.intersections.put(rect, intersection);
		return intersection.getHeight() * intersection.getWidth();
	}

	/**
	 * Remove the given intersection with the given rectangle
	 * If the given intersection does not exist, a runtime exception is thrown.
	 * @param rect
	 * @param intersection
	 * @return the difference of total overlapping area
	 */
	public double removeIntersection(MRectangle rect, Rectangle intersection) {
		if (!this.intersections.containsKey(rect) ||
				!this.intersections.get(rect).equals(intersection))
			throw new RuntimeException("Invalid removal of not existing intersection!");
		
		this.intersections.remove(rect);
		return -1 * intersection.getHeight() * intersection.getWidth();
	}

	/**
	 * Replace the given intersection with the given rectangle
	 * If the given intersection does not exist, a runtime exception is thrown.
	 * @param rect
	 * @param intersection
	 * @return the difference of total overlapping area
	 */
	public double replaceIntersection(MRectangle rect, Rectangle intersection) {
		if (!this.intersections.containsKey(rect) ||
				!this.intersections.get(rect).equals(intersection))
			throw new RuntimeException("Invalid Replacement of not existing intersection!");
		
		this.intersections.put(rect, intersection);

		return intersection.getHeight() * intersection.getWidth() -
				this.intersections.get(rect).getHeight() *
				this.intersections.get(rect).getWidth();
	}
	
	/**
	 * Update the intersections list of this MRectangle with the given one.
	 * For each update case update the affected rectangle accordingly.
	 * @param newIntersections
	 * @return the difference of total overlapping area
	 */
	public double updateIntersections(HashMap<MRectangle, Rectangle> newIntersections) {
		if (newIntersections.isEmpty())
			return 0;
		
		HashMap<MRectangle, Rectangle> oldIntersections = new HashMap<>();
		oldIntersections.putAll(this.intersections);
		double diff = 0;
		
		// Case 1: New intersection added
		for (MRectangle newRect : newIntersections.keySet()) {
			if (!oldIntersections.containsKey(newRect)) {
				this.intersections.put(newRect, newIntersections.get(newRect));
				diff += newRect.addIntersection(this, newIntersections.get(newRect));
			}
		}
		
		// Case 2: Old intersection removed
		for (MRectangle oldRect : oldIntersections.keySet()) {
			if (!newIntersections.containsKey(oldRect)) {
				this.intersections.remove(oldRect);
				diff += oldRect.removeIntersection(this, oldIntersections.get(oldRect));
			}
		}
		
		// Case 3: Old intersection changed
		for (MRectangle oldRect : oldIntersections.keySet()) {
			if (newIntersections.containsKey(oldRect) &&
					!newIntersections.get(oldRect).equals(oldIntersections.get(oldRect))) {
				this.intersections.put(oldRect, newIntersections.get(oldRect));
				diff += oldRect.replaceIntersection(this, newIntersections.get(oldRect));
			}
		}
		return diff;
	}
	
	public HashMap<MRectangle, Rectangle> getIntersections(){
		return this.intersections;
	}
	
	/**
	 * Return a new MRectangle which is the rotation of this MRectangle
	 * @return the rotated MRectangle
	 */
	public MRectangle rotate() {
		MRectangle rotatedM = this.clone();
		rotatedM.setSize(this.height, this.width);
		return rotatedM;
	}
	
	public static void setOverlap(boolean ouverlap) {
		overlap = ouverlap;
		System.out.println("Overlap permitted: " + overlap);
	}
	
	public static boolean isOverlapPermitted() {
		return overlap;
	}

	public static double getOverlapRate() {
		return overlapRate;
	}
	
	public double getOverlapArea() {
		return this.overlapArea;
	}
	
	public static void setOverlapRate(double rate) {
		overlapRate = rate < MIN_OVERLAP_RATE ? MIN_OVERLAP_RATE : rate;
		overlapRate = rate < 0 ? 0 : rate;
		System.out.println("OverlapRate = " + overlapRate);
	}

	public MBox getBox() {
		return box;
	}

	public void setBox(MBox box) {
		this.box = box;
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
