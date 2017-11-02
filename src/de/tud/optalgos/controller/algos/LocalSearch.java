package de.tud.optalgos.controller.algos;

import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

public class LocalSearch extends NeighborhoodBased {

	private boolean terminated = false;

	
	public LocalSearch (OptProblem optProblem, 
			Neighborhood neighborhood, Solution startSolution) {
		super(optProblem, neighborhood, startSolution);
	}
	

	/**
	 * Run the algorithm 
	 */
	public void run() {
		while (neighborhood.hasNext()) {
			Solution neighbor = neighborhood.next(); 
			if (neighbor.isBetterThan(currentSolution)) {
				currentSolution = neighbor;
				neighborhood.onCurrentSolutionChange(neighbor);
			}
		}
		terminated = true;
	}
	

	@Override
	public Solution getOptimum() {
		if (terminated)
			return currentSolution;
		else
			throw new RuntimeException("The Algorithm has not yet terminated!");
	}
}
