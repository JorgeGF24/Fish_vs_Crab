package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.GameScreen;
import me.jorge.myfirstgame.screens.SpriteHandlerScreen;

public class Bubble {

    private static final float GAME_WIDTH = MyGame.GAME_WIDTH;
    private static SpriteHandlerScreen spriteHandlerScreen;
    private static Player player;
    private static HelperFish helper;
    public static boolean helperAlive;
    public static boolean scoreConverting;
    private static Texture[] bubbleTextures;
    public static Sound popSound, coinSound;
    public static boolean soundOff;
    private static float goldenProbability;

    private static final float HEIGHT = GameScreen.BUBBLE_HEIGHT;
    private static final float WIDTH = GameScreen.BUBBLE_WIDTH;

    private static final float GRAVITY = -300, BOUYANCY = -1.4f*GRAVITY;
    private float ySpeed, y, x;
    private int iTexture;
    // texture 0 is airBall, 1 is goldBall, 2 is pop1, 3 is pop2, 4 is pop3, 5 is pop4

    private static float seaLevel;
    private boolean inWater, isPoppin, isGolden, isSuperGolden;
    private float afloatTimer = 3f, poppinTimer = 0.1f, scheduledPopTimer, width = WIDTH, height = HEIGHT;

    private boolean remove;
    private boolean ejecting;
    private Rectangle collisionRect;

    private final float EJECT_SPEED_MAX = 250;
    private final float EJECT_SPEED_MIN = 130;
    private float ejectSpeed;

    public Bubble(float y, boolean right) {
        isGolden = MathUtils.randomBoolean(goldenProbability);
        iTexture = isGolden?1:0;

        this.y = y;
        ejectSpeed = MathUtils.random(EJECT_SPEED_MIN,EJECT_SPEED_MAX);
        if (right) {
            ejectSpeed *= -1;
            x = GAME_WIDTH - WIDTH;
        }
        ejecting = true;

        collisionRect = new Rectangle(0,0,WIDTH-8,HEIGHT -8);
    }

    public void reset(float y, boolean right) {
        isGolden = MathUtils.randomBoolean(goldenProbability);
        iTexture = isGolden?1:0;


        this.y = y;
        ejectSpeed = MathUtils.random(EJECT_SPEED_MIN,EJECT_SPEED_MAX);
        if (right) {
            ejectSpeed *= -1;
            x = GAME_WIDTH - WIDTH;
        } else {
            x = 0;
        }
        ejecting = true;

        collisionRect = new Rectangle(0,0,WIDTH-8,HEIGHT -8);

        if (isSuperGolden) {
            width = WIDTH;
            height = HEIGHT;
        }

        ySpeed = 0;
        remove = false;
        inWater = false;
        isPoppin = false;
        isSuperGolden = false;
        afloatTimer = 3f;
        poppinTimer = 0.1f;
        scheduledPopTimer = 0;
    }

    public static void setUp(Player player, GameScreen game, int goldLevel) {
        Bubble.player = player;
        Bubble.spriteHandlerScreen = game;
        goldenProbability = 0.1f * goldLevel;
    }

    public static void setUpHelper(HelperFish helper) {
        Bubble.helper = helper;
        Bubble.helperAlive = true;
    }

    public void update(float delta) {
        if (!remove) {
            if (scheduledPopTimer > 0) {
                scheduledPopTimer -= delta;
                if (scheduledPopTimer < 0) {
                    isPoppin = true;
                    ySpeed *= 0.3f;
                    if (!soundOff) {
                        popSound.play();
                        coinSound.play(0.2f);
                    }
                    if (isSuperGolden) {
                        spriteHandlerScreen.coinUp(25);
                    } else {
                        spriteHandlerScreen.coinUp(isGolden ? 3 : 1);
                    }
                    iTexture = 2;
                }
            }

            if (isPoppin) {
                poppinTimer -= delta;
                if (poppinTimer < 0) {
                    remove = true;
                    spriteHandlerScreen.addBubbleToRemove();
                } /*else if (poppinTimer < 0.025f && iTexture == 4) {
                    iTexture = 5;
                } else if (poppinTimer < 0.05f && iTexture == 3) {
                    iTexture = 4;
                } else if (poppinTimer < 0.075f && iTexture == 2) {
                    iTexture = 3;
                }*/
            }

            y += ySpeed * delta;

            if (ejecting) {
                x += ejectSpeed * delta;
                ejectSpeed *= 0.98f;
                if (Math.abs(ejectSpeed) < 5) {
                    ejecting = false;
                }
            }

            if (!scoreConverting) {


                if (y < seaLevel - 23) {

                    inWater = true;
                    ySpeed += BOUYANCY * delta;
                    if (ySpeed < -20) {
                        ySpeed *= 0.8f;
                    }
                } else if (!isPoppin) {
                    ySpeed += GRAVITY * delta;
                }

                if (!isPoppin) {
                    if (checkCollision()) {
                        isPoppin = true;
                        ySpeed *= 0.3f;
                        if (!soundOff) {
                            popSound.play(1);
                            coinSound.play(0.2f);
                        }
                        iTexture = 2;
                        spriteHandlerScreen.coinUp(isGolden ? 3 : 1);
                    }
                }

                if (inWater) {
                    afloatTimer -= delta;
                    if (afloatTimer < 0) {
                        isPoppin = true;
                        ySpeed *= 0.3f;
                        iTexture = 2;
                    }
                }
            } else {
                if (isPoppin) {
                    poppinTimer -= delta;
                    if (poppinTimer < 0) {
                        remove = true;
                        spriteHandlerScreen.addBubbleToRemove();
                    }
                } else if (y < 0) {
                    remove = true;
                    spriteHandlerScreen.addBubbleToRemove();
                } else {
                    ySpeed += GRAVITY * delta;
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (!remove) {
            batch.draw(bubbleTextures[iTexture], x, y, width, height);
        }
    }

    private boolean checkCollision() {
        if  (getCollisionRect().overlaps(player.getCollisionRect())) return true;
        else if (helperAlive) {
            return getCollisionRect().overlaps(helper.getBoundingRectangle());
        }
        return false;
    }

    public boolean shouldRemove() {
        return remove;
    }

    private Rectangle getCollisionRect() {
        collisionRect.setPosition(x+4,y+4);
        return collisionRect;
    }

    public static void setSeaLevel(float newSeaLevel) {
        seaLevel = newSeaLevel;
    }

    public static void setBubbleTextures(Texture[] bubbleTextures) {
        Bubble.bubbleTextures = bubbleTextures;
    }

    public void schedulePop(float time) {
        scheduledPopTimer = time;
    }

    public void makeGolden() {
        isGolden = true;
        iTexture = 1;
    }

    public void makeSuperGolden() {
        isSuperGolden = true;
        iTexture = 1;
        width = 1.7f*WIDTH;
        height = 1.7f*HEIGHT;
    }

    public static void setSpriteHandlerScreen(SpriteHandlerScreen spriteHandlerScreen) {
        Bubble.spriteHandlerScreen = spriteHandlerScreen;
    }
}
