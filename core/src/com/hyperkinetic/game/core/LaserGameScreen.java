package com.hyperkinetic.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.hyperkinetic.game.board.AbstractGameBoard;

public class LaserGameScreen implements Screen {

    /**
     * The {@link SpriteBatch} object responsible for rendering textures onto the game canvas.
     */
    private static SpriteBatch batch;
    /**
     * The game board that tracks current configuration and player movements.
     */
    private AbstractGameBoard board;

    private Stage stage;
    private LaserGame game;
    private float width;
    private float height;
    private OrthographicCamera camera;

    public LaserGameScreen(LaserGame aGame) {
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        game = aGame;
        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false,width, height);
        stage = new Stage(new StretchViewport(width, height, camera));
        board = null;
        batch = new SpriteBatch();

/*
        Skin pink = new Skin(Gdx.files.internal("pinkSkin/neon-ui.json"));

        Button quit = new TextButton("QUIT", pink);
        quit.setSize(200, 100);
        quit.setPosition(width / 2,height / 2);
        quit.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        stage.addActor(quit);*/
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputProcessor(game));
        //Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if(board == null && LaserGame.client.getPlayer().getBoard() != null)
        {
            board = LaserGame.client.getPlayer().getBoard();
            board.initialize();
        }

        if(board != null)
        {
            /*
            if(board.getGameState().equals("NoWin")){
                board.render(batch);
            } else {
                if(board.getGameState().equals("AWin") ^ board.flipBoard)
                    LaserGame.client.getPlayer().won();
                else
                    LaserGame.client.getPlayer().lost();
                
                game.setScreen(new GameOverScreen(game));
            }
            */
            if(LaserGame.client.getPlayer().getBoard().isOver){
                game.setScreen(new GameOverScreen(game));
            } else {
                board.render(batch);
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
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
        stage.dispose();
    }
}