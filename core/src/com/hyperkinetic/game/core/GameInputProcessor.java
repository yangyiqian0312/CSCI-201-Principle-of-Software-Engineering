package com.hyperkinetic.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.hyperkinetic.game.board.AbstractBoardTile;
import com.hyperkinetic.game.board.AbstractGameBoard;

/**
 * Receives and processes inputs.
 *
 * @author cqwillia
 */
public class GameInputProcessor implements InputProcessor
{
    private int rightClickX;
    private int rightClickY;
    private int leftClickX;
    private int leftClickY;
    
    private LaserGame game;
    
    public GameInputProcessor(LaserGame g)
    {
        game = g;
    }

    public boolean keyDown (int keycode)
    {
        return false;
    }

    public boolean keyUp (int keycode)
    {
        if(keycode == Input.Keys.Q)
        {
            return AbstractGameBoard.keyPressed("Q");
        }
        else if(keycode == Input.Keys.E)
        {
            return AbstractGameBoard.keyPressed("E");
        }
        else if(keycode == Input.Keys.ESCAPE)
        {
            game.returnToMenu();
        }
        return false;
    }

    public boolean keyTyped (char character)
    {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int button)
    {
        if(button == Input.Buttons.RIGHT)
        {
            rightClickX = x;
            rightClickY = y;
            return true;
        }
        else if(button == Input.Buttons.LEFT)
        {
            leftClickX = x;
            leftClickY = y;
        }
        return false;
    }

    public boolean touchUp (int x, int y, int pointer, int button)
    {
        if(button == Input.Buttons.RIGHT)
        {
            return AbstractGameBoard.rightClick(rightClickX, Gdx.graphics.getHeight() - rightClickY,
                                                x, Gdx.graphics.getHeight() - y);
        }
        if(button == Input.Buttons.LEFT)
        {
            return AbstractGameBoard.leftClick(leftClickX, Gdx.graphics.getHeight() - leftClickY,
                                               x, Gdx.graphics.getHeight() - y);
        }
        return false;
    }

    public boolean touchDragged (int x, int y, int pointer)
    {
        return false;
    }

    public boolean mouseMoved (int x, int y)
    {
        return false;
    }

    public boolean scrolled (int amount)
    {
        return false;
    }
}
