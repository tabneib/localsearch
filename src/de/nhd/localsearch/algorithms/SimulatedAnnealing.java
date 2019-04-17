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
	// private static final int[] STEP_LENGTHS = {10, 20, 30, 40, 50, 60, 70,
	// 80, 90, 100,
	// 200};
	private static final int[] STEP_LENGTHS = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20};

	private static final int EMPTY_BOX_REMOVING_AGGRESSIVELESSNESS = 5;

	/**
	 * Maximal number of consecutive unsuccessful attempts at searching for
	 * better neighbor
	 */
	public static final int MAX_SEARCHING_ATTEMPTS = 100;

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
		if (this.isFinished())
			throw new RuntimeException("Algorithm already finished. Reset before rerun.");

		this.startTimer();

		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			((MSolution) this.currentSolution)
					.removeEmptyBoxes(EMPTY_BOX_REMOVING_AGGRESSIVELESSNESS);
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			} else if (this.biasedCoinFlip(this.currentSolution, neighbor)) {
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			}
			System.out.println("Not better: " + this.currentSolution.getObjective()
			+ " < " + neighbor.getObjective());
			this.increaseTotalRounds();

		}
		this.stopTimer();
		this.setFinished();
		((MSolution) this.currentSolution).removeEmptyBoxes(0);
		System.out.println("[+] Total Round:  " + this.getTotalRounds());
		System.out.println("[+] Running time: " + this.getRunningTime());
	}

	@Override
	public void runStep() {
		if (this.isFinished())
			throw new RuntimeException("Algorithm already terminated!");
		((MSolution) this.currentSolution)
				.removeEmptyBoxes(EMPTY_BOX_REMOVING_AGGRESSIVELESSNESS);
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext() ) {
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				neighbor.setIndex(this.currentSolution.getIndex() + 1);
				this.currentSolution = neighbor;
				break;
			} else if (this.biasedCoinFlip(this.currentSolution, neighbor)) {
				neighbor.setIndex(this.currentSolution.getIndex() + 1);
				this.currentSolution = neighbor;
				this.currentSolution.setWorseThanPrevious();
				break;
			}
			this.increaseTotalRounds();
		}
		if (!neighborhood.hasNext()) {
			this.setFinished();
			((MSolution) this.currentSolution).removeEmptyBoxes(0);
		}
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
