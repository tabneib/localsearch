package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Collections;

public class MSolution extends Solution implements Cloneable{
	private ArrayList<MBox> boxes;
	public MSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem);
		this.boxes = boxes;
	}

	@Override
	public double getObjective() {
		MInstance instance = (MInstance) this.optProblem.getInstance();
		int num = instance.getRechtangles().size();
		ArrayList<MBox> boxes = instance.getBoxes();
		ArrayList<Double> fillGrades = new ArrayList<Double>();
		for (MBox box : boxes) {
			fillGrades.add((Double)box.getFillGrade());
		}
		Collections.sort(fillGrades);
		
		double score = 0;
		for (int i = 0; i < fillGrades.size() ; i++) {
			score += fillGrades.get(i)*(num-i);
		}
		return score;
	}
	
	@Override
	public MSolution clone() {
		ArrayList<MBox> newBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.boxes) {
			newBoxes.add(mBox);
		}
		MSolution newSolution = new MSolution(this.optProblem, newBoxes);
		return newSolution;
	}

	public ArrayList<MBox> getBoxes() {
		return boxes;
	}

	public void setBoxes(ArrayList<MBox> boxes) {
		this.boxes = boxes;
	}
}
