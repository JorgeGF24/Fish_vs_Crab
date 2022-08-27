package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.GameScreen;
import me.jorge.myfirstgame.screens.TutorialScreen;

public class Player extends Sprite {

    private final GameScreen game;
    private static final float GAME_WIDTH = MyGame.GAME_WIDTH;
    private static final float GAME_HEIGHT = me.jorge.myfirstgame.MyGame.GAME_HEIGHT;
    private static final float TUBE_BORDER = GameScreen.WALL_WIDTH + GameScreen.TUBE_WIDTH;

    private static final float X_SPEED = GameScreen.X_SPEED;
    private static final float WIDTH = GameScreen.PLAYER_WIDTH, HEIGHT = GameScreen.PLAYER_HEIGHT;
    private static final float JUMP = GameScreen.JUMP;
    private float waterJump = X_SPEED*0.7f;
    private float waterSink = -X_SPEED*0.5f;
    private static final float GRAVITY = -770, BOUYANCY = -2*GRAVITY;
    private float seaLevel = Sea.HEIGHT, hitTimer;

    private final Rectangle collisionRect;
    private Sprite loopSprite;
    public Sound jumpSound, splashSound, soundOfLife, soundOfDeath;
    public static int difficulty;
    public boolean soundOff;

    private float xVeloc = X_SPEED;
    private float yVeloc;
    private float rotation;
    private boolean flipped;
    private Texture mainTexture, happyMainTexture, flapTexture, happyFlapTexture, deadTexture, exclamation, beam, poseidonTexture;
    private final Texture[] loops;
    private float flapTimer, happyTimer, deadTimer = GameScreen.FADE_OUT_TIME, revivalTimer, inmunityTimer, alpha;
    private boolean isFlapping;
    private boolean inWater;
    private boolean isHappy;
    private boolean isReviving;
    private boolean isInmune;
    private boolean isLoopy;
    private boolean poseidonActive;
    private boolean endFlap;
    private boolean dead, finishedFading;
    private float koTimer;
    private boolean koTimerStarted;
    private boolean tutorialOn, tutorialDeath;
    private TutorialScreen tutorialScreen;

    private int lives, betterSwimmerLevel;

    public Player(Texture[] textures, Texture[] loops, int lives, GameScreen game) {
        super(textures[0]);

        setSize(WIDTH,HEIGHT);
        setOrigin(WIDTH/2f,HEIGHT/2f);
        collisionRect = new Rectangle(0,0,WIDTH-10,HEIGHT-6);

        this.game = game;

        mainTexture = textures[0];
        happyMainTexture = textures[1];
        flapTexture = textures[2];
        happyFlapTexture = textures[3];
        deadTexture = textures[4];
        exclamation = textures[5];
        beam = textures[6];
        poseidonTexture = textures[7];

        this.loops = loops;
        flapTimer = 0f;
        this.lives = lives;

        if (lives > 0) {
            loopSprite = new Sprite(loops[lives-1]);
            isLoopy = true;
            loopSprite.setBounds(getX(),getY(),WIDTH,HEIGHT*24/23f);
            loopSprite.setOrigin(WIDTH/2f,HEIGHT/2f);
        }

        if (xVeloc>0) {
            flip(true,false);
            flipped = true;
        }
    }

    public void setSwimmingLvL(int lvl) {
        betterSwimmerLevel = lvl;
        waterSink = waterSink*(1-lvl*0.075f);
        waterJump = waterJump*(1+lvl*0.075f);
    }

    public void setHardcore(boolean hardcoreOn) {
        if (hardcoreOn) {
            waterSink = waterSink / (1 - betterSwimmerLevel * 0.075f);
            waterJump = waterJump / (1 + betterSwimmerLevel * 0.075f);
        } else {
            waterSink = waterSink * (1 - betterSwimmerLevel * 0.075f);
            waterJump = waterJump * (1 + betterSwimmerLevel * 0.075f);
        }
    }

    public void update(float delta) {
        if (finishedFading || poseidonActive)
            return;

        if (!isReviving) {
            updateTimers(delta);
            setRotation(updateRotation(delta));
            checkForBorderCollisions();
        } else {
            revivalTimer -= delta;
            if (revivalTimer < 0) {
                isReviving = false;
                game.setIsReviving(false);
                inWater = false;
                isInmune = true;
                inmunityTimer = 3;
                xVeloc = flipped?X_SPEED:-X_SPEED;
            }
        }

        if (!poseidonActive) {
            setPosition(getX() + xVeloc * delta, getY() + yVeloc * delta);
        }

        if (!(isReviving || poseidonActive)) {
            yVeloc += GRAVITY * delta;

            if (getY() < seaLevel - 23) {
                if (!inWater) {
                    inWater = true;
                    game.playerInWater(true);
                    if (!soundOff) {
                        splashSound.play();
                    }
                }
                if (dead) {
                    yVeloc += BOUYANCY * delta;
                    if (yVeloc < 1.5f * waterSink) {
                        yVeloc = 1.5f * waterSink;
                    }
                } else if (yVeloc < waterSink) {
                    yVeloc = waterSink;
                }
                if (getY() < -25) {
                    die(true);
                }
            }
            if (getY() > GAME_HEIGHT - getHeight()- 150 + 40*difficulty) {
                setY(GAME_HEIGHT - getHeight()- 150+ 40*difficulty);
                game.climbSpeedAdd(yVeloc);
            }

            if (dead) {
                xVeloc = xVeloc * 0.984f;
            } /*else if (startJump) {
            xVeloc = 96;
            if (getX() > GAME_WIDTH - getWidth() - 8) {
                startJump = false;
            }
        }*/
        }
    }

