package de.nhd.localsearch.algorithms;

import de.nhd.localsearch.neighborhoods.Neighborhood;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.MSolution;
import de.nhd.localsearch.solutions.Solution;

/**
 * Class representing the basic local search algorithm.
 *
 */
public class LocalSearch extends NeighborhoodBasedAlgo {

	public LocalSearch(OptProblem optProblem, String neighborhood) {
		super(optProblem, neighborhood);
	}

	@Override
	public void run() {
		if (this.isFinished())
			throw new RuntimeException("Algorithm already finished. Reset before rerun.");

		// uh huh ? TODO: why not in this class locally
		MSolution.resetRound();

		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		this.startTimer();
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			((MSolution) this.currentSolution).removeEmptyBoxes(0);
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				MSolution.increaseRound();
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			}
		}
		this.stopTimer();
		this.setFinished();
		System.out.println("[+] Running time: " + this.getRunningTime());
	}

	@Override
	public void runStep() {
		if (this.isFinished())
			throw new RuntimeException("Algorithm already terminated!");
		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		((MSolution) this.currentSolution).removeEmptyBoxes(0);
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				neighbor.setIndex(this.currentSolution.getIndex() + 1);
				this.currentSolution = neighbor;
				break;
			}
		}
		if (!neighborhood.hasNext())
			this.setFinished();
	}

	@Override
	public Solution getCurrentSolution() {
		return this.currentSolution;
	}
}
