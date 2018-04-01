package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Random;

import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

public class GeometryBasedSolution extends MSolution {

	public GeometryBasedSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem, boxes);
		if(boxes==null) {
			this.optProblem = optProblem;
			this.boxes = new ArrayList<MBox>();
			MOptProblem instance = (MOptProblem)this.optProblem; 
			for (MRectangle m : instance.getRechtangles()) {
				MBox mBox = new MBox(instance.getBoxLength());
				mBox.optimalInsert(m);
				this.boxes.add(mBox);
			}
		}	
	}
	
	/**
	 * Randomly select a box in this solution. The probability that a box is selected is
	 * proportional to its free area.
	 * 
	 * @return	index of the selected box
	 */
	public int getRandomBoxProportionally() {
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
	 * Randomly select a box in this solution.
	 * 
	 * @return	index of the selected box
	 */
	public int getRandomBox() {
		Random r = new Random();
		return r.nextInt(this.boxes.size())-1;
	}

	
	/**
	 * Remove a box from this solution
	 * 
	 * @param index of the box
	 */
	public void removeBox(int index) {
		this.boxes.remove(index);
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
