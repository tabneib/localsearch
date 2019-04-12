package de.nhd.localsearch.algorithms;

import de.nhd.localsearch.solutions.Solution;

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
	 * Get the current solution
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
	 * Get the running state of the algorithm
	 * 
	 * @return true if the algorithm has finished, otherwise false
	 */
	public boolean isFinished();
}
