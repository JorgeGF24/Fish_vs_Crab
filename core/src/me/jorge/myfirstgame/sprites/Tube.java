package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.GameScreen;
import me.jorge.myfirstgame.screens.SpriteHandlerScreen;

public class Tube extends Sprite {

    private static final float GAME_HEIGHT = MyGame.GAME_HEIGHT;
    private static final float GAME_WIDTH = me.jorge.myfirstgame.MyGame.GAME_WIDTH;
    private SpriteHandlerScreen spriteHandlerScreen;

    private static final float HEIGHT = GameScreen.TUBE_HEIGHT;
    private static final float WIDTH = GameScreen.TUBE_WIDTH;
    private static Texture greenTube, OrangeTube;

    private static float speed = GameScreen.climbSpeed, OrangeProbability;
    private static final float PUSH_SPEED = 100, WALL_WIDTH = GameScreen.WALL_WIDTH;

    private Rectangle collisionRect;

    private boolean remove, right, isPushed, isOrange;
    private float removeOrangeTimer;

    public static boolean soundOff;
    public static Sound tapSound;

    public Tube(float x, float y, boolean right, SpriteHandlerScreen spriteHandlerScreen) {
        super(greenTube);
        if (MathUtils.randomBoolean(OrangeProbability)) {
            isOrange = true;
            setTexture(OrangeTube);
        }
        setSize(WIDTH,HEIGHT);
        if (right) {
            flip(true, false);
        }
        this.right = right;

        this.spriteHandlerScreen = spriteHandlerScreen;

        collisionRect = new Rectangle(0,0,getWidth(),getHeight()-9);

        setPosition(x + MathUtils.random(-5,5),y);
    }

    public void reset(float x, float y, boolean right, SpriteHandlerScreen spriteHandlerScreen) {
        setTexture(greenTube);
        isOrange = false;
        removeOrangeTimer = 0;
        if (MathUtils.randomBoolean(OrangeProbability)) {
            isOrange = true;
            setTexture(OrangeTube);
        }
        setSize(WIDTH,HEIGHT);
        if (right != this.right) {
            flip(true, false);
        }
        this.right = right;

        this.spriteHandlerScreen = spriteHandlerScreen;

        collisionRect = new Rectangle(0,0,getWidth(),getHeight()-9);

        setPosition(x + MathUtils.random(-5,5),y);

        remove = false;
        isPushed = false;
    }

    public static void set(Texture greenTube, Texture OrangeTube, int strongerTubesLvL) {
        Tube.greenTube = greenTube;
        Tube.OrangeTube = OrangeTube;
        OrangeProbability = 0.2f*strongerTubesLvL;
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        getCollisionRect();
        shapeRenderer.rect(collisionRect.x,collisionRect.y,collisionRect.width,collisionRect.height);
        shapeRenderer.end();
    }

    public void update(float delta) {
        if (removeOrangeTimer > 0) {
            removeOrangeTimer -= delta;
            if (removeOrangeTimer < 0) {
                removeOrangeTimer = 0;
            }
            setX(getX() + 0.35f*delta*(right?PUSH_SPEED:-PUSH_SPEED));
            if (getX() < -WIDTH+WALL_WIDTH || getX() > GAME_WIDTH-WALL_WIDTH) {
                remove = true;
                spriteHandlerScreen.setRemoveTubes(true);
            }

        }
        setY(getY() + speed *delta);

        if (getY() < -HEIGHT) {
            remove = true;
            spriteHandlerScreen.setRemoveTubes(true);
        }

        if (isPushed) {
            setX(getX() + delta*(right?PUSH_SPEED:-PUSH_SPEED));
            if (getX() < -WIDTH || getX() > GAME_WIDTH) {
                remove = true;
                spriteHandlerScreen.setRemoveTubes(true);
            }
        }

    }

    public void push() {
        if (isOrange) {
            if (removeOrangeTimer == 0) {

                removeOrangeTimer = 0.4f;
                if (!soundOff) {
                    tapSound.play();
                }
            }
        } else if (!isPushed) {
            isPushed = true;
            if (!soundOff) {
                tapSound.play();
            }
        }
    }

    public boolean shouldRemove() {
        return remove;
    }

    public Rectangle getCollisionRect() {
        collisionRect.setPosition(getX(),getY()+5);
        return collisionRect;
    }

    public static void setSpeed(float speed) {
        Tube.speed = speed;
    }

    public boolean isPushed() {
        return (isPushed || removeOrangeTimer>0);
    }

    public void setSpriteHandlerScreen(SpriteHandlerScreen spriteHandlerScreen) {
        this.spriteHandlerScreen = spriteHandlerScreen;
    }
}
