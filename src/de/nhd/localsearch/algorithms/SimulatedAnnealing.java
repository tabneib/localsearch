package de.nhd.localsearch.algorithms;

import de.nhd.localsearch.neighborhoods.Neighborhood;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.solutions.MSolution;
import de.nhd.localsearch.solutions.Solution;

/**
 * Class representing the simulated annealing algorithm.
 *
 */
public class SimulatedAnnealing extends NeighborhoodBasedAlgo {

	/**
	 * Maximal number of consecutive unsuccessful attempts at searching for
	 * better neighbor
	 */
	public static final int MAX_SEARCHING_ATTEMPTS = 100;

	/**
	 * Running time of the algorithm
	 */
	private long runningTime = -1;

	private boolean isFinished = false;

	public SimulatedAnnealing(OptProblem optProblem, String neighborhood) {
		super(optProblem, neighborhood);
	}

	@Override
	public void run() {
		if (this.isFinished)
			throw new RuntimeException("Algorithm already finished. Reset before rerun.");

		// uh huh ? TODO: why not in this class locally
		MSolution.resetRound();

		int attempt = 0;
		int countStep = 0;
		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		long startTime = System.currentTimeMillis();

		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext() && attempt < MAX_SEARCHING_ATTEMPTS) {
			((MSolution) this.currentSolution).removeEmptyBoxes();
			countStep++;
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				MSolution.increaseRound();
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
				attempt = 0;
			} else
				attempt++;
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

		int attempt = 0;
		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		((MSolution) this.currentSolution).removeEmptyBoxes();
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext() && attempt < MAX_SEARCHING_ATTEMPTS) {
			//System.out.println("Moved Rect: " + neighborhood.getM);
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				break;
			} else
				attempt++;
		}
		if (!neighborhood.hasNext() || attempt >= MAX_SEARCHING_ATTEMPTS)
			// No more neighbor
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

	@Override
	public void reset() {
		this.runningTime = -1;
		this.isFinished = false;
		// this.neighborhood.reset();
		try {
			this.currentSolution = (Solution) this.startSolution.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
