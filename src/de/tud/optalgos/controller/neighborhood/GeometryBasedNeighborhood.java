package de.tud.optalgos.controller.neighborhood;

import java.util.Random;

import de.tud.optalgos.model.GeometryBasedSolution;
import de.tud.optalgos.model.MOptProblem;
import de.tud.optalgos.model.Solution;
import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

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
	private GeometryBasedSolution nextNeighborSolution;

	/**
	 * Flag to determine whether process was executed
	 */
	private boolean findNext;

	public GeometryBasedNeighborhood(MOptProblem instance, GeometryBasedSolution currentSolution) {
		super(instance, currentSolution);
		this.nextNeighborSolution = null;
		this.findNext = false;
		
	}

	@Override
	public boolean hasNext() {
		this.findNext = true;
		this.nextNeighborSolution = null;

		// Attempt to find next solution
		int attempt = 0;
		while (attempt < MAX_REPOSITIONING_ATTEMPTS) {
			if (this.attempt()) {
				// new solution was found
				
				this.findNext = true;
				return true;
			}
			attempt++;
		}
		// if no solution was found
		return false;
	}

	/**
	 * TODO: Comment me
	 * 
	 * @return
	 */
	public boolean attempt() {
		GeometryBasedSolution newSolution = ((GeometryBasedSolution) this.currentSolution).clone();
		Random r = new Random();

		if (newSolution.getBoxes().size() < 2)
			return false;

		// pick two random boxes
		int sourceBoxIndex = newSolution.getRandomBoxIndexForEmpty();
		int destinationBoxIndex = newSolution.getRandomBoxIndexForEmpty();
		if(sourceBoxIndex == destinationBoxIndex) {
			return false;
		}

		// pick random rectangle
		MBox sourceBox = newSolution.getBoxes().get(sourceBoxIndex);
		int sourceBoxSize = sourceBox.getMRectangles().size();
		MRectangle m = null;
		if (sourceBoxSize > 0) {
			int item = 0;
			if (sourceBoxSize > 1)
				item = r.nextInt(sourceBoxSize - 1);
			int i = 0;
			for (MRectangle tempRectangle : sourceBox.getMRectangles()) {
				if (i == item)
					m = tempRectangle;
				i++;
			}
		} else
			return false;
		System.out.println("here1");
		// insert this rectangle into new box
		
		MBox destinationBox = newSolution.getBoxes().get(destinationBoxIndex);
		destinationBox.optimalSort();
		if (destinationBox.insert(m.clone())) {
			sourceBox.getMRectangles().remove(m);
			if (sourceBox.getMRectangles().isEmpty())
				newSolution.removeEmptyBox(sourceBoxIndex);
			else
				sourceBox.optimalSort();
		} else {
			System.out.println("here2");
			return false;
		}
			
		
		this.nextNeighborSolution = newSolution;
		return true;
	}

	@Override
	public Solution next() {
		if (this.findNext) {
			// the searching process for new solution was executed
			this.findNext = false;
			return this.nextNeighborSolution;
		} else {
			if (this.hasNext()) {
				// execute a new searching process for new solution
				this.findNext = false;
				return this.nextNeighborSolution;
			} else
				return null;
		}
	}

	@Override
	public void onCurrentSolutionChange(Solution newSolution) {
		this.currentSolution = (GeometryBasedSolution) newSolution;
		this.nextNeighborSolution = null;
	}
}
