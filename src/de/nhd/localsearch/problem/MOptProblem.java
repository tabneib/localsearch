package de.nhd.localsearch.problem;

import java.util.ArrayList;

import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

/**
 * Class represents the concrete optimization problem to be solved in this
 * project.
 *
 */
public class MOptProblem extends OptProblem {

	/**
	 * The rectangles to be placed into the boxes
	 */
	private ArrayList<MRectangle> rechtangles;

	/**
	 * The total area of the rectangles
	 */
	private double rectArea = 0;

	/**
	 * The edge length of the boxes
	 */
	private final int boxLength;

	public MOptProblem(String direction, int boxLength, ArrayList<MRectangle> rectangles,
			ArrayList<MBox> boxes) {
		super(direction);
		this.boxLength = boxLength;
		this.rechtangles = rectangles;
		for (MRectangle r : rectangles)
			this.rectArea += r.getArea();
	}

	public ArrayList<MRectangle> getRechtangles() {
		return rechtangles;
	}

	public int getBoxLength() {
		return boxLength;
	}

	public double getTotalRectArea() {
		return this.rectArea;
	}
}
