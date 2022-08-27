package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import me.jorge.myfirstgame.screens.GameScreen;
import me.jorge.myfirstgame.screens.SpriteHandlerScreen;

public class Crab extends Sprite {

    private SpriteHandlerScreen spriteHandlerScreen;
    private Player player;

    private static final float HEIGHT = GameScreen.CRAB_HEIGHT;
    private static final float WIDTH = GameScreen.CRAB_WIDTH;
    private static final float STRIKE_WIDTH = 28* GameScreen.CRAB_WIDTH/15f, STRIKE_HEIGHT = 32*HEIGHT/27f;
    private static float climbSpeed = GameScreen.climbSpeed;
    private static final float GRAVITY = GameScreen.GRAVITY;
    private float fallYSpeed, xVeloc;

    private static Texture[] redCrab;
    private static Texture[] purpulCrab;
    private static Texture deadCrab, exclamation;
    private Texture one;
    private Texture oneBlink;
    private boolean isOne;
    private Texture two;
    private Texture twoBlink, readyStrike, strike;
    private boolean isBlinking, isStriking, readyToStrike, isPurpul;
    private float blinkTimer;
    private final static float BLINK_TIME = 0.2f;
    private final static float BLINK_INTERVAL = 2.4f;
    private float animTimer, strikeTimer;
    private final static float ANIM_TIME = 0.2f;

    private Rectangle collisionRect;

    private boolean remove, killed;
    private boolean right;

    public static Sound slashSound;
    public static boolean soundOff;

    public Crab(float x, float y, boolean right, boolean isPurpul, Player player, SpriteHandlerScreen spriteHandlerScreen) {
        super(isPurpul?purpulCrab[2]:redCrab[2]);
        setSize(WIDTH,HEIGHT);

        this.isPurpul = isPurpul;

        if (isPurpul) {
            one = purpulCrab[0];
            oneBlink = purpulCrab[1];
            two = purpulCrab[2];
            twoBlink = purpulCrab[3];
            readyStrike = purpulCrab[4];
            strike = purpulCrab[5];
        } else {
            one = redCrab[0];
            oneBlink = redCrab[1];
            two = redCrab[2];
            twoBlink = redCrab[3];
        }

        blinkTimer = MathUtils.random(BLINK_INTERVAL);
        animTimer = MathUtils.random(ANIM_TIME);

        if (right) {
            flip(true, true);
        } else {
            flip(false,true);
        }

        this.right = right;

        this.spriteHandlerScreen = spriteHandlerScreen;
        this.player = player;

        collisionRect = new Rectangle(0,0,getWidth(),getHeight()-14);

        setPosition(x,y);
    }

    public void reset(float x, float y, boolean right, boolean isPurpul, Player player, SpriteHandlerScreen spriteHandlerScreen) {
        setTexture(isPurpul?purpulCrab[2]:redCrab[2]);
        setSize(WIDTH,HEIGHT);

        this.isPurpul = isPurpul;

        if (isPurpul) {
            one = purpulCrab[0];
            oneBlink = purpulCrab[1];
            two = purpulCrab[2];
            twoBlink = purpulCrab[3];
            readyStrike = purpulCrab[4];
            strike = purpulCrab[5];
        } else {
            one = redCrab[0];
            oneBlink = redCrab[1];
            two = redCrab[2];
            twoBlink = redCrab[3];
        }

        blinkTimer = MathUtils.random(BLINK_INTERVAL);
        animTimer = MathUtils.random(ANIM_TIME);

        if (right != this.right) {
            flip(true, false);
        }

        this.right = right;

        this.spriteHandlerScreen = spriteHandlerScreen;
        this.player = player;

        collisionRect = new Rectangle(0,0,getWidth(),getHeight()-14);

        setPosition(x,y);

        killed = false;
        remove = false;
        isStriking = false;
        readyToStrike = false;
    }

