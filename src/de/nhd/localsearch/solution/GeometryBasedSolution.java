package de.nhd.localsearch.solution;

import java.util.ArrayList;
import java.util.Random;

import de.nhd.localsearch.neighborhood.GeometryBasedNeighborhood;
import de.nhd.localsearch.neighborhood.Neighborhood;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

public class GeometryBasedSolution extends MSolution {

	/**
	 * Construct a dummy geometry based solution 
	 * @param problem
	 * @param boxes
	 */
	public GeometryBasedSolution(OptProblem problem, ArrayList<MBox> boxes) {
		super(problem, boxes);
		if (boxes == null) {
			this.problem = problem;
			this.boxes = new ArrayList<MBox>();
			MOptProblem mProblem = (MOptProblem) this.problem;
			for (MRectangle m : mProblem.getRechtangles()) {
				MBox mBox = new MBox(mProblem.getBoxLength());
				mBox.optimalInsert(m);
				this.boxes.add(mBox);
			}
		}
	}

	/**
	 * Randomly select a box in this solution. The probability that a box is
	 * selected is proportional to its free area.
	 * 
	 * TODO: adapt for the case
	 * 
	 * @return index of the selected box
	 */
	public int getRandomBoxProportionally() {
		double totalFreeArea = 0;
		for (MBox mBox : this.boxes)
			totalFreeArea += mBox.getFreeArea();
		double random = totalFreeArea * Math.random();
		int index = 0;
		for (MBox mBox : this.boxes) {
			random -= mBox.getFreeArea();
			if (random <= 0)
				return index;
			index++;
		}
		return this.boxes.size() - 1;
	}

	/**
	 * Randomly select a box in this solution.
	 * 
	 * @return index of the selected box
	 */
	public int getRandomBox() {
		Random r = new Random();
		return r.nextInt(this.boxes.size()) - 1;
	}

	/**
	 * Remove a box from this solution
	 * 
	 * @param index
	 *            of the box
	 */
	public void removeBox(int index) {
		this.boxes.remove(index);
	}

	@Override
	public GeometryBasedSolution clone() {
		ArrayList<MBox> clonedBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.boxes)
			clonedBoxes.add(mBox.clone());
		return new GeometryBasedSolution(this.problem, clonedBoxes);
	}

	@Override
	public Neighborhood getNeighborhood() {
		return new GeometryBasedNeighborhood((MOptProblem) this.problem, this);
	}
}
