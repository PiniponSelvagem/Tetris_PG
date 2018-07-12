package com.piniponselvagem.Tetris.model;

import com.piniponselvagem.Tetris.TetrisCtrl;
import isel.leic.pg.Console;

public class Piece {
	public static int type;  	 // Type of piece (I,J,L,O,S,T or Z)
	public static int nexttype;  // Type of next piece (I,J,L,O,S,T or Z)
	private int line, column;	 // Current position
    public static int direction; // Current direction (0,1,2 or 3)
    
    public Piece(int type) {	// Create a piece of one type
      Piece.type = nexttype;
      column = Board.DIM_COLS/2 -GRID_COLS/2;  // Centered in board
      line = 0;								   // Top line
      direction = 0;						   // First direction
    }
    
    public Piece() { 	// Piece of random type
    	this(nexttype);
    }
    
    public static void calcNextPiece() {  // Calculate new Piece
		nexttype = (int)( Math.random() * (Piece.Z + 1) );
    }
    
    public void show() {  // Show the blocks of the piece in board
    	draw(Board.BASE_LINE+line, Board.BASE_COL+column,
    		  COLORS[type], BLOCKS[type][direction], 'o');    		
    }
    
    public void hide() {  // Show the grid in the position of the blocks
    	draw(Board.BASE_LINE+line, Board.BASE_COL+column,
    		  Console.BLACK, BLOCKS[type][direction], '.');
    }
        
    public void solid() { // Makes the piece solid, saving it to the array
    	savePiece(line, column, COLORS[type], BLOCKS[type][direction]);
    	draw(Board.BASE_LINE+line, Board.BASE_COL+column,
    			COLORS[type], BLOCKS[type][direction], ' ');
    	Board.lineCheck();
    	
    }
        
    private static final int MAX_DIRECTIONS = 4;
    
    public boolean rotateLeft()  { return rotate(MAX_DIRECTIONS-1); }
    public boolean rotateRight() { return rotate(1); }
    
    private boolean rotate(int by) {
        int dir = (direction + by) % MAX_DIRECTIONS;		 // The new direction
        if (! validBlocks(line,column,BLOCKS[type][dir]))  // Is possible ?
            return false;
        hide();
        direction = dir;
        show();
        return true;
    }
    
    public boolean moveLeft()	{ return move(0,-1); }
	public boolean moveRight()	{ return move(0,+1); }
    public boolean down() 		{ return move(1,0);  }
    public void downFast()	{ 
    	while(down()) Board.linePause(Board.WAIT / 4); // makes that fancy effect when falling
    }
    
    private boolean move(int dLine, int dCol) {
    	if ( !validBlocks(line+dLine, column+dCol, BLOCKS[type][direction]))
    		return false; 		// If the new position is not possible
		hide();
		line += dLine;
		column += dCol;
		show();
        Score.scoreCalc(1);
		return true;
	}
    
    private boolean validBlocks(int line, int col, int blocks) {
    	int mask = MASK_INIT;
    	for(int l=0 ; l<GRID_LINES ; ++l)				// For each line
    		for(int c=0 ; c<GRID_COLS ; ++c , mask>>=1)	// For each column
    			if ((blocks&mask)!=0 && !Board.validPosition(line + l, col + c))
    				return false; 						// If the block has no room
		return true;
	}
    
	public static void draw(int line, int col, int color, int blocks, char txt) {
    	Console.color(Console.WHITE, color);	
    	int mask = MASK_INIT;
    	for(int l=0; l<GRID_LINES ; ++l)				// For each line
    		for(int c=0; c<GRID_COLS ; ++c , mask>>=1 )	// For each column
    			if ((blocks&mask)!=0) { 				// If has a block
    				Board.put(line + l, col + c, txt); 		// Move cursor and write the char
    			}
    }
	
	public static void savePiece(int line, int col, int color, int blocks) {	
    	int mask = MASK_INIT, points=0;
    	for(int l=0; l<GRID_LINES ; ++l)				// For each line
    		for(int c=0; c<GRID_COLS ; ++c , mask>>=1 )	// For each column
    			if ((blocks&mask)!=0) { 				// If has a block
    				Board.board[line+l][col+c] = color;
    				++points;
    			}
    	Score.scoreCalc(points * Score.SCORE_MINIBLOCK);
    }
	
	public void isGameOver() {
		int blocks = BLOCKS[type][direction];
		int mask = MASK_INIT;
		for(int l=0; l<GRID_LINES ; ++l)				// For each line of the possible next piece
			// Check if is possible to place next piece in the 4x4 space
    		for(int c=(Board.DIM_COLS/2-2); c<(Board.DIM_COLS/2)+GRID_COLS/2 ; ++c , mask>>=1 ) {
    			if ((blocks&mask)!=0 && Board.board[l][c]!=0) {	// If has a block
    				TetrisCtrl.gameOver=true;
    			}
    		}
	} 
	
    public static final int  // Type of Tetriminos 
    	I=0, J=1, L=2, O=3, S=4, T=5, Z=6;
    
    public static final int[] COLORS = {  // Colors of Tetriminos
    	Console.RED    /*I*/,	Console.BLUE   /*J*/,	Console.ORANGE/*L*/,
    	Console.YELLOW /*O*/,	Console.MAGENTA/*S*/,	Console.CYAN  /*T*/,
    	Console.GREEN  /*Z*/
    };
    
    public static final int GRID_LINES=4;
	public static final int GRID_COLS=4;
    private static final int MASK_INIT=0x8000;
    
    /**
     * Blocks of each piece of <b>Tetris</b> <br>
     * Defined in 4x4 bitmap 
     * <a href="http://thoth.cc.e.ipl.pt/classes/Pg/1415i/LI11D/resources/4755">more...</a>
     */
    public static final int[][] BLOCKS = { // Blocks of Tetriminos in 4 directions
    	{0x0F00, 0x4444, 0x0F00, 0x2222},  //I
    	{0x2260, 0x8E00, 0x6440, 0x0710},  //J
    	{0x4460, 0x0E80, 0x6220, 0x0170},  //L
    	{0x6600, 0x6600, 0x6600, 0x6600},  //O
    	{0x4620, 0x06C0, 0x4620, 0x0360},  //S
    	{0x2620, 0x04E0, 0x4640, 0x0720},  //T
    	{0x2640, 0x0C60, 0x2640, 0x0630},  //Z
    };
}
