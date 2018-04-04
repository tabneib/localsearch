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
public abstract class MSolution extends Solution implements Cloneable {

	/**
	 * Total number of rounds that the algorithm has run
	 */
	private static int round = 0;
	
	/**
	 * The percentage amount to reduce the overlap rate in one reduction step.
	 */
	private static final double OVERLAP_RATE_REDUCE_STEP = 0.1; 
	
	/**
	 * List of the boxes used by this solution to store the rectangles given by
	 * the corresponding instance of the optimization problem.
	 */
	protected ArrayList<MBox> boxes;
	
	private double objectiveValue = -1;

	public MSolution(OptProblem optProblem, ArrayList<MBox> boxes) {
		super(optProblem);
		this.boxes = boxes;
	}

	@Override
	public double getObjective() {
		if (this.objectiveValue == -1){
			int totalRects = ((MOptProblem) optProblem).getRechtangles().size();
			ArrayList<Double> fillGrades = new ArrayList<Double>();
			for (MBox box : this.boxes)
				fillGrades.add((Double) box.getFillRate());
			Collections.sort(fillGrades);
			Collections.reverse(fillGrades);
			this.objectiveValue = 0;
			for (int i = 0; i < fillGrades.size(); i++) 
				this.objectiveValue += fillGrades.get(i) * (totalRects - i);
		}
		
		if (MRectangle.isOverlapPermitted())
			this.objectiveValue -= MSolution.getPenaltyRate() * this.getTotalOverlapArea();

		return this.objectiveValue;
	}
	
	/**
	 * Sum up all overlap area of the boxes
	 * @return	The total overlap area
	 */
	private double getTotalOverlapArea(){
		double total = 0;
		for (MBox box : this.boxes) 
			total += box.getOverlapArea();
		return total;
	}
	
	/**
	 * This method forces the objective value to be updated (recomputed) the next time.
	 */
	public void updateObjective() {
		this.objectiveValue = -1;
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
	
	public static double getPenaltyRate() {
		if (MRectangle.isOverlapPermitted())
			return Math.pow(round, 1.2);
		else 
			throw new RuntimeException("Cannot get penalty rate in case overlap is not permitted!");
	}
	
	/**
	 * Increase the counted round of the algorithm. This is used for the penalty of overlapping
	 */
	public static void increaseRound() {
		round++;
		if (MRectangle.isOverlapPermitted() && MRectangle.getOverlapRate() > MRectangle.MIN_OVERLAP_RATE)
			MRectangle.setOverlapRate(MRectangle.getOverlapRate() - OVERLAP_RATE_REDUCE_STEP);
	}
	
	public static void resetRound() {
		round = 0;
	}
}
