package de.nhd.localsearch.algorithms;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.GeometryBasedSolution;
import de.nhd.localsearch.solutions.MSolution;
import de.nhd.localsearch.solutions.RuleBasedSolution;
import de.nhd.localsearch.solutions.Solution;

/**
 * Abstract class represents the class of neighborhood-based optimization
 * algorithms
 *
 */
public abstract class NeighborhoodBasedAlgo implements IOptAlgo {

	public static final String NEIGHBORHOOD_GEO = "GEOMETRY_BASED";
	public static final String NEIGHBORHOOD_PERM = "PERMUTATION";

	private static final int DEFAULT_EMPTY_BOX_REMOVING_AGGRESSIVELESSNESS = 10;

	/**
	 * The percentage amount to reduce the overlap rate in one reduction step.
	 */
	private static final double OVERLAP_RATE_REDUCTION_AMOUNT = 0.1;

	/**
	 * Number of algorithm rounds run before the next reduction of the overlap
	 * rate.
	 */
	private static final int OVERLAP_RATE_REDUCTION_STEP = 10;

	/**
	 * Number of rounds that this algorithm has run
	 */
	private int totalRounds = 0;

	/**
	 * Total running time of the algorithm
	 */
	private long startTime = -1;
	private long endTime = -1;
	private boolean isFinished = false;

	/**
	 * The optimization problem instance this algorithm has to solve
	 */
	protected final OptProblem problem;

	/**
	 * The neighborhood relation used by this algorithm
	 */
	protected final String neighborhood;

	protected Solution currentSolution;
	protected Solution startSolution;

	public NeighborhoodBasedAlgo(OptProblem optProblem, String neighborhood) {
		this.problem = optProblem;
		this.neighborhood = neighborhood;
		try {
			switch (this.neighborhood) {
				case NEIGHBORHOOD_GEO :
					this.startSolution = new GeometryBasedSolution(this.problem, null);
					break;
				case NEIGHBORHOOD_PERM :
					this.startSolution = new RuleBasedSolution(this.problem,
							((MOptProblem) this.problem).getRechtangles());
					break;
				default :
					throw new RuntimeException(
							"Unknown neighborhood: " + this.neighborhood);
			}
			this.currentSolution = (Solution) startSolution.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFinished() {
		return this.isFinished;
	};

	protected void setFinished() {
		this.isFinished = true;
	}

	@Override
	public long getRunningTime() {
		if (this.startTime == -1)
			throw new RuntimeException("Timer not yet started");
		if (this.endTime == -1)
			throw new RuntimeException("Timer not yet stopped");
		long runningTime = this.endTime - this.startTime;
		if (runningTime <= 0)
			throw new RuntimeException("Invalid running time: " + runningTime);
		return runningTime;
	}

	protected void startTimer() {
		if (this.startTime > -1)
			throw new RuntimeException("Timer already started");
		this.startTime = System.currentTimeMillis();
	}

	protected void stopTimer() {
		if (this.startTime == -1)
			throw new RuntimeException("Timer not yet started");
		this.endTime = System.currentTimeMillis();
	}

	public OptProblem getProblem() {
		return problem;
	}

	public int getTotalRounds() {
		return totalRounds;
	}
	
	@Override
	public Solution getCurrentSolution() {
		return this.currentSolution;
	}
	
	/**
	 * Increase the total executed rounds of the algorithm. This is used for the
	 * penalty of overlapping.
	 */
	public void increaseTotalRounds() {
		this.totalRounds++;
//		System.out.println("increaseTotalRounds()");
		if (MRectangle.isOverlapPermitted()
				&& MRectangle.getOverlapRate() > MRectangle.MIN_OVERLAP_RATE
				&& this.totalRounds % OVERLAP_RATE_REDUCTION_STEP == 0)
			MRectangle.setOverlapRate(
					MRectangle.getOverlapRate() - OVERLAP_RATE_REDUCTION_AMOUNT);
	}

	protected int getEmptyBoxRemovingAggressivelessness() {
		if (((MSolution) this.currentSolution).getBoxes()
				.size() >= DEFAULT_EMPTY_BOX_REMOVING_AGGRESSIVELESSNESS * 5)
			return DEFAULT_EMPTY_BOX_REMOVING_AGGRESSIVELESSNESS;
		else
			return (int) (this.currentSolution.getObjective() / 5);
	}
}
