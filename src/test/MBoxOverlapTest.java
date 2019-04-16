package test;

import org.junit.Before;
import org.junit.Test;

import de.nhd.localsearch.problem.MInstanceFactory;
import de.nhd.localsearch.problem.MOptProblem;
import de.nhd.localsearch.problem.geometry.MBox;
import de.nhd.localsearch.problem.geometry.MRectangle;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class MBoxOverlapTest {

	private final int BOX_LENGTH = 400;
	private MBox box;
	private MRectangle rect_401_400;
	private MRectangle rect_400_400;
	private MRectangle rect_400_200A;
	private MRectangle rect_200_400A;
	private MRectangle rect_400_200B;
	private MRectangle rect_200_400B;
	private MRectangle rect_400_201;
	private MRectangle rect_201_400;
	private MRectangle rect_400_300;
	private MRectangle rect_300_400;
	private MRectangle rect_400_100;
	private MRectangle rect_100_400;
	private MRectangle rect_50_400;
	private MRectangle rect_400_50;

	private MRectangle rect_300_300;
	private MRectangle rect_300_200;
	private MRectangle rect_200_300;
	private MRectangle rect_300_100A;
	private MRectangle rect_300_100B;
	private MRectangle rect_300_100C;
	private MRectangle rect_100_300;
	private MRectangle rect_300_50;
	private MRectangle rect_50_300;

	private MRectangle rect_200_200A;
	private MRectangle rect_200_200B;
	private MRectangle rect_200_100;
	private MRectangle rect_100_200;
	private MRectangle rect_200_50;
	private MRectangle rect_50_200;
	private MRectangle rect_200_25;
	private MRectangle rect_25_200;

	private MRectangle rect_100_100;
	private MRectangle rect_150_150;
	private MRectangle rect_100_50;
	private MRectangle rect_50_100;
	private MRectangle rect_100_25;
	private MRectangle rect_25_100;

	@Before
	public void prepareInputs() {
		box = new MBox(BOX_LENGTH);
		MRectangle.setOverlapMode(true);
		rect_401_400 = new MRectangle(401, 400, BOX_LENGTH);
		rect_400_400 = new MRectangle(400, 400, BOX_LENGTH);
		rect_400_200A = new MRectangle(400, 200, BOX_LENGTH);
		rect_200_400A = new MRectangle(200, 400, BOX_LENGTH);
		rect_400_200B = new MRectangle(400, 200, BOX_LENGTH);
		rect_200_400B = new MRectangle(200, 400, BOX_LENGTH);
		rect_400_201 = new MRectangle(400, 201, BOX_LENGTH);
		rect_201_400 = new MRectangle(201, 400, BOX_LENGTH);
		rect_300_400 = new MRectangle(300, 400, BOX_LENGTH);
		rect_200_200A = new MRectangle(200, 200, BOX_LENGTH);
		rect_200_200B = new MRectangle(200, 200, BOX_LENGTH);
		rect_100_100 = new MRectangle(100, 100, BOX_LENGTH);
		rect_100_200 = new MRectangle(100, 200, BOX_LENGTH);
		rect_200_100 = new MRectangle(200, 100, BOX_LENGTH);
		rect_400_300 = new MRectangle(400, 300, BOX_LENGTH);
		rect_100_300 = new MRectangle(100, 300, BOX_LENGTH);
		rect_300_100A = new MRectangle(300, 100, BOX_LENGTH);
		rect_300_100B = new MRectangle(300, 100, BOX_LENGTH);
		rect_300_100C = new MRectangle(300, 100, BOX_LENGTH);
		rect_400_100 = new MRectangle(400, 100, BOX_LENGTH);
		rect_150_150 = new MRectangle(150, 150, BOX_LENGTH);
	}

	@Test
	public void insert_fullBox_valid() {
		// assertEquals(BOX_LENGTH * BOX_LENGTH, box.getFreeArea(), 0);
		// assertEquals(0, box.getFillArea(), 0);
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
		assertNotNull(box.optimalInsert(rect_200_400A));
		assertNotNull(box.optimalInsert(rect_200_400B));
		assertEquals(400 * 200, box.getMRectById(rect_200_400A.getId()).getOverlapArea(),
				0);
		assertEquals(400 * 200, box.getMRectById(rect_200_400B.getId()).getOverlapArea(),
				0);
		assertEquals(400 * 400 / 2, box.getFillArea(), 0);
		assertEquals(400 * 400 / 2, box.getFreeArea(), 0);
		assertEquals(400 * 400 / 2, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_twoVerticalHalves_valid() {
		assertNotNull(box.optimalInsert(rect_400_200A));
		assertNotNull(box.optimalInsert(rect_400_200B));
		assertEquals(400 * 200, box.getMRectById(rect_400_200A.getId()).getOverlapArea(),
				0);
		assertEquals(400 * 200, box.getMRectById(rect_400_200B.getId()).getOverlapArea(),
				0);
		assertEquals(400 * 400 / 2, box.getFillArea(), 0);
		assertEquals(400 * 400 / 2, box.getFreeArea(), 0);
		assertEquals(400 * 400 / 2, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_twoDiffrentSizedRects_valid() {
		assertNotNull(box.optimalInsert(rect_200_400A));
		assertNotNull(box.optimalInsert(rect_300_400));
		assertEquals(100 * 400, box.getFreeArea(), 0);
		assertEquals(200 * 400, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_totalSizeExceedsBoxSize_invalid() {
		assertNotNull(box.optimalInsert(rect_400_200A));
		assertNotNull(box.optimalInsert(rect_400_200B));
		assertNull(box.optimalInsert(rect_400_300));
		assertEquals(200 * 400, box.getFreeArea(), 0);
		assertEquals(200 * 400, box.getOverlapArea(), 0);
	}

	@Test
	public void insert_manyMRectsMaxOverlapRate_valid() {

		assertEquals(0, box.getFillArea(), 0);
		assertEquals(400 * 400, box.getFreeArea(), 0);
		assertEquals(0, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_200_200A));
		assertEquals(4 * 100 * 100, box.getFillArea(), 0);
		assertEquals(12 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(0, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_100_300));
		assertEquals(5 * 100 * 100, box.getFillArea(), 0);
		assertEquals(11 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(2 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_300_100A));
		assertEquals(7 * 100 * 100, box.getFillArea(), 0);
		assertEquals(9 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(3 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_100_100));
		assertEquals(8 * 100 * 100, box.getFillArea(), 0);
		assertEquals(8 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(3 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_200_100));
		assertEquals(9 * 100 * 100, box.getFillArea(), 0);
		assertEquals(7 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(4 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_300_100B));
		assertEquals(11 * 100 * 100, box.getFillArea(), 0);
		assertEquals(5 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(5 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_200_200B));
		assertEquals(12 * 100 * 100, box.getFillArea(), 0);
		assertEquals(4 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(8 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_400_100));
		assertEquals(15 * 100 * 100, box.getFillArea(), 0);
		assertEquals(1 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(9 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_300_100C));
		assertEquals(16 * 100 * 100, box.getFillArea(), 0);
		assertEquals(0 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(11 * 100 * 100, box.getOverlapArea(), 0);

		assertEquals(200, box.getMRectById(rect_200_200A.getId()).x);
		assertEquals(200, box.getMRectById(rect_200_200A.getId()).y);
		assertEquals(200 * 200, box.getMRectById(rect_200_200A.getId()).getOverlapArea(),
				0);

		assertEquals(300, box.getMRectById(rect_100_300.getId()).x);
		assertEquals(100, box.getMRectById(rect_100_300.getId()).y);
		assertEquals(100 * 300, box.getMRectById(rect_100_300.getId()).getOverlapArea(),
				0);

		assertEquals(100, box.getMRectById(rect_300_100A.getId()).x);
		assertEquals(100, box.getMRectById(rect_300_100A.getId()).y);
		assertEquals(300 * 100, box.getMRectById(rect_300_100A.getId()).getOverlapArea(),
				0);

		assertEquals(300, box.getMRectById(rect_100_100.getId()).x);
		assertEquals(0, box.getMRectById(rect_100_100.getId()).y);
		assertEquals(100 * 100, box.getMRectById(rect_100_100.getId()).getOverlapArea(),
				0);

		assertEquals(200, box.getMRectById(rect_200_100.getId()).x);
		assertEquals(0, box.getMRectById(rect_200_100.getId()).y);
		assertEquals(200 * 100, box.getMRectById(rect_200_100.getId()).getOverlapArea(),
				0);

		assertEquals(0, box.getMRectById(rect_300_100B.getId()).x);
		assertEquals(300, box.getMRectById(rect_300_100B.getId()).y);
		assertEquals(2 * 100 * 100,
				box.getMRectById(rect_300_100B.getId()).getOverlapArea(), 0);

		assertEquals(100, box.getMRectById(rect_200_200B.getId()).x);
		assertEquals(100, box.getMRectById(rect_200_200B.getId()).y);
		assertEquals(3 * 100 * 100,
				box.getMRectById(rect_200_200B.getId()).getOverlapArea(), 0);

		assertEquals(0, box.getMRectById(rect_400_100.getId()).x);
		assertEquals(0, box.getMRectById(rect_400_100.getId()).y);
		assertEquals(100, box.getMRectById(rect_400_100.getId()).getWidth(), 0);
		assertEquals(400, box.getMRectById(rect_400_100.getId()).getHeight(), 0);
		assertEquals(2 * 100 * 100,
				box.getMRectById(rect_400_100.getId()).getOverlapArea(), 0);

		assertEquals(0, box.getMRectById(rect_300_100C.getId()).x);
		assertEquals(0, box.getMRectById(rect_300_100C.getId()).y);
		assertEquals(2 * 100 * 100,
				box.getMRectById(rect_300_100C.getId()).getOverlapArea(), 0);
	}

	@Test
	public void insert_manyMRectsMiddleOverlapRate_valid() {
		MRectangle.setOverlapRate(0.5);

		assertEquals(0, box.getFillArea(), 0);
		assertEquals(400 * 400, box.getFreeArea(), 0);
		assertEquals(0, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_300_100A));
		assertEquals(3 * 100 * 100, box.getFillArea(), 0);
		assertEquals(13 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(0, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_100_200));
		assertEquals(4 * 100 * 100, box.getFillArea(), 0);
		assertEquals(12 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(1 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_200_200A));
		assertEquals(8 * 100 * 100, box.getFillArea(), 0);
		assertEquals(8 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(1 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_300_100B));
		assertEquals(9.5 * 100 * 100, box.getFillArea(), 0);
		assertEquals(6.5 * 100 * 100, box.getFreeArea(), 0);
		assertEquals(2.5 * 100 * 100, box.getOverlapArea(), 0);

		assertNotNull(box.optimalInsert(rect_150_150));
		assertEquals(9.5 * 100 * 100 + 100 * 100 + 50 * 150, box.getFillArea(), 0);
		assertEquals(6.5 * 100 * 100 - 100 * 100 - 50 * 150, box.getFreeArea(), 0);
		assertEquals(3 * 100 * 100, box.getOverlapArea(), 0);

		// Test MRectangles' position & overlap area
		assertEquals(100, box.getMRectById(rect_300_100A.getId()).x);
		assertEquals(300, box.getMRectById(rect_300_100A.getId()).y);
		assertEquals(100 * 300 / 2,
				box.getMRectById(rect_300_100A.getId()).getOverlapArea(), 0);

		assertEquals(300, box.getMRectById(rect_100_200.getId()).x);
		assertEquals(200, box.getMRectById(rect_100_200.getId()).y);
		assertEquals(100 * 200 / 2,
				box.getMRectById(rect_100_200.getId()).getOverlapArea(), 0);

		assertEquals(200, box.getMRectById(rect_200_200A.getId()).x);
		assertEquals(0, box.getMRectById(rect_200_200A.getId()).y);
		assertEquals(100 * 150, box.getMRectById(rect_200_200A.getId()).getOverlapArea(),
				0);

		assertEquals(50, box.getMRectById(rect_300_100B.getId()).x);
		assertEquals(100, box.getMRectById(rect_300_100B.getId()).y);
		assertEquals(100 * 300 / 2,
				box.getMRectById(rect_300_100A.getId()).getOverlapArea(), 0);

		assertEquals(50, box.getMRectById(rect_150_150.getId()).x);
		assertEquals(200, box.getMRectById(rect_150_150.getId()).y);
		assertEquals(50 * 100,
				box.getMRectById(rect_150_150.getId()).getOverlapArea(), 0);
	}

	@Test
	public void checkPlaceable_maxOverlapRate() {

		MBox box = makeTestMBox();

		MRectangle rect = new MRectangle(100, 100, BOX_LENGTH);
		// Not overlap
		assertTrue(box.checkPlaceable(rect, 100, 100));
		assertTrue(box.checkPlaceable(rect, 0, 0));
		assertTrue(box.checkPlaceable(rect, 300, 0));
		assertTrue(box.checkPlaceable(rect, 0, 30));
		assertTrue(box.checkPlaceable(rect, 100, 300));

		// Overlap or Exceed
		assertTrue(box.checkPlaceable(rect, 200, 200));
		assertTrue(box.checkPlaceable(rect, 300, 300));
		assertTrue(box.checkPlaceable(rect, 250, 150));
		assertTrue(box.checkPlaceable(rect, 50, 150));
		assertFalse(box.checkPlaceable(rect, 150, 350));

		rect = new MRectangle(200, 100, BOX_LENGTH);
		// Not overlap
		assertTrue(box.checkPlaceable(rect, 0, 0));
		assertTrue(box.checkPlaceable(rect, 10, 0));
		assertTrue(box.checkPlaceable(rect, 100, 0));
		assertTrue(box.checkPlaceable(rect, 200, 0));
		assertTrue(box.checkPlaceable(rect, 0, 1));
		assertTrue(box.checkPlaceable(rect, 0, 99));
		assertTrue(box.checkPlaceable(rect, 0, 100));
		assertTrue(box.checkPlaceable(rect, 0, 300));

		// Overlap or Exceed
		assertTrue(box.checkPlaceable(rect, 100, 100));
		assertTrue(box.checkPlaceable(rect, 10, 1));
		assertTrue(box.checkPlaceable(rect, 1, 10));
		assertTrue(box.checkPlaceable(rect, 1, 1));
		assertTrue(box.checkPlaceable(rect, 100, 1));
		assertTrue(box.checkPlaceable(rect, 10, 99));
		assertTrue(box.checkPlaceable(rect, 100, 99));
		assertFalse(box.checkPlaceable(rect, 201, 0));
		assertTrue(box.checkPlaceable(rect, 200, 1));
		assertTrue(box.checkPlaceable(rect, 1, 300));
		assertFalse(box.checkPlaceable(rect, 0, 301));
		assertFalse(box.checkPlaceable(rect, 0, 400));
		assertFalse(box.checkPlaceable(rect, 400, 0));
		assertFalse(box.checkPlaceable(rect, 300, 0));
		assertTrue(box.checkPlaceable(rect, 0, 101));
		assertTrue(box.checkPlaceable(rect, 0, 200));
		assertTrue(box.checkPlaceable(rect, 200, 200));
		assertTrue(box.checkPlaceable(rect, 199, 199));
		assertFalse(box.checkPlaceable(rect, 500, 0));
		assertFalse(box.checkPlaceable(rect, 500, 200));
		assertFalse(box.checkPlaceable(rect, 500, 400));
		assertFalse(box.checkPlaceable(rect, 500, 500));
	}

	@Test
	public void checkPlaceable_MiddleOverlapRate() {

		MRectangle.setOverlapRate(0.5);
		MBox box = makeTestMBox();

		MRectangle rect = new MRectangle(100, 100, BOX_LENGTH);
		// Not overlap
		assertTrue(box.checkPlaceable(rect, 100, 100));
		assertTrue(box.checkPlaceable(rect, 0, 0));
		assertTrue(box.checkPlaceable(rect, 300, 0));
		assertTrue(box.checkPlaceable(rect, 0, 30));
		assertTrue(box.checkPlaceable(rect, 100, 300));

		// Overlap or Exceed
		assertFalse(box.checkPlaceable(rect, 200, 200));
		assertFalse(box.checkPlaceable(rect, 300, 300));
		assertFalse(box.checkPlaceable(rect, 250, 150));
		assertTrue(box.checkPlaceable(rect, 50, 150));
		assertTrue(box.checkPlaceable(rect, 100, 150));
		assertFalse(box.checkPlaceable(rect, 150, 150));
		assertFalse(box.checkPlaceable(rect, 150, 350));

		rect = new MRectangle(200, 100, BOX_LENGTH);
		// Not overlap
		assertTrue(box.checkPlaceable(rect, 0, 0));
		assertTrue(box.checkPlaceable(rect, 10, 0));
		assertTrue(box.checkPlaceable(rect, 100, 0));
		assertTrue(box.checkPlaceable(rect, 200, 0));
		assertTrue(box.checkPlaceable(rect, 0, 1));
		assertTrue(box.checkPlaceable(rect, 0, 99));
		assertTrue(box.checkPlaceable(rect, 0, 100));
		assertTrue(box.checkPlaceable(rect, 0, 300));

		// Overlap or Exceed
		assertTrue(box.checkPlaceable(rect, 100, 100));
		assertTrue(box.checkPlaceable(rect, 10, 1));
		assertTrue(box.checkPlaceable(rect, 1, 10));
		assertTrue(box.checkPlaceable(rect, 1, 1));
		assertTrue(box.checkPlaceable(rect, 100, 1));
		assertTrue(box.checkPlaceable(rect, 10, 99));
		assertTrue(box.checkPlaceable(rect, 100, 99));
		assertFalse(box.checkPlaceable(rect, 201, 0));
		assertTrue(box.checkPlaceable(rect, 200, 1));
		assertTrue(box.checkPlaceable(rect, 200, 50));
		assertFalse(box.checkPlaceable(rect, 200, 51));
		assertTrue(box.checkPlaceable(rect, 1, 300));
		assertFalse(box.checkPlaceable(rect, 0, 301));
		assertFalse(box.checkPlaceable(rect, 0, 400));
		assertFalse(box.checkPlaceable(rect, 400, 0));
		assertFalse(box.checkPlaceable(rect, 300, 0));
		assertTrue(box.checkPlaceable(rect, 0, 101));
		assertFalse(box.checkPlaceable(rect, 0, 200));
		assertFalse(box.checkPlaceable(rect, 1, 200));
		assertFalse(box.checkPlaceable(rect, 200, 200));
		assertFalse(box.checkPlaceable(rect, 199, 199));
		assertFalse(box.checkPlaceable(rect, 500, 0));
		assertFalse(box.checkPlaceable(rect, 500, 200));
		assertFalse(box.checkPlaceable(rect, 500, 400));
		assertFalse(box.checkPlaceable(rect, 500, 500));
	}

	@Test
	public void checkPlaceable_minOverlapRate() {

		MRectangle.setOverlapRate(0);
		MBox box = makeTestMBox();

		MRectangle rect = new MRectangle(100, 100, BOX_LENGTH);
		// Not overlap
		assertTrue(box.checkPlaceable(rect, 100, 100));
		assertTrue(box.checkPlaceable(rect, 0, 0));
		assertTrue(box.checkPlaceable(rect, 300, 0));
		assertTrue(box.checkPlaceable(rect, 0, 30));
		assertTrue(box.checkPlaceable(rect, 100, 300));

		// Overlap or Exceed
		assertFalse(box.checkPlaceable(rect, 200, 200));
		assertFalse(box.checkPlaceable(rect, 300, 300));
		assertFalse(box.checkPlaceable(rect, 250, 150));
		assertFalse(box.checkPlaceable(rect, 50, 150));
		assertFalse(box.checkPlaceable(rect, 150, 350));

		rect = new MRectangle(200, 100, BOX_LENGTH);
		// Not overlap
		assertTrue(box.checkPlaceable(rect, 0, 0));
		assertTrue(box.checkPlaceable(rect, 10, 0));
		assertTrue(box.checkPlaceable(rect, 100, 0));
		assertTrue(box.checkPlaceable(rect, 200, 0));
		assertTrue(box.checkPlaceable(rect, 0, 1));
		assertTrue(box.checkPlaceable(rect, 0, 99));
		assertTrue(box.checkPlaceable(rect, 0, 100));
		assertTrue(box.checkPlaceable(rect, 0, 300));

		// Overlap or Exceed
		assertFalse(box.checkPlaceable(rect, 100, 100));
		assertFalse(box.checkPlaceable(rect, 10, 1));
		assertFalse(box.checkPlaceable(rect, 1, 10));
		assertFalse(box.checkPlaceable(rect, 1, 1));
		assertFalse(box.checkPlaceable(rect, 100, 1));
		assertFalse(box.checkPlaceable(rect, 10, 99));
		assertFalse(box.checkPlaceable(rect, 100, 99));
		assertFalse(box.checkPlaceable(rect, 201, 0));
		assertFalse(box.checkPlaceable(rect, 200, 1));
		assertFalse(box.checkPlaceable(rect, 1, 300));
		assertFalse(box.checkPlaceable(rect, 0, 301));
		assertFalse(box.checkPlaceable(rect, 0, 400));
		assertFalse(box.checkPlaceable(rect, 400, 0));
		assertFalse(box.checkPlaceable(rect, 300, 0));
		assertFalse(box.checkPlaceable(rect, 0, 101));
		assertFalse(box.checkPlaceable(rect, 0, 200));
		assertFalse(box.checkPlaceable(rect, 200, 200));
		assertFalse(box.checkPlaceable(rect, 199, 199));
		assertFalse(box.checkPlaceable(rect, 500, 0));
		assertFalse(box.checkPlaceable(rect, 500, 200));
		assertFalse(box.checkPlaceable(rect, 500, 400));
		assertFalse(box.checkPlaceable(rect, 500, 500));
	}

	private MBox makeTestMBox() {
		ArrayList<MRectangle> mRectangles = new ArrayList<>();
		rect_200_200A.setLocation(200, 200);
		rect_100_100.setLocation(100, 200);
		rect_200_100.setLocation(200, 100);
		mRectangles.add(rect_200_200A);
		mRectangles.add(rect_100_100);
		mRectangles.add(rect_200_100);
		return new MBox(BOX_LENGTH, mRectangles);
	}
}
