package de.nhd.localsearch.neighborhoods;

import java.util.Iterator;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.solutions.Solution;

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
