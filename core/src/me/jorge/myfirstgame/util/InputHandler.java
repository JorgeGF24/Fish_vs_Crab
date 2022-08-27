package me.jorge.myfirstgame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import me.jorge.myfirstgame.screens.NameSetScreen;
import me.jorge.myfirstgame.screens.ScreenInputProcessor;
import me.jorge.myfirstgame.MyGame;

public class InputHandler extends GestureDetector {

    public InputHandler(GestureListener listener) {
        super(listener);
    }

    private me.jorge.myfirstgame.MyGame game;
    private ScreenInputProcessor screen;

    private float x,y, gameDeltaX, justTouchedTimer, overallDeltaX,overallDeltaY;

    public void setScreen(ScreenInputProcessor screen) {
        System.out.println("Changed screen to " + screen);
        this.screen = screen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX,screenY,pointer,button);
        y = (MyGame.SCREEN_HEIGHT - screenY) * (MyGame.GAME_HEIGHT/ MyGame.SCREEN_HEIGHT);
        x = screenX* MyGame.GAME_WIDTH/ MyGame.SCREEN_WIDTH;
        screen.touchDown(x,y);
        justTouchedTimer = 0.05f;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX,screenY,pointer,button);
        y = (MyGame.SCREEN_HEIGHT - screenY) * (MyGame.GAME_HEIGHT/ MyGame.SCREEN_HEIGHT);
        x = screenX* MyGame.GAME_WIDTH/ MyGame.SCREEN_WIDTH;
        screen.touchUp(x,y);
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        return super.touchDragged(x, y, pointer);
    }

    @Override
    public boolean keyDown(int keycode) {
        /*if (keycode == 67) {
            ((NameSetScreen) screen).delete();
        }*/
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        if (screen instanceof  NameSetScreen) {
            ((NameSetScreen) screen).keyTyped(character);
        }
        return false;
    }

    public void fligUp(float startX, float startY) {
        y = MyGame.SCREEN_HEIGHT - startY;
        y *= MyGame.GAME_HEIGHT / MyGame.SCREEN_HEIGHT;
        x = startX * MyGame.GAME_WIDTH / MyGame.SCREEN_WIDTH;
        screen.flingUp(x, y);

    }

    public void fligDown(float startX, float startY) {
        y = MyGame.SCREEN_HEIGHT - startY;
        y *= MyGame.GAME_HEIGHT / MyGame.SCREEN_HEIGHT;
        x = startX * MyGame.GAME_WIDTH / MyGame.SCREEN_WIDTH;
        screen.flingDown(x, y);
    }

    public void fligRight(float startX, float startY) {
        y = MyGame.SCREEN_HEIGHT - startY;
        y *= MyGame.GAME_HEIGHT / MyGame.SCREEN_HEIGHT;
        x = startX * MyGame.GAME_WIDTH / MyGame.SCREEN_WIDTH;
        screen.flingRight(x, y);
    }

    public void fligLeft(float startX, float startY) {
        y = MyGame.SCREEN_HEIGHT - startY;
        y *= MyGame.GAME_HEIGHT / MyGame.SCREEN_HEIGHT;
        x = startX * MyGame.GAME_WIDTH / MyGame.SCREEN_WIDTH;
        screen.flingLeft(x, y);
    }

    public void tap(float screenX, float screenY) {
        y = MyGame.SCREEN_HEIGHT - screenY;
        y *= MyGame.GAME_HEIGHT / MyGame.SCREEN_HEIGHT;
        x = screenX * MyGame.GAME_WIDTH / MyGame.SCREEN_WIDTH;
        screen.tap(x,y);
    }

    public void pan(float deltaX, float deltaY) {
        if (justTouchedTimer > 0) {
            overallDeltaX += Math.abs(deltaX);
            overallDeltaY += Math.abs(deltaY);
            justTouchedTimer -= Gdx.graphics.getDeltaTime();
        } else if (overallDeltaX*2>overallDeltaY) {
            gameDeltaX = deltaX* MyGame.GAME_WIDTH/ me.jorge.myfirstgame.MyGame.SCREEN_WIDTH;
            screen.pan(gameDeltaX);
        }
    }

    public void panStop() {
        screen.panStop(gameDeltaX);
    }

    public static class DirectionListener implements GestureDetector.GestureListener {

        private float downX, downY;
        private InputHandler inputHandler;

        public DirectionListener() {

        }

        public void setInputHandler(InputHandler inputHandler) {
            this.inputHandler = inputHandler;
        }

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            downX = x;
            downY = y;
            return true;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            inputHandler.tap(x,y);
            return true;
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            if(Math.abs(velocityX)>Math.abs(velocityY)){
                if(velocityX>0){
                    inputHandler.fligRight(downX,downY);
                }else{
                    inputHandler.fligLeft(downX,downY);
                }
            }else{
                if(velocityY<0){
                    System.out.println("up");
                    inputHandler.fligUp(downX,downY);
                }else{
                    inputHandler.fligDown(downX,downY);
                }
            }
            return true;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            inputHandler.pan(deltaX, deltaY);
            return true;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            inputHandler.panStop();
            return true;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }

        @Override
        public void pinchStop() {

        }

    }
}
