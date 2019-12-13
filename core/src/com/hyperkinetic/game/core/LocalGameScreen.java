package com.hyperkinetic.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hyperkinetic.game.board.AbstractGameBoard;
import com.hyperkinetic.game.board.StandardBoard;

public class LocalGameScreen implements Screen
{
    private static SpriteBatch batch;

    private AbstractGameBoard board;

    private Stage stage;
    private LaserGame game;

    public LocalGameScreen(LaserGame g)
    {
        game = g;
        stage = new Stage(new ScreenViewport());
        board = new StandardBoard(true, true);
        board.initialize();

        batch = new SpriteBatch();
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(new GameInputProcessor(game));
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        board.render(batch);
        
        if(!board.getGameState().equals("NoWin"))
            game.setScreen(new MainMenuScreen(game));

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