    private void checkForBorderCollisions() {
        if (getX() < TUBE_BORDER) {
            if (game.checkTubeCollisions(false)) {
                if (!flipped ){
                    flip(false);
                    game.spawnBubble(true);
                }
            } else if (getX() < 9) {
                if (game.checkCrabCollisions(false)) {
                    die(false);
                }
                if (!flipped) {
                    flip(false);
                    game.spawnBubble(true);
                }
            }
        } else if (getX() > GAME_WIDTH - TUBE_BORDER - WIDTH) {
            if (game.checkTubeCollisions(true)) {
                if (flipped) {
                    flip(true);
                    game.spawnBubble(false);
                }
            } else if (getX() > GAME_WIDTH - getWidth() - 8) {
                if (game.checkCrabCollisions(true)) {
                    die(false);
                }
                if (flipped) {
                    flip(true);
                    game.spawnBubble(false);
                }
            }
        }
    }

    private void flip(boolean isRight) {
        if (isRight == flipped) {
            xVeloc = -xVeloc;
            flip(true, false);
            flipped = !flipped;
            rotation = -rotation;
        }
        game.playerBounced(isRight);
    }

    private void updateTimers(float delta) {
        if (hitTimer > 0) {
            hitTimer -= delta;
            if (hitTimer < 0) {
                setColor(Color.WHITE);
            }
        }
        if (isInmune) {
            inmunityTimer -= delta;
            if (inmunityTimer%0.5f > 0.25f) {
                setAlpha(inmunityTimer%0.5f*2);
            } else {
                setAlpha(1-inmunityTimer%0.5f*2);
            }
            if (inmunityTimer < 0) {
                isInmune = false;
                setAlpha(1);
            }
        }
        if (dead) {
            deadTimer -= delta;
            if (deadTimer<0  && getY() < GAME_HEIGHT/2) {
                if (tutorialOn) {
                    revive();
                } else if (tutorialDeath) {

                } else if (lives == 0) {
                    if (!finishedFading) {
                        game.finishedFading();
                        finishedFading = true;
                    }
                } else {
                    revive();
                    lives--;
                    if (lives == 0) {
                        isLoopy = false;
                    } else {
                        loopSprite.setTexture(loops[lives-1]);
                    }
                }
            }
        }
        if (flapTimer > 0) {
            flapTimer -= delta;
        } else if (isFlapping && !dead) {
            if (isHappy) {
                setTexture(happyMainTexture);
            } else {
                setTexture(mainTexture);
            }
            isFlapping = false;
        }
        if (happyTimer > 0) {
            happyTimer -= delta;
        } else if (isHappy && !dead) {
            isHappy = false;
            if (isFlapping) {
                setTexture(flapTexture);
            } else {
                setTexture(mainTexture);
            }
        }
    }

    public void revive() {
        yVeloc = 40;
        finishedFading = false;
        isReviving = true;
        game.setIsReviving(true);
        revivalTimer = 3;
        if (tutorialOn) {
            tutorialScreen.playerReviving(revivalTimer);
        }
        if (!soundOff) {
            soundOfLife.play(0.6f);
        }
        xVeloc = 0;
        dead = false;
        game.setIsDead(false);
        setTexture(mainTexture);
    }

    public void updateInMenu(float delta) {

        if (getX() < 8) {
            flip(false);
        } else if (getX() > GAME_WIDTH - 8 - WIDTH) {
            flip(true);
        }

        if (getY() < seaLevel - 23) {
            yVeloc += 0.7f*BOUYANCY * delta;
        } else {
            yVeloc += GRAVITY * delta;
        }

        if (Math.abs(yVeloc) > 220) {
            yVeloc *= 220/Math.abs(yVeloc);
        }

        setRotation(0.4f*updateRotation(delta));

        setPosition(getX() + 0.7f*xVeloc * delta,getY() + yVeloc * delta);

    }

