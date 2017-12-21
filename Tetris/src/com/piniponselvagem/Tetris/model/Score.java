import java.awt.event.KeyEvent;
import static isel.leic.pg.Console.NO_KEY;
import isel.leic.pg.Console;

public class Score {
	private static final boolean 
		SCORE_SHOW  = true,  // draw Score Board?
		KEYMAP_SHOW = true;  // draw KeyMapping ?
	
	private static final String[] KEYMAP_TXT = {
		" Left - Move Left   ", 
	    "Right - Move Right  ",
	    "Q / W - R.Left/Right", 
	    " Down - Move Down   ", 
	    "Space - Drop        ",
	    "Esc/P - Close/Pause ",
	    "    M - Music ON/OFF"
	    };
	
	private static final int 
		//Color customization
		CR_FOREG_SCORE  = Console.WHITE, CR_BACKG_SCORE  = Console.BLACK,
		CR_FOREG_KEYMAP = Console.WHITE, CR_BACKG_KEYMAP = Console.DARK_GRAY,
		CR_MUSIC_ON     = Console.GREEN, CR_MUSIC_OFF    = Console.RED,
		CR_FOREG_PAUSED = Console.RED,   CR_BACKG_PAUSED = Console.YELLOW,
		CR_BACKG_NPIECE = Console.BLACK,	 	  // Color next Piece BackGround (for better results: use a color NOT being used by a piece)
		
		//Next Piece Settings
		NEXT_PIECE_COL  = 2, NEXT_PIECE_LINE = 6, // nextPiece(...) counting after the grid not the board (including zero '0')
		NEXT_LEVEL_IN   = 40, 				 	  // next level each 40 pieces
		
		//Position of Score Pieces
		H_POS_I = 18, V_POS_I = 1, //I -> default position: (18,1)
		H_POS_J = 13, V_POS_J = 4, //J -> default position: (13,4)
		H_POS_L = 13, V_POS_L = 6, //L -> default position: (13,6)
		H_POS_O = 17, V_POS_O = 4, //O -> default position: (17,4)
		H_POS_S = 18, V_POS_S = 6, //S -> default position: (18,6)
		H_POS_T = 12, V_POS_T = 0, //T -> default position: (12,0)
		H_POS_Z = 17, V_POS_Z = 9; //Z -> default position: (17,9)
	
	public static final int
		SCORE_MINIBLOCK = 1;	// score per mini block that the piece has, also affects score per line linearly
	
	private static final String MUSIC = "tetris_classic_a_compressed_v3";
	private static boolean
		musicState = false; // if TRUE, music starts by default
	
	/*
	 * END OF EDITABLE ZONE
	 */
	
	
	public static int score=0;
	public static int lines=0;
	public static int level=1;
	public static int[] pieceCount;
	private static boolean pause; // pauses the game if assigned key is pressed (default is 'P')
	
	public static void drawInfo() {
		if(isSpaceAvailable()==true) Score.mainUI();	// Draw Score Board
		if (KEYMAP_SHOW==true && Board.DIM_LINES>=20) Score.keyMap();	// Draw Key Mapping
	}
	
	public static int isEnabled() {
		if ((SCORE_SHOW==true || KEYMAP_SHOW==true) && Board.DIM_LINES>=12) return 22; // 22 is the space required to show the SCORE / KEYMAP.
		return 0;
	}
	
	public static int addLine() {
		// 7 is the default space required to show the default KeyMap
		if (KEYMAP_TXT.length>7 && KEYMAP_SHOW==true) return (KEYMAP_TXT.length-7);
		return 0;
	}
	
	private static void mainUI() {
		Console.color(CR_FOREG_SCORE, CR_BACKG_SCORE);
		uiText(2, 1, "Score:"); 
		uiText(2, 2, "Lines:");
		uiText(2, 3, "Level:");
		uiText(2, 5, "Next");
		uiText(2,10, "Music:");
		showMusic();
		showPause(false);
		
		Console.color(CR_FOREG_KEYMAP, CR_BACKG_KEYMAP);
		score=0; lines=0; level=1; // resets every new game
		uiText(8, 1, "    ");  //*
		uiText(8, 2, "    ");  //* this 3 lines makes the "score/lines/level" have a LCD style
		uiText(8, 3, "    ");  //*
		scoreCalc(score);
		linesCalc(lines);
		levelCalc(level);
		
		showAllPiece();
	}

