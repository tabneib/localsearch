package de.nhd.localseach.algos;

import de.nhd.localseach.solution.GeometryBasedSolution;
import de.nhd.localseach.solution.RuleBasedSolution;
import de.nhd.localseach.solution.Solution;
import problem.MOptProblem;
import problem.OptProblem;

/**
 * Abstract class represents the class of neighborhood-based optimization
 * algorithms
 *
 */
public abstract class NeighborhoodBasedAlgo implements IOptAlgo {

	public static final String NEIGHBORHOOD_GEO = "NEIGHBORHOOD_GEO";
	public static final String NEIGHBORHOOD_PERM = "NEIGHBORHOOD_PERM";

	/**
	 * The optimization problem instance this algorithm has to solve
	 */
	protected final OptProblem optProblem;

	public OptProblem getOptProblem() {
		return optProblem;
	}

	/**
	 * The neighborhood relation used by this algorithm
	 */
	protected final String neighborhood;

	// TODO correct ? Should a neighborhood-based optimization algorithm hold a
	// current solution regarding the theory?
	protected Solution currentSolution;

	protected Solution startSolution;

	public NeighborhoodBasedAlgo(OptProblem optProblem, String neighborhood) {
		this.optProblem = optProblem;
		this.neighborhood = neighborhood;
		try {
			switch (this.neighborhood) {
				case NEIGHBORHOOD_GEO :
					this.startSolution = new GeometryBasedSolution(this.optProblem, null);
					break;
				case NEIGHBORHOOD_PERM :
					this.startSolution = new RuleBasedSolution(this.optProblem,
							((MOptProblem) this.optProblem).getRechtangles());
					break;
				default :
					throw new RuntimeException(
							"Unknown neighborhood: " + this.neighborhood);
			}
			this.currentSolution = (Solution) startSolution.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
