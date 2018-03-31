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
	 * Attempt to find the next neighbor
	 * 
	 * @return
	 */
	public boolean attempt() {
		
		if (((GeometryBasedSolution) this.currentSolution).getBoxes().size() < 2)
			return false;
		
		GeometryBasedSolution nextSolution = 
				((GeometryBasedSolution) this.currentSolution).clone();

		// Pick two random boxes
		int sourceBoxIndex = nextSolution.getRandomBoxProportionally();
		int destinationBoxIndex = nextSolution.getRandomBoxProportionally();
		if(sourceBoxIndex == destinationBoxIndex) {
			return false;
		}

		// Pick random rectangle
		MBox sourceBox = nextSolution.getBoxes().get(sourceBoxIndex);
		int sourceBoxSize = sourceBox.getMRectangles().size();
		MRectangle m = null;
		if (sourceBoxSize > 0) {
			int item = sourceBoxSize > 1 ? new Random().nextInt(sourceBoxSize - 1) : 0;
			int i = 0;
			for (MRectangle tempRectangle : sourceBox.getMRectangles()) {
				if (i == item){
					m = tempRectangle;
					break;
				}
				i++;
			}
		} else
			return false;
		
		// Insert this rectangle into the destination box	
		MBox destinationBox = nextSolution.getBoxes().get(destinationBoxIndex);
		//destinationBox.optimalSort(); <- No need to sort the destination box due to optimal insert
		if (destinationBox.insert(m.clone())) {
			sourceBox.removeRect(m);
			if (sourceBox.getMRectangles().isEmpty())
				nextSolution.removeBox(sourceBoxIndex);
			else
				sourceBox.optimalSort();
		} else 
			return false;
		
		// Don't forget to update the objective value of the next solution
		nextSolution.updateObjective();	
		this.nextNeighborSolution = nextSolution;
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
