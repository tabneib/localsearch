package de.nhd.localsearch.algos;

import de.nhd.localsearch.neighborhood.GeometryBasedNeighborhood;
import de.nhd.localsearch.neighborhood.Neighborhood;
import de.nhd.localsearch.neighborhood.RuleBasedNeighborhood;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solution.MSolution;
import de.nhd.localsearch.solution.Solution;

/**
 * Class represents the basic local search algorithm.
 *
 */
public class LocalSearch extends NeighborhoodBasedAlgo {

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

	public LocalSearch(OptProblem optProblem, String neighborhood) {
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
		if (MRectangle.isOverlapPermitted())
			MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		long startTime = System.currentTimeMillis();

		Neighborhood neighborhood = this.getCurrentNeighborhood();
		while (neighborhood.hasNext() && attempt < MAX_SEARCHING_ATTEMPTS) {
			countStep++;
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				MSolution.increaseRound();
				this.currentSolution = neighbor;
				neighborhood.onCurrentSolutionChange(neighbor);
				attempt = 0;
			}
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
		Neighborhood neighborhood = this.getCurrentNeighborhood();
		while (neighborhood.hasNext() && attempt < MAX_SEARCHING_ATTEMPTS) {
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				neighborhood.onCurrentSolutionChange(neighbor);
				break;
			} else
				attempt++;
		}
		if (!neighborhood.hasNext() || attempt >= MAX_SEARCHING_ATTEMPTS) {
			// No more neighbor
			this.isFinished = true;
		}
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

	private Neighborhood getCurrentNeighborhood() {
		switch (this.neighborhood) {
			case NeighborhoodBasedAlgo.NEIGHBORHOOD_GEO :
				return (GeometryBasedNeighborhood) this.currentSolution.getNeighborhood();
			case NeighborhoodBasedAlgo.NEIGHBORHOOD_PERM :
				return (RuleBasedNeighborhood) this.currentSolution.getNeighborhood();
			default :
				throw new RuntimeException("Unknown neighborhood: " + this.neighborhood);
		}
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

	@Override
	public Solution getStartSolution() {
		return this.startSolution;
	}
}