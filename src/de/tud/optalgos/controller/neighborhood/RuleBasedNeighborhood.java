package de.tud.optalgos.controller.neighborhood;

import de.tud.optalgos.model.MSolution;
import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.RuleBasedSolution;
import de.tud.optalgos.model.Solution;

public class RuleBasedNeighborhood extends Neighborhood {

	RuleBasedSolution clonedSolution = null;
	
	public RuleBasedNeighborhood(OptProblem instance, Solution currentSolution) {
		super(instance, currentSolution);
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Solution next() {
		if (clonedSolution == null)
			clonedSolution = ((RuleBasedSolution)this.currentSolution).clone(); 
		clonedSolution.permute();
		return clonedSolution;
	}

	@Override
	public void onCurrentSolutionChange(Solution newSolution) {
		this.currentSolution = (MSolution) newSolution;
	}
}
