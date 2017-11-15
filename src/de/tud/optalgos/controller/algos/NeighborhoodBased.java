package de.tud.optalgos.controller.algos;

import de.tud.optalgos.model.OptProblem;
import de.tud.optalgos.model.Solution;

public abstract class NeighborhoodBased implements IOptAlgo{
	
	// Input 1 
	protected final OptProblem optProblem;
	// Input 2 => noch nicht gemacht
	protected final Neighborhood neighborhood;
	// correct ?
	protected Solution currentSolution;
	
	public NeighborhoodBased(
			OptProblem optProblem, Neighborhood neighborhood, Solution startSolution) {
		this.optProblem = optProblem;
		this.neighborhood = neighborhood;
		this.currentSolution = startSolution;
	}

}
