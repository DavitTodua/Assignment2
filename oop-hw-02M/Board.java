// Board.java

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	private boolean[][] backupGrid;
	private int[] widths;
	private int[] heights;
	private int[] backupWidths;
	private int[] backupHeights;
	

	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		backupGrid = new boolean[width][height];
		committed = true;
		widths = new int[height];
		heights = new int[width];
		
		// YOUR CODE HERE
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {
		int maxCount = 0;
			for(int i = 0; i < grid.length; i++) {
				int curr = 0;
				for(int j = 0; j < grid[i].length; j++) {
					if(grid[i][j] == true) curr++;
				}
				if(curr > maxCount) maxCount = curr;
			}
		return maxCount;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {

		if (DEBUG) {
			// YOUR CODE HERE
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int where = 0;
		for(int i = x; i <piece.getWidth() + x; i++) {
			int curr = heights[i] - skirt[i-x];
			if(where < curr) {
				where = curr;
			}
		}
		return where;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/

	public int getColumnHeight(int x) {
		return heights[x];
	}

	private int countColumnHeight(int x) {
		int maxCount = -1;
		for(int i = 0; i < grid[x].length; i++) {
			if(grid[x][i] == true) {
				maxCount = i;
			}
		}
		if(maxCount != -1 ) {
			maxCount++;
		} else {
			return 0;
		}

		return maxCount;
	}



	public void printGrid() {
		for(int i = grid.length-1; i> -1; i--) {
			for(int j = 0; j< grid[i].length; j++) {
				System.out.print((grid[i][j]) + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		return widths[y];
	}
	private int countRowWidth(int y) {
		int maxCount = 0;
		for(int i = 0; i < grid.length; i++) {
			if(grid[i][y] == true)
				maxCount++;
		}
		return maxCount;
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if(y >= grid[0].length || x >= grid.length ) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return grid[x][y];
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		if(x >= grid.length || y >= grid[0].length || x < 0 || y < 0) {
			System.out.println("PLACE_OUT_BOUNDS");
			return PLACE_OUT_BOUNDS;
		}
		//commit();
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		committed = false;
		int result = PLACE_OK;

		if(y + piece.getHeight() > grid[x].length || x + piece.getWidth() > grid.length) {
			System.out.println("PLACE_OUT_BOUNDS");
			return PLACE_OUT_BOUNDS;
		}
		TPoint[] body = piece.getBody();
		for(int i =0; i < body.length; i++) {
			if(grid[x + body[i].x][y + body[i].y]) {
				System.out.println("PLACE_BAD");
				//undo();
				return PLACE_BAD;
			} else {
				grid[x+body[i].x][y+body[i].y] = true;
				widths[y+body[i].y]++;
				if(widths[y] == grid.length) {
					result = PLACE_ROW_FILLED;
				}
			}
		}
		updateHeights();
		//updateRow();
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;

		while(checkRows() >= 0) {
			//checkrows gives us the height position, which can be cleared
			int start = checkRows();
			for (int i = 0; i < grid.length; i++) {
				grid[i][start] = false;

			}
			// move everything down
			for (int i = start + 1; i < grid[0].length; i++) {
				for (int j = 0; j < grid.length; j++) {
					grid[j][i - 1] = grid[j][i];
					grid[j][i] = false;
				}
			}
			rowsCleared = rowsCleared+1;
			updateHeights();
			updateRow();
		}
		if(rowsCleared > 0) {

			updateHeights();
			updateRow();
			committed = false;
		}
		// YOUR CODE HERE
		sanityCheck();
		return rowsCleared;
	}

	private void updateHeights() {
		for(int i = 0; i < grid.length; i++) {
			heights[i] = countColumnHeight(i);
		}
	}
	private void updateRow() {
		for(int i = 0; i < grid[0].length; i++ ) {
			widths[i] = countRowWidth(i);
		}
	}
	private int checkRows() {
		for(int i = 0; i < grid[0].length; i ++ ) {
			int total = grid.length;
			//System.out.println(i +" "+ getRowWidth(i));
			if(getRowWidth(i) == total) {

				//System.out.println(i);
				return i;
			}
		}
		return -1;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(committed = false) {
			return;
		}
		grid = backupGrid;
		int[] tempH = new int[height];
		int[] tempW = new int[width];
		heights = backupHeights;
		widths = backupWidths;
		backupWidths = tempW;
		backupHeights = tempH;
		committed = true;
		commit();
		// YOUR CODE HERE
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		//System.out.println(backupGrid.length);
		backupGrid = new boolean[width][height];
		for(int i = 0; i <grid.length; i++) {
			System.arraycopy(grid[i], 0, backupGrid[i], 0, grid[i].length);
		}
		//System.arraycopy(backupHeights,0,heights,0);
		backupHeights = heights.clone();
		backupWidths = widths.clone();
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


