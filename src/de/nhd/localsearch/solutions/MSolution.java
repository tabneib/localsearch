package de.nhd.localsearch.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

/**
 * This class represents a solution for the optimization problem targeted in
 * this project.
 *
 */
public abstract class MSolution extends Solution {

	private static final int AGGRESSIVELESSNESS = 10;

	/**
	 * Total number of rounds that the algorithm has run
	 */
	private static int round = 0;

	/**
	 * The percentage amount to reduce the overlap rate in one reduction step.
	 */
	private static final double OVERLAP_RATE_REDUCE_STEP = 0.1;

	/**
	 * List of the boxes used by this solution to store the rectangles given by
	 * the corresponding instance of the optimization problem.
	 */
	protected ArrayList<MBox> boxes;

	private double objectiveValue = -1;

	public MSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem);
		this.boxes = boxes;
	}

	@Override
	public double getObjective() {
		if (this.objectiveValue == -1) {
			int totalRects = ((MOptProblem) problem).getRechtangles().size();
			ArrayList<Double> fillGrades = new ArrayList<Double>();
			for (MBox box : this.boxes)
				fillGrades.add((Double) box.getFillRate());
			Collections.sort(fillGrades);
			Collections.reverse(fillGrades);
			this.objectiveValue = 0;
			for (int i = 0; i < fillGrades.size(); i++)
				this.objectiveValue += fillGrades.get(i) * (totalRects - i);
		}

		if (MRectangle.isOverlapPermitted())
			this.objectiveValue -= MSolution.getPenaltyRate()
					* this.getTotalOverlapArea();

		return this.objectiveValue;
	}

	/**
	 * Sum up all overlap area of the boxes
	 * 
	 * @return The total overlap area
	 */
	private double getTotalOverlapArea() {
		double total = 0;
		for (MBox box : this.boxes)
			total += box.getOverlapArea();
		return total;
	}

	/**
	 * This method forces the objective value to be updated (recomputed) the
	 * next time.
	 */
	public void revalidateObjective() {
		this.objectiveValue = -1;
	}

	/**
	 * Retrieve the list of all rectangles stored in the boxes of this solution.
	 * 
	 * @return
	 */
	public ArrayList<MRectangle> getRechtangles() {
		ArrayList<MRectangle> rects = new ArrayList<>();
		for (MBox box : this.boxes)
			rects.addAll(box.getMRectangles());
		return rects;
	}

	public ArrayList<MBox> getBoxes() {
		return this.boxes;
	}

	public void setBoxes(ArrayList<MBox> boxes) {
		this.boxes = boxes;
	}

	/**
	 * Remove all empty boxes or all but except the last (empty) one
	 * 
	 * @param aggressive
	 *            if all empty boxes are to be removed
	 */
	public void removeEmptyBoxes(boolean aggressive) {
		Iterator<MBox> iter = this.boxes.iterator();
		if (aggressive) {
			while (iter.hasNext()) {
				if (((MBox) iter.next()).isEmptyBox())
					iter.remove();
			}
			return;
		}
		ArrayList<MBox> toBeRemoved = new ArrayList<>();
		while (iter.hasNext()) {
			MBox box = (MBox) iter.next();
			if (box.isEmptyBox())
				toBeRemoved.add(box);
		}
		if (toBeRemoved.isEmpty() || toBeRemoved.size() <= AGGRESSIVELESSNESS)
			return;
		for (int i = 0; i <= AGGRESSIVELESSNESS; i++)
			toBeRemoved.remove(toBeRemoved.size() - 1);
		this.boxes.removeAll(toBeRemoved);
	}

	/**
	 * Compute and return the penalty rate which depends on the executed rounds
	 * until now.
	 * 
	 * @return
	 */
	public static double getPenaltyRate() {
		if (MRectangle.isOverlapPermitted())
			return Math.pow(round, 1.2);
		else
			throw new RuntimeException(
					"Cannot get penalty rate in case overlap is not permitted!");
	}

	/**
	 * Increase the counted round of the algorithm. This is used for the penalty
	 * of overlapping
	 */
	public static void increaseRound() {
		round++;
		if (MRectangle.isOverlapPermitted()
				&& MRectangle.getOverlapRate() > MRectangle.MIN_OVERLAP_RATE
				&& round % 20 == 0)
			MRectangle.setOverlapRate(
					MRectangle.getOverlapRate() - OVERLAP_RATE_REDUCE_STEP);
	}

	public static void resetRound() {
		round = 0;
	}
}
