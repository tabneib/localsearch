package de.nhd.localseach.neighborhood;

import de.nhd.localseach.solution.MSolution;
import de.nhd.localseach.solution.RuleBasedSolution;
import de.nhd.localseach.solution.Solution;
import problem.MOptProblem;
import problem.OptProblem;

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
