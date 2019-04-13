package de.nhd.localsearch.neighborhoods;

import java.util.ArrayList;
import java.util.Iterator;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.solutions.MFeature;
import de.nhd.localsearch.solutions.Solution;

/**
 * Abstract class that defines the basic functionalities of a neighborhood
 * relation
 *
 */
public abstract class Neighborhood implements Iterator<Solution> {

	/**
	 * Maximal number of neighbors to be iterated through. This tuning
	 * guarantees a termination of the algorithm.
	 */
	public static final int MAX_NEIGHBORS = 100;

	/**
	 * The instance of the optimization problem that this neighborhood
	 * corresponds to
	 */
	private MOptProblem problem;

	/**
	 * The solution that holds this neighborhood
	 */
	protected Solution owner;

	private boolean tabooMode = false;
	private ArrayList<MFeature> recentInsertedFeatures;
	private ArrayList<MFeature> recentRemovedFeatures;

	public Neighborhood(MOptProblem problem, Solution owner) {
		this.problem = problem;
		this.owner = owner;
	}

	protected OptProblem getProblem() {
		return problem;
	}

	protected Solution getOwner() {
		return this.owner;
	}

	protected boolean isTabooMode() {
		return tabooMode;
	}

	public void setTabooMode(ArrayList<MFeature> recentInsertedFeatures,
			ArrayList<MFeature> recentRemovedFeatures) {
		this.tabooMode = true;
		this.recentInsertedFeatures = recentInsertedFeatures;
		this.recentRemovedFeatures = recentRemovedFeatures;
	}

	/**
	 * Check if the to be inserted feature is not recently removed, therefore
	 * insertable
	 * 
	 * @param toBeInsertedFeature
	 * @return
	 */
	protected boolean checkInsertable(MFeature toBeInsertedFeature) {
		if (!this.isTabooMode())
			throw new RuntimeException("Not in taboo mode");
		return !this.recentRemovedFeatures.contains(toBeInsertedFeature);
	}

	/**
	 * Check if the to be removed feature is not recently inserted, therefore
	 * removable
	 * @param toBeRemovedFeature
	 * @return
	 */
	protected boolean checkRemovable(MFeature toBeRemovedFeature) {
		if (!this.isTabooMode())
			throw new RuntimeException("Not in taboo mode");
		return !this.recentInsertedFeatures.contains(toBeRemovedFeature);
	}

}
