package de.nhd.localsearch.problem.geometry;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Class representing a box that contains rectangles
 *
 */
public class MBox extends Rectangle implements Cloneable, Comparable<MBox> {

	private static final long serialVersionUID = 1L;

	private final String id;
	static private final String EMPTY_BOX_ID = "EMPTY_BOX_ID";

	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;
	private final int boxLength;

	/**
	 * If this box is the source of the repositioning that generates the
	 * corresponding solution
	 */
	private boolean repositionSrc = false;

	/**
	 * If this box is the destination of the repositioning that generates the
	 * corresponding solution
	 */
	private boolean repositionDest = false;

	/**
	 * The rectangle, if any, that was removed to generate the corresponding
	 * (geometry-based) solution
	 */
	private MRectangle removedRect;

	private ArrayList<MRectangle> mRectangles;
	private HashMap<String, MRectangle> mRectangleMap;
	private double freeArea;
	private double fillArea;
	private double fillRate;
	private double overlapArea = 0;

	/**
	 * Construct a box from the given box length and MRectangles. <br>
	 * The given MRectangles must not intersect each other for correctness!
	 * 
	 * @param boxLength
	 * @param mRectangles
	 */
	public MBox(int boxLength, ArrayList<MRectangle> mRectangles) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.freeArea = this.boxLength * this.boxLength;
		this.mRectangles = mRectangles;
		for (MRectangle rect : mRectangles) {
			this.freeArea -= rect.getArea();
			this.fillArea += rect.getArea();
		}
		this.fillRate = this.fillArea / (this.boxLength * this.boxLength);
		this.syncMRectangleMap();
		this.id = UUID.randomUUID().toString();
	}

	public MBox(int boxLength, ArrayList<MRectangle> mRectangles, String id,
			double freeArea, double fillArea, double fillRate) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.freeArea = this.boxLength * this.boxLength;
		this.mRectangles = mRectangles;
		this.syncMRectangleMap();
		this.freeArea = freeArea;
		this.fillArea = fillArea;
		this.fillRate = fillRate;
		this.id = id;
	}

	public MBox(int boxLength) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.freeArea = this.boxLength * this.boxLength;
		this.mRectangles = new ArrayList<MRectangle>();
		this.syncMRectangleMap();
		this.id = UUID.randomUUID().toString();
	}

	public int getBoxLength() {
		return boxLength;
	}

	/**
	 * Automatically insert the given rectangle into this box at an optimal
	 * position. <br>
	 * Current approach: Iterate each position of this box and try to insert the
	 * rectangle at that position with insert method, stop immediately after the
	 * first feasible position.
	 * 
	 * @param rect
	 *            The given rectangle
	 * @return the inserted rectangle (clone of the given rectangle) if
	 *         inserted, null otherwise
	 */
	public MRectangle optimalInsert(MRectangle rect) {
		if (this.mRectangles.contains(rect))
			throw new RuntimeException(
					"The given MRectangle is already contained in this box");
		if ((!MRectangle.isOverlapPermitted() && this.getFreeArea() < rect.getArea()))
			return null;

		MRectangle insertedRect;
		// Along horizontal axis
		for (int i = 0; i < this.boxLength; i++) {
			// Along vertical axis
			for (int j = 0; j < this.boxLength; j++) {
				// without rotating
				if (i <= this.boxLength - rect.width
						&& j <= this.boxLength - rect.height) {
					insertedRect = this.insert(rect, i, j, false);
					if (insertedRect != null)
						return insertedRect;
				}
				// with rotating
				if (i <= this.boxLength - rect.height
						&& j <= this.boxLength - rect.width) {
					insertedRect = this.insert(rect, i, j, true);
					if (insertedRect != null)
						return insertedRect;
				}
			}
		}
		return null;
	}

	/**
	 * Insert a clone of the given rectangle at the given location. After
	 * inserting successfully at the given location, optimize by pushing the
	 * rectangle towards the edges.
	 * 
	 * @param rect
	 * @param x
	 * @param y
	 * @param rotated
	 * @return True if inserted, False otherwise
	 */
	public MRectangle insert(MRectangle rect, int x, int y, boolean rotated) {

		if (rotated)
			rect = rect.rotate();
		else
			rect = rect.clone();

		rect.removeAllIntersections();
		rect.setLocation(x, y);

		if (!this.contains(rect))
			return null;

		if (!rect.checkPlaceable(this.mRectangles))
			return null;

		for (int i = 0; i <= 2; i++) {
			this.push(rect, HORIZONTAL, this.boxLength / 2);
			this.push(rect, VERTICAL, this.boxLength / 2);
		}

		// After pushing, compute filled, free and total overlapped area
		if (MRectangle.isOverlapPermitted()) {
			for (MRectangle currentRect : this.mRectangles) {
				Rectangle intersection = rect.addIntersectionWith(currentRect);
				if (intersection != null) {
					currentRect.addIntersectionWith(rect);
					this.overlapArea += intersection.getWidth()
							* intersection.getHeight();
				}
			}
			this.fillArea += (rect.getArea() - rect.getOverlapArea());
			this.freeArea -= (rect.getArea() - rect.getOverlapArea());
			this.fillRate = this.fillArea / (this.boxLength * this.boxLength);
		} else {
			this.freeArea -= rect.getArea();
			this.fillArea += rect.getArea();
			this.fillRate = this.fillArea / (this.boxLength * this.boxLength);
		}
		// Insert the given rectangle into this Box
		this.addMRectangle(rect);
		return rect;
	}

	/**
	 * Try to push each rectangle in this box in different directions to
	 * optimize the placement of the rectangles (to eliminate "holes"). <br>
	 * Do nothing if overlapping between rectangles is possible. <br>
	 * This should be called after removing a rectangle from this box.
	 */
	public void optimalSort() {
		if (MRectangle.isOverlapPermitted() && MRectangle.getOverlapRate() > 0)
			return;
		for (MRectangle rect : this.mRectangles) {
			this.push(rect, HORIZONTAL, this.boxLength / 2);
			this.push(rect, VERTICAL, this.boxLength / 2);
		}
	}

	/**
	 * Push the given rectangle step by step in the given direction with the
	 * given initial step length until the step length is reduced to 0. The
	 * rectangle must always fit the current state of this box. The given
	 * rectangle might be already included in this block (case optimalSort).
	 * 
	 * @param rect
	 *            The rectangle to move
	 * @param direction
	 *            The direction to move the rectangle
	 * @param stepLength
	 *            The length of the moving step
	 */
	private void push(MRectangle rect, int direction, int stepLength) {

		if (stepLength <= 0)
			return;
		int tempX = (int) rect.getX();
		int tempY = (int) rect.getY();

		switch (direction) {
			case VERTICAL : {
				if (this.checkPlaceable(rect, tempX, tempY + stepLength)) {
					rect.setLocation(tempX, tempY + stepLength);
					this.push(rect, direction, stepLength);
				} else
					this.push(rect, direction, stepLength / 2);
				break;
			}
			case HORIZONTAL : {
				if (this.checkPlaceable(rect, tempX + stepLength, tempY)) {
					rect.setLocation(tempX + stepLength, tempY);
					this.push(rect, direction, stepLength);
				} else
					this.push(rect, direction, stepLength / 2);
				break;
			}
			default :
				throw new RuntimeException("Unknown moving direction: " + direction);
		}
	}

	/**
	 * Check if the given rectangle can be inserted into this box at the given
	 * position. Note that the given rectangle might be already included in this
	 * box (case {@link #optimalSort() optimalSort} is called).
	 * 
	 * @param rect
	 *            The given rectangle
	 * @param x
	 *            x-coordinate of the inserting position to be checked
	 * @param y
	 *            y-coordinate of the inserting position to be checked
	 * @return True if fit, False otherwise
	 */
	public boolean checkPlaceable(MRectangle rect, int x, int y) {
		MRectangle clone = rect.clone();
		clone.setLocation(x, y);
		if (!this.contains(clone))
			return false;
		if (this.mRectangles.contains(clone)) {
			ArrayList<MRectangle> clonedMRects = new ArrayList<>(this.mRectangles);
			clonedMRects.remove(clone);
			return clone.checkPlaceable(clonedMRects);
		}
		return clone.checkPlaceable(this.mRectangles);
	}

	/**
	 * Select a rectangle from this box. If overlapping is permitted, the
	 * probability that a rectangle is selected is proportional to its overlap
	 * area.
	 * 
	 * @return The index of the selected rectangle
	 */
	public MRectangle getRandomRect() {
		if (this.isEmptyBox())
			throw new RuntimeException("Cannot get random rect from an empty box");
		if (MRectangle.isOverlapPermitted()) {
			// Case overlapping is permitted
			if (this.mRectangles.size() > 0) {
				int randomPart = new Random().nextInt((int) this.getOverlapArea() + 1);
				for (MRectangle m : this.mRectangles) {
					randomPart -= m.getOverlapArea();
					if (randomPart <= 0)
						return m;
				}
				throw new RuntimeException(
						"Cannot get a random rectangle from this box!");
			}
			return null;
		} else
			return this.mRectangles.get(new Random().nextInt(this.mRectangles.size()));
	}

	/**
	 * Get the total area that is still free in this box
	 * 
	 * @return The free area
	 */
	public double getFreeArea() {
		return this.freeArea;
	}

	/**
	 * Get the total area that is filled in this box
	 * 
	 * @return The filled area
	 */
	public double getFillArea() {
		return this.fillArea;
	}

	/**
	 * Get the rate of filled area over the total area of this box
	 * 
	 * @return The fill rate
	 */
	public double getFillRate() {
		return this.fillRate;
	}

	public double getOverlapArea() {
		return this.overlapArea;
	}

	/**
	 * Remove the given rectangle from this box. <br>
	 * Box data is updated accordingly
	 * 
	 * @param rect
	 */
	public void removeRect(MRectangle rect) {
		if (MRectangle.isOverlapPermitted()) {
			this.overlapArea -= rect.getOverlapArea();
			this.fillArea -= (rect.getArea() - rect.getOverlapArea());
			this.freeArea += (rect.getArea() - rect.getOverlapArea());
			this.fillRate = this.fillArea / (this.boxLength * this.boxLength);
			for (String otherId : rect.getIntersections().keySet())
				this.getMRectById(otherId).removeIntersectionWith(rect);
			rect.removeAllIntersections();
			this.removeMRectangle(rect);
		} else {
			this.removeMRectangle(rect);
			this.fillArea = this.getFillArea() - rect.getArea();
			this.freeArea = this.getFreeArea() + rect.getArea();
			this.fillRate = this.fillArea / (this.boxLength * this.boxLength);
		}
	}

	public boolean isEmptyBox() {
		return this.mRectangles.size() <= 0;
	}

	/**
	 * Get all the rectangles currently contained in this box
	 * 
	 * @return The ArrayList of all rectangles
	 */
	public ArrayList<MRectangle> getMRectangles() {
		return mRectangles;
	}

	@Override
	public String toString() {
		return "((" + getLocation().getX() + ", " + getLocation().getY() + "), "
				+ this.boxLength + ")";
	}

	@Override
	public MBox clone() {
		ArrayList<MRectangle> clonedRectangles = new ArrayList<MRectangle>();
		for (MRectangle mRectangle : this.mRectangles) {
			clonedRectangles.add(mRectangle.clone());
		}
		MBox clone = new MBox(this.boxLength, clonedRectangles, this.id, this.freeArea,
				this.fillArea, this.fillRate);
		return clone;
	}

	private void syncMRectangleMap() {
		this.mRectangleMap = new HashMap<>();
		for (MRectangle rect : this.mRectangles)
			this.mRectangleMap.put(rect.getId(), rect);
	}

	private void addMRectangle(MRectangle rect) {
		this.mRectangles.add(rect);
		this.mRectangleMap.put(rect.getId(), rect);
	}

	private void removeMRectangle(MRectangle rect) {
		this.mRectangles.remove(rect);
		this.mRectangleMap.remove(rect.getId(), rect);
	}

	public MRectangle getMRectById(String id) {
		return this.mRectangleMap.get(id);
	}
	public boolean isRepositionSrc() {
		return repositionSrc;
	}

	public void setRepositionSrc() {
		this.repositionSrc = true;
	}

	public boolean isRepositionDest() {
		return repositionDest;
	}

	public void setRepositionDest() {
		this.repositionDest = true;
	};

	public MRectangle getRemovedRect() {
		return removedRect;
	}

	public void setRemovedRect(MRectangle removedRect) {
		this.removedRect = removedRect;
	}

	public String getId() {
		if (this.isEmptyBox())
			return EMPTY_BOX_ID;
		else
			return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mRectangles == null) ? 0 : mRectangles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBox other = (MBox) obj;
		if (this.getId().equals(other.getId()))
			return true;
		else
			return false;
	}

	@Override
	public int compareTo(MBox other) {
		if (this.getFillRate() > other.getFillRate())
			return 1;
		else if (this.getFillRate() < other.getFillRate())
			return -1;
		else
			return 0;
	}
}