    public static void setTextures(Texture[] redCrab, Texture[] purpulCrab, Texture deadCrab, Texture exclamation) {
        Crab.redCrab = redCrab;
        Crab.purpulCrab = purpulCrab;
        Crab.deadCrab = deadCrab;
        Crab.exclamation = exclamation;
    }
/*
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        getCollisionRect();
        shapeRenderer.rect(collisionRect.x,collisionRect.y,collisionRect.width,collisionRect.height);
        shapeRenderer.end();
    }*/

    public void update(float delta) {
        if (!killed) {
            updateTimers(delta);
        }

        if (killed) {
            fallYSpeed += GRAVITY*delta;
            setPosition(getX() + xVeloc*delta,getY() + fallYSpeed * delta);
        } else {
            setY(getY() + climbSpeed * delta);
        }

        if (getY() < -HEIGHT) {
            remove = true;
            spriteHandlerScreen.setRemoveCrabs(true);
        }

    }

    private void updateTimers(float delta) {
        blinkTimer -= delta;
        animTimer -= delta;
        strikeTimer -= delta;

        if (blinkTimer < 0) {
            if (isBlinking) {
                blinkTimer += BLINK_INTERVAL;
                isBlinking = false;
            } else {
                blinkTimer += BLINK_TIME;
                isBlinking = true;
            }
        }

        if (strikeTimer < 0.3f && readyToStrike) {
                if (!isStriking) {
                setTexture(readyStrike);
                setSize(STRIKE_WIDTH, STRIKE_HEIGHT);
                if (isRight()) {
                    setX(getX() - STRIKE_WIDTH + WIDTH);
                    setY(getY() - STRIKE_HEIGHT + HEIGHT);
                }
                if (!soundOff) {
                    slashSound.play(0.5f);
                }
            }
            isStriking = true;
            if (strikeTimer < 0.087f) {
                if (player.getCollisionRect().overlaps(getBoundingRectangle()) && !player.isPoseidonActive()) {
                    player.die(false);
                }
                setTexture(strike);
                if (strikeTimer < 0) {
                    animTimer = -0.01f;
                    isStriking = false;
                    readyToStrike = false;
                    setSize(WIDTH, HEIGHT);
                    if (isRight()) {
                        setX(getX() + STRIKE_WIDTH - WIDTH);
                        setY(getY() + STRIKE_HEIGHT - HEIGHT);
                    }
                }
            }
        }

        if (animTimer < 0 && !isStriking) {
            if (isOne && isBlinking) {
                setTexture(twoBlink);
                isOne = false;
            } else if (isOne) {
                setTexture(two);
                isOne = false;
            } else if (isBlinking) {
                setTexture(oneBlink);
                isOne = true;
            } else {
                setTexture(one);
                isOne = true;
            }
            animTimer += ANIM_TIME;
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (strikeTimer > 0.3f && !killed) {
            if (right) {
                batch.draw(exclamation,getX()-5, getY(), 5,15);
            } else {
                batch.draw(exclamation,getX()+WIDTH + 5, getY(), 5,15);
            }
        }
    }

    public void setReadyStrike(float timer) {
        if (isPurpul && !killed) {
            readyToStrike = true;
            strikeTimer = timer;
        }
    }

    public void kill() {
        killed = true;
        fallYSpeed = MathUtils.random(40,80);
        xVeloc = MathUtils.random(60,160) * (right?-1:1);
        setTexture(deadCrab);
        if (isPurpul) {
            setSize(WIDTH, HEIGHT);
        }
    }

    public boolean shouldRemove() {
        return remove;
    }

    private boolean isRight() {
        return right;
    }

    public boolean isAlive() {
        return !killed;
    }

    public Rectangle getCollisionRect() {
        collisionRect.setPosition(getX(),getY()+7);
        return collisionRect;
    }

    public static void setClimbSpeed(float climbSpeed) {
        Crab.climbSpeed = climbSpeed;
    }

    public void setSpriteHandlerScreen(SpriteHandlerScreen spriteHandlerScreen) {
        this.spriteHandlerScreen = spriteHandlerScreen;
    }
}
