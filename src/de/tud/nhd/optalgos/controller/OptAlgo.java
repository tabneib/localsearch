package de.tud.nhd.optalgos.controller;

import de.tud.nhd.optalgos.model.Solution;

public interface OptAlgo {
	
	
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
