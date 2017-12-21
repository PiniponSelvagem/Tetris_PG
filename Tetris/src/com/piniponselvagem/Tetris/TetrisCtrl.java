package com.piniponselvagem.Tetris;

import static isel.leic.pg.Console.NO_KEY;

import com.piniponselvagem.Tetris_v2.model.Board;
import com.piniponselvagem.Tetris_v2.model.Piece;
import com.piniponselvagem.Tetris_v2.model.Score;
import com.piniponselvagem.Tetris_v2.view.TetrisView;
import isel.leic.pg.Console;
import java.awt.event.KeyEvent;

/**
 * Tetris Version: v2 ALPHA.2 (09052016)
 * @PiniponSelvagem
 */

public class TetrisCtrl {
	private Board model = new Board();
	private TetrisView view = new TetrisView();

	public static final int STEP_TIME = 1000; // 1 second by each step

	public static long waitTime;
	public static long nextStepTime;  		  // Time to call next step()
	private static Piece piece;		 		  // Current piece controlled
	public static boolean gameOver;
	public static boolean exit;

	public static void main(String[] args) {
		TetrisCtrl control = new TetrisCtrl();
		control.init();
		control.start();
	}

	private void init() {
		Console.open("PG Tetris",
				model.getDimLins()+model.getBaseLin()+Score.addLine()+1,
				model.getDimCols()+model.getBaseCol()+Score.isEnabled()+1);
		Console.exit(true);
	}

	public void start() {
		view.setModelInfoGetter(model);
		model.setUpdateListener(view);

		do {
			startBoardScore();		// Prepares the Board/Score
			Piece.calcNextPiece();	// Calculate 1st Piece
			piece = new Piece();	// The first piece
			piece.show();
			Piece.calcNextPiece();	// Calculate next Piece
			Score.nextPiece();		// Shows next Piece
			nextStepTime = System.currentTimeMillis()+STEP_TIME;
			run();					// Run the game
			terminate();			// Show GameOver Screen and ask for a new game
			if (exit) break;
			clearNewGame();			// Answer was "yes", prepare for new game
		} while (!exit);
		Console.close();
	}

	private static void startBoardScore() {
		Board.drawGrid();			// Draw board
		Board.board = new int[Board.DIM_LINES][Board.DIM_COLS];
		Score.pieceCount = new int[Piece.BLOCKS.length];
		Score.drawInfo();			    // Draw score
		for (int type=0 ; type<Piece.BLOCKS.length ; ++type) {
			Score.showPieceCount(type); // Draw pieces counters
		}
	}
	
	public void run() {
		int key = NO_KEY;
		do {			
			waitTime = nextStepTime - System.currentTimeMillis();
			if (waitTime <= 0) 
				step();						// Next step of the game
			else {
				key = Console.waitKeyPressed(waitTime);
				if (key!=NO_KEY) {  		// A key was pressed ? 
					action(key);			// Do action for the key
					while (Console.isKeyPressed(key))  // Wait to release key
						if (System.currentTimeMillis() >= nextStepTime)
							step();			// Next step with the key held down
				}
			}
		} while (!gameOver);
	}
	
	private boolean terminate() {
		view.showGameOverMsg();
		int kexit;
		do {
			kexit = Console.waitKeyPressed(0);
			if (kexit==KeyEvent.VK_Y) { exit=false; break; }
			if (kexit==KeyEvent.VK_N) { exit=true; break; }
		} while(true);
		return exit;
	}
	
	private static void clearNewGame() { // Clears for a new game
		Console.color(Console.BLACK, Console.BLACK);
		Console.clear();
		gameOver=false;
	}

	private static void step() {
		if (! piece.down()) {   			// If possible, move the piece down 
			piece.solid();					// Make piece solid
			Score.pieceCounters(Piece.type);  // Counts the piece that hit bottom
			Score.showPieceCount(Piece.type); // Shows the updated piece counter
			Score.nextLevel();				// Level calculation
			Piece.type=Piece.nexttype;		// Places the "next" piece in the game to play
			piece = new Piece();			// Create a new piece of random type
 			piece.isGameOver();
  			if (!gameOver) {
  				piece.show(); 				// Show the new piece
  				Piece.calcNextPiece();		// Calculates the next piece for next turn
				Score.nextPiece();			// Shows the updated next piece
  			}
		}
		nextStepTime += STEP_TIME/Score.level;  // Set next time to move the piece, also speeds up based on current level
	}

	private static void action(int key) {
		switch (key) {
		case KeyEvent.VK_LEFT: 	piece.moveLeft(); 	break;
		case KeyEvent.VK_RIGHT:	piece.moveRight(); 	break;
		case KeyEvent.VK_Q:		piece.rotateLeft();	break;
		case KeyEvent.VK_W:		piece.rotateRight();break;
		case KeyEvent.VK_DOWN:	piece.down(); 		break;
		case KeyEvent.VK_SPACE:	piece.downFast(); 	break;
		case KeyEvent.VK_ESCAPE:gameOver=true; 		break;
		case KeyEvent.VK_M:     Score.musicState(); break;
		case KeyEvent.VK_P:		Score.gamePause(); 	break;
		}
	}
}