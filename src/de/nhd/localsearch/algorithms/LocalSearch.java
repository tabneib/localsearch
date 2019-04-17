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

	public LocalSearch(OptProblem optProblem, String neighborhood) {
		super(optProblem, neighborhood);
	}

	@Override
	public void run() {
		if (this.isFinished())
			throw new RuntimeException("Algorithm already finished. Reset before rerun.");

		this.startTimer();

		System.out.println("initial solution's objective value: "
				+ this.currentSolution.getObjective());
//		if (true)
//			throw new RuntimeException();

		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			((MSolution) this.currentSolution).removeEmptyBoxes(0);
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
				// System.out.println(
				// "------------------------- current solution UPDATED
				// -----------------------------");
			}
			System.out.println("Not better: " + this.currentSolution.getObjective()
					+ " < " + neighbor.getObjective());
			this.increaseTotalRounds();
		}
		this.stopTimer();
		this.setFinished();
		System.out.println("[+] Total Round:  " + this.getTotalRounds());
		System.out.println("[+] Running time: " + this.getRunningTime());
	}

	@Override
	public void runStep() {
		if (this.isFinished())
			throw new RuntimeException("Algorithm already terminated!");
		((MSolution) this.currentSolution).removeEmptyBoxes(0);
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				neighbor.setIndex(this.currentSolution.getIndex() + 1);
				this.currentSolution = neighbor;
				break;
			}
			this.increaseTotalRounds();
		}
		if (!neighborhood.hasNext())
			this.setFinished();
	}
}
