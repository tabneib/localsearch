package de.tud.optalgos.model;

import java.util.ArrayList;

import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

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

	/**
	 * Initial (dummy) solution which we include here for convenience. In this
	 * solution we place one rectangle into one box. That means the number of
	 * boxes and rectangles in this initial solution are equal.
	 */
	private MSolution initSolution;

	public MOptProblem(String direction, int boxLength, ArrayList<MRectangle> rectangles,
			ArrayList<MBox> boxes) {
		super(direction);
		this.boxLength = boxLength;
		this.rechtangles = rectangles;
		this.initSolution = new GeometryBasedSolution(this, boxes);
		for (MRectangle r : rectangles) 
			this.rectArea += r.getArea();
	}

	public MSolution getInitSolution() {
		return this.initSolution;
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
