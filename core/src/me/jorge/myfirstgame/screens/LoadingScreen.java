package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.sprites.Player;
import me.jorge.myfirstgame.util.AssetAdmin;
import me.jorge.myfirstgame.util.Button;

public class LoadingScreen extends ScreenInputProcessor  {

    private final Texture loadingBar, loadingJuice;

    private final Sprite crab;
    private final Texture[] crabTextures;
    private boolean crabBlinking, crabInOne = true;
    private float crabBlinkTimer, crabAnimTimer;
    private final float CRAB_ANIM = 0.65f, CRAB_BLINK_INTERVAL = 1.8f, CRAB_BLINK = 0.3f;

    public LoadingScreen(MyGame game, AssetAdmin admin) {

        System.out.println("We are in loadingScreen");
        myGame = game;
        ScreenInputProcessor.admin = admin;
        batch = game.getBatch();
        shapeRenderer = game.getShapeRenderer();
        fontBatch = MyGame.getFontBatch();

        //resources for loading screen should be loaded here
        final AssetManager manager = admin.getManager();
        manager.load("Mysc/loadingbar.png", Texture.class);
        manager.load("Mysc/loadingjuice.png", Texture.class);
        manager.load("Crab/crab1.png", Texture.class);
        manager.load("Crab/crab1blink.png", Texture.class);
        manager.load("Crab/crab2.png", Texture.class);
        manager.load("Crab/crab2blink.png", Texture.class);
        manager.finishLoading();

        loadingBar = admin.getAsset("loadingbar");
        loadingJuice = admin.getAsset("loadingjuice");

        crabTextures = new Texture[4];
        crabTextures[0] = admin.getAsset("crab");
        crabTextures[1] = admin.getAsset("crab2");
        crabTextures[2] = admin.getAsset("crabblink");
        crabTextures[3] = admin.getAsset("crab2blink");
        crab = new Sprite(crabTextures[0]);
        crab.rotate90(false);
        crab.setBounds(GAME_WIDTH/2-25,GAME_HEIGHT/2 +10, 50,31);

        //Now load the rest of resources
        admin.load();
        coins = admin.getData("coins");
        System.out.println("username is " + admin.getString("userName"));

        MenuScreen.gamemodeHard = (admin.getData("gamemodeHard")==1);
        MenuScreen.hardUnlocked = (admin.getData("Hardcore mode") == 0);

        final int highscore = admin.getData("highscore");
        game.newHighscore(highscore);
        game.newHardcoreHighscore(admin.getData("hardcorehighscore"));
        if (highscore > 310) {
            Player.difficulty = 3;
        } else if (highscore > 250) {
            Player.difficulty = 2;
        } else if (highscore > 190) {
            Player.difficulty = 1;
        }/*

        // CHANGE
        if (admin.getData("milestoneUpdated") == 0) {
            int milestoneLevel = 0;
            final int[] milestones = GameScreen.milestones;
            while (milestones[milestoneLevel] < highscore) {
                milestoneLevel++;
            }
            if (milestoneLevel > 0) {
                milestoneLevel--;
            }
            admin.saveData("milestoneLevel", milestoneLevel);
            admin.saveData("milestoneUpdated", 1);
            admin.flush();
        }*/
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (admin.update()) {
            soundOff = (1==admin.getData("soundOff"));
            Button.soundOff = soundOff;
            Button.clickSound = admin.getAsset("button sound");
            final MenuScreen menu = new MenuScreen(true);
            if (admin.getData("tutorialDone") != 0) {
                menu.setTutotialOn(true);
            }
            if (admin.getString("userName").equals("") || admin.getString("userName") == null) {
                myGame.setScreen(new NameSetScreen(menu));
            } else {
                myGame.setScreen(menu);
            }
        } else {
            //Draw loading screen. manager.getProgress() returns percentage loaded. Use to create loading bar
            //myGame.getBatch().draw();


            crabBlinkTimer -= delta;
            crabAnimTimer -= delta;

            crabAnimTimer -= delta;
            if (crabAnimTimer < 0) {
                crabAnimTimer += CRAB_ANIM;
                crabInOne = !crabInOne;
                crab.setTexture(crabTextures[crabInOne?crabBlinking?2:0:crabBlinking?3:1]);
            }

            if (crabBlinkTimer < 0) {
                if (crabBlinking) {
                    crabBlinkTimer += MathUtils.random(CRAB_BLINK_INTERVAL,CRAB_BLINK_INTERVAL+1.8f);
                    crabBlinking = false;
                    crab.setTexture(crabTextures[crabInOne?2:3]);
                } else {
                    crabBlinkTimer += CRAB_BLINK;
                    crabBlinking = true;
                    crab.setTexture(crabTextures[crabInOne?0:1]);
                }
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();

            crab.draw(batch);

            batch.draw(loadingJuice, GAME_WIDTH/2-65, GAME_HEIGHT/2-25, 130*admin.getProgress(), 22);
            batch.draw(loadingBar, GAME_WIDTH/2-65, GAME_HEIGHT/2-25, 130, 22);

            batch.end();
        }
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
        admin.dispose();
    }

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {

    }

    @Override
    public void touchDown(float x, float y) {

    }

    @Override
    public void touchUp(float x, float y) {

    }

    @Override
    public void tap(float x, float y) {

    }

    @Override
    public void flingUp(float startX, float startY) {

    }

    @Override
    public void flingDown(float startX, float startY) {

    }

    @Override
    public void flingRight(float startX, float startY) {

    }

    @Override
    public void flingLeft(float startX, float startY) {

    }

    @Override
    public void pan(float deltaX) {

    }

    @Override
    public void panStop(float deltaX) {

    }
}