	private static void keyMap() {
		Console.color(CR_FOREG_KEYMAP, CR_BACKG_KEYMAP);
		for (int i=0, line=13; i<KEYMAP_TXT.length ; ++i, ++line) {
			uiText(2, line, KEYMAP_TXT[i]);
		}
	}
		
	private static void uiText(int colAfterBoard, int line, String txt) {
		Console.cursor(line+Board.BASE_LINE, Board.DIM_COLS+Board.BASE_COL+colAfterBoard);
		Console.println(txt);
	}
	
	private static void uiPoints(int colAfterBoard, int line, int score) {
		Console.color(CR_FOREG_KEYMAP, CR_BACKG_KEYMAP);
		Console.cursor(line+Board.BASE_LINE, Board.DIM_COLS+Board.BASE_COL+colAfterBoard);
		Console.println(score);
	}
	
	public static void showPieceCount(int type) {
		if(isSpaceAvailable()==true) {
			switch (type) { // only updates the counter needed
			case Piece.I: uiPoints(H_POS_I+2, V_POS_I,   pieceCount[Piece.I]); break; //I
			case Piece.J: uiPoints(H_POS_J+2, V_POS_J,   pieceCount[Piece.J]); break; //J
			case Piece.L: uiPoints(H_POS_L+2, V_POS_L+2, pieceCount[Piece.L]); break; //L
			case Piece.O: uiPoints(H_POS_O+3, V_POS_O+1, pieceCount[Piece.O]); break; //O
			case Piece.S: uiPoints(H_POS_S+2, V_POS_S+2, pieceCount[Piece.S]); break; //S
			case Piece.T: uiPoints(H_POS_T+3, V_POS_T+2, pieceCount[Piece.T]); break; //T
			case Piece.Z: uiPoints(H_POS_Z+3, V_POS_Z+1, pieceCount[Piece.Z]); break; //Z
			}
		}
	}
	
	private static void showAllPiece() {
		// I
		Piece.draw(Board.BASE_LINE+V_POS_I, Board.DIM_COLS+Board.BASE_COL+H_POS_I, 
	    		  Piece.COLORS[Piece.I], Piece.BLOCKS[Piece.I][0], 'o');
		
		// J
		Piece.draw(Board.BASE_LINE+V_POS_J, Board.DIM_COLS+Board.BASE_COL+H_POS_J, 
			      Piece.COLORS[Piece.J], Piece.BLOCKS[Piece.J][1], 'o');
		
		// L
		Piece.draw(Board.BASE_LINE+V_POS_L, Board.DIM_COLS+Board.BASE_COL+H_POS_L, 
				  Piece.COLORS[Piece.L], Piece.BLOCKS[Piece.L][1], 'o');
		
		// O
		Piece.draw(Board.BASE_LINE+V_POS_O, Board.DIM_COLS+Board.BASE_COL+H_POS_O, 
				  Piece.COLORS[Piece.O], Piece.BLOCKS[Piece.O][0], 'o');
		
		// S
		Piece.draw(Board.BASE_LINE+V_POS_S, Board.DIM_COLS+Board.BASE_COL+H_POS_S, 
				  Piece.COLORS[Piece.S], Piece.BLOCKS[Piece.S][1], 'o');
		
		// T
		Piece.draw(Board.BASE_LINE+V_POS_T, Board.DIM_COLS+Board.BASE_COL+H_POS_T, 
				  Piece.COLORS[Piece.T], Piece.BLOCKS[Piece.T][3], 'o');
		
		// Z
		Piece.draw(Board.BASE_LINE+V_POS_Z, Board.DIM_COLS+Board.BASE_COL+H_POS_Z, 
				  Piece.COLORS[Piece.Z], Piece.BLOCKS[Piece.Z][3], 'o');
	}
	
