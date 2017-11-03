package test;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.tud.optalgos.model.InstanceFactory;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;
import static org.junit.Assert.*;


public class InstanceFactoryTest {

	ArrayList<int[]> getInstanceRandom_ValidInput;
	ArrayList<int[]> getInstanceSplit_ValidInput;
	
	@Before
	public void prepareInputs(){
		getInstanceRandom_ValidInput = new ArrayList<>();
		// amount, minLength, maxLength, boxLength
		getInstanceRandom_ValidInput.add(new int[]{1, 1, 1, 10});
		getInstanceRandom_ValidInput.add(new int[]{2, 1, 2, 10});
		getInstanceRandom_ValidInput.add(new int[]{2, 1, 10, 10});
		getInstanceRandom_ValidInput.add(new int[]{2, 9, 10, 10});
		getInstanceRandom_ValidInput.add(new int[]{2, 10, 10, 10});
		getInstanceRandom_ValidInput.add(new int[]{50, 4, 7, 10});
		getInstanceRandom_ValidInput.add(new int[]{100, 1, 10, 20});
		getInstanceRandom_ValidInput.add(new int[]{1000, 1, 100, 1000});
		getInstanceRandom_ValidInput.add(new int[]{10000, 1, 200, 1000});
		
		getInstanceSplit_ValidInput = new ArrayList<>();
		// initLength, boxLength, mnLength
		getInstanceSplit_ValidInput.add(new int[]{1, 1, 1});
		getInstanceSplit_ValidInput.add(new int[]{2, 2, 1});
		getInstanceSplit_ValidInput.add(new int[]{20, 10, 1});
		getInstanceSplit_ValidInput.add(new int[]{20, 20, 1});
		getInstanceSplit_ValidInput.add(new int[]{100, 10, 5});
		//getInstanceSplit_ValidInput.add(new int[]{1000, 10, 5}); // Too slow
		getInstanceSplit_ValidInput.add(new int[]{1000, 1000, 1});
		getInstanceSplit_ValidInput.add(new int[]{1000, 500, 50});
		getInstanceSplit_ValidInput.add(new int[]{1000, 900, 1});
	}

	
	@Test
	public void getInstanceRandom_validOutput() {
		for (int[] input : getInstanceRandom_ValidInput) 
			assertValidMInstance(InstanceFactory.
					getInstanceRandom(input[0], input[1], input[2], input[3]), 
					input[3], input[1], input[3]);
	}
	
	
	@Test
	public void getInstanceSplit_validOutput() {
		for (int[] input : getInstanceSplit_ValidInput) 
			assertValidMInstance(InstanceFactory.
					getInstanceSplit(input[0], input[1], input[2]), 
					input[1], 1, input[1]);
	}

	
	// ---------------------------------------------------------------------------------->
	// Auxiliary methods
	// <----------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param mInstance
	 * @param expectedBoxLength
	 * @param minLength
	 * @param maxLength
	 */
	private void assertValidMInstance(
			MInstance mInstance, int expectedBoxLength, int minLength, int maxLength) {
		
		assertEquals(expectedBoxLength, mInstance.getBoxLength());
		
		for (MRectangle r : mInstance.getRechtangles()) {
			assertValidMRectangle(r, mInstance.getBoxLength(), minLength, maxLength);
			// Box referenced from this rectangle are included in the instance
			assertTrue(mInstance.getBoxes().contains(r.getmBox()));
		}
		
		for (MBox b : mInstance.getBoxes()) {
			assertValidMbox(b, mInstance.getBoxLength());
			// All rectangles referenced from this box are included in the instance
			for (MRectangle r : b.getMRectangles())
				assertTrue(mInstance.getRechtangles().contains(r));
		}
	}
	
	
	/**
	 * 
	 * @param mRectangle
	 * @param expectedBoxLength
	 * @param minLength
	 * @param maxLength
	 */
	private void assertValidMRectangle(
			MRectangle mRectangle, int expectedBoxLength, int minLength, int maxLength) {

		assertEquals(expectedBoxLength, mRectangle.getBoxLength());
		
		assertTrue(mRectangle.height >= minLength);
		assertTrue(mRectangle.width <= maxLength);
		assertTrue(mRectangle.height <= expectedBoxLength);
		assertTrue(mRectangle.width <= expectedBoxLength);
	}
	
	
	/**
	 * 
	 * @param mBox
	 * @param expectedBoxLength
	 */
	private void assertValidMbox(MBox mBox, int expectedBoxLength) {
		
		assertEquals(expectedBoxLength, mBox.getBoxLength());
		
		// Must not be empty
		assertFalse(mBox.getMRectangles().isEmpty());
		
		// All rectangles are inside the box
		for (MRectangle r : mBox.getMRectangles()) 
			assertTrue(mBox.contains(r));
		
		// Rectangles do not pairwise intersect
		for (MRectangle r1 : mBox.getMRectangles()) {
			for (MRectangle r2 : mBox.getMRectangles()) {
				if (! r1.equals(r2))
					assertFalse(r1.intersects(r2));
			}
		}
	}
}
