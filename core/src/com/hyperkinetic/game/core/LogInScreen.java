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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.hyperkinetic.game.playflow.ClientThread;
import com.hyperkinetic.game.playflow.GameServer;

public class LogInScreen  extends InputAdapter implements Screen {
    public static final String LOGIN_FAILURE_FLAG = "LOGIN_FAILURE";

    LaserGame game;
    private Stage stage;
    private SpriteBatch batch;
    private Label outputLabel;
    private Texture backgroundPic;
    private Texture titlePic;
    private float width;
    private float height;
    private OrthographicCamera camera;

    public LogInScreen (final LaserGame game) {
        // constructor
        this.game = game;

        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false,width, height);
        stage = new Stage(new StretchViewport(width, height, camera));

        batch = new SpriteBatch();

        backgroundPic = new Texture(Gdx.files.internal("reboundBackground.jpg"));

        Skin neon = new Skin(Gdx.files.internal("skin/neon-ui.json"));
        Skin pink = new Skin(Gdx.files.internal("pinkSkin/neon-ui.json"));
        neon.getFont("font").getData().setScale(1.30f * width / 1920, 1.30f * height / 1280);
        pink.getFont("font").getData().setScale(1.30f * width / 1920, 1.30f * height / 1280);

        Label usernameLabel = new Label("Username", neon);
        usernameLabel.setPosition(width / 2 - (float)(width / 9.6) / 2 + (float)(width / 9.6) / 4,height / 2 + (float)(height / 21.6) + 2 * (float)(height / 21.6));
        usernameLabel.setSize((float)(width / 9.6), (float)(height / 21.6) );

        Label passwordLabel = new Label("Password", neon);
        passwordLabel.setPosition(width / 2 - (float)(width / 9.6) / 2 + (float)(width / 9.6) / 4,height / 2 - (float)(height / 21.6) + 2 * (float)(height / 21.6));
        passwordLabel.setSize((float)(width / 9.6), (float)(height / 21.6) );

        final TextField username = new TextField("", neon);
        username.setPosition(width / 2 - (float)(width / 9.6) / 2 ,height / 2 + 2 * (float)(height / 21.6));
        username.setSize((float)(width / 9.6), (float)(height / 21.6) );

        final TextField password = new TextField("", neon);
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        password.setPosition(width / 2 - (float)(width / 9.6) / 2,height / 2 - 2 * (float)(height / 21.6) + 2 * (float)(height / 21.6));
        password.setSize((float)(width / 9.6), (float)(height / 21.6) );


        final Label warningLabel = new Label("", pink);
        warningLabel.setPosition(width / 2 - (float)(width / 9.6) / 2 + (float)(width / 9.6) / 4,height / 2 + (float)(height / 21.6) + 3 * (float)(height / 21.6));
        warningLabel.setSize((float)(width / 9.6), (float)(height / 21.6));
        //warningLabel.setFontScale(2.0f);

        Button settings = new TextButton("LOG IN", neon);
        settings.setSize((float)(width / 9.6),(float)(height / 10.8));
        settings.setPosition(width/2 - (float)(width / 9.6) / 2, (float) (height / 2 - 2.5 * (height / 10.8) + 2 * (float)(height / 21.6)));
        settings.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                MainMenuScreen.clickSound.play();
                // System.out.println(username.getText());
                // System.out.println(password.getText());
                ClientThread newPlayer = new ClientThread("localhost", GameServer.port,false,false, game);
                newPlayer.getPlayer().login(username.getText(), password.getText());

                while(newPlayer.userName == null)
                {
                    try
                    {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) { e.printStackTrace(); }
                }
                if(newPlayer.userName.equals(LOGIN_FAILURE_FLAG))
                {
                    warningLabel.setText("Login Failure");
                    newPlayer.resetPlayerID();
                }
                else
                {
                    LaserGame.client = newPlayer;
                    game.setScreen(new MainMenuScreen(game));
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });



        Button register = new TextButton("REGISTER", neon);
        register.setSize((float)(width / 9.6),(float)(height / 10.8));
        register.setPosition(width/2 - (float)(width / 9.6) / 2, height / 2 - (float) (3.5 * (height / 10.8)) + 2 * (float)(height / 21.6));
        register.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                MainMenuScreen.clickSound.play();
                // System.out.println(username.getText());
                // System.out.println(password.getText());

                ClientThread newPlayer = new ClientThread("localhost", GameServer.port,false,false, game);
                newPlayer.getPlayer().register(username.getText(), password.getText());

                while(newPlayer.userName == null)
                {
                    try
                    {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) { e.printStackTrace(); }
                }
                if(newPlayer.userName.equals(LOGIN_FAILURE_FLAG))
                {
                    warningLabel.setText("Username is taken");
                    newPlayer.resetPlayerID();
                }
                else
                {
                    LaserGame.client = newPlayer;
                    game.setScreen(new MainMenuScreen(game));
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        Button back = new TextButton("BACK", neon);
        back.setSize((float)(width / 9.6),(float)(height / 10.8));
        back.setPosition(width / 2 - (float)(width / 9.6) / 2, height / 2 - (float) (4.5 * (height / 10.8)) + 2 * (float)(height / 21.6));
        back.addListener(new InputListener(){
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


        stage.addActor(back);
        stage.addActor(settings);
        stage.addActor(register);
        stage.addActor(username);
        stage.addActor(warningLabel);
        stage.addActor(password);
        stage.addActor(usernameLabel);
        stage.addActor(passwordLabel);
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
        stage.getBatch().draw(backgroundPic, 0, 0, width, height);
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