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
		int sourceBoxIndex = neighbor.getRandomBoxIdx();
		int destinationBoxIndex = neighbor.getRandomBoxIdx();
		while (sourceBoxIndex == destinationBoxIndex
				|| neighbor.getBoxes().get(sourceBoxIndex).isEmptyBox()) {
			sourceBoxIndex = neighbor.getRandomBoxIdx();
			destinationBoxIndex = neighbor.getRandomBoxIdx();
		}

		// Pick random rectangle
		MBox sourceBox = neighbor.getBoxes().get(sourceBoxIndex);
		MRectangle randomRect = sourceBox.getRandomRect();
		MBox destinationBox = neighbor.getBoxes().get(destinationBoxIndex);
		MRectangle insertedRect = destinationBox.optimalInsert(randomRect);
		if (insertedRect == null)
			return null;

		if (this.isTabooMode()) {
			MFeature removedFeature = new GeometryBasedFeature(randomRect, sourceBox);
			MFeature insertedFeature = new GeometryBasedFeature(insertedRect,
					destinationBox);
			if (!this.checkInsertable(insertedFeature)
					|| !this.checkRemovable(removedFeature))
				return null;
			else {
				neighbor.addRemovedFeature(removedFeature);
				neighbor.addInsertedFeature(insertedFeature);
			}
		} 
		sourceBox.removeRect(randomRect);
		if (!sourceBox.isEmptyBox())
			sourceBox.optimalSort();
		neighbor.revalidateObjective();
		insertedRect.setRepositioned();
		sourceBox.setRepositionSrc();
		sourceBox.setRemovedRect(randomRect);
		destinationBox.setRepositionDest();
		this.iteratedNeighbors++;
		return neighbor;
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
