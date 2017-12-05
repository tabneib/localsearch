package de.tud.optalgos.controller.algos;

import de.tud.optalgos.model.Solution;

/**
 * Interface that defines the basic functionalities of an optimization algorithm
 *
 */
public interface IOptAlgo {

	/**
	 * Run the whole algorithm
	 */
	public void run();

	/**
	 * Get the final optimal solution
	 * 
	 * @return The optimal solution
	 */
	public Solution getOptimum();

	/**
	 * Get the running time of the algorithm
	 * 
	 * @return the running time
	 */
	public long getRunningTime();
}
