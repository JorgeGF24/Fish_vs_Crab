package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.GameScreen;

public class HelperFish extends Sprite {

    private me.jorge.myfirstgame.screens.GameScreen game;
    private static final float GAME_WIDTH = MyGame.GAME_WIDTH;
    private static final float GAME_HEIGHT = me.jorge.myfirstgame.MyGame.GAME_HEIGHT;

    private static final float X_SPEED = GameScreen.X_SPEED, FLAP_TIME = 0.25f;
    private static final float WIDTH = 45, HEIGHT = 32;
    private static final float GRAVITY = -770, BOUYANCY = -2*GRAVITY;
    private float seaLevel = Sea.HEIGHT;

    private float xVeloc = X_SPEED;
    private float yVeloc;
    private float rotation;
    private boolean flipped;
    private Texture mainTexture, flapTexture;

    private float flapTimer, revivalClock, revivalTime;
    private boolean isFlapping, dead;

    public HelperFish(Texture texture1, Texture texture2, int level, me.jorge.myfirstgame.screens.GameScreen game) {
        super(texture1);

        setSize(WIDTH,HEIGHT);
        setOrigin(WIDTH/2f,HEIGHT/2f);

        this.game = game;

        revivalTime = 13 - level*2;

        mainTexture = texture1;
        flapTexture = texture2;

        if (xVeloc>0) {
            flip(true,false);
            flipped = true;
        }
    }

    public void update(float delta) {

        if (dead) {
            revivalClock -= delta;
            if (revivalClock < 0) {
                dead = false;
                game.activateHelper();
            }
        } else {
            if (flapTimer > 0) {
                flapTimer -= delta;
            } else {
                flapTimer += FLAP_TIME;
                isFlapping = !isFlapping;
                setTexture(isFlapping?flapTexture:mainTexture);
            }

            if (getX() < 8) {
                flip(false);
            } else if (getX() > GAME_WIDTH - 8 - WIDTH) {
                flip(true);
            }

            if (getY() < seaLevel - 23) {
                yVeloc += 0.7f * BOUYANCY * delta;
            } else {
                yVeloc += GRAVITY * delta;
            }

            if (yVeloc > 220) {
                yVeloc = 220;
            }

            setRotation(0.4f * updateRotation(delta));

            setPosition(getX() + 0.7f * xVeloc * delta, getY() + yVeloc * delta);
        }
    }

    private void flip(boolean isRight) {
        if (isRight == flipped) {
            xVeloc = -xVeloc;
            flip(true, false);
            flipped = !flipped;
            rotation = -rotation;
        }
    }

    private float updateRotation(float delta) {
        // if in 2nd or 4th cuadrant
        if (yVeloc*xVeloc < 0) {
            // if in 2nd cuadrant
            if (yVeloc>0) {
                rotation -= 600 * delta;
                if (rotation<-20) {
                    rotation = -20;
                }
                // if in 4th cuadrant
            } else {
                rotation -= 130 * delta;
                if (rotation < -45 ) {
                    rotation = -45;
                }
            }
            // if in 1st or 3rd cuadrant
        } else {
            // if in 1st cuadrant
            if (yVeloc>0) {
                rotation +=600*delta;
                if (rotation>20) {
                    rotation = 20;
                }
                // if in 3rd cuadrant
            } else {
                rotation +=130*delta;
                if (rotation > 45) {
                    rotation = 45;
                }
            }
        }
        return rotation;
    }

    public void setSeaLevel(float newSeaLevel) {
        seaLevel = newSeaLevel;
    }

    public void die() {
        dead = true;
        setY(-HEIGHT);
        revivalClock = revivalTime;
    }

}
