package de.tud.nhd.optalgos.model;

public abstract class Solution {

	protected double objective;
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public abstract boolean compare(Solution s);
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	public abstract double getObjective();
}
