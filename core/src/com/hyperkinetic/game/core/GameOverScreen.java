package com.hyperkinetic.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameOverScreen  extends InputAdapter implements Screen {

    LaserGame game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundPic;
    private float width;
    private float height;
    private OrthographicCamera camera;

    public GameOverScreen(final LaserGame game){
        this.game = game;
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        this.game = game;

        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false,width, height);
        stage = new Stage(new StretchViewport(width, height, camera));

        backgroundPic = new Texture(Gdx.files.internal("reboundBackground.jpg"));

        Skin neon = new Skin(Gdx.files.internal("skin/neon-ui.json"));
        neon.getFont("font").getData().setScale(1.20f * width / 1920, 1.20f * height / 1280);


        boolean lastGame = game.client.getPlayer().getLastGame();
        int numPlayed = game.client.getPlayer().getNumPlayed();
        int numWin = game.client.getPlayer().getNumWin();
        int numLoss = game.client.getPlayer().getNumLoss();

        Label record = new Label("", neon);
        if(lastGame) {
            record.setText("You won!");
        } else {
            record.setText("You lost!");
        }
        record.setPosition(width / 2 - (float)(width / 9.6) / 2,height / 2 + (float)(height / 21.6) + 2 * (float)(height / 21.6));
        record.setSize((float)(width / 9.6), (float)(height / 21.6) );


        Label totalGames = new Label("", neon);
        totalGames.setText("You have played " + numPlayed + " games." );
        totalGames.setPosition(width / 2 - (float)(width / 9.6) / 2,height / 2 - (float)(height / 21.6) + 2 * (float)(height / 21.6));
        totalGames.setSize((float)(width / 9.6), (float)(height / 21.6) );

        Label won = new Label("", neon);
        won.setText("You have won " + numWin + " games." );
        won.setSize((float)(width / 9.6),(float)(height / 10.8));
        won.setPosition(width/2 - (float)(width / 9.6) / 2, height / 2 - (float)(height / 10.8));

        Label loss = new Label("", neon);
        loss.setText("You have lost " + numLoss + " games." );
        loss.setSize((float)(width / 9.6),(float)(height / 10.8));
        loss.setPosition(width / 2 - (float)(width / 9.6) / 2, height / 2 - (float) (3.5 * (height / 10.8)) + 2 * (float)(height / 21.6));

        Button back = new TextButton("BACK TO THE MAIN MENU", neon);
        back.setSize((float)(width / 9.6),(float)(height / 10.8));
        back.setPosition(width / 2 - (float)(width / 9.6) / 2, height / 2 - (float) (4.5 * (height / 10.8)) + 2 * (float)(height / 21.6));
        back.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(back);
        stage.addActor(record);
        stage.addActor(totalGames);
        stage.addActor(loss);
        stage.addActor(won);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);



    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255,255,255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundPic, 0, 0, this.width, this.height);
        //stage.getBatch().draw(titlePic, 1920 / 2 - 958/ 2 , 1000);
        stage.getBatch().end();

        stage.act();
        stage.draw();
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
        stage.dispose();
    }
}