    void die(boolean drowned) {
        if (!(dead || isInmune)) {
            deadTimer = GameScreen.FADE_OUT_TIME;
            dead = true;
            if (tutorialOn) {
                tutorialScreen.playerDied(drowned);
            }
            hitTimer = 0.12f;
            setColor(new Color(0.6f,0.3f,0.3f,1));
            if (!soundOff) {
                soundOfDeath.play(0.5f);
            }
            game.setIsDead(true);
            setTexture(deadTexture);
            if (!(isLoopy || tutorialOn)) {
                if (tutorialDeath) {
                    tutorialScreen.fadeOut();
                } else {
                    game.fadeOut();
                }
            }
        }
    }

    private float updateRotation(float delta) {
        // if in 2nd or 4th cuadrant
        if (yVeloc*xVeloc < 0) {
            // if in 2nd cuadrant
            if (yVeloc>0) {
                rotation -= 600 * delta;
                /*if (startJump) {
                    if (rotation<-60) {
                        rotation = -60;
                    }
                } else*/ if (rotation<-20) {
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
                /*if (startJump) {
                    if (rotation>60) {
                        rotation = 60;
                    }
                } else */if (rotation>20) {
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

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (inWater&&!dead) {
            if (xVeloc > 0) {
                batch.draw(exclamation,getX()+40, getY() + 33, 5,15);
            } else {
                batch.draw(exclamation,getX()+8, getY() + 33, 5,15);
            }
        }
        if (isReviving) {
            batch.draw(beam, getX() - 20, 0, getWidth() + 40, GAME_HEIGHT);
        }
        if (isLoopy) {
            loopSprite.draw(batch);
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        getCollisionRect();
        shapeRenderer.rect(collisionRect.x,collisionRect.y,collisionRect.width,collisionRect.height);
        shapeRenderer.end();
    }

    public void jump() {
        if (!(dead || isReviving || poseidonActive)) {
            if (!inWater) {
                yVeloc = JUMP;
                if (!soundOff) {
                    jumpSound.play(0.5f);
                }
                isFlapping = true;
                if (isHappy) {
                    setTexture(happyFlapTexture);
                } else {
                    setTexture(flapTexture);
                }
                flapTimer = 0.13f;
            } else {
                yVeloc = waterJump;
                if (getY() > seaLevel -15) {
                    inWater = false;
                    game.playerInWater(false);
                }
            }
        }
    }

    public void lifeUpgrade(int level) {
        lives = level;
        if (lives == 0) {
            isLoopy = false;
            loopSprite = null;
        } else {
            if (loopSprite == null) {
                loopSprite = new Sprite(loops[lives - 1]);
            }
            loopSprite.setTexture(loops[lives - 1]);
            isLoopy = true;
            loopSprite.setBounds(getX(), getY(), WIDTH, HEIGHT * 24 / 23f);
            loopSprite.setOrigin(WIDTH / 2f, HEIGHT / 2f);
        }
    }

    public void startJump() {
        yVeloc = 1.4f*JUMP;
        if (!soundOff) {
            jumpSound.play(0.5f);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x,y);
        if (isLoopy) {
            loopSprite.setPosition(x, y);
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        if (isLoopy) {
            loopSprite.setY(y);
        }
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        if (isLoopy) {
            loopSprite.setRotation(degrees);
        }
    }

    @Override
    public void flip(boolean x, boolean y) {
        super.flip(x,y);
        if (isLoopy) {
            loopSprite.flip(x, y);
        }
    }

    public Rectangle getCollisionRect() {
        collisionRect.setPosition(getX()+3,getY()+3);
        return collisionRect;
    }

    public void addToY(float add) {
        setY(getY()+add);
        if (isLoopy) {
            loopSprite.setY(getY());
        }
    }

    public void setTexturePack(Texture[] textures) {
        setTexture(textures[0]);

        mainTexture = textures[0];
        happyMainTexture = textures[1];
        flapTexture = textures[2];
        happyFlapTexture = textures[3];
        deadTexture = textures[4];
        exclamation = textures[5];
        beam = textures[6];
        poseidonTexture = textures[7];
    }

    public void setSeaLevel(float seaLevel) {
        this.seaLevel = seaLevel;
    }

    public void setInmune(boolean inmune, float time) {
        isInmune = inmune;
        inmunityTimer = time;
    }

    public void activatePoseidon(boolean newState) {
        poseidonActive = newState;
        setTexture(poseidonTexture);
        if (xVeloc > 0) {
            setRotation(20);
        } else {
            setRotation(-20);
        }

        if (!newState) {
            if (getY() < seaLevel -15) {
                inWater = true;
                game.playerInWater(true);
            }
        }
    }

    public void setTutorialOn(boolean tutorialOn, TutorialScreen tutorialScreen) {
        this.tutorialOn = tutorialOn;
        tutorialDeath = true;
        this.tutorialScreen = tutorialScreen;
    }

    boolean isPoseidonActive() {
        return poseidonActive;
    }

    public void makeHappy() {
        if (!(dead || poseidonActive)) {
            happyTimer = 0.7f;
            isHappy = true;
            if (isFlapping) {
                setTexture(happyFlapTexture);
            } else {
                setTexture(happyMainTexture);
            }
        }
    }
}
