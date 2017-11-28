package de.tud.optalgos.controller.algos;

import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

public class LocalSearch extends NeighborhoodBased {

	public static final int ATTEMPTS =1000;
	private boolean terminated = false;
	
	
	public LocalSearch (OptProblem optProblem, 
			Neighborhood neighborhood, Solution startSolution) {
		super(optProblem, neighborhood, startSolution);
	}
	

	/**
	 * Run the algorithm 
	 */
	public void run() {
		System.out.println("start searching");
		while (neighborhood.hasNext()) {
			System.out.println("try");
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
