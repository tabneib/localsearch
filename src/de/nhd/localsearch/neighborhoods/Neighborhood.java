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
	 * The solution that holds this neighborhood
	 */
	protected Solution Owner;

	public Neighborhood(MOptProblem problem, Solution owner) {
		this.problem = problem;
		this.Owner = owner;
	}

	protected OptProblem getProblem() {
		return problem;
	}

	protected Solution getOwner() {
		return this.Owner;
	}
	
	/*public void reset(){
		this.currentSolution = this.problem.getInitSolution();
	}*/
}
