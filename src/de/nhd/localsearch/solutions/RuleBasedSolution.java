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
 * Class representing a rule-based solution. The solution depends on a permutation of
 * the rectangles and a method that inserting the rectangles in the permutation into 
 * boxes.
 *
 */
public class RuleBasedSolution extends MSolution {
	
	private ArrayList<MRectangle> permutation;
	
	private double optimalFillRate = 0.9;
	private double sparseFillRate = 0.6;

	public RuleBasedSolution(
			OptProblem optProblem, ArrayList<MRectangle> initPermutation) {
		super(optProblem, null);
		
		if(initPermutation == null){
			this.boxes = new ArrayList<>();
			// Insert all rectangles in the instance into the permutation
			permutation = new ArrayList<>();
			
			// Insert all rectangles in the permutation 
			for (MRectangle r : ((MOptProblem) optProblem).getRechtangles())
				permutation.add(r);
			// Sort the rectangles first
			Comparator<MRectangle> comp = new Comparator<MRectangle>() {

				@Override
				public int compare(MRectangle o1, MRectangle o2) {
					if (o1.getArea() < o2.getArea())
						return -1;
					else if (o1.getArea() == o2.getArea())
						return 0;
					else return 1;
					}
			};
			
			Collections.sort(permutation, comp);
			
			// Break it into parts
			ArrayList<MRectangle> firstHalf = new ArrayList<>();
			ArrayList<MRectangle> secondHalf = new ArrayList<>();
			for (int i = 0; i < permutation.size() / 2; i++)
				firstHalf.add(permutation.get(i));
			for (int i = permutation.size() / 2; i < permutation.size(); i++)
				secondHalf.add(permutation.get(i));
			Collections.reverse(firstHalf);
			
			// Re-insert the parts alternatively	
			this.permutation = new ArrayList<>();
			for (int i = 0; i < firstHalf.size(); i++){
				this.permutation.add(firstHalf.get(i));
				this.permutation.add(secondHalf.get(i));
			}
			if (secondHalf.size() > firstHalf.size())
				this.permutation.add(secondHalf.get(secondHalf.size()-1));
			
			this.revalidate();
		}
		else {
			this.permutation = initPermutation;
			this.revalidate();
		}
	}
	
	/**
	 * Re-insert the rectangles in the permutation into boxes. This will replace the old 
	 * boxes with the new ones.
	 *
	 */
	private void revalidate(){
		this.boxes = new ArrayList<>();
		MBox box = new MBox(((MOptProblem) this.problem).getBoxLength());
		for (MRectangle r : permutation) {
			box.optimalSort();
			while (box.optimalInsert(r) == null){
				box.optimalSort();
				this.boxes.add(box);
				box = new MBox(((MOptProblem) this.problem).getBoxLength());
			}
		}
		this.boxes.add(box);
	}
	
	/**
	 * Randomly make an one-step change in the permutation. This will yield a neighbor of
	 * the current solution. 
	 * Current approach: switch a random rectangle in the first half with another random 
	 * one in the other half and do this for a given time. 
	 * 
	 * @param randomScore	The number of time to switch a pair of rectangles
	 */
	public void permute() {
		
		Collections.sort(this.boxes, new Comparator<MBox>() {

			@Override
			public int compare(MBox o1, MBox o2) {
				if (o1.getFreeArea() < o2.getFreeArea())
					return -1;
				else if (o1.getFreeArea() == o2.getFreeArea())
					return 0;
				else
					return 1;
			}
		});
		
		double totalFreeArea = 0;
		
		for (MBox box : boxes)
			totalFreeArea += box.getFreeArea();
		
		// We do not touch the boxes that are already optimal
		ArrayList<MBox> optimalBoxes = new ArrayList<>();
		ArrayList<MBox> tmpBoxes = new ArrayList<>();
		tmpBoxes.addAll(this.boxes);
		Iterator<MBox> iter = tmpBoxes.iterator();
		while (iter.hasNext()) {
			MBox box = iter.next();
			if (box.getFillRate() >= optimalFillRate) { 
				optimalBoxes.add(box);
				totalFreeArea -= box.getFreeArea();
				iter.remove();
			}
		}
		
		if (tmpBoxes.size() <= 1) 
			return;
		
		// Select a random box from the not yet optimal boxes proportionally to their
		// free area. If there are boxes that are not yet optimal, select the first one
		// of them instead of a random one.
		int randomIndex = 0;
		for(int i = tmpBoxes.size() - 1; i > 0; i--){
			if (tmpBoxes.get(i).getFillRate() < sparseFillRate){
				randomIndex = i;
				break;
			}
		}
		if (randomIndex == 0){
			double randomPart = new Random().nextInt((int) totalFreeArea);
			
			for (MBox box : tmpBoxes) {
				randomPart -= box.getFillArea();
				if (randomPart <= 0)
					break;
				randomIndex++;
			}
		}
		
		// No idea why randomIndex could be equal to tmpBoxes size, hence this hack
		randomIndex = randomIndex == tmpBoxes.size() ? randomIndex - 1 : randomIndex;
		
		// Update the permutation
		this.permutation = new ArrayList<>();

		// First, add all optimal boxes
		for (MBox box : optimalBoxes)
			this.permutation.addAll(box.getMRectangles());
		
		// Second, add all other boxes. The selected box in selected in the last step
		// is added first.
		this.permutation.addAll(tmpBoxes.get(randomIndex).getMRectangles());
		tmpBoxes.remove(randomIndex);
		for (int i = 0; i < tmpBoxes.size(); i++) 
			this.permutation.addAll(tmpBoxes.get(i).getMRectangles());
			
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
		return new RuleBasedSolution(this.problem, null).
				setPermutation(clonedPermutation);
	}
}
