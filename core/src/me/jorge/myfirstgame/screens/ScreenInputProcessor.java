package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.util.AssetAdmin;

public abstract class ScreenInputProcessor implements Screen {

    public static final float GAME_HEIGHT = me.jorge.myfirstgame.MyGame.GAME_HEIGHT, GAME_WIDTH = MyGame.GAME_WIDTH;
    public static int coins, hearts = 2;
    public static boolean soundOff, adLoaded;

    static final float SCALE_X = MyGame.SCREEN_WIDTH/ MyGame.GAME_WIDTH;
    static final float SCALE_Y = MyGame.SCREEN_HEIGHT/ me.jorge.myfirstgame.MyGame.GAME_HEIGHT;

    protected static SpriteBatch batch, fontBatch;
    protected static ShapeRenderer shapeRenderer;
    protected static AssetAdmin admin;
    protected static MyGame myGame;

    public abstract void changeSoundOff();

    public abstract void onRewarded();

    public abstract void touchDown(float x, float y);

    public abstract void touchUp(float x, float y);

    public abstract void tap(float x, float y);

    public abstract void flingUp(float startX, float startY);

    public abstract void flingDown(float startX, float startY);

    public abstract void flingRight(float startX, float startY);

    public abstract void flingLeft(float startX, float startY);

    public abstract void pan(float deltaX);

    public abstract void panStop(float deltaX);

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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

    }
}
