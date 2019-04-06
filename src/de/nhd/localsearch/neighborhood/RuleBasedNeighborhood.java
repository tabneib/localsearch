package de.nhd.localsearch.neighborhood;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.solution.MSolution;
import de.nhd.localsearch.solution.RuleBasedSolution;
import de.nhd.localsearch.solution.Solution;

public class RuleBasedNeighborhood extends Neighborhood {

	RuleBasedSolution clonedSolution = null;
	
	public RuleBasedNeighborhood(MOptProblem instance, Solution currentSolution) {
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
