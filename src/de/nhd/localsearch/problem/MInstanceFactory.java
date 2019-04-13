package de.nhd.localsearch.problem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

/**
 * Factory class for generating instances as inputs for the algorithms
 *
 */
public class MInstanceFactory {

	private static final String direction = OptProblem.Direction.MINIMIZING;

	/**
	 * ratio between box size and the maximum size of the resulted rects.
	 * Value 1 means the longer size rects can be as large as the box size
	 */
	private static final double MAX_SIZE_RATIO = 1.5;
	
	/**
	 * ratio between box size and the minimum size of the resulted rects.
	 * Value 1 means the shorter size rects can be as large as the box size
	 */
	private static final double MIN_SIZE_RATIO = 10;

	/**
	 * 
	 * @param amount
	 * @param minLength
	 * @param maxLength
	 * @param boxLength
	 * @return
	 */
	public static MOptProblem getInstanceRandom(int amount, int minLength, int maxLength,
			int boxLength) {

		ArrayList<MRectangle> rectangles = new ArrayList<>();
		ArrayList<MBox> boxes = new ArrayList<>();
		Random widthR = new Random();
		Random heightR = new Random();

		MRectangle rectangle;
		MBox box;
		ArrayList<MRectangle> initRectSet;
		for (int i = 0; i < amount; i++) {
			rectangle = new MRectangle(0, 0,
					widthR.nextInt(maxLength - minLength + 1) + minLength,
					heightR.nextInt(maxLength - minLength + 1) + minLength, boxLength);
			initRectSet = new ArrayList<MRectangle>();
			initRectSet.add(rectangle);
			box = new MBox(boxLength, initRectSet);
			rectangle.setBox(box);
			rectangles.add(rectangle);
			boxes.add(box);
		}
		return new MOptProblem(direction, boxLength, rectangles, boxes);
	}

	/**
	 * 
	 * @param initLength
	 * @param boxLength
	 * @param minLength
	 * @return
	 */
	public static MOptProblem getInstanceSplit(int initLength, int boxLength,
			int minLength) {

		// Create rectangles
		ArrayList<MRectangle> rectangles = split(initLength, boxLength, minLength);

		// Create corresponding boxes
		ArrayList<MBox> boxes = new ArrayList<>();
		MBox box;
		ArrayList<MRectangle> initRectSet;
		for (MRectangle rect : rectangles) {
			// Set the anchor to (0,0)
			rect.setLocation(0, 0);
			initRectSet = new ArrayList<MRectangle>();
			initRectSet.add(rect);
			box = new MBox(boxLength, initRectSet);
			rect.setBox(box);
			boxes.add(box);
		}

		return new MOptProblem(direction, boxLength, rectangles, boxes);
	}

	// ---------------------------------------------------------------------------------->
	// Auxiliary methods
	// <----------------------------------------------------------------------------------

	/**
	 * (For testing GUI purpose)
	 * 
	 * @param initLength
	 * @param boxLength
	 * @return
	 */
	public static ArrayList<MRectangle> split(int initLength, int boxLength,
			int minLength) {

		if (minLength < 0) {
			minLength = (int) (boxLength / MIN_SIZE_RATIO);
		}
		
		PriorityQueue<MRectangle> rects = new PriorityQueue<>(
				new Comparator<MRectangle>() {

					@Override
					public int compare(MRectangle r1, MRectangle r2) {
						if (r1.getMaxSize() > r2.getMaxSize())
							return -1;
						else if (r2.getMaxSize() > r1.getMaxSize())
							return 1;
						return 0;
					}
				});

		rects.add(new MRectangle(0, 0, initLength, initLength, boxLength));
		ArrayList<MRectangle> results = new ArrayList<>();

		MRectangle r;
		MRectangle[] rSplit;
		while (!rects.isEmpty() && rects.peek().getMaxSize() >= boxLength / MAX_SIZE_RATIO
				&& rects.peek().getMaxSize() >= minLength * 2) {
			r = getRandomRect(rects);
			rects.remove(r);

			// Check if r has an edge that cannot be split any further
			// and the other edge is not needed to split any further
			if (r.getMaxSize() <= boxLength / MAX_SIZE_RATIO
					&& r.getMinSize() < minLength * 2)
				results.add(r);
			else {
				rSplit = randomlySplit(r, minLength);

				for (MRectangle rekt : rSplit)
					if (rekt.getMinSize() < minLength * 2
							&& rekt.getMaxSize() <= boxLength / MAX_SIZE_RATIO)
						results.add(rekt);
					else
						rects.add(rekt);
			}
		}

		results.addAll(rects);
		return results;
	}

	/**
	 * Find a random rectangle from the given list of rectangles. The
	 * probability that a rectangle is chosen is proportional with its max size
	 * (the longer one out of its width and height).
	 * 
	 * @param rectangles
	 * @return
	 */
	private static MRectangle getRandomRect(PriorityQueue<MRectangle> rectangles) {
		int sum = 0;
		for (MRectangle r : rectangles)
			sum = sum + r.getMaxSize();

		double randomNum = Math.random() * sum;

		for (MRectangle r : rectangles) {
			randomNum -= r.getMaxSize();
			if (randomNum <= 0)
				return r;
		}

		throw new RuntimeException("Cannot find a random rectangle!");
	}

	/**
	 * 
	 * @param rect
	 * @return
	 */
	private static MRectangle[] randomlySplit(MRectangle rect, int minLength) {
		MRectangle[] result = new MRectangle[2];

		if (rect.getHeight() < 2 * minLength && rect.getWidth() < 2 * minLength)
			throw new RuntimeException("Both edges cannot be split : (" + rect.getWidth()
					+ ", " + rect.getHeight() + ")");

		double randomNum = Math.random() * (rect.getHeight() + rect.getWidth());
		boolean splitVertically;
		
		if ((randomNum - rect.getHeight()) <= 0)
			// Height edge is chosen..
			if (rect.getHeight() >= 2 * minLength)
				// ..and it can be split further
				splitVertically = false;
			else
				// It cannot be split further => split the other edge instead
				splitVertically = true;
		else
		// Width edge is chosen..
		if (rect.getWidth() >= 2 * minLength)
			// ..and it can be split further
			splitVertically = true;
		else
			// It cannot be split further => split the other edge instead
			splitVertically = false;

		if (!splitVertically) {
			// split horizontally
			// minLength <= splitY <= (height - minLength)
			int splitY = ((int) (Math.random() * (rect.getHeight() + 1 - 2 * minLength)))
					+ minLength;

			result[0] = new MRectangle((int) rect.getX(), (int) rect.getY(),
					(int) rect.getWidth(), splitY, rect.getBoxLength());

			result[1] = new MRectangle((int) rect.getX(), (int) rect.getY() + splitY,
					(int) rect.getWidth(), (int) rect.getHeight() - splitY,
					rect.getBoxLength());
		} else {
			// split vertically
			// minLength <= splitX <= (width - minLength)
			int splitX = ((int) (Math.random() * (rect.getWidth() + 1 - 2 * minLength)))
					+ minLength;

			result[0] = new MRectangle((int) rect.getX(), (int) rect.getY(), splitX,
					(int) rect.getHeight(), rect.getBoxLength());

			result[1] = new MRectangle((int) rect.getX() + splitX, (int) rect.getY(),
					(int) rect.getWidth() - splitX, (int) rect.getHeight(),
					rect.getBoxLength());
		}
		return result;
	}
}
