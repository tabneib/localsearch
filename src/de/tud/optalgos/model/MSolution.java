package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Collections;

public class MSolution extends Solution {

	public MSolution(OptProblem optProblem) {
		super(optProblem);
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
}
