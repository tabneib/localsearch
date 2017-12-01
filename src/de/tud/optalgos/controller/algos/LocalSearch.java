package de.tud.optalgos.controller.algos;

import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

public class LocalSearch extends NeighborhoodBased {

	//maximal number of unsuccessful attempt at searching for better neighbor
	public static final int ATTEMPTS =10;

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
		int attempt = 0;
		while (neighborhood.hasNext() && attempt <ATTEMPTS) {
			Solution neighbor = neighborhood.next(); 
			if (neighbor.isBetterThan(this.currentSolution)) {
				System.out.println("New solution is better");
				this.currentSolution = neighbor;
				neighborhood.onCurrentSolutionChange(neighbor);
				attempt = 0;
			}
			attempt++;
			
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
