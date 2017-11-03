package de.tud.optalgos.model;

import java.util.ArrayList;
import java.util.HashSet;
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
	public static MInstance getRandomInstance(
			int amount, int minLength, int maxLength, int boxLength) {
		
		ArrayList<MRectangle> rectangles = new ArrayList<>();
		ArrayList<MBox> boxes = new ArrayList<>();
		Random widthR = new Random(System.currentTimeMillis());
		Random heightR = new Random(System.currentTimeMillis());
		
		MRectangle rectange;
		MBox box;
		for (int i = 0; i < amount; i++) {
			rectange = new MRectangle(0, 0,
					widthR.nextInt(maxLength-minLength+1) + minLength , 
					heightR.nextInt(maxLength-minLength+1) + minLength ,
					boxLength);
			HashSet<MRectangle> initRectSet = new HashSet<MRectangle>();
			initRectSet.add(rectange);
			box = new MBox(boxLength, initRectSet);
			rectange.setmBox(box);
			rectangles.add(rectange);
			boxes.add(box);
		}
		return new MInstance(boxLength, rectangles, boxes);
	}
}
