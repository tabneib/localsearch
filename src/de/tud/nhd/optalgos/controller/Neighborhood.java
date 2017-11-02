package de.tud.nhd.optalgos.controller;


import de.tud.nhd.optalgos.model.Solution;

public interface Neighborhood {
	
	/**
	 * 
	 * @param s
	 * @param direction
	 * @return
	 */
	public default Solution findBetterNbor(Solution s, String direction) {
		// TODO: Default is finding the first better neighbor
		return null;
	}

}
