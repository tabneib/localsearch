package de.nhd.localsearch.neighborhoods;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.GeometryBasedFeature;
import de.nhd.localsearch.solutions.GeometryBasedSolution;
import de.nhd.localsearch.solutions.MFeature;
import de.nhd.localsearch.solutions.Solution;

/**
 * This class represents the geometry-based neighborhood.
 *
 */
public class GeometryBasedNeighborhood extends Neighborhood {

	/**
	 * Maximal number of attempts to reposition the rectangles between boxes for
	 * constructing a neighbor.
	 */
	public static final int MAX_REPOSITIONING_ATTEMPTS = 100;

	private int iteratedNeighbors;
	private boolean noMoreNeighbor = false;

	/**
	 * The next neighbor to be returned
	 */
	private GeometryBasedSolution nextNeighbor;

	public GeometryBasedNeighborhood(MOptProblem instance, GeometryBasedSolution owner) {
		super(instance, owner);
	}

	@Override
	public boolean hasNext() {
//		System.out.println("[GeoNeigh] hasNext()");
		if (this.noMoreNeighbor)
			return false;
		if (this.nextNeighbor == null)
			// already reset by next()
			this.searchNextNeighbor();
		if (this.nextNeighbor == null) {
			this.noMoreNeighbor = true;
			return false;
		}
		return true;
	}

	private void searchNextNeighbor() {
		int attempts = 0;
		this.nextNeighbor = (GeometryBasedSolution) this.makeRandomMove();
		while (this.nextNeighbor == null && attempts < MAX_REPOSITIONING_ATTEMPTS) {
			// System.out.println("attempts: " + attempts);
			this.nextNeighbor = (GeometryBasedSolution) this.makeRandomMove();
			attempts++;
		}
	}

	/**
	 * Attempt to find the next neighbor
	 * 
	 * @return the generated neighbor if found such one, otherwise null
	 */
	public Solution makeRandomMove() {
		if (((GeometryBasedSolution) this.owner).getBoxes().size() < 2
				|| this.iteratedNeighbors >= MAX_NEIGHBORS)
			return null;

		GeometryBasedSolution neighbor = ((GeometryBasedSolution) this.owner).clone();

		// Pick two random boxes
		int sourceBoxIndex = this.getRandomBoxIdx(neighbor);
		int destinationBoxIndex = this.getRandomBoxIdx(neighbor);
		while (sourceBoxIndex == destinationBoxIndex
				|| neighbor.getBoxes().get(sourceBoxIndex).isEmptyBox()) {
			sourceBoxIndex = this.getRandomBoxIdx(neighbor);
			destinationBoxIndex = this.getRandomBoxIdx(neighbor);
		}

		// Pick random rectangle
		MBox sourceBox = neighbor.getBoxes().get(sourceBoxIndex);
		MRectangle randomRect = sourceBox.getRandomRect();
//		System.out.println(
//				"---> Found randomRect with overlapArea: " + randomRect.getOverlapArea());
		MBox destinationBox = neighbor.getBoxes().get(destinationBoxIndex);
		// MRectangle insertedRect = destinationBox.optimalInsert(randomRect);
		if (!destinationBox.checkInsertable(randomRect))
			return null;

		if (this.isTabooMode()) {
			MFeature removedFeature = new GeometryBasedFeature(randomRect, sourceBox);
			MFeature insertedFeature = new GeometryBasedFeature(randomRect,
					destinationBox);
			if (!this.checkInsertable(insertedFeature)
					|| !this.checkRemovable(removedFeature))
				return null;
			else {
				neighbor.addRemovedFeature(removedFeature);
				neighbor.addInsertedFeature(insertedFeature);
			}
		}
//		System.out.println(
//				"[GeoNeigh] makeRandomMove() - found new Reposition. Going to remove from SOURCE BOX...");
		sourceBox.removeRect(randomRect);
//		System.out.println("removed.");
		if (!sourceBox.isEmptyBox())
			sourceBox.optimalSort();
		// neighbor.revalidateObjective();
		destinationBox.optimalInsert(randomRect).setRepositioned();
		sourceBox.setRepositionSrc();
		sourceBox.setRemovedRect(randomRect);
		destinationBox.setRepositionDest();
		this.iteratedNeighbors++;
		neighbor.revalidateTotalOverlapArea();
		return neighbor;
	}

	/**
	 * Randomly select a box in the given solution. The probability that a box
	 * is selected is proportional to its free area. <br>
	 * If overlap is permitted, overlap area is also taken into account.
	 * 
	 * TODO: adapt for Overlap Mode
	 * 
	 * @return index of the selected box
	 */
	private int getRandomBoxIdx(GeometryBasedSolution solution) {
		double totalFreeArea = 0;
		for (MBox mBox : solution.getBoxes())
			totalFreeArea += (mBox.getFreeArea() + mBox.getOverlapArea() * Math
					.pow(mBox.getOverlapArea(), MRectangle.getOverlapPenaltyRate()));
		double random = totalFreeArea * Math.random();
		int index = 0;
		for (MBox mBox : solution.getBoxes()) {
			random -= (mBox.getFreeArea() + mBox.getOverlapArea() * Math
					.pow(mBox.getOverlapArea(), MRectangle.getOverlapPenaltyRate()));
			if (random <= 0) {
				return index;
			}
			index++;
		}
		return solution.getBoxes().size() - 1;
	}

	@Override
	public Solution next() {
		if (this.hasNext()) {
			GeometryBasedSolution tmp = this.nextNeighbor;
			this.nextNeighbor = null;
			return tmp;
		} else
			return null;
	}
}
