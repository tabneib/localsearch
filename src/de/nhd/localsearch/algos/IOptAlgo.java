package de.nhd.localsearch.algos;

import de.nhd.localsearch.solution.Solution;

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
	 * Run one single algorithm step
	 */
	public void runStep();
	

	/**
	 * Get the start solution
	 * 
	 * @return The solution
	 */
	public Solution getStartSolution();

	/**
	 * Get the final optimal solution
	 * 
	 * @return The optimal solution
	 */
	public Solution getCurrentSolution();

	/**
	 * Get the running time of the algorithm
	 * 
	 * @return the running time
	 */
	public long getRunningTime();
	
	/**
	 * Remove or reset all intermediate data to rerun the algorithm
	 */
	public void reset();
}
