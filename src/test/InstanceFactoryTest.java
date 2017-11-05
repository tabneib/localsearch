package test;

import org.junit.Before;
import org.junit.Test;

import de.tud.optalgos.model.InstanceFactory;
import de.tud.optalgos.model.MBox;
import de.tud.optalgos.model.MInstance;
import de.tud.optalgos.model.MRectangle;
import static org.junit.Assert.*;


public class InstanceFactoryTest {

	int[] getInstanceRandom_ValidInput1;
	int[] getInstanceRandom_ValidInput2;
	int[] getInstanceRandom_ValidInput3;
	int[] getInstanceRandom_ValidInput4;
	int[] getInstanceRandom_ValidInput5;
	int[] getInstanceRandom_ValidInput6;
	int[] getInstanceRandom_ValidInput7;
	int[] getInstanceRandom_ValidInput8;
	int[] getInstanceRandom_ValidInput9;
	
	int[] getInstanceSplit_ValidInput1;
	int[] getInstanceSplit_ValidInput2;
	int[] getInstanceSplit_ValidInput3;
	int[] getInstanceSplit_ValidInput4;
	int[] getInstanceSplit_ValidInput5;
	int[] getInstanceSplit_ValidInput6;
	int[] getInstanceSplit_ValidInput7;
	int[] getInstanceSplit_ValidInput8;
	int[] getInstanceSplit_ValidInput9;
	
	
	@Before
	public void prepareInputs(){
		// amount, minLength, maxLength, boxLength
		getInstanceRandom_ValidInput1 = new int[]{1, 1, 1, 10};
		getInstanceRandom_ValidInput2 = new int[]{2, 1, 2, 10};
		getInstanceRandom_ValidInput3 = new int[]{2, 1, 10, 10};
		getInstanceRandom_ValidInput4 = new int[]{2, 9, 10, 10};
		getInstanceRandom_ValidInput5 = new int[]{2, 10, 10, 10};
		getInstanceRandom_ValidInput6 = new int[]{50, 4, 7, 10};
		getInstanceRandom_ValidInput7 = new int[]{100, 1, 10, 20};
		getInstanceRandom_ValidInput8 = new int[]{1000, 1, 100, 1000};
		getInstanceRandom_ValidInput9 = new int[]{10000, 1, 200, 1000};
		
		// initLength, boxLength, minLength
		getInstanceSplit_ValidInput1 = new int[]{1, 1, 1};
		getInstanceSplit_ValidInput2 = new int[]{2, 2, 1};
		getInstanceSplit_ValidInput3 = new int[]{20, 10, 1};
		getInstanceSplit_ValidInput4 = new int[]{20, 20, 1};
		getInstanceSplit_ValidInput5 = new int[]{20, 10, 5};
		getInstanceSplit_ValidInput6 = new int[]{1000, 10, 5}; 
		getInstanceSplit_ValidInput7 = new int[]{1000, 1000, 1};
		getInstanceSplit_ValidInput8 = new int[]{1000, 500, 50};
		getInstanceSplit_ValidInput9 = new int[]{1000, 900, 1};
	}

	
	@Test
	public void getInstanceRandom_validOutput() {
		
		int[] input;
		input = getInstanceRandom_ValidInput1;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);

		input = getInstanceRandom_ValidInput2;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput3;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput4;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput5;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput6;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput7;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput8;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
		
		input = getInstanceRandom_ValidInput9;
		assertValidMInstance(InstanceFactory.
				getInstanceRandom(input[0], input[1], input[2], input[3]), 
				input[3], input[1], input[3]);
	}
	
	
	@Test
	public void getInstanceSplit_validOutput() {
		
		// initLength, boxLength, minLength
		int[] input;

		input = getInstanceSplit_ValidInput1;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput2;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput3;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput4;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput5;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput6;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput7;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput8;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);

		input = getInstanceSplit_ValidInput9;
		assertValidMInstance(InstanceFactory.
				getInstanceSplit(input[0], input[1], input[2]), 
				input[1], input[2], input[1]);
		
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
			assertTrue(mBox.contains(r) || mBox.equals(r));
		
		// Rectangles do not pairwise intersect
		for (MRectangle r1 : mBox.getMRectangles()) {
			for (MRectangle r2 : mBox.getMRectangles()) {
				if (! r1.equals(r2))
					assertFalse(r1.intersects(r2));
			}
		}
	}
}
