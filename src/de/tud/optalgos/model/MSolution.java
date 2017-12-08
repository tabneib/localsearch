package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

/**
 * This class represents a solution for the optimization problem targeted in
 * this project.
 *
 */
public abstract class MSolution extends Solution implements Cloneable {

	/**
	 * List of the boxes used by this solution to store the rectangles given by
	 * the corresponding instance of the optimization problem.
	 */
	protected ArrayList<MBox> boxes;

	public MSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem);
		this.boxes = boxes;
	}

	

	@Override
	public double getObjective() {
		int num = ((MOptProblem) optProblem).getRechtangles().size();
		ArrayList<Double> fillGrades = new ArrayList<Double>();
		for (MBox box : this.boxes)
			fillGrades.add((Double) box.getFillGrade());
		Collections.sort(fillGrades);
		Collections.reverse(fillGrades);
		double score = 0;
		for (int i = 0; i < fillGrades.size(); i++) {
			score += fillGrades.get(i) * (num - i);
		}
		return score;
	}

	

	/**
	 * Retrieve the list of all rectangles stored in the boxes of this solution.
	 * 
	 * @return
	 */
	public ArrayList<MRectangle> getRechtangles() {
		ArrayList<MRectangle> rects = new ArrayList<>();
		for (MBox box : this.boxes)
			rects.addAll(box.getMRectangles());
		return rects;
	}

	public ArrayList<MBox> getBoxes() {
		return this.boxes;
	}

	public void setBoxes(ArrayList<MBox> boxes) {
		this.boxes = boxes;
	}
	
	/**
	 * TODO: Comment me
	 * 
	 * @return
	 */
	public int countRechtangles() {
		int count = 0;
		for (MBox box : this.boxes)
			count += box.getMRectangles().size();
		return count;
	}
}
