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
	 * Maximal number of neighbors to be iterated through. This tuning
	 * guarantees a termination of the algorithm.
	 */
	public static final int MAX_NEIGHBORS = 100;
	
	/**
	 * The instance of the optimization problem that this neighborhood corresponds to 
	 */
	private MOptProblem problem;

	/**
	 * The solution that holds this neighborhood
	 */
	protected Solution owner;
	
	public Neighborhood(MOptProblem problem, Solution owner) {
		this.problem = problem;
		this.owner = owner;
	}

	protected OptProblem getProblem() {
		return problem;
	}

	protected Solution getOwner() {
		return this.owner;
	}
	
	/*public void reset(){
		this.currentSolution = this.problem.getInitSolution();
	}*/
}
