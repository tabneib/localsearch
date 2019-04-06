package de.nhd.localsearch.solution;

import de.nhd.localsearch.neighborhood.Neighborhood;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.OptProblem.Direction;

/**
 * Abstract class that defines the basic functionalities of a solution for a
 * optimization problem
 *
 */
public abstract class Solution implements Comparable<Solution>, Cloneable {

	/**
	 * The corresponding optimization problem
	 */
	OptProblem problem;

	public Solution(OptProblem optProblem) {
		this.problem = optProblem;
	}
	
	/**
	 * Provide the iterator over the set of all neighboring solutions of this
	 * solution
	 * 
	 * @return
	 */
	public abstract Neighborhood getNeighborhood();

	/**
	 * Check if this solution is better than the given one
	 * 
	 * @param other
	 *            The other solution to compare with
	 * @return true if better, otherwise false
	 */
	public boolean isBetterThan(Solution other) {
		return (this.compareTo(other) > 0);
	}

	@Override
	public int compareTo(Solution other) {
		if (this.getObjective() < other.getObjective())
			// worse than => smaller if Maximizing
			return problem.getDirection().equals(OptProblem.Direction.MAXIMIZING)
					? -1
					: 1;
		else if (this.getObjective() > other.getObjective())
			return problem.getDirection().equals(OptProblem.Direction.MAXIMIZING)
					? 1
					: -1;
		else
			return 0;
	}

	/**
	 * Return the objective value of this solution
	 * 
	 * @return The value
	 */
	public abstract double getObjective();

	public OptProblem getOptProblem() {
		return this.problem;
	}

	@Override
	public abstract Object clone() throws CloneNotSupportedException;

}