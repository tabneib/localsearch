package de.tud.nhd.optalgos.controller.algos;

import de.tud.nhd.optalgos.model.Solution;

public interface IOptAlgo {
	
	
	/**
	 * 
	 */
	public void run();
	
	/**
	 * 
	 * @return
	 */
	public Solution getOptimum();

}
