package de.tud.optalgos.controller.algos;

import de.tud.optalgos.controller.neighborhood.Neighborhood;
import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

/**
 * Class represents the basic local search algorithm.
 *
 */
public class LocalSearch extends NeighborhoodBasedAlgo {

	/**
	 * Maximal number of consecutive unsuccessful attempts at searching for
	 * better neighbor
	 */
	public static final int MAX_SEARCHING_ATTEMPTS = 50;

	/**
	 * Running time of the algorithm
	 */
	private long runningTime = -1;
	
	public static int countStep  = 0;

	public LocalSearch(OptProblem optProblem, Neighborhood neighborhood,
			Solution startSolution) {
		super(optProblem, neighborhood, startSolution);
		countStep  = 0;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		int attempt = 0;
		while (neighborhood.hasNext() && attempt < MAX_SEARCHING_ATTEMPTS) {
			countStep++;
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				neighborhood.onCurrentSolutionChange(neighbor);
				attempt = 0;
				
			}
			attempt++;
		}
		runningTime = System.currentTimeMillis() - startTime;
		System.out.println("Step: "+countStep);
	}

	@Override
	public long getRunningTime() {
		return runningTime;
	}

	@Override
	public Solution getOptimum() {
		if (runningTime != -1)
			return currentSolution;
		else
			throw new RuntimeException("The Algorithm has not yet terminated!");
	}
}
