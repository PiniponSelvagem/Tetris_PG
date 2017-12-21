package com.piniponselvagem.Tetris.view;

import com.piniponselvagem.Tetris.model.ModelInfoGetter;
import com.piniponselvagem.Tetris.model.ModelListener;
import isel.leic.pg.Console;

public class TetrisView implements ModelListener {
    private static final String GAME_OVER_TXT = "GAME  OVER",
                                NEW_GAME_TXT  = "New game ?",
                                Y_N_TXT       = "  Y / N   ";

    private ModelInfoGetter modelInfo;

    public void setModelInfoGetter(ModelInfoGetter modelInfo) {
        this.modelInfo = modelInfo;
    }

    public void showGameOverMsg() {
        gameOverScreen(1, Console.RED, Console.YELLOW);
        Console.print(GAME_OVER_TXT);		// Message GAME OVER

        Console.waitKeyPressed(5000);		// Wait 5 seconds for any key
        while (Console.isKeyPressed())
            ;

        gameOverScreen(2, Console.BLACK, Console.WHITE);
        Console.print(NEW_GAME_TXT);		// Ask for a new game

        gameOverScreen(3, Console.BLACK, Console.WHITE);
        Console.print(Y_N_TXT);				// Message for wish keys are recognized
    }

    private void gameOverScreen(int line, int foreground, int background) {
        int gameOverCol=0;
        if (GAME_OVER_TXT.length()/2<modelInfo.getDimCols() && modelInfo.getDimCols()>GAME_OVER_TXT.length())
            gameOverCol=(modelInfo.getDimCols()-GAME_OVER_TXT.length())/2;
        Console.cursor(modelInfo.getBaseLin()+line, gameOverCol+modelInfo.getBaseCol());
        Console.color(foreground, background);
    }
}
