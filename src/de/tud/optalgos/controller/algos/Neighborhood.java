package de.tud.optalgos.controller.algos;


import java.util.Iterator;

import de.tud.optalgos.model.Instance;
import de.tud.optalgos.model.Solution;

public abstract class Neighborhood implements Iterator<Solution> {
	
	private Instance instance;
	protected Solution currentSolution;
	
	public Neighborhood (Instance instance, Solution currentSolution) {
		this.instance = instance;
		this.currentSolution = currentSolution;
	}
	
	/**
	 * 
	 * @param newSolution
	 */
	public abstract void onCurrentSolutionChange(Solution newSolution);

	protected Instance getInstance() {
		return instance;
	}

	
	protected Solution getCurrentSolution() {
		return this.currentSolution;
	}
	
}
