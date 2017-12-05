package de.tud.optalgos.controller.neighborhood;

import java.util.Iterator;

import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

/**
 * Abstract class that defines the basic functionalities of a neighborhood relation
 *
 */
public abstract class Neighborhood implements Iterator<Solution> {

	/**
	 * The instance of the optimization problem that this
	 */
	private OptProblem instance;

	/**
	 * The solution that this neighborhood corresponds to.
	 */
	protected Solution currentSolution;

	public Neighborhood(OptProblem instance, Solution currentSolution) {
		this.instance = instance;
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

	protected OptProblem getInstance() {
		return instance;
	}

	protected Solution getCurrentSolution() {
		return this.currentSolution;
	}
}
