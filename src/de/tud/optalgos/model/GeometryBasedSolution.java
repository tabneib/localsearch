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
				mBox.insert(m);
				this.boxes.add(mBox);
			}
		}
		
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
