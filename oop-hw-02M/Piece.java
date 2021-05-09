// Piece.java

import java.util.*;

/**
 An immutable representation of a tetris piece in a particular rotation.
 Each piece is defined by the blocks that make up its body.
 
 Typical client code looks like...
 <pre>
 Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 int width = pyra.getWidth();			// 3
 Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way
 
 Piece[] pieces = Piece.getPieces();	// the array of root pieces
 Piece stick = pieces[STICK];
 int width = stick.getWidth();		// get its width
 Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 </pre>
*/
public class Piece {
	// Starter code specs out a few basic things, leaving
	// the algorithms to be done.
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation
	public int rotateCount;

	static private Piece[] pieces;	// singleton static array of first rotations

	/**
	 Defines a new piece given a TPoint[] array of its body.
	 Makes its own copy of the array and the TPoints inside it.
	*/
	public Piece(TPoint[] points) {
		body = points.clone();

		rotateCount = 0;

		Iterator It = Arrays.stream(points).iterator();
		height = 0;
		width = 0;
		//get Height, Width And Skirt

		while(It.hasNext()) {
			TPoint curr = (TPoint) It.next();
			if(height < curr.y) {
				height = curr.y;
			}
			if(width < curr.x) {
				width = curr.x;
			}
		}
		width++;
		height++;

		//get skirt
		skirt = new int[width];
		for(int i = 0; i< skirt.length; i++) {
			skirt[i] = -1;
		}
		for(TPoint curr: body) {
			if (skirt[curr.x] == -1) {
				skirt[curr.x] = curr.y;
			} else {
				if (skirt[curr.x] > curr.y) {
					skirt[curr.x] = curr.y;
				}
			}
		}
	}
	

	
	
	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/**
	 Returns the width of the piece measured in blocks.
	*/
	public int getWidth() {
		return width;
	}

	/**
	 Returns the height of the piece measured in blocks.
	*/
	public int getHeight() {
		return height;
	}

	/**
	 Returns a pointer to the piece's body. The caller
	 should not modify this array.
	*/
	public TPoint[] getBody() {
		return body;
	}

	/**
	 Returns a pointer to the piece's skirt. For each x value
	 across the piece, the skirt gives the lowest y value in the body.
	 This is useful for computing where the piece will land.
	 The caller should not modify this array.
	*/
	public int[] getSkirt() {
		return skirt;
	}

	
	/**
	 Returns a new piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		if(rotateCount < 4) {
			rotateCount++;
		} else {
			rotateCount = 0;
		}
		TPoint[] myNext = new TPoint[body.length];
		//System.arraycopy(body,0,myNext,0,body.length);

		Piece nextOne = new Piece(getNextRotation(myNext));
		nextOne.rotateCount = rotateCount;
		//System.out.println(body.equals(myNext));
		//System.out.println(body == myNext);
		rotateCount--;

		return nextOne;
	}
	private TPoint[] getNextRotation(TPoint[] myNext) {
			for(int i = 0; i < myNext.length; i++) {
				myNext[i] = new TPoint(0,0);
				TPoint curr = body[i];
				int x = curr.y;
				int y = -curr.x;
				myNext[i].x = x;
				myNext[i].y = y;
			}
		return move(myNext);
	}

	private TPoint[] move(TPoint[] myNext) {
		int maxY = 0;
		int maxX = 0;
		for(TPoint curr: myNext) {
			if(curr.x < maxX) {
				maxX = curr.x;
			}
			if(curr.y < maxY) {
				maxY = curr.y;
			}
		}
		for(TPoint curr: myNext) {
			curr.x = curr.x - maxX;
			curr.y = curr.y - maxY;
		}
		return myNext;
	}

	/**
	 Returns a pre-computed piece that is 90 degrees counter-clockwise
	 rotated from the receiver.	 Fast because the piece is pre-computed.
	 This only works on pieces set up by makeFastRotations(), and otherwise
	 just returns null.
	*/	
	public Piece fastRotation() {
		if(next == null) System.out.println("nulia");
		return next;
	}
	


	/**
	 Returns true if two pieces are the same --
	 their bodies contain the same points.
	 Interestingly, this is not the same as having exactly the
	 same body arrays, since the points may not be
	 in the same order in the bodies. Used internally to detect
	 if two rotations are effectively the same.
	*/
	public boolean equals(Object obj) {
		// standard equals() technique 1
		if (obj == this) return true;
		
		// standard equals() technique 2
		// (null will be false)
		if (!(obj instanceof Piece)) return false;
		Piece other = (Piece)obj;
		
		// YOUR CODE HERE
		TPoint[] Body2 = ((Piece) obj).getBody();
		if(body.length != Body2.length) return false;
		for(int i = 0; i < Body2.length; i++ ) {
			if(!body[i].equals(Body2[i])) {
				return false;
			}

		}
		return true;
	}


	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";
	
	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;
	
	/**
	 Returns an array containing the first rotation of
	 each of the 7 standard tetris pieces in the order
	 STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
	 The next (counterclockwise) rotation can be obtained
	 from each piece with the {@link #fastRotation()} message.
	 In this way, the client can iterate through all the rotations
	 until eventually getting back to the first rotation.
	 (provided code)
	*/
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		
		
		return Piece.pieces;
	}
	


	/**
	 Given the "first" root rotation of a piece, computes all
	 the other rotations and links them all together
	 in a circular list. The list loops back to the root as soon
	 as possible. Returns the root piece. fastRotation() relies on the
	 pointer structure setup here.
	*/
	/*
	 Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	*/
	private static Piece makeFastRotations(Piece root) {
		Piece curr = root.computeNextRotation();
		Piece next2 = curr;
		root.next = curr;

		while(!root.equals(curr)) {
			System.out.println("itvlis");
			curr.next = curr.computeNextRotation();
			if(curr.next.equals(root)) {
				curr.next = root;
				break;
			}
			curr = curr.next;
		}
		//curr = root;
		//curr.next = next2;
		return root;
	}
	
	

	/**
	 Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 the points into a TPoint[] array.
	 (Provided code)
	*/
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}
		
		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

	


}
