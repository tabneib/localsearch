package de.tud.nhd.optalgos;

import java.util.Set;

public abstract class OptProblem {
	protected Input input;
	protected Set<Output> outputs;
	protected String direction;

	
	public OptProblem(Input input, String direction){
		this.input = input;
		this.direction = direction;
	}
	
	/**
	 * 
	 * @param output
	 * @return
	 */
	public abstract double objective(Output output);
	
	/**
	 * 
	 * @return
	 */
	public abstract Output getOptimum();
	
	
	public static final class Direction{
		public static final String MINIMALMIZING = "MINIMALMIZING";
		public static final String MAXIMALMIZING = "MAXIMALMIZING";
	}
}
