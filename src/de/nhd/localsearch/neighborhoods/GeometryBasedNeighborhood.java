package de.nhd.localsearch.neighborhoods;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.GeometryBasedSolution;
import de.nhd.localsearch.solutions.Solution;

/**
 * This class represents the geometry-based neighborhood.
 *
 */
public class GeometryBasedNeighborhood extends Neighborhood {

	/**
	 * Maximal number of attempts to reposition the rectangles between boxes.
	 */
	public static final int MAX_REPOSITIONING_ATTEMPTS = 100;

	/**
	 * The next neighbor to be returned
	 */
	private GeometryBasedSolution nextNeighbor;

	public GeometryBasedNeighborhood(MOptProblem instance, GeometryBasedSolution owner) {
		super(instance, owner);
	}

	@Override
	public boolean hasNext() {
		this.searchNextNeighbor();
		return this.nextNeighbor != null;
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
	 * @return true if next neighbor solution found, otherwise false
	 */
	public Solution makeRandomMove() {
		if (((GeometryBasedSolution) this.Owner).getBoxes().size() < 2)
			return null;
		GeometryBasedSolution neighbor = ((GeometryBasedSolution) this.Owner).clone();
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

		// Insert this rectangle into the destination box
		MBox destinationBox = neighbor.getBoxes().get(destinationBoxIndex);
		// destinationBox.optimalSort(); <- No need to sort the destination box
		// due to optimal insert
		if (destinationBox.optimalInsert(randomRect.clone())) {
			sourceBox.removeRect(randomRect);
			if (sourceBox.isEmptyBox())
				neighbor.removeBox(sourceBoxIndex);
			else
				sourceBox.optimalSort();
		} else
			return null;
		neighbor.revalidateObjective();
		return neighbor;
	}

	@Override
	public Solution next() {
		if (this.nextNeighbor == null)
			this.searchNextNeighbor();
		return this.nextNeighbor;
	}
}
