package de.tud.optalgos.model;

/**
 * Abstract class that defines the basic functionalities of a solution for a
 * optimization problem
 *
 */
public abstract class Solution implements Comparable<Solution>, Cloneable {

	/**
	 * The corresponding optimization problem
	 */
	OptProblem optProblem;

	public Solution(OptProblem optProblem) {
		this.optProblem = optProblem;
	}

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
			return optProblem.getDirection().equals(OptProblem.Direction.MAXIMIZING) ? -1
					: 1;
		else if (this.getObjective() > other.getObjective())
			return optProblem.getDirection().equals(OptProblem.Direction.MAXIMIZING) ? 1
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
		return this.optProblem;
	}

}
