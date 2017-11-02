package de.tud.nhd.optalgos.controller.localsearch;

import de.tud.nhd.optalgos.controller.Neighborhood;
import de.tud.nhd.optalgos.controller.OptAlgo;
import de.tud.nhd.optalgos.model.OptProblem;
import de.tud.nhd.optalgos.model.Solution;

public class LocalSearch implements OptAlgo {

	OptProblem optProblem;
	Neighborhood neighborhood;
	private Solution currentSolution;
	private boolean terminated = false;
	
	
	public LocalSearch (
			OptProblem optProblem, Neighborhood neighborhood, Solution startSolution) {
		this.optProblem = optProblem;
		this.neighborhood = neighborhood;
		this.currentSolution = startSolution;
	}

	/**
	 * Run the algorithm 
	 */
	public void run() {
		while (!terminated) {
			Solution betterSolution = neighborhood.findBetterNbor(
					currentSolution, optProblem.getDirection());
			if (betterSolution != null)
				currentSolution = betterSolution;
			else 
				terminated = true;
		}
	}
	

	@Override
	public Solution getOptimum() {
		if (terminated)
			return currentSolution;
		else
			return null;
	}
}
