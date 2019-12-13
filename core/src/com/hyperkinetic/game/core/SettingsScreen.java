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

public class SettingsScreen  extends InputAdapter implements Screen {
    LaserGame game;
    private Stage stage;
    private SpriteBatch batch;
    private Label outputLabel;
    private Texture backgroundPic;
    private Texture titlePic;
    private float width;
    private float height;
    private OrthographicCamera camera;

    public SettingsScreen (final LaserGame game) {
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        this.game = game;

        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false,width, height);
        stage = new Stage(new StretchViewport(width, height, camera));

        backgroundPic = new Texture(Gdx.files.internal("reboundBackground.jpg"));
        //titlePic = new Texture(Gdx.files.internal("LaserGameTitle.png"));

        Skin neon = new Skin(Gdx.files.internal("skin/neon-ui.json"));
        neon.getFont("font").getData().setScale(1.20f * width / 1920, 1.20f * height / 1280);

        // music on and off checkbox group
        ButtonGroup musicChoiceGroup = new ButtonGroup<CheckBox>();

        CheckBox music = new CheckBox("Music", neon);
        music.setSize((float)(width / 9.6),(float)(height / 10.8));
        music.setPosition(width / 2 - 2 * (float)(width / 9.6) / 2, height / 2);

        CheckBox noMusic = new CheckBox("No Music", neon);
        noMusic.setSize((float)(width / 9.6),(float)(height / 10.8));
        noMusic.setPosition(width / 2 - 2 * (float)(width / 9.6) / 2 + (float)(width / 9.6) , height / 2);

        musicChoiceGroup.add(noMusic);
        musicChoiceGroup.add(music);
        musicChoiceGroup.setMaxCheckCount(1);
        musicChoiceGroup.setMinCheckCount(1);
        musicChoiceGroup.setUncheckLast(true);

        music.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                MainMenuScreen.clickSound.play();
                //System.out.println("touch up on music");
                //System.out.println("printing bgm " + bgm);
                if(!MainMenuScreen.bgm.isPlaying()) {
                    MainMenuScreen.bgm.play();
                }

                //System.out.println("if the same instance " + bgm.);
                MainMenuScreen.playBgm = true;
                MainMenuScreen.initialPlaying = false;
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        noMusic.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                MainMenuScreen.clickSound.play();
                //System.out.println("touch up on no music");
                if(MainMenuScreen.bgm.isPlaying()) {
                    MainMenuScreen.bgm.pause();
                }
                MainMenuScreen.playBgm = false;
                MainMenuScreen.initialPlaying = false;
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        if(MainMenuScreen.playBgm) {
            musicChoiceGroup.setChecked("Music");
        } else {
            musicChoiceGroup.setChecked("No Music");
        }

        Label bar = new Label("VOLUME", neon);
        bar.setSize((float)(width / 9.6),(float)(height / 10.8));
        bar.setPosition(width / 2 - (float)(width / 9.6) / 6, height / 2 + (float) ((1.5) * (height / 10.8)));


        final Slider slider = new Slider(0, 100,1, false, neon);
        slider.setPosition(width / 2 - (float)(width / 9.6) , height / 2 + (float)(height / 10.8));
        slider.setSize((float)(width / 4.8), (float)(height / 10.8));
        slider.setValue(50);
        slider.addListener(new DragListener() {
            public void dragStart(InputEvent event, float x, float y, int pointer)
            {
                MainMenuScreen.bgm.setVolume(slider.getPercent());
            }

            public void drag(InputEvent event, float x, float y, int pointer)
            {
                MainMenuScreen.bgm.setVolume(slider.getPercent());
            }

            public void dragStop(InputEvent event, float x, float y, int pointer)
            {
                MainMenuScreen.bgm.setVolume(slider.getPercent());
            }

        });

        Button settings = new TextButton("BACK", neon);
        settings.setSize((float)(width / 9.6),(float)(height / 10.8));
        settings.setPosition(width / 2 - (float)(width / 9.6) / 2, height / 2 - (float)(height / 10.8));
        settings.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                MainMenuScreen.clickSound.play();
                game.setScreen(new MainMenuScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(settings);
        stage.addActor(bar);
        stage.addActor(slider);
        stage.addActor(music);
        stage.addActor(noMusic);
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