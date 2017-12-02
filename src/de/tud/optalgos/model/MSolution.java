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
		ArrayList<Double> fillGrades = new ArrayList<Double>();
		for (MBox box : this.boxes) {
			fillGrades.add((Double)box.getFillGrade());
		}
		Collections.sort(fillGrades);
		Collections.reverse(fillGrades);
		double score = 0;
		for (int i = 0; i < fillGrades.size() ; i++) {
			score += fillGrades.get(i)*(num - i);
		}
		return score;
	}
	
	@Override
	public MSolution clone() {
		ArrayList<MBox> newBoxes = new ArrayList<MBox>();
		for (MBox mBox : this.boxes) {
				newBoxes.add(mBox.clone());
		}
		MSolution newSolution = new MSolution(this.optProblem, newBoxes);
		return newSolution;
	}
	
	

	public ArrayList<MBox> getBoxes() {
		return this.boxes;
	}
	
	public int getRandomBoxIndexForEmpty(){
		double totalFreeArea = 0;
		for (MBox mBox : this.boxes) 
		{
			totalFreeArea += mBox.getFreeArea();
		}
		double random = totalFreeArea*Math.random();
		int index = 0;
		for (MBox mBox : this.boxes) 
		{
			random -= mBox.getFreeArea();
			if(random<=0) {
				return index;
			}
			index++;
		}
		return this.boxes.size()-1;
	}
	
	public int getRandomBoxIndexForFilling(){
		double totalUsedArea = 0;
		for (MBox mBox : this.boxes) 
		{
			totalUsedArea += mBox.getFillGrade();
		}
		double random = totalUsedArea*Math.random();
		int index = 0;
		for (MBox mBox : this.boxes) 
		{
			random -= mBox.getFillGrade();
			if(random<=0) {
				return index;
			}
			index++;
		}
		return 0;
	}
	
	public void removeEmptyBox(int indexOfEmptyBox) {
		this.boxes.remove(indexOfEmptyBox);
	}

	public void setBoxes(ArrayList<MBox> boxes) {
		this.boxes = boxes;
	}
	
}
