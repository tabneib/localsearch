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

	private static final double[] TEMPERATURES = {100, 90, 80, 70, 60, 50, 40, 30, 20, 10,
			1};
	//private static final int[] STEP_LENGTHS = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100,
	//		200};
	private static final int[] STEP_LENGTHS = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20};
	
	/**
	 * Maximal number of consecutive unsuccessful attempts at searching for
	 * better neighbor
	 */
	public static final int MAX_SEARCHING_ATTEMPTS = 100;

	/**
	 * Total running time of the algorithm
	 */
	private long runningTime = -1;
	private boolean isFinished = false;

	private int currentTempIdx;
	private int currentStepIdx;

	public SimulatedAnnealing(OptProblem optProblem, String neighborhood) {
		super(optProblem, neighborhood);
		if (SimulatedAnnealing.TEMPERATURES.length != SimulatedAnnealing.STEP_LENGTHS.length)
			throw new RuntimeException(
					"Temperature list and step length list have different lengths");
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
			((MSolution) this.currentSolution).removeEmptyBoxes(false);
			countStep++;
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				MSolution.increaseRound();
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			} else if (this.biasedCoinFlip(this.currentSolution, neighbor)) {
				MSolution.increaseRound(); // ????
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			}
		}
		runningTime = System.currentTimeMillis() - startTime;
		this.isFinished = true;
		((MSolution) this.currentSolution).removeEmptyBoxes(true);
		System.out.println("[+] Total Round:  " + countStep);
		System.out.println("[+] Running time: " + runningTime);
	}

	@Override
	public void runStep() {
		if (this.isFinished)
			throw new RuntimeException("Algorithm already terminated!");
		// if (MRectangle.isOverlapPermitted())
		// MRectangle.setOverlapRate(MRectangle.MAX_OVERLAP_RATE);
		((MSolution) this.currentSolution).removeEmptyBoxes(false);
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				break;
			} else if (this.biasedCoinFlip(this.currentSolution, neighbor)) {
				this.currentSolution = neighbor;
				this.currentSolution.setWorseThanPrevious();
				break;
			}
		}
		if (!neighborhood.hasNext()) {
			this.isFinished = true;
			((MSolution) this.currentSolution).removeEmptyBoxes(true);
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

	/**
	 * Make a biased coin flip whose probabilities depend on the difference
	 * between the objective values of the given solutions and the current
	 * temperature
	 * 
	 * @param s1
	 * @param s2
	 * @return true if the second solution is chosen, otherwise false
	 */
	private boolean biasedCoinFlip(Solution s1, Solution s2) {
		double temp = this.getTemp();
		if (temp < 0)
			return false;
		double yesProb = Math.exp(-1 * s1.getObjDiff(s2) / temp);
		return (Math.random() < yesProb);
	}

	private double getTemp() {
		while (this.currentTempIdx < SimulatedAnnealing.TEMPERATURES.length
				&& this.currentStepIdx >= SimulatedAnnealing.STEP_LENGTHS[this.currentTempIdx]) {
			this.currentStepIdx = 0;
			this.currentTempIdx++;
		}

		if (this.currentTempIdx >= SimulatedAnnealing.TEMPERATURES.length)
			return -1;
		this.currentStepIdx++;
		return SimulatedAnnealing.TEMPERATURES[currentTempIdx];
	}
}