	public static void nextPiece() {
		if(isSpaceAvailable()==true) nextPieceShow();
	}
	
	public static void nextPieceShow() {
		nextPieceDelete();
		Piece.draw(Board.BASE_LINE+NEXT_PIECE_LINE, Board.DIM_COLS+Board.BASE_COL+NEXT_PIECE_COL, 
	    		  Piece.COLORS[Piece.nexttype], Piece.BLOCKS[Piece.nexttype][0], 'o'); 
	}
	
	private static void nextPieceDelete() { // clears the space to show next piece
		for(int l=0 ; l<Piece.GRID_LINES ; ++l)
			for(int c=0 ; c<Piece.GRID_COLS ; ++c) {
				Console.cursor(Board.BASE_LINE+NEXT_PIECE_LINE+l, Board.DIM_COLS+Board.BASE_COL+NEXT_PIECE_COL+c);
				Console.color(Console.WHITE, CR_BACKG_NPIECE); 
				Console.print(' ');
			}
	}
	
	private static boolean isSpaceAvailable() {
		if (SCORE_SHOW==true && Board.DIM_LINES>=12) return true;
		return false;
	}
	
	public static void scoreCalc(int point) {
		score+=point;
		if(isSpaceAvailable()==true) uiPoints(8, 1, score);
	}

	public static void linesCalc(int point) {
		lines+=point;
		if(isSpaceAvailable()==true) uiPoints(8, 2, lines);
	}
	
	private static void levelCalc(int point) {
		if(isSpaceAvailable()==true) uiPoints(8, 3, level);
	}
	
	public static void pieceCounters(int type) {
		switch (type) {
			case Piece.I: ++pieceCount[Piece.I]; break;
			case Piece.J: ++pieceCount[Piece.J]; break;
			case Piece.L: ++pieceCount[Piece.L]; break;
			case Piece.O: ++pieceCount[Piece.O]; break;
			case Piece.S: ++pieceCount[Piece.S]; break;
			case Piece.T: ++pieceCount[Piece.T]; break;
			case Piece.Z: ++pieceCount[Piece.Z]; break;
			}
	}
	
	public static void nextLevel() {
		int count=0;
		for (int i=0; i<pieceCount.length ; ++i) count+=pieceCount[i]; // counts how many pieces placed to calculate the level
		if (count%NEXT_LEVEL_IN == 0) ++level; // every NEXT_LEVEL_IN pieces levels UP!
		levelCalc(level);
	}
	
	public static void musicState() {
		if (musicState==false) {
			Console.startMusic(MUSIC);
			musicState=true;
			showMusic();
		}
		else {
			Console.stopMusic();
			musicState=false;
			showMusic();
		}
	}
	
	private static void showMusic() {
		if (musicState==true) {
			Console.color(CR_MUSIC_ON, CR_BACKG_KEYMAP);
			uiText(8,10, "ON ");			
		}
		else {
			Console.color(CR_MUSIC_OFF, CR_BACKG_KEYMAP);
			uiText(8,10, "OFF");
		}
	}

	public static void gamePause() {
		showPause(true);
		while(Console.isKeyPressed());
		for (int key=NO_KEY ; key!=KeyEvent.VK_P ; Tetris.nextStepTime+=Tetris.STEP_TIME/Score.level) {
			key = Console.waitKeyPressed(Tetris.STEP_TIME/Score.level);
		}
		showPause(false);
	}
	
	private static void showPause(boolean pause) {
		if (pause==true && isSpaceAvailable()==true) {
			Console.color(CR_FOREG_PAUSED, CR_BACKG_PAUSED);
			uiText(8,11, "PAUSED");
		}
		else if(pause==false && isSpaceAvailable()==true){
			Console.color(Console.BLACK, Console.BLACK);
			uiText(8,11, "      ");
		}
	}
}
