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

	public static int getSmoothDegree() {
		return SMOOTH_DEGREE;
	}

	public static int getVertical() {
		return VERTICAL;
	}

	public static int getHorizontal() {
		return HORIZONTAL;
	}

	public int getGridStep() {
		return gridStep;
	}

	public int getMaxRange() {
		return maxRange;
	}

	/**
	 * TODO: comment me
	 */
	public static final int SMOOTH_DEGREE = 1;

	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;
	private final int boxLength;

	/**
	 * TODO: comment me
	 */
	private int gridStep;

	/**
	 * TODO: comment me
	 */
	private int maxRange;

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
	 * solution
	 */
	private MRectangle removedRect;

	private ArrayList<MRectangle> mRectangles;
	private double freeArea = -1;
	private double fillArea = -1;
	private double fillRate = -1;
	private double overlapArea = 0;

	public MBox(int boxLength, ArrayList<MRectangle> mRectangles) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = mRectangles;
		resetGridStep();
		this.maxRange = this.boxLength / this.gridStep;
		this.id = UUID.randomUUID().toString();
	}

	public MBox(int boxLength, ArrayList<MRectangle> mRectangles, String id) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = mRectangles;
		resetGridStep();
		this.maxRange = this.boxLength / this.gridStep;
		this.id = id;
	}

	public MBox(int boxLength) {
		super.setRect(0, 0, boxLength, boxLength);
		this.boxLength = boxLength;
		this.mRectangles = new ArrayList<MRectangle>();
		resetGridStep();
		this.maxRange = this.boxLength / this.gridStep;
		this.id = UUID.randomUUID().toString();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getBoxLength() {
		return boxLength;
	}

	private void resetGridStep() {
		this.gridStep = this.boxLength / SMOOTH_DEGREE;
		if (this.gridStep == 0)
			this.gridStep = 1;
	}
	/**
	 * Automatically insert the given rectangle into this box at an optimal
	 * position.
	 * 
	 * @param rect
	 *            The given rectangle
	 * @return the inserted rectangle (clone of the given rectangle) if
	 *         inserted, null otherwise
	 */
	public MRectangle optimalInsert(MRectangle rect) {

		if (this.getFreeArea() < rect.getArea())
			return null;

		// Try to insert at different positions
		// Along horizontal axis
		for (int i = 0; i < maxRange; i++) {
			// Along vertical axis
			for (int j = 0; j < maxRange; j++) {
				// without rotating
				MRectangle insertedRect = this.insert(rect, i * gridStep, j * gridStep,
						false);
				if (insertedRect != null)
					return insertedRect;
				// with rotating
				insertedRect = this.insert(rect, i * gridStep, j * gridStep, true);
				if (insertedRect != null)
					return insertedRect;
			}
		}
		return null;
	}

	/**
	 * Insert a clone of the given rectangle at the given location. After
	 * inserting successfully at the given location, optimize by pushing the
	 * rectangle to the edges.
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

		rect.setLocation(x, y);
		if (!this.contains(rect))
			return null;

		// Check if no rect currently in this box intersect the given rect
		// For this check overlapping is not permitted, hence intersection
		// method is used
		for (MRectangle internalRect : this.mRectangles)
			if (!internalRect.intersection(rect).isEmpty())
				return null;

		// The given rectangle can be inserted !

		// Update grid step
		// if(rect.getMinSize() < gridStep*SMOOTH_DEGREE) // TODO <- This is not
		// necessary?
		this.gridStep = rect.getMinSize() / SMOOTH_DEGREE;
		if (this.gridStep == 0)
			this.gridStep = 1;

		for (int i = 0; i <= 2; i++) {
			this.push(rect, VERTICAL, this.gridStep);
			this.push(rect, HORIZONTAL, this.gridStep);
		}

		// After pushing, compute filled, free and total overlapped area
		if (MRectangle.isOverlapPermitted()) {
			// In case overlap is permitted
			HashMap<MRectangle, Rectangle> intersektions = new HashMap<>();
			for (MRectangle r : this.mRectangles) {
				if (!rect.equals(r) && !rect.intersects(r)
						&& !rect.intersection(r).isEmpty())
					// If there exists valid overlapping
					intersektions.put(r, rect.intersection(r));
				else if (rect.intersects(r))
					throw new RuntimeException(
							"The to be inserted rectangle overlaps invalidly other rectangle(s)!");
			}

			// No 3-layer-overlapping is allowed
			// TODO this check is for safety, if other parts are correct, this
			// can be removed
			ArrayList<Rectangle> interzektions = new ArrayList<>();
			interzektions.addAll(intersektions.keySet());
			if (interzektions.size() >= 2) {
				for (int i = 0; i < interzektions.size(); i++)
					for (int j = i + 1; j < interzektions.size(); j++)
						if (interzektions.get(i).intersects(interzektions.get(j)))
							throw new RuntimeException(
									"3-layer-overlapping by insertion!");
			}

			// Update the intersection list of the rectangles
			double diff = rect.updateIntersections(intersektions);
			this.overlapArea += diff;
			this.fillArea += (rect.getArea() - diff);
			this.freeArea = Math.pow(this.boxLength, 2) - this.fillArea;
		} else {
			// No Overlapping
			this.freeArea = this.getFreeArea() - rect.getArea();
			this.fillArea = this.getFillArea() + rect.getArea();
		}

		// Insert the given rectangle into this Box
		this.mRectangles.add(rect);
		resetGridStep();
		return rect;
	}

	/**
	 * Try to push each rectangle in this box in different directions to
	 * optimize the placement of the rectangles (to eliminate "holes"). Note:
	 * This should be called after removing a rectangle from this box.
	 * 
	 */
	public void optimalSort() {
		if (MRectangle.isOverlapPermitted() && MRectangle.getOverlapRate() > 0)
			return;
		for (MRectangle mRectangle : this.mRectangles) {
			this.push(mRectangle, VERTICAL, this.gridStep);
			this.push(mRectangle, HORIZONTAL, this.gridStep);
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
				if (this.fit(rect, tempX + stepLength, tempY, false)) {
					// Pushing further by 1 step is OK
					rect.setLocation(tempX + stepLength, tempY);
					this.push(rect, direction, stepLength);
				} else
					// Next step does not fit, reduce step length and continue
					this.push(rect, direction, stepLength / 2);
				break;
			}
			case HORIZONTAL : {
				if (this.fit(rect, tempX, tempY + stepLength, false)) {
					// Pushing further by 1 step is OK
					rect.setLocation(tempX, tempY + stepLength);
					this.push(rect, direction, stepLength);
				} else
					// Next step does not fit, reduce step length and continue
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
	 * @param r
	 *            The given rectangle
	 * @param x
	 *            x-coordinate of the inserting position to be checked
	 * @param y
	 *            y-coordinate of the inserting position to be checked
	 * @param rotated
	 *            If the rectangle is rotated before checking
	 * @return True if fit, False otherwise
	 */
	public boolean fit(MRectangle r, int x, int y, boolean rotated) {
		MRectangle clonedR;
		if (rotated)
			clonedR = r.rotate();
		else
			clonedR = r.clone();

		clonedR.setLocation(x, y);

		// Correctness: the given rectangle must be inside the box
		if (!this.contains(clonedR))
			return false;

		return clonedR.checkPlaceable(this.mRectangles);
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
		if (this.freeArea == -1)
			this.freeArea = this.boxLength * this.boxLength - this.getFillArea();
		return this.freeArea;
	}

	/**
	 * Get the total area that is filled in this box
	 * 
	 * @return The filled area
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
	 * 
	 * @return The fill rate
	 */
	public double getFillRate() {
		if (this.fillRate == -1)
			this.fillRate = this.getFillArea() / (this.boxLength * this.boxLength);
		return this.fillRate;
	}

	public double getOverlapArea() {
		return this.overlapArea;
	}

	/**
	 * Remove the given rectangle from this box
	 * 
	 * @param rect
	 */
	public void removeRect(MRectangle rect) {
		if (MRectangle.isOverlapPermitted()) {
			// Case overlapping is permitted
			this.mRectangles.remove(rect);
			double diff = 0;
			for (MRectangle intersectedRect : rect.getIntersections().keySet())
				diff += intersectedRect.removeIntersection(rect,
						rect.getIntersections().get(intersectedRect));
			this.overlapArea += diff;
			this.fillArea -= (rect.getArea() + diff);
			this.freeArea = Math.pow(this.boxLength, 2) - this.fillArea;
		} else {
			this.mRectangles.remove(rect);
			this.fillArea = this.getFillArea() - rect.getArea();
			this.freeArea = this.getFreeArea() + rect.getArea();
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
		MBox clone = new MBox(this.boxLength, clonedRectangles, this.id);
		return clone;
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
