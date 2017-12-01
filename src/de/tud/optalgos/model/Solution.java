package de.tud.optalgos.model;

public abstract class Solution implements Comparable<Solution>, Cloneable{

	OptProblem optProblem;
	
	public Solution(OptProblem optProblem) {
		this.optProblem = optProblem;
	}
	
	public OptProblem getOptProblem() {
		return this.optProblem;
	}
	
	public boolean isBetterThan(Solution other) {
		if (this.compareTo(other) > 0 ) {
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public int compareTo(Solution other) {
		if (this.getObjective() < other.getObjective()) {
			return optProblem.getDirection().equals(OptProblem.Direction.MINIMALMIZING) ? -1 : 1;
		}
		else { 
			if (this.getObjective() > other.getObjective()) {
				return optProblem.getDirection().equals(OptProblem.Direction.MAXIMALMIZING) ? 1 : -1;
			}
			return 0;
		}
			
		
	}
	

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract double getObjective();
	
}
