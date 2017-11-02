package de.tud.nhd.optalgos.controller.algos;


import java.util.Iterator;

import de.tud.nhd.optalgos.model.Instance;
import de.tud.nhd.optalgos.model.Solution;

public abstract class Neighborhood implements Iterator<Solution> {
	
	private Instance instance;
	private Solution currentSolution;
	
	public Neighborhood (Instance instance, Solution currentSolution) {
		this.instance = instance;
		this.currentSolution = currentSolution;
	}
	
	/**
	 * 
	 * @param newSolution
	 */
	public void onCurrentSolutionChange(Solution newSolution) {
		this.currentSolution = newSolution;
	}

}
