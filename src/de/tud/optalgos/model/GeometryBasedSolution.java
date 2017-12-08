package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Random;

import de.tud.optalgos.model.geometry.MBox;

public class GeometryBasedSolution extends MSolution {

	public GeometryBasedSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem, boxes);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * TODO: Comment me
	 * 
	 * @return
	 */
	public int getRandomBoxIndexForEmpty() {
		double totalFreeArea = 0;
		for (MBox mBox : this.boxes)
			totalFreeArea += mBox.getFreeArea();
		double random = totalFreeArea * Math.random();
		int index = 0;
		for (MBox mBox : this.boxes) {
			random -= mBox.getFreeArea();
			if (random <= 0)
				return index;
			index++;
		}
		return this.boxes.size() - 1;
	}

	/**
	 * TODO: Comment me
	 * 
	 * @return
	 */
	public int getRandomBoxIndexForFilling() {
		Random r = new Random();
		return r.nextInt(this.boxes.size())-1;
	}

	/**
	 * TODO: Comment me
	 * 
	 * @param indexOfEmptyBox
	 */
	public void removeEmptyBox(int indexOfEmptyBox) {
		this.boxes.remove(indexOfEmptyBox);
	}
	
	@Override
	public GeometryBasedSolution clone() {
		ArrayList<MBox> newBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.boxes)
			newBoxes.add(mBox.clone());
		GeometryBasedSolution newSolution = new GeometryBasedSolution(this.optProblem, newBoxes);
		return newSolution;
	}
	
	
}
