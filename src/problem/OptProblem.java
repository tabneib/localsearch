package problem;

/**
 * This class represents an abstracted optimization problem.
 * Any concrete optimization problem must extend this class.
 *
 */
public class OptProblem {

	/**
	 * Direction of the optimization: whether maximize or minimizing the
	 * objective value of the end solution.
	 */
	private String direction;

	public OptProblem(String direction) {
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

	/**
	 * Inner class that holds constants that define the direction
	 */
	public static final class Direction {
		public static final String MINIMIZING = "MINIMIZING";
		public static final String MAXIMIZING = "MAXIMIZING";
	}
}
