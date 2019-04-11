package de.nhd.localsearch.neighborhoods;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.solutions.RuleBasedSolution;
import de.nhd.localsearch.solutions.Solution;

public class RuleBasedNeighborhood extends Neighborhood {

	private int iteratedNeighbors;
	private boolean noMoreNeighbor = false;
	
	public RuleBasedNeighborhood(MOptProblem instance, Solution currentSolution) {
		super(instance, currentSolution);
	}

	@Override
	public boolean hasNext() {
		return !noMoreNeighbor;
	}

	@Override
	public Solution next() {
		if (this.noMoreNeighbor || this.iteratedNeighbors > MAX_NEIGHBORS)
			throw new RuntimeException("No more neighbor");
		RuleBasedSolution clonedOwner = ((RuleBasedSolution) this.owner).clone(); 
		clonedOwner.permute();
		this.iteratedNeighbors++;
		if (this.iteratedNeighbors > MAX_NEIGHBORS)
			this.noMoreNeighbor = true;
		return clonedOwner;
	}
}
