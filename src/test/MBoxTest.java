package test;

import org.junit.Before;
import org.junit.Test;

import de.tud.optalgos.model.MInstanceFactory;
import de.tud.optalgos.model.MOptProblem;
import de.tud.optalgos.model.geometry.MBox;
import de.tud.optalgos.model.geometry.MRectangle;

import static org.junit.Assert.*;

public class MBoxTest {

	private final int BOX_LENGTH = 400;

	private MBox box1;
	private MBox box2;
	private MBox box3;
	private MBox box4;
	private MBox box5;
	private MBox box6;
	private MBox box7;
	private MBox box8;
	private MBox box9;
	private MBox box10;
	private MBox box11;
	private MBox box12;
	private MBox box13;
	private MBox box14;
	private MBox box15;
	private MBox box16;
	private MBox box17;
	private MBox box18;
	private MBox box19;
	private MBox box20;

	// rect_width_height
	private MRectangle rect_400_400;
	private MRectangle rect_400_200;
	private MRectangle rect_200_400;
	private MRectangle rect_400_100;
	private MRectangle rect_100_400;
	private MRectangle rect_50_400;
	private MRectangle rect_400_50;

	private MRectangle rect_300_300;
	private MRectangle rect_300_200;
	private MRectangle rect_200_300;
	private MRectangle rect_300_100;
	private MRectangle rect_100_300;
	private MRectangle rect_300_50;
	private MRectangle rect_50_300;

	private MRectangle rect_200_200;
	private MRectangle rect_200_100;
	private MRectangle rect_100_200;
	private MRectangle rect_200_50;
	private MRectangle rect_50_200;
	private MRectangle rect_200_25;
	private MRectangle rect_25_200;

	private MRectangle rect_100_100;
	private MRectangle rect_100_50;
	private MRectangle rect_50_100;
	private MRectangle rect_100_25;
	private MRectangle rect_25_100;

	@Before
	public void prepareInputs() {

		box1 = new MBox(BOX_LENGTH);
		box2 = new MBox(BOX_LENGTH);
		box3 = new MBox(BOX_LENGTH);
		box4 = new MBox(BOX_LENGTH);
		box5 = new MBox(BOX_LENGTH);
		box6 = new MBox(BOX_LENGTH);
		box7 = new MBox(BOX_LENGTH);
		box8 = new MBox(BOX_LENGTH);
		box9 = new MBox(BOX_LENGTH);
		box10 = new MBox(BOX_LENGTH);
		box11 = new MBox(BOX_LENGTH);
		box12 = new MBox(BOX_LENGTH);
		box13 = new MBox(BOX_LENGTH);
		box14 = new MBox(BOX_LENGTH);
		box15 = new MBox(BOX_LENGTH);
		box16 = new MBox(BOX_LENGTH);
		box17 = new MBox(BOX_LENGTH);
		box18 = new MBox(BOX_LENGTH);
		box19 = new MBox(BOX_LENGTH);
		box20 = new MBox(BOX_LENGTH);

		rect_400_400 = new MRectangle(400, 400, BOX_LENGTH);
		rect_400_200 = new MRectangle(400, 200, BOX_LENGTH);
		rect_200_400 = rect_400_200.clone().rotate();
		rect_400_100 = new MRectangle(400, 100, BOX_LENGTH);
		rect_100_400 = rect_400_100.clone().rotate();
		rect_50_400 = new MRectangle(50, 400, BOX_LENGTH);
		rect_400_50 = rect_50_400.clone().rotate();

		rect_300_300 = new MRectangle(300, 300, BOX_LENGTH);
		rect_300_200 = new MRectangle(300, 200, BOX_LENGTH);
		rect_200_300 = rect_300_200.clone().rotate();
		rect_300_100 = new MRectangle(300, 100, BOX_LENGTH);
		rect_100_300 = rect_300_100.clone().rotate();
		rect_300_50 = new MRectangle(300, 50, BOX_LENGTH);
		rect_50_300 = rect_300_50.clone().rotate();

		rect_200_200 = new MRectangle(200, 200, BOX_LENGTH);
		rect_200_100 = new MRectangle(200, 100, BOX_LENGTH);
		rect_100_200 = rect_200_100.clone().rotate();
		rect_200_50 = new MRectangle(200, 50, BOX_LENGTH);
		rect_50_200 = rect_200_50.clone().rotate();
		rect_200_25 = new MRectangle(200, 25, BOX_LENGTH);
		rect_25_200 = rect_200_25.clone().rotate();

		rect_100_100 = new MRectangle(100, 100, BOX_LENGTH);
		rect_100_50 = new MRectangle(100, 50, BOX_LENGTH);
		rect_50_100 = rect_100_50.clone().rotate();
		rect_100_25 = new MRectangle(100, 25, BOX_LENGTH);
		rect_25_100 = rect_100_25.clone().rotate();
		
		
		
		
		
		// Case 1a: Box is full & contains 1 rectangle
		box1.insert(rect_400_400, 0, 0, false);

		// Case 1b: Box is full & contains 2 rectangles
		box2.insert(rect_400_200, 0, 0, false);
		box2.insert(rect_400_200, 0, 200, false);
		
		// Case 1c: Box is not full but not enough space

		// From East
		
		// Case 2a: succeeds immediately w/o push
		
		// Case 2a': succeeds immediately with push
		
		// Case 2b: succeeds after moving w/o push
		
		// Case 2b': succeeds after moving with push
		
		// Case 2b_rand: succeeds at last step w/o push
		
		// Case 2b'_rand: succeeds at last step with push

		// Case r2a
		
		// Case r2a'

		// Case r2b
		
		// Case r2b'
		
		// Case r2b_rand
		
		// Case r2b'_rand
		
		
		
	}

	@Test
	public void insert_valid() {

		assertTrue(box1.optimalInsert(rect_100_100));
		assertTrue(box2.optimalInsert(rect_50_100));

	}

	@Test
	public void insert_invalid() {
		// TODO

	}

	// ---------------------------------------------------------------------------------->
	// Auxiliary methods
	// <----------------------------------------------------------------------------------

}
