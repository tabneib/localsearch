package de.nhd.localsearch.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

	/**
	 * List of the boxes used by this solution to store the rectangles given by
	 * the corresponding instance of the optimization problem.
	 */
	private ArrayList<MBox> boxes;

	private double objectiveValue = -1;

	private double overlapArea;

	private HashSet<MFeature> removedFeatures;
	private HashSet<MFeature> insertedFeatures;

	/**
	 * current taboo rectangles, for which repositioning it is not allowed. This
	 * is used solely for visualization purpose.
	 */
	private HashSet<MRectangle> tabooRectangles;

	public MSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem);
		this.boxes = boxes;
		if (boxes != null && MRectangle.isOverlapPermitted())
			for (MBox box : this.boxes)
				this.overlapArea += box.getOverlapArea();
		tabooRectangles = new HashSet<>();
	}

	@Override
	public double getObjective() {

		int integerPart = this.getNonEmptyBoxAmount()
				- ((MOptProblem) this.problem).getOptimalBoxAmount();
		if (integerPart <= 0)
			integerPart = 0;

		double fractionalPart = 0;
		int totalRects = ((MOptProblem) problem).getRechtangles().size();
		ArrayList<Double> fillRates = new ArrayList<Double>();
		for (MBox box : this.boxes)
			fillRates.add((Double) box.getFillRate());
		Collections.sort(fillRates);
		Collections.reverse(fillRates);
		for (int i = 0; i < fillRates.size(); i++)
			fractionalPart += fillRates.get(i) * (totalRects - i);
		fractionalPart = Math.pow(fractionalPart + 1, -1);

		this.objectiveValue = integerPart + fractionalPart;

		if (MRectangle.isOverlapPermitted()) {
//			System.out.println("this.objectiveValue = " + this.objectiveValue);
//			System.out.println("this.getTotalOverlapArea()" + this.getTotalOverlapArea());
//			System.out.println("Penalty rate: " + MRectangle.getOverlapPenaltyRate());
//			System.out.println("Penalty: "
//					+ (MRectangle.getOverlapPenaltyRate() * this.getTotalOverlapArea()));
			this.objectiveValue += MRectangle.getOverlapPenaltyRate()
					* this.getTotalOverlapArea();
		}
		return this.objectiveValue;
	}

	/**
	 * Sum up all overlap area of the boxes
	 * 
	 * @return The total overlap area
	 */
	public double getTotalOverlapArea() {
		return this.overlapArea;
	}

	/**
	 * This method forces the objective value to be updated (recomputed) the
	 * next time.
	 */
//	public void revalidateObjective() {
//		this.objectiveValue = -1;
//	}

	/**
	 * Retrieve the list of all rectangles stored in the boxes of this solution.
	 * 
	 * @return
	 */
	public ArrayList<MRectangle> getRectangles() {
		ArrayList<MRectangle> rects = new ArrayList<>();
		for (MBox box : this.boxes)
			rects.addAll(box.getMRectangles());
		return rects;
	}

	public int getNonEmptyBoxAmount() {
		int amount = 0;
		for (MBox box : this.boxes)
			if (!box.isEmptyBox())
				amount++;
		return amount;
	}

	public ArrayList<MBox> getBoxes() {
		return new ArrayList<>(this.boxes);
	}

	protected void addBox(MBox box) {
		if (this.boxes == null)
			this.boxes = new ArrayList<>();
		this.boxes.add(box);
		if (MRectangle.isOverlapPermitted() && box.getOverlapArea() > 0) {
			this.overlapArea += box.getOverlapArea();
//			System.out.println("MSolution - Updated overlapArea: " + this.overlapArea);
		}
//		this.revalidateObjective();
	}

	protected void removeBoxes() {
		this.boxes = new ArrayList<>();
		this.overlapArea = 0;
//		this.revalidateObjective();
	}

	/**
	 * Remove all empty boxes or all but except a given amount of the last
	 * (empty) ones
	 * 
	 * @param aggressive
	 *            amount of last empty boxes not to be removed
	 */
	public void removeEmptyBoxes(int aggressivelessness) {
		Iterator<MBox> iter = this.boxes.iterator();
		if (aggressivelessness == 0) {
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
		if (toBeRemoved.isEmpty() || toBeRemoved.size() <= aggressivelessness)
			return;
		for (int i = 0; i <= aggressivelessness; i++)
			toBeRemoved.remove(toBeRemoved.size() - 1);
		this.boxes.removeAll(toBeRemoved);
//		this.revalidateObjective();
	}

	/**
	 * Compute and return the penalty rate which depends on the executed rounds
	 * until now.
	 * 
	 * @return
	 */
	// public static double getPenaltyRate() {
	// if (MRectangle.isOverlapPermitted())
	// return Math.pow(round, 1.2);
	// else
	// throw new RuntimeException(
	// "Cannot get penalty rate in case overlap is not permitted!");
	// }

	public HashSet<MFeature> getRemovedFeatures() {
		return removedFeatures;
	}

	public void addRemovedFeature(MFeature removedFeature) {
		if (this.removedFeatures == null)
			this.removedFeatures = new HashSet<>();
		this.removedFeatures.add(removedFeature);
	}

	public HashSet<MFeature> getInsertedFeatures() {
		return insertedFeatures;
	}

	public void addInsertedFeature(MFeature insertedFeature) {
		if (this.insertedFeatures == null)
			this.insertedFeatures = new HashSet<>();
		this.insertedFeatures.add(insertedFeature);
	}

	public void addTaboo(MRectangle rect) {
		if (!this.tabooRectangles.add(rect))
			throw new RuntimeException("Double insert taboo rectangle");
	}

	public void removeTaboo(MRectangle rect) {
		if (!this.tabooRectangles.remove(rect))
			throw new RuntimeException("Taboo rectangle not present");
	}

	public HashSet<MRectangle> getTabooRectangles() {
		return tabooRectangles;
	}

	public void setTabooRectangles(HashSet<MRectangle> tabooRectangles) {
		this.tabooRectangles = tabooRectangles;
	}

	public boolean checkTaboo(MRectangle rect) {
		return this.tabooRectangles.contains(rect);
	}
}
