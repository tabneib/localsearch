package test;

import org.junit.Before;
import org.junit.Test;

import de.nhd.localsearch.problem.MInstanceFactory;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

import static org.junit.Assert.*;

public class MBoxOverlapTest {

	private final int BOX_LENGTH = 400;
	private MBox box;
	private MRectangle rect_401_400;
	private MRectangle rect_400_400;
	private MRectangle rect_400_200;
	private MRectangle rect_200_400;
	private MRectangle rect_400_201;
	private MRectangle rect_201_400;
	private MRectangle rect_400_300;
	private MRectangle rect_300_400;
	private MRectangle rect_400_100 = new MRectangle(400, 100, BOX_LENGTH);
	private MRectangle rect_100_400 = rect_400_100.rotate();
	private MRectangle rect_50_400 = new MRectangle(50, 400, BOX_LENGTH);
	private MRectangle rect_400_50 = rect_50_400.rotate();

	private MRectangle rect_300_300 = new MRectangle(300, 300, BOX_LENGTH);
	private MRectangle rect_300_200 = new MRectangle(300, 200, BOX_LENGTH);
	private MRectangle rect_200_300 = rect_300_200.rotate();
	private MRectangle rect_300_100 = new MRectangle(300, 100, BOX_LENGTH);
	private MRectangle rect_100_300 = rect_300_100.rotate();
	private MRectangle rect_300_50 = new MRectangle(300, 50, BOX_LENGTH);
	private MRectangle rect_50_300 = rect_300_50.rotate();

	private MRectangle rect_200_200 = new MRectangle(200, 200, BOX_LENGTH);
	private MRectangle rect_200_100 = new MRectangle(200, 100, BOX_LENGTH);
	private MRectangle rect_100_200 = rect_200_100.rotate();
	private MRectangle rect_200_50 = new MRectangle(200, 50, BOX_LENGTH);
	private MRectangle rect_50_200 = rect_200_50.rotate();
	private MRectangle rect_200_25 = new MRectangle(200, 25, BOX_LENGTH);
	private MRectangle rect_25_200 = rect_200_25.rotate();

	private MRectangle rect_100_100 = new MRectangle(100, 100, BOX_LENGTH);
	private MRectangle rect_100_50 = new MRectangle(100, 50, BOX_LENGTH);
	private MRectangle rect_50_100 = rect_100_50.rotate();
	private MRectangle rect_100_25 = new MRectangle(100, 25, BOX_LENGTH);
	private MRectangle rect_25_100 = rect_100_25.rotate();

	@Before
	public void prepareInputs() {
		box = new MBox(BOX_LENGTH);
		MRectangle.setOverlap(true);
		rect_401_400 = new MRectangle(401, 400, BOX_LENGTH);
		rect_400_400 = new MRectangle(400, 400, BOX_LENGTH);
		rect_400_200 = new MRectangle(400, 200, BOX_LENGTH);
		rect_200_400 = rect_400_200.rotate();
		rect_400_201 = new MRectangle(400, 201, BOX_LENGTH);

	}

	@Test
	public void insert_fullBox_valid() {
//		assertEquals(BOX_LENGTH * BOX_LENGTH, box.getFreeArea(), 0);
//		assertEquals(0, box.getFillArea(), 0);
		assertNotNull(box.optimalInsert(rect_400_400));
		assertEquals(0, box.getFreeArea(), 0);
	}

	@Test
	public void insert_largerBox_invalid() {
		assertNull(box.optimalInsert(rect_401_400));
		assertEquals(400 * 400, box.getFreeArea(), 0);
	}

	@Test
	public void insert_twoHorizontalHalves_valid() {
		assertNotNull(box.optimalInsert(rect_200_400));
		assertNotNull(box.optimalInsert(rect_200_400));
		assertEquals(200 * 200 / 2, box.getFreeArea(), 0);
		assertEquals(200 * 200 / 2, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_twoVerticalHalves_valid() {
		assertNotNull(box.optimalInsert(rect_400_200));
		assertNotNull(box.optimalInsert(rect_400_200));
		assertEquals(400 * 400 / 2, box.getFillArea(), 0);
		assertEquals(400 * 400 / 2, box.getFreeArea(), 0);
		assertEquals(200 * 200 / 2, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_twoLargerHorizontalHalves_valid() {
		assertNotNull(box.optimalInsert(rect_200_400));
		assertNull(box.optimalInsert(rect_300_400));
		assertEquals(200 * 200 / 2, box.getFreeArea(), 0);
		assertEquals(200 * 200 / 2, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_twoLargeVerticalHalves_valid() {
		assertNotNull(box.optimalInsert(rect_400_200));
		assertEquals(200 * 200 / 2, box.getFreeArea(), 0);
		assertEquals(200 * 200 / 2, box.getOverlapArea(), 0);
	}

}
