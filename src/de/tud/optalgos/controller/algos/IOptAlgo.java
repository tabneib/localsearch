package de.tud.optalgos.controller.algos;

import de.tud.optalgos.model.Solution;

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

	/**
	 * 
	 * @return
	 */
	public long getRunningTime();
}
