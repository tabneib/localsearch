package de.tud.nhd.optalgos.model;


public class OptProblem {
	
	private Instance instance;
	private String direction;
	
	
	public OptProblem(Instance instance, String direction){
		this.instance = instance;
		this.direction = direction;
	}
	
	
	public Instance getInstance() {
		return instance;
	}


	public String getDirection() {
		return direction;
	}


	public static final class Direction{
		public static final String MINIMALMIZING = "MINIMALMIZING";
		public static final String MAXIMALMIZING = "MAXIMALMIZING";
	}
}
