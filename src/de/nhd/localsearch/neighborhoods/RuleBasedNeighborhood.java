package de.nhd.localsearch.neighborhoods;

import java.util.ArrayList;
import java.util.Collections;

import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;
import de.nhd.localsearch.solutions.RuleBasedSolution;
import de.nhd.localsearch.solutions.Solution;

public class RuleBasedNeighborhood extends Neighborhood {

	/**
	 * boxes whose fill-rate passes this threshold will not be touch by
	 * permuting
	 */
	private static final double OPTIMAL_FILL_RATE = 0.8;

	private int iteratedNeighbors;
	private boolean noMoreNeighbor = false;

	public RuleBasedNeighborhood(MOptProblem instance, Solution currentSolution) {
		super(instance, currentSolution);
	}

	@Override
	public boolean hasNext() {
		return !noMoreNeighbor;
	}

	@Override
	public Solution next() {
		if (this.noMoreNeighbor || this.iteratedNeighbors > MAX_NEIGHBORS)
			throw new RuntimeException("No more neighbor");
		Solution neighbor = this.findNextNeighbor((RuleBasedSolution) this.owner);
		this.iteratedNeighbors++;
		if (this.iteratedNeighbors > MAX_NEIGHBORS || neighbor == null)
			this.noMoreNeighbor = true;
		return neighbor;
	}

	/**
	 * Randomly make an one-step change in the permutation and return the
	 * corresponding neighbor of the given solution
	 * 
	 * @param solution
	 * @return the neighbor solution or null if cannot generate such a neighbor.
	 */
	private Solution findNextNeighbor(RuleBasedSolution solution) {
		ArrayList<MRectangle> neighborPerm = new ArrayList<>();
		ArrayList<MRectangle> rects = solution.getRechtangles();
		ArrayList<MBox> boxes = new ArrayList<>(solution.getBoxes());
		Collections.sort(boxes);
		Collections.reverse(boxes);
		int prependingOptimalBoxes = 0;
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).getFillRate() < OPTIMAL_FILL_RATE) {
				for (int j = 0; j < prependingOptimalBoxes; j++) {
					neighborPerm.addAll(boxes.get(j).getMRectangles());
					rects.removeAll(boxes.get(j).getMRectangles());
				}
				break;
			} else
				prependingOptimalBoxes++;
		}
		// Permute all rectangles of non-optimal boxes
		neighborPerm.addAll(this.permute(rects));

		// neighborPerm = solution.getPermutation();
		Solution neighbor = new RuleBasedSolution(this.owner.getProblem(), neighborPerm);
		return neighbor;
	}

	/**
	 * Permute the given list of rectangles. Current approach: sort the list,
	 * divide in two half, reinsert them alternately, and at the end make a
	 * random switch (this is crucial to prevent generating the same permutation
	 * every time)
	 * 
	 * @param rectangles
	 * @return
	 */
	private ArrayList<MRectangle> permute(ArrayList<MRectangle> rectangles) {
		ArrayList<MRectangle> permutation = new ArrayList<>();
		if (rectangles.size() >= 4) {
			Collections.sort(rectangles);
			ArrayList<MRectangle> firstPart = new ArrayList<>();
			ArrayList<MRectangle> secondPart = new ArrayList<>();
			ArrayList<MRectangle> thirdPart = new ArrayList<>();
			ArrayList<MRectangle> fourthPart = new ArrayList<>();

			for (int i = 0; i < rectangles.size() / 4; i++)
				firstPart.add(rectangles.get(i));
			for (int i = rectangles.size() / 4; i < rectangles.size() / 2; i++)
				secondPart.add(rectangles.get(i));
			for (int i = rectangles.size() / 2; i < rectangles.size() * 3 / 4; i++)
				thirdPart.add(rectangles.get(i));
			for (int i = rectangles.size() * 3 / 4; i < rectangles.size(); i++)
				fourthPart.add(rectangles.get(i));

			Collections.reverse(firstPart);
			Collections.reverse(fourthPart);

			// Re-insert the parts alternately
			for (int i = 0; i < firstPart.size(); i++) {
				permutation.add(firstPart.get(i));
				permutation.add(secondPart.get(i));
				permutation.add(thirdPart.get(i));
				permutation.add(fourthPart.get(i));
			}
			if (fourthPart.size() > firstPart.size())
				permutation.add(fourthPart.get(fourthPart.size() - 1));
		} else
			permutation.addAll(rectangles);

		// One single random switch
		MRectangle randomRect = permutation
				.get((int) (Math.random() * permutation.size()));
		permutation.remove(randomRect);
		permutation.add(0, randomRect);
		return permutation;
	}
}
