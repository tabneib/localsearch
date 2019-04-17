package de.nhd.localsearch.solutions;

import java.util.ArrayList;

import de.nhd.localsearch.neighborhoods.GeometryBasedNeighborhood;
import de.nhd.localsearch.neighborhoods.Neighborhood;
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
			MOptProblem mProblem = (MOptProblem) this.problem;
			for (MRectangle m : mProblem.getRechtangles()) {
				MBox mBox = new MBox(mProblem.getBoxLength());
				mBox.optimalInsert(m);
				this.addBox(mBox);
			}
		}
	}

	@Override
	public GeometryBasedSolution clone() {
		ArrayList<MBox> clonedBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.getBoxes())
			clonedBoxes.add(mBox.clone());
		return new GeometryBasedSolution(this.problem, clonedBoxes);
	}

	@Override
	public Neighborhood getNeighborhood() {
		return new GeometryBasedNeighborhood((MOptProblem) this.problem, this);
	}
}
