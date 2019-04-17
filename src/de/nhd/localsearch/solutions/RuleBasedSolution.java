package de.nhd.localsearch.solutions;

import java.util.ArrayList;

import de.nhd.localsearch.neighborhoods.Neighborhood;
import de.nhd.localsearch.neighborhoods.RuleBasedNeighborhood;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

/**
 * Class representing a rule-based solution. The solution depends on a
 * permutation of the rectangles and a method that inserting the rectangles in
 * the permutation into boxes.
 *
 */
public class RuleBasedSolution extends MSolution {

	private ArrayList<MRectangle> permutation;

	public RuleBasedSolution(OptProblem problem, ArrayList<MRectangle> initPermutation) {
		super(problem, null);
		if (initPermutation == null)
			this.genDefaultPermutation();
		else
			this.permutation = initPermutation;
		this.revalidate();
	}

	private void genDefaultPermutation() {
		this.permutation = new ArrayList<>();
		this.permutation.addAll(((MOptProblem) this.problem).getRechtangles());
	}

	/**
	 * Re-insert the rectangles in the permutation into boxes. This will replace
	 * the old boxes with the new ones.
	 *
	 */
	public void revalidate() {
		this.removeBoxes();
		MBox box = new MBox(((MOptProblem) this.problem).getBoxLength());
		for (MRectangle r : permutation) {
			while (box.optimalInsert(r) == null) {
				this.addBox(box);
				box = new MBox(((MOptProblem) this.problem).getBoxLength());
			}
		}
		this.addBox(box);
	}

	private RuleBasedSolution setPermutation(ArrayList<MRectangle> permutation) {
		this.permutation = permutation;
		return this;
	}

	@Override
	public Neighborhood getNeighborhood() {
		return new RuleBasedNeighborhood((MOptProblem) this.problem, this);
	}

	@Override
	public RuleBasedSolution clone() {
		ArrayList<MRectangle> clonedPermutation = new ArrayList<>();
		clonedPermutation.addAll(permutation);
		return new RuleBasedSolution(this.problem, null)
				.setPermutation(clonedPermutation);
	}
}
