package de.nhd.localsearch.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import de.nhd.localsearch.neighborhoods.Neighborhood;
import de.nhd.localsearch.neighborhoods.RuleBasedNeighborhood;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.OptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

/**
 * Class representing a rule-based solution. The solution depends on a
 * permutation of the rectangles and a method that inserting the rectangles in
 * the permutation into boxes.
 *
 */
public class RuleBasedSolution extends MSolution {

	private ArrayList<MRectangle> permutation;

	/**
	 * boxes whose fill-rate passes this threshold will not be touch by
	 * permuting
	 */
	private static final double OPTIMAL_FILL_RATE = 0.9;

	public RuleBasedSolution(OptProblem problem, ArrayList<MRectangle> initPermutation) {
		super(problem, null);
		if (initPermutation == null)
			this.genDefaultPermutation();
		else
			this.permutation = initPermutation;
		this.revalidate();
	}

	private void genDefaultPermutation() {
		this.boxes = new ArrayList<>();
		this.permutation = new ArrayList<>();
		permutation.addAll(((MOptProblem) this.problem).getRechtangles());
		Collections.sort(permutation, areaComparator);

		ArrayList<MRectangle> firstHalf = new ArrayList<>();
		ArrayList<MRectangle> secondHalf = new ArrayList<>();

		for (int i = 0; i < permutation.size() / 2; i++)
			firstHalf.add(permutation.get(i));
		for (int i = permutation.size() / 2; i < permutation.size(); i++)
			secondHalf.add(permutation.get(i));
		Collections.reverse(firstHalf);

		// Re-insert the parts alternatively
		this.permutation = new ArrayList<>();
		for (int i = 0; i < firstHalf.size(); i++) {
			this.permutation.add(firstHalf.get(i));
			this.permutation.add(secondHalf.get(i));
		}
		if (secondHalf.size() > firstHalf.size())
			this.permutation.add(secondHalf.get(secondHalf.size() - 1));
	}

	/**
	 * Re-insert the rectangles in the permutation into boxes. This will replace
	 * the old boxes with the new ones.
	 *
	 */
	private void revalidate() {
		this.boxes = new ArrayList<>();
		MBox box = new MBox(((MOptProblem) this.problem).getBoxLength());
		for (MRectangle r : permutation) {
			while (box.optimalInsert(r) == null) {
				this.boxes.add(box);
				box = new MBox(((MOptProblem) this.problem).getBoxLength());
			}
		}
		this.boxes.add(box);
	}

	/**
	 * Randomly make an one-step change in the permutation. This will yield a
	 * neighbor of the current solution. Current approach: switch a random
	 * rectangle in the first half with another random one in the other half and
	 * do this for a given time.
	 * 
	 * @param randomScore
	 *            The number of time to switch a pair of rectangles
	 */
	public void permute() {

		Collections.sort(this.boxes, freeAreaComparator);
		double totalFreeArea = 0;
		ArrayList<MBox> optimalBoxes = new ArrayList<>();
		ArrayList<MBox> nonOptimalBoxes = new ArrayList<>();
		nonOptimalBoxes.addAll(this.boxes);
		Iterator<MBox> iter = nonOptimalBoxes.iterator();

		while (iter.hasNext()) {
			MBox box = iter.next();
			if (box.getFillRate() >= OPTIMAL_FILL_RATE) {
				optimalBoxes.add(box);
				iter.remove();
			} else
				totalFreeArea += box.getFreeArea();
		}
		if (nonOptimalBoxes.size() <= 1)
			return;

		double randomPart = new Random().nextInt((int) totalFreeArea);
		int randomIndex = 0;

		for (MBox box : nonOptimalBoxes) {
			randomPart -= box.getFillArea();
			if (randomPart <= 0)
				break;
			randomIndex++;
		}
		randomIndex = randomIndex >= nonOptimalBoxes.size() ? randomIndex - 1 : randomIndex;

		this.permutation = new ArrayList<>();
		for (MBox box : optimalBoxes)
			this.permutation.addAll(box.getMRectangles());
		this.permutation.addAll(nonOptimalBoxes.get(randomIndex).getMRectangles());
		nonOptimalBoxes.remove(randomIndex);
		for (int i = 0; i < nonOptimalBoxes.size(); i++)
			this.permutation.addAll(nonOptimalBoxes.get(i).getMRectangles());

		this.revalidate();
	}

	private RuleBasedSolution setPermutation(ArrayList<MRectangle> permutation) {
		this.permutation = permutation;
		return this;
	}

	@Override
	public Neighborhood getNeighborhood() {
		return new RuleBasedNeighborhood((MOptProblem) this.problem, this);
	}

	@Override
	public RuleBasedSolution clone() {
		ArrayList<MRectangle> clonedPermutation = new ArrayList<>();
		clonedPermutation.addAll(permutation);
		return new RuleBasedSolution(this.problem, null)
				.setPermutation(clonedPermutation);
	}

	private static final Comparator<MRectangle> areaComparator = new Comparator<MRectangle>() {

		@Override
		public int compare(MRectangle o1, MRectangle o2) {
			if (o1.getArea() < o2.getArea())
				return -1;
			else if (o1.getArea() == o2.getArea())
				return 0;
			else
				return 1;
		}
	};

	private static final Comparator<MBox> freeAreaComparator = new Comparator<MBox>() {

		@Override
		public int compare(MBox o1, MBox o2) {
			if (o1.getFreeArea() < o2.getFreeArea())
				return -1;
			else if (o1.getFreeArea() == o2.getFreeArea())
				return 0;
			else
				return 1;
		}
	};

}
