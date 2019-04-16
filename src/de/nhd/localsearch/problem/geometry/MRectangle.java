package de.nhd.localsearch.problem.geometry;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.w3c.dom.css.Rect;

/**
 * Class representing a rectangle to be placed into a box.
 *
 */
public class MRectangle extends Rectangle implements Comparable<MRectangle> {

	private static final long serialVersionUID = 1L;

	private final String id;
	private int boxLength;
	private MBox box;
	private final int maxSize;
	private final int minSize;

	/**
	 * If this rectangle is repositioned to generate the corresponding solution
	 */
	private boolean repositioned = false;

	/**
	 * If overlapping of rectangles is permitted
	 */
	private static boolean overlapMode = false;

	/**
	 * The maximal percentage of permitted overlapping area. This is the
	 * starting value of the reduction of overlapping percentage.
	 */
	public static final double MAX_OVERLAP_RATE = 1;

	/**
	 * The minimal percentage of permitted overlapping area. This is the lower
	 * bound for the reduction of overlapping percentage.
	 */
	public static final double MIN_OVERLAP_RATE = 0;

	/**
	 * The current percentage of permitted overlapping area.
	 * 
	 */
	private static double overlapRate;

	/**
	 * The total area of this rectangle that overlap other rectangles
	 */
	private double overlapArea = 0;

	/**
	 * Map of all intersections of this rectangle to the respective rectangles.
	 * This intersections must not overlap with each other. Keys are IDs of
	 * MRectangle.
	 */
	private HashMap<String, Rectangle> intersections;

	public MRectangle(int x, int y, int width, int height, int boxLength) {
		super.setRect(x, y, width, height);
		this.boxLength = boxLength;
		this.maxSize = Math.max(width, height);
		this.minSize = Math.min(width, height);
		this.id = UUID.randomUUID().toString();
		this.intersections = new HashMap<>();
	}

	public MRectangle(int width, int height, int boxLength) {
		super.setRect(-1, -1, width, height);
		this.boxLength = boxLength;
		this.maxSize = Math.max(width, height);
		this.minSize = Math.min(width, height);
		this.id = UUID.randomUUID().toString();
		this.intersections = new HashMap<>();
	}

	/**
	 * Constructor for cloning this MRectangle
	 * 
	 * @param width
	 * @param height
	 * @param boxLength
	 * @param id
	 */
	private MRectangle(int x, int y, int width, int height, int boxLength, String id,
			HashMap<String, Rectangle> intersections) {
		super.setRect(x, y, width, height);
		this.boxLength = boxLength;
		this.maxSize = Math.max(width, height);
		this.minSize = Math.min(width, height);
		this.id = id;
		// ISSUE: cyclic clone!!!! The keys of intersections still reference to
		// original
		// MRectangles which in turn belong to the original solution!
		// If we try to clone all the keys, this will trigger an infinite
		// cloning cycle
		// => stack overflow...
		this.intersections = new HashMap<>(intersections);
	}

	/**
	 * Check whether this MRectangle overlap the given MRectangle but
	 * overlapping is not allowed, or the overlapping area is invalid in case
	 * overlapping is allowed
	 * 
	 * @param other
	 *            the given MRectangle to check against
	 * @return true if invalidly overlap, otherwise false
	 */
	public boolean invalidlyOverlap(MRectangle other, ArrayList<Rectangle> currentIntersections) {
		if (!MRectangle.overlapMode || overlapRate <= 0)
			return super.intersects(other);
		else {
			if (!super.intersects(other))
				return false;
			else {
				double currentOverlapArea = 0;
				if (currentIntersections != null)
					for (Rectangle rect : currentIntersections)
						currentOverlapArea += rect.getWidth() * rect.getHeight();
				Rectangle intersection = this.intersection(other);
				double intersectionArea = intersection.getWidth()
						* intersection.getHeight();
				if (Math.max((intersectionArea + currentOverlapArea) / this.getArea(),
						(intersectionArea + other.getOverlapArea()) / other.getArea()) <= overlapRate)
					return false;
				else
					return true;
			}
		}
	}

