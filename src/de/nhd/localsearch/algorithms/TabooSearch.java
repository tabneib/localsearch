package de.nhd.localsearch.algorithms;

import java.util.ArrayList;

import de.nhd.localsearch.neighborhoods.Neighborhood;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.MFeature;
import de.nhd.localsearch.solutions.MSolution;
import de.nhd.localsearch.solutions.Solution;

/**
 * Class representing the taboo search algorithm.
 *
 */
public class TabooSearch extends NeighborhoodBasedAlgo {

	private static final int MAX_STUCK_STEPS = 20;

	/**
	 * Number of neighbors that are scanned for the best among them
	 */
	private static final int NEIGHBORHOOD_SEARCH_SPACE_SIZE = 10;

	/**
	 * Ratio between taboo list length and the total amount of rectangles
	 */
	private static final double TABOO_LIST_LENGTH_RATIO = 0.1;

	private int tabooListLength;
	private ArrayList<MFeature> recentInsertedFeatures;
	private ArrayList<MFeature> recentRemovedFeatures;
	private int stuckSteps;

	public TabooSearch(OptProblem optProblem, String neighborhood) {
		super(optProblem, neighborhood);
		this.recentInsertedFeatures = new ArrayList<>();
		this.recentRemovedFeatures = new ArrayList<>();
		this.tabooListLength = (int) (((MOptProblem) this.problem).getRechtangles().size()
				* TABOO_LIST_LENGTH_RATIO);
	}

	@Override
	public void run() {
		if (this.isFinished())
			throw new RuntimeException("Algorithm already finished. Reset before rerun.");

		this.startTimer();

		Solution bestNeighbor = this.getBestNeighbor();
		while (bestNeighbor != null && (!MRectangle.checkOverlapNotEffective()
				|| this.stuckSteps <= MAX_STUCK_STEPS)) {
			// We only consider stuck step if overlap is no more effective
			if (MRectangle.checkOverlapNotEffective())
				if (this.currentSolution.getObjDiff(bestNeighbor) < 1)
					this.stuckSteps++;
				else
					this.stuckSteps = 0;
			this.currentSolution = bestNeighbor;
			bestNeighbor = this.getBestNeighbor();
		}
		this.convergeToNearestLocalOptimum();
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
				.removeEmptyBoxes(this.getEmptyBoxRemovingAggressivelessness());

		Solution bestNeighbor = this.getBestNeighbor();
		if (bestNeighbor == null || stuckSteps > MAX_STUCK_STEPS) {
			this.convergeToNearestLocalOptimum();
			this.setFinished();
			((MSolution) this.currentSolution).removeEmptyBoxes(0);
			return;
		}

		if (this.currentSolution.getObjDiff(bestNeighbor) < 1)
			this.stuckSteps++;
		else
			this.stuckSteps = 0;

		bestNeighbor.setIndex(this.currentSolution.getIndex() + 1);
		if (this.neighborhood.equals(NEIGHBORHOOD_GEO)) {
			((MSolution) bestNeighbor).setTabooRectangles(
					((MSolution) this.currentSolution).getTabooRectangles());
		}
		if (this.currentSolution.isBetterThan(bestNeighbor)) {
			this.currentSolution = bestNeighbor;
			this.currentSolution.setWorseThanPrevious();
		} else
			this.currentSolution = bestNeighbor;
		this.updateTaboos(bestNeighbor);
		if (!this.currentSolution.getNeighborhood().hasNext()) {
			this.setFinished();
			((MSolution) this.currentSolution).removeEmptyBoxes(0);
		}
	}

	/**
	 * Use local search at the end to converge to the nearest local optimum
	 */
	private void convergeToNearestLocalOptimum() {
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		while (neighborhood.hasNext()) {
			((MSolution) this.currentSolution).removeEmptyBoxes(0);
			Solution neighbor = neighborhood.next();
			if (neighbor.isBetterThan(this.currentSolution)) {
				this.increaseTotalRounds();
				neighbor.setIndex(this.currentSolution.getIndex() + 1);
				this.currentSolution = neighbor;
				neighborhood = neighbor.getNeighborhood();
			}
		}
	}

	/**
	 * Search for the best neighbor. (In our current approach there is always
	 * one such neighbor => (almost?) never return null => an additional
	 * termination criteria is needed)
	 * 
	 * @return the best neighbor or null if no more neighbor
	 */
	private Solution getBestNeighbor() {
		Neighborhood neighborhood = this.currentSolution.getNeighborhood();
		neighborhood.setTabooMode(this.recentInsertedFeatures,
				this.recentRemovedFeatures);
		Solution bestNeighbor = null;
		int count = 1;
		while (neighborhood.hasNext()) {
			if (count > NEIGHBORHOOD_SEARCH_SPACE_SIZE)
				break;
			if (bestNeighbor == null) {
				bestNeighbor = neighborhood.next();
				count++;
				continue;
			}
			Solution nextNeighbor = neighborhood.next();
			if (nextNeighbor.isBetterThan(bestNeighbor))
				bestNeighbor = nextNeighbor;
			count++;
			this.increaseTotalRounds();
		}
		return bestNeighbor;
	}

	private void updateTaboos(Solution newSolution) {
		for (MFeature insertedFeature : ((MSolution) newSolution).getInsertedFeatures()) {
			if (this.recentInsertedFeatures.contains(insertedFeature)) {
				// throw new RuntimeException("Taboo feature cannot be
				// inserted.");
				// System.out.println("Double inserted Taboo feature");
				continue;
			}
			if (this.recentInsertedFeatures.size() == this.tabooListLength){
				this.recentInsertedFeatures.remove(0);
			}
			this.recentInsertedFeatures.add(insertedFeature);
			if (this.neighborhood.equals(NEIGHBORHOOD_GEO)) {
				((MSolution) this.currentSolution).addTaboo(insertedFeature.getRect());
				if (this.recentInsertedFeatures.size() == this.tabooListLength) {
					((MSolution) this.currentSolution)
							.removeTaboo(this.recentInsertedFeatures.get(0).getRect());
				}
			}
		}
		for (MFeature removedFeature : ((MSolution) newSolution).getRemovedFeatures()) {
			if (this.recentRemovedFeatures.contains(removedFeature)) {
				// throw new RuntimeException("Taboo feature cannot be
				// removed.");
				// System.out.println("Double removed Taboo feature");
				continue;
			}
			if (this.recentRemovedFeatures.size() == this.tabooListLength) {
				this.recentRemovedFeatures.remove(0);
			}
			this.recentRemovedFeatures.add(removedFeature);
		}
	}
}
