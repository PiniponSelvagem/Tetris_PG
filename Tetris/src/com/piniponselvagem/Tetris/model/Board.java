package com.piniponselvagem.Tetris.model;

import com.piniponselvagem.Tetris.TetrisCtrl;
import isel.leic.pg.Console;

public class Board implements ModelInfoGetter {
	public static final int 
		BASE_LINE =  0, BASE_COL =  1, //BASE_COL needs to be equal or above '1'
		DIM_LINES = 20, DIM_COLS = 10,
		WAIT = 50;
		/* WAIT -> How long 'X' stays when deleting line.
		 *         Affects lineDelete time, also see "Board.lineDeleteEffect()"
		 */

	public static int[][] board;

	private ModelListener listener;

	@Override
	public int getDimLins() {
		return DIM_LINES;
	}

	@Override
	public int getDimCols() {
		return DIM_COLS;
	}

	@Override
	public int getBaseLin() {
		return BASE_LINE;
	}

	@Override
	public int getBaseCol() {
		return BASE_COL;
	}

	public void setUpdateListener(ModelListener listener) {
		this.listener = listener;
	}












	public static void drawGrid() {
		for(int l=0 ; l<=DIM_LINES ; ++l)
			for(int c=0 ; c<=DIM_COLS+1 ; ++c) {
				if (l!=DIM_LINES && c<=DIM_COLS && c>0) {
					Console.color(Console.WHITE, Console.BLACK);
					put(BASE_LINE+l, c+BASE_COL-1,'.');
				}
				else { // draw walls
					Console.color(Console.BLACK, Console.WHITE);
					if (l!=DIM_LINES && (c==0 || c==DIM_COLS+1)) put(BASE_LINE+l, c+BASE_COL-1,'|');
					if (l==DIM_LINES && (c==0 || c==DIM_COLS+1)) put(BASE_LINE+l, c+BASE_COL-1,'+');
					if (l==DIM_LINES && (c>0  && c<=DIM_COLS)) 	 put(BASE_LINE+l, c+BASE_COL-1,'-');
				}
			}
	}
	
	public static void put(int line, int col, char c) { // draw a char at (line, col)
		Console.cursor(line, col); 
		Console.print(c);		
	}
	
	public static boolean validPosition(int line, int col) {
		return line>=0 && line<DIM_LINES && col>=0 && col<DIM_COLS && board[line][col]==0;
	}
	
	public static void lineCheck() {
		boolean recheck=false;
		do {
			for (int line=DIM_LINES-1; line>0 ; --line) {
	    		recheck=false;
				int count=0;
	    		for (int col=0 ; col<DIM_COLS ; ++col) {
	    			if (board[line][col]!=0) ++count;	// counts the "mini" blocks on the line
	    			if (count==DIM_COLS) recheck=true;	// goes true when line is full
	    		}
	    		if (count==0) break; //check if line is empty, breaks and game continues
	    		if (count==DIM_COLS) {
	    			lineDeleteEffect(line,'X', WAIT*6);
	    			lineDelete(line);
	    			linePause(WAIT);
	    			lineDraw(line);
	    			lineMove(line-1); //moves next line down, '1' because its the line above.
	    			Score.scoreCalc(DIM_COLS * Score.SCORE_MINIBLOCK);
	    			Score.linesCalc(1); //increments lines counter
	    			break; //if not here, will only delete max 2 lines per turn, leaves this 'for' and do 'recheck'.
	    		}
			}
		} while(recheck);
	}
	
	private static void lineDelete(int line) {
		if (line>0) {
			board[line]=board[line-1];
			board[line-1]=new int[DIM_COLS];
		} else 
			board[line]=new int[DIM_COLS];
		for (int col=0 ; col<DIM_COLS; ++col)
			gridShow(line+BASE_LINE,col);
		
	}
	
	private static void lineDraw(int line) {
		for (int col=0 ; col<DIM_COLS ; ++col) {
			lineColor(line,col,' ');
		}
	}
	
	private static void lineMove(int line) {
		int count;
		while (line>=0) {
			lineDelete(line);
			linePause(WAIT);
			lineDraw(line);  //shows replaced line
			
			count=0; //resets counter
			for (int col=0 ; col<DIM_COLS; ++col) { //checks if needs to continue moving pieces (is the line empty?)
				if (board[line][col]!=0) ++count;
			}
			if (count==0) break;
			--line; //goes to next line above
		}
	}
	
	public static void linePause(int wait) {
		TetrisCtrl.nextStepTime+=wait; //stops the timer for "WAIT"ms
		Console.sleep(wait);
	}
	
	private static void lineColor(int line, int col, char letter) {
		for (int i=0 ; i< Piece.COLORS.length ; ++i) {
			if (board[line][col]==Piece.COLORS[i]) {
				Console.color(Console.WHITE, Piece.COLORS[i]);
				put(line+BASE_LINE, col+BASE_COL, letter);
			}
		}
	}
	
	private static void lineDeleteEffect(int line, char letter, int wait) {
		for (int col=0 ; col<DIM_COLS ; ++col) {
			lineColor(line, col, letter);
		}
		linePause(wait);
	}
	
	private static void gridShow(int line, int col) {
		Console.color(Console.WHITE, Console.BLACK);
		put(line, col+BASE_COL ,'.');
	}
}