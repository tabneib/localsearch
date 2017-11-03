package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Factory class for generating instances as inputs for the algorithms
 *
 */
public class InstanceFactory {

	
	/**
	 * 
	 * @param amount
	 * @param minLength
	 * @param maxLength
	 * @param boxLength
	 * @return
	 */
	public static MInstance getInstanceRandom(
			int amount, int minLength, int maxLength, int boxLength) {
		
		ArrayList<MRectangle> rectangles = new ArrayList<>();
		ArrayList<MBox> boxes = new ArrayList<>();
		Random widthR = new Random(System.currentTimeMillis());
		Random heightR = new Random(System.currentTimeMillis());
		
		MRectangle rectangle;
		MBox box;
		HashSet<MRectangle> initRectSet;
		for (int i = 0; i < amount; i++) {
			rectangle = new MRectangle(0, 0,
					widthR.nextInt(maxLength-minLength+1) + minLength , 
					heightR.nextInt(maxLength-minLength+1) + minLength ,
					boxLength);
			initRectSet = new HashSet<MRectangle>();
			initRectSet.add(rectangle);
			box = new MBox(boxLength, initRectSet);
			rectangle.setmBox(box);
			rectangles.add(rectangle);
			boxes.add(box);
		}
		return new MInstance(boxLength, rectangles, boxes);
	}
	
	
	/**
	 * 
	 * @param amount
	 * @param initLength
	 * @return
	 */
	public static MInstance getInstanceSplit(
			int initLength, int boxLength, int minLength) {
		
		// Create rectangles
		ArrayList<MRectangle> rectangles = splitter(initLength, boxLength, minLength);
		
		// Create corresponding boxes
		ArrayList<MBox> boxes = new ArrayList<>();
		MBox box;
		HashSet<MRectangle> initRectSet;
		for (MRectangle rect : rectangles) {
			initRectSet = new HashSet<MRectangle>();
			initRectSet.add(rect);
			box = new MBox(boxLength, initRectSet);
			rect.setmBox(box);
			boxes.add(box);
			// Set the anchor to (0,0)
			rect.setLocation(0, 0);
		}
		
		return new MInstance(boxLength, rectangles, boxes);
	}
	

	// ---------------------------------------------------------------------------------->
	// Auxiliary methods
	// <----------------------------------------------------------------------------------
	
	
	/**
	 * (For testing GUI purpose)
	 * @param initLength
	 * @param boxLength
	 * @return
	 */
	public static ArrayList<MRectangle> splitter(
			int initLength, int boxLength, int minLength) {
		PriorityQueue<Integer> maxSize = 
				new PriorityQueue<>(new Comparator<Integer>() {

			@Override
			public int compare(Integer n1, Integer n2) {
				if (n1 > n2)
					return -1;
				else if (n2 > n1)
					return 1;
				return 0;
			}
		});

		maxSize.add(initLength);
		
		ArrayList<MRectangle> rectangles = new ArrayList<>();
		rectangles.add(new MRectangle(0, 0, initLength, initLength, boxLength));
		ArrayList<MRectangle> results = new ArrayList<>();
		
		MRectangle r;
		MRectangle[] rSplit;
		while (!maxSize.isEmpty() && 
				maxSize.peek() >= boxLength && maxSize.peek() >= 3) {

			System.out.println("maxSize               ===> " + maxSize);
			r = getRandomRect(rectangles);
			rectangles.remove(r);
			maxSize.remove(r.getMaxSize());
			
			if (r.getMinSize() < minLength * 2)
				results.add(r);
			else{
				rSplit = randomlySplit(r, minLength);

				for (int i = 0; i < 2; i++) {
					if (rSplit[i].getMinSize() == minLength) 
						results.add(rSplit[i]);
					else {
						maxSize.add(rSplit[i].getMaxSize());
						rectangles.add(rSplit[i]);
					}
				}
			}
			System.out.println("-----------------------------------------------------");
		}
		return rectangles;
	}
	
	
	/**
	 * Find a random rectangle from the given list of rectangles.
	 * The probability that a rectangle is chosen is proportional with its max size
	 * (the longer one out of its width and height).
	 * @param rectangles
	 * @return
	 */
	private static MRectangle getRandomRect(ArrayList<MRectangle> rectangles) {
		int sum = 0;
		for (MRectangle r : rectangles) 
			sum = sum + r.getMaxSize();
		//System.out.println("getRandomRect : sum ===> " + sum);
		
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
	 * @param rectangle
	 * @return
	 */
	private static MRectangle[] randomlySplit(MRectangle rectangle, int minLength) {
		MRectangle[] result = new MRectangle[2];
		
		double randomNum = Math.random() * (rectangle.getHeight() + rectangle.getWidth());

		if (randomNum - rectangle.getHeight() <= 0) {
			// split horizontally
			// minLength + 1 <= splitY <= (height - 1) 
			int splitY = (int) (Math.random() * 
					(rectangle.getHeight() - 1 - minLength)) + minLength;
			result[0] = new MRectangle((int) rectangle.getX(), (int) rectangle.getY(), 
					(int) rectangle.getWidth(), splitY, rectangle.getBoxLength());
			result[1] = new MRectangle((int) rectangle.getX(), (int) splitY, 
					(int) rectangle.getWidth(), (int) rectangle.getHeight() - splitY, 
					rectangle.getBoxLength());
		}
		else {
			// split vertically
			// minLength +1 <= splitX <= (width - 1) 
			int splitX = (int) (Math.random() * 
					(rectangle.getWidth() - 1 - minLength)) + minLength;
			result[0] = new MRectangle((int) rectangle.getX(), (int) rectangle.getY(), 
					splitX, (int) rectangle.getHeight(), rectangle.getBoxLength());
			result[1] = new MRectangle(splitX,  (int) rectangle.getY(), 
					(int) rectangle.getWidth() - splitX, (int) rectangle.getHeight(), 
					rectangle.getBoxLength());
		}
		return result;
	}
}
