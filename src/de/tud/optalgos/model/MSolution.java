package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Collections;

import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

/**
 * This class represents a solution for the optimization problem targeted in
 * this project.
 *
 */
public class MSolution extends Solution implements Cloneable {

	/**
	 * List of the boxes used by this solution to store the rectangles given by
	 * the corresponding instance of the optimization problem.
	 */
	private ArrayList<MBox> boxes;

	public MSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem);
		this.boxes = boxes;
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
		double totalUsedArea = 0;
		for (MBox mBox : this.boxes)
			totalUsedArea += mBox.getFillGrade();
		double random = totalUsedArea * Math.random();
		int index = 0;
		for (MBox mBox : this.boxes) {
			random -= mBox.getFillGrade();
			if (random <= 0)
				return index;
			index++;
		}
		return 0;
	}

	/**
	 * TODO: Comment me
	 * 
	 * @param indexOfEmptyBox
	 */
	public void removeEmptyBox(int indexOfEmptyBox) {
		this.boxes.remove(indexOfEmptyBox);
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

	@Override
	public MSolution clone() {
		ArrayList<MBox> newBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.boxes)
			newBoxes.add(mBox.clone());
		MSolution newSolution = new MSolution(this.optProblem, newBoxes);
		return newSolution;
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
}
