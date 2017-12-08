package de.tud.optalgos.controller.neighborhood;

import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.RuleBasedSolution;
import de.tud.optalgos.model.Solution;

public class RuleBasedNeighborhood extends Neighborhood {

	/**
	 * The next neighbor to be returned
	 */
	private RuleBasedSolution nextNeighborSolution;
	
	public RuleBasedNeighborhood(OptProblem instance, Solution currentSolution) {
		super(instance, currentSolution);
		this.nextNeighborSolution = null;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Solution next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCurrentSolutionChange(Solution newSolution) {
		this.currentSolution = (MSolution) newSolution;
		this.nextNeighborSolution = null;
	}

}