	/**
	 * Check if it is possible to place this rectangle into the set of the given
	 * existing rectangles. In case overlapping is permitted, no
	 * 3-layer-overlapping is allowed.
	 * 
	 * @param givenRects
	 * @return
	 */
	public boolean checkPlaceable(ArrayList<MRectangle> givenRects) {
		if (givenRects.contains(this))
			throw new RuntimeException(
					"The given MRectangle set contains this MRectangle");
		if (MRectangle.overlapMode) {
			ArrayList<Rectangle> intersections = new ArrayList<>();
			for (MRectangle rect : givenRects)
				if (this.invalidlyOverlap(rect, intersections))
					return false;
				else if (!this.intersection(rect).isEmpty())
					intersections.add(this.intersection(rect));

			// No 3-layer Overlapping
			if (intersections.size() >= 2)
				for (int i = 0; i < intersections.size(); i++)
					for (int j = i + 1; j < intersections.size(); j++)
						if (intersections.get(i).intersects(intersections.get(j)))
							return false;
		} else
			for (MRectangle r : givenRects)
				if (this.invalidlyOverlap(r, null))
					return false;
		return true;
	}

	/**
	 * Add the given intersection with the given rectangle and update
	 * overlapping area accordingly. <br>
	 * If an intersection with the given MRectangle already exists, a runtime
	 * exception is thrown.
	 * 
	 * @param other
	 * @return the intersection between two MRectangles, null if not intersected
	 */
	public Rectangle addIntersectionWith(MRectangle other) {
		if (this.intersections.containsKey(other.getId()))
			throw new RuntimeException(
					"An intersection with the given MRectangle is already present.");
		Rectangle intersection = this.intersection(other);
		if (intersection.isEmpty())
			return null;
		this.intersections.put(other.getId(), intersection);
		this.overlapArea += intersection.getWidth() * intersection.getHeight();
		return intersection;
	}

	/**
	 * Remove the given intersection with the given rectangle and update
	 * overlapping area accordingly. <br>
	 * If no intersection with the given MRectangle is present, a runtime
	 * exception is thrown.
	 * 
	 * @param other
	 * @param intersection
	 * @return the difference of total overlapping area
	 */
	public Rectangle removeIntersectionWith(MRectangle other) {
		if (!this.intersections.containsKey(other.getId()))
			throw new RuntimeException(
					" No intersection with the given MRectangle is present");
		Rectangle intersection = this.intersections.remove(other.getId());
		this.overlapArea -= intersection.getWidth() * intersection.getHeight();
		return intersection;
	}

	public void removeAllIntersections() {
		this.intersections = new HashMap<>();
		this.overlapArea = 0;
	}

	public HashMap<String, Rectangle> getIntersections() {
		return this.intersections;
	}

	/**
	 * Return a new MRectangle which is the rotation of this MRectangle
	 * 
	 * @return the rotated MRectangle (clone of this rectangle)
	 */
	public MRectangle rotate() {
		MRectangle rotatedM = this.clone();
		rotatedM.setSize(this.height, this.width);
		return rotatedM;
	}

	public static void setOverlapMode(boolean overlap) {
		MRectangle.overlapMode = overlap;
		if (overlap)
			MRectangle.overlapRate = MAX_OVERLAP_RATE;
	}

	public static boolean isOverlapPermitted() {
		return MRectangle.overlapMode;
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

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return ((int) this.getWidth()) + "" + ((int) this.getHeight()) + "-"
				+ this.id.substring(0, 2);
	};

	@Override
	public MRectangle clone() {
		return new MRectangle(this.x, this.y, this.width, this.height, boxLength, this.id,
				this.intersections);
	}

	public boolean isRepositioned() {
		return repositioned;
	}

	public void setRepositioned() {
		this.repositioned = true;
	};

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MRectangle other = (MRectangle) obj;
		if (this.getId().equals(other.getId()))
			return true;
		else
			return false;
	}

	@Override
	public int compareTo(MRectangle other) {
		if (this.getArea() > other.getArea())
			return 1;
		else if (this.getArea() < other.getArea())
			return -1;
		else
			return 0;
	}
}
