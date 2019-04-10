package de.nhd.localsearch.algorithms;

import de.nhd.localsearch.neighborhoods.Neighborhood;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.solutions.MSolution;
import de.nhd.localsearch.solutions.Solution;

/**
 * Class representing the basic local search algorithm.
 *
 */
public class LocalSearch extends NeighborhoodBasedAlgo {

	/**
	 * Running time of the algorithm
	 */
	private long runningTime = -1;

	private boolean isFinished = false;

	public LocalSearch(OptProblem optProblem, String neighborhood) {
		super(optProblem, neighborhood);
	}

	@Override
	public void run() {
		if (this.isFinished)
			throw new RuntimeException("Algorithm already finished. Reset before rerun.");

		// uh huh ? TODO: why not in this class locally
		MSolution.resetRound();

		int countStep = 0;
		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		long startTime = System.currentTimeMillis();

		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			((MSolution) this.currentSolution).removeEmptyBoxes(true);
			countStep++;
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				MSolution.increaseRound();
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			} 
		}
		runningTime = System.currentTimeMillis() - startTime;
		this.isFinished = true;
		System.out.println("[+] Total Round:  " + countStep);
		System.out.println("[+] Running time: " + runningTime);
	}

	@Override
	public void runStep() {
		if (this.isFinished)
			throw new RuntimeException("Algorithm already terminated!");

		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		((MSolution) this.currentSolution).removeEmptyBoxes(true);
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				break;
			} 
		}
		if (!neighborhood.hasNext())
			this.isFinished = true;
	}

	@Override
	public long getRunningTime() {
		return runningTime;
	}

	@Override
	public Solution getCurrentSolution() {
		return this.currentSolution;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
