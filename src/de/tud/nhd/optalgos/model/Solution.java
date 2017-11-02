package de.tud.nhd.optalgos.model;

public abstract class Solution implements Comparable<Solution>{

	OptProblem optProblem;
	
	public Solution(OptProblem optProblem) {
		this.optProblem = optProblem;
	}
	
	public boolean isBetterThan(Solution other) {
		if (compareTo(other) > 0 )
			return true;
		return false;
	}
	
	
	@Override
	public int compareTo(Solution other) {
		if (this.getObjective() < other.getObjective())
			return optProblem.getInstance().equals(OptProblem.Direction.MINIMALMIZING) ?
					-1 : 1;
		else if (this.getObjective() > other.getObjective())
			return optProblem.getInstance().equals(OptProblem.Direction.MINIMALMIZING) ?
					1 : -1;
		return 0;
	}
	

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract double getObjective();
	
	
}
