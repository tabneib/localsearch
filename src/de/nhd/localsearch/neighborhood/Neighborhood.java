package de.nhd.localsearch.neighborhood;

import java.util.Iterator;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.solution.Solution;

/**
 * Abstract class that defines the basic functionalities of a neighborhood relation
 *
 */
public abstract class Neighborhood implements Iterator<Solution> {

	/**
	 * The instance of the optimization problem that this neighborhood corresponds to 
	 */
	private MOptProblem problem;

	/**
	 * The solution that this neighborhood corresponds to
	 */
	protected Solution currentSolution;

	public Neighborhood(MOptProblem problem, Solution currentSolution) {
		this.problem = problem;
		this.currentSolution = currentSolution;
	}

	/**
	 * Callback for updating the current solution. This should be triggered
	 * every time the optimization algorithm finds and updates its new current
	 * solution.
	 * 
	 * @param newSolution
	 */
	public abstract void onCurrentSolutionChange(Solution newSolution);

	protected OptProblem getProblem() {
		return problem;
	}

	protected Solution getCurrentSolution() {
		return this.currentSolution;
	}
	
	/*public void reset(){
		this.currentSolution = this.problem.getInitSolution();
	}*/
}
