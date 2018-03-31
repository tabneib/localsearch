package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

/**
 * Class representing a rule-based solution. The solution depends on a permutation of
 * the rectangles and a method that inserting the rectangles in the permutation into 
 * boxes.
 *
 */
public class RuleBasedSolution extends MSolution {
	
	private ArrayList<MRectangle> permutation;

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
			Collections.sort(permutation, new Comparator<MRectangle>() {

				@Override
				public int compare(MRectangle o1, MRectangle o2) {
					if (o1.getArea() < o2.getArea())
						return 1;
					else if (o1.getArea() == o2.getArea())
						return 0;
					else return -1;
					}
			});
			// Then permute for a lot of times
			this.permute(permutation.size());
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
		MBox box = new MBox(((MOptProblem) this.optProblem).getBoxLength());
		for (MRectangle r : permutation) {
			box.optimalSort();
			while (!box.insert(r)){
				box.optimalSort();
				this.boxes.add(box);
				box = new MBox(((MOptProblem) this.optProblem).getBoxLength());
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
	public void permute(int randomScore) {
		for (int i = 0; i <= randomScore; i++) {
			int pos0 = new Random().nextInt(permutation.size() / 2);
			int pos1 = new Random().nextInt(permutation.size() / 2) + permutation.size() / 2;
			Collections.swap(permutation, pos0, pos1);
		}
		this.revalidate();
	}
	
	private RuleBasedSolution setPermutation(ArrayList<MRectangle> permutation) {
		this.permutation = permutation;
		return this;
	}
	
	@Override
	public RuleBasedSolution clone() {
	
		ArrayList<MRectangle> clonedPermutation = new ArrayList<>();
		clonedPermutation.addAll(permutation);
		return new RuleBasedSolution(this.optProblem, null).
				setPermutation(clonedPermutation);
	}
}
