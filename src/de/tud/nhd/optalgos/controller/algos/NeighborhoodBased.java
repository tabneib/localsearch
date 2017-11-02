package de.tud.nhd.optalgos.controller.algos;

import de.tud.nhd.optalgos.model.OptProblem;
import de.tud.nhd.optalgos.model.Solution;

public abstract class NeighborhoodBased implements IOptAlgo{
	
	protected final OptProblem optProblem;
	protected final Neighborhood neighborhood;
	protected Solution currentSolution;
	
	public NeighborhoodBased(
			OptProblem optProblem, Neighborhood neighborhood, Solution startSolution) {
		this.optProblem = optProblem;
		this.neighborhood = neighborhood;
		this.currentSolution = startSolution;
	}

}
