import junit.framework.TestCase;


public class BoardTest extends TestCase {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4,pyr5, s, sRotated;

	// This shows how to build things in setUp() to re-use
	// across tests.
	
	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	
	protected void setUp() throws Exception {
		b = new Board(3, 6);
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		//b.place(pyr1,0,0);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		//pyr5 = pyr4.computeNextRotation();
		//b.place(pyr1,0,0);
		//b.place(pyr1,0,2);
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
		

	}
	
	// Check the basic width/height/max after the one placement
	public void testSample1() {
		assertEquals(2,b.dropHeight(pyr1,0));
		assertEquals(1,b.dropHeight(pyr1,0));
		b.place(pyr1,2,0);
		b.printGrid();
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		assertEquals(2, b.getMaxHeight());
		//b.place(pyr1,2,2);
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	public void testSample3() {

		b.place(pyr1,0,1);
		b.commit();
		b.place(pyr2,0,2);
		b.commit();
		b.place(pyr4,1,3);
		//System.out.println(b.toString());
		b.printGrid();
		b.clearRows();

		b.printGrid();

		System.out.println(b.clearRows());
		b.printGrid();
		assertEquals(3, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());

		//b.place(pyr1,2,2);
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(3, b.getRowWidth(2));
	}
	
	// Place sRotated into the board, then check some measures
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}

	public void testSample4() {
		b.commit();
		for(int  i = 0; i < 4; i++) {
			b.place(pyr1,0,i);

			System.out.println(b.toString());

			b.undo();
			System.out.println(b.toString());
			System.out.println(b.getColumnHeight(0));
			System.out.println(b.getColumnHeight(1));
			System.out.println(b.getColumnHeight(2));
		}

	}
	
	// Makre  more tests, by putting together longer series of 
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.
	
	
}
