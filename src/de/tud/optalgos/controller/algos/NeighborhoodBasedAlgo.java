package de.tud.optalgos.controller.algos;

import de.tud.optalgos.controller.neighborhood.Neighborhood;
import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

/**
 * Abstract class represents the class of neighborhood-based optimization algorithms
 *
 */
public abstract class NeighborhoodBasedAlgo implements IOptAlgo {

	/**
	 * The optimization problem instance this algorithm has to solve
	 */
	protected final OptProblem optProblem;
	
	/**
	 * The neighborhood relation used by this algorithm
	 */
	protected final Neighborhood neighborhood;
	
	// TODO correct ? Should a neighborhood-based optimization algorithm hold a
	// current solution regarding the theory?
	protected Solution currentSolution;

	public NeighborhoodBasedAlgo(OptProblem optProblem, Neighborhood neighborhood,
			Solution startSolution) {
		this.optProblem = optProblem;
		this.neighborhood = neighborhood;
		this.currentSolution = startSolution;
	}
}
