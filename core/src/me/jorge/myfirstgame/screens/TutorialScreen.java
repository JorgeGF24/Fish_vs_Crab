package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;

import java.util.ArrayList;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.sprites.Background;
import me.jorge.myfirstgame.sprites.Bubble;
import me.jorge.myfirstgame.sprites.Crab;
import me.jorge.myfirstgame.sprites.Player;
import me.jorge.myfirstgame.sprites.Sea;
import me.jorge.myfirstgame.sprites.Tube;
import me.jorge.myfirstgame.sprites.Wall;
import me.jorge.myfirstgame.util.AssetAdmin;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class TutorialScreen extends ScreenInputProcessor implements SpriteHandlerScreen {

    // First "Tap to jump" "Do not drown" "Try to survive"
    // Avoid crabs
    // Collect bubbles! Tubes spawn bubbles

    private final GameScreen gameScreen;
    private UpgradesScreen shop;

    private boolean fadingOut, fadeInShop1, fadeInShop2;
    private int displayScore;

    private final Player player;
    private final Wall leftWall, rightWall;
    private final Sea sea;
    private final float SEA_Y = 30;

    // 0: Intro fade + animation
    // 1: "Tap to jump" "Don't sink too deep or you'll drown" "Try to survive"
    // 2: Avoid crabs (SPAWN CRAB) (SPAWN CRAB 2 SPACES BETWEEN)
    // 3: Collect 10 bubbles! Tubes spawn bubbles
    private int state = 1, collectedBubbles;
    private final int BUBBLES_TO_COLLECT = 5;
    private boolean rightCrabSpawned, secondCrabSpawned, isDead, isReviving, isDrowned, fade1IsFinished;
    private float revivalTimer;

    private final Color BEIGE_CANVAS = new Color(0.95f,0.7f,0.4f,0.6f);
    private static final float ROUNDNESS_RADIUS = 13, SIDE_BORDER = 25, MESSAGE_BOX_HEIGHT = 100;


    public static final float FADE_OUT_TIME = 4f;
    public float fadeOutTimer = FADE_OUT_TIME;
    private final Color fadingColor = new Color(0, 0, 0, 0);
    private final float targetFade1Alpha = 0.5f;
    private final ColorAction fade = new ColorAction();

    private Background background;

    public static final float X_SPEED = 172;
    private boolean isInWater;

    // The difficulty variable (0-easy up to 3 hard) will modify the crab spawn probability to make it easier for new players
    // and thus increase retention rates.
    private final Texture bubble4score;

    private final ArrayList<Crab> leftCrabs;
    private final ArrayList<Crab> rightCrabs;
    private float crabProb;
    public static final float CRAB_HEIGHT = 45;
    public static final float CRAB_WIDTH = 27;
    private boolean removeCrabs;

    private final ArrayList<Tube> leftTubes;
    private final ArrayList<Tube> rightTubes;
    private float tubeProb;
    public static final float TUBE_HEIGHT = 36;
    public static final float TUBE_WIDTH = 30;
    private boolean removeTubes;

    private int tapCount;

    private final float normalClimbSpeed = -30;
    public static float climbSpeed;
    private static final float SPRITE_MARGIN = 16;
    private float rightSpawnClock = CRAB_HEIGHT + SPRITE_MARGIN, leftSpawnClock;
    private static Sprite godDummySprite;
    private static Sprite dummySprite;
    private static Bubble godBubble;
    private static Bubble dumbBubble;

    private final ArrayList<Bubble> bubbles, bubbleCementery;
    public static final float BUBBLE_HEIGHT = 20;
    public static final float BUBBLE_WIDTH = 20;
    private static int bubblesToRemove;
    private static final float bubbleProb = 0.5f;

    private final Texture landscape, cloud, cloud2;

    public final static float WALL_WIDTH = 13;

    private final BitmapFont fontS, fontL, coinsFont, titleFont, tutorialFont;

    public TutorialScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        gameScreen.setTutorialScreen(this);

        player = gameScreen.getPlayer();
        leftWall = gameScreen.getLeftWall();
        rightWall = gameScreen.getRightWall();
        sea = gameScreen.getSea();

        bubble4score = admin.getAsset("bubble4score");
        fontL = admin.getAsset("canonfont80");
        fontS = admin.getAsset("canonfont60");
        fontL.setColor(0.8f, 0.6f, 0.13f, 1);
        fontS.setColor(0.8f, 0.6f, 0.13f, 1);
        coinsFont = admin.getAsset("khula10");
        titleFont = admin.getAsset("coolville30");
        titleFont.setColor(0.2f, 0.2f, 0.2f, 1);
        tutorialFont = admin.getAsset("coolville15");
        tutorialFont.setColor(0.1f, 0.1f, 0.1f, 1);

        landscape = admin.getAsset("background");
        cloud = admin.getAsset("cloud");
        cloud2 = admin.getAsset("cloud2");

        leftTubes = gameScreen.getLeftTubes();
        rightTubes = gameScreen.getRightTubes();
        leftCrabs = gameScreen.getLeftCrabs();
        rightCrabs = gameScreen.getRightCrabs();
        bubbles = gameScreen.getBubbles();
        bubbleCementery = gameScreen.getBubbleCementery();
    }

    @Override
    public void show() {
        sea.setY(SEA_Y);
        player.setSeaLevel(SEA_Y + Sea.HEIGHT);
        Bubble.setSeaLevel(SEA_Y + Sea.HEIGHT + 5);
        Bubble.setSpriteHandlerScreen(this);
        player.setTutorialOn(true, this);
        climbSpeed = normalClimbSpeed;
        gameScreen.climbSpeedAdd(0);
        Tube.set((Texture) admin.getAsset("tube"), (Texture) admin.getAsset("orangetube"), 0);
    }

    @Override
    public void render(float delta) {

        // ================================ UPDATE GAME ============================================

        // Update all sprites on the screen
        updateSprites(delta);

        if (state == 2) {
            if (!rightCrabSpawned) {
                leftSpawnClock -= climbSpeed * delta;
                if (leftSpawnClock > CRAB_HEIGHT * 5.2) {
                    gameScreen.createCrab(true).setSpriteHandlerScreen(this);
                    rightCrabSpawned = true;
                }
                if (!secondCrabSpawned) {
                    if (leftSpawnClock > CRAB_HEIGHT * 3.1) {
                        gameScreen.createCrab(false).setSpriteHandlerScreen(this);
                        secondCrabSpawned = true;
                    }
                }
            }
            if (leftCrabs.size() == 0 && !isDead) {
                state = 3;
                leftSpawnClock = CRAB_HEIGHT + SPRITE_MARGIN;
                gameScreen.createTube(true).setSpriteHandlerScreen(this);
                gameScreen.createTube(false).setSpriteHandlerScreen(this);
            }
        } else if (state == 3 || state == 4) {
            leftSpawnClock += climbSpeed * delta;
            rightSpawnClock += climbSpeed * delta;
            if (leftSpawnClock < 0) {
                leftSpawnClock = triggerSpawn(true) ? TUBE_HEIGHT + SPRITE_MARGIN : CRAB_HEIGHT + SPRITE_MARGIN;
                displayScore += 3;
            }
            if (rightSpawnClock < 0) {
                rightSpawnClock = triggerSpawn(false) ? TUBE_HEIGHT + SPRITE_MARGIN : CRAB_HEIGHT + SPRITE_MARGIN;
            }
        }

        if (isReviving) {
            revivalTimer -= delta;
            if (revivalTimer < 0) {
                isReviving = false;
                isDead = false;
                if (state == 2) {
                    gameScreen.createCrab(false).setSpriteHandlerScreen(this);
                    climbSpeed /= 5;
                    Crab.setClimbSpeed(climbSpeed);
                    Wall.speed = climbSpeed;
                    leftSpawnClock = 0;
                    rightCrabSpawned = false;
                    secondCrabSpawned = false;
                }
                if (state == 3) {
                    collectedBubbles = 0;
                }
            }
        }

        // ================================= RENDER GAME ===========================================

        if (fadeInShop1 || fadeInShop2) {
            shop.render(delta);

            fade.act(delta);

            if (fade.isComplete()) {
                if (fadeInShop2) {
                    myGame.setScreen(shop);
                } else if (fadeInShop1) {
                    fade1IsFinished = true;
                }
            }

            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fadingColor);
            shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            shapeRenderer.setColor(BEIGE_CANVAS.r,BEIGE_CANVAS.g,BEIGE_CANVAS.b,fadeInShop2?fade.getColor().a/targetFade1Alpha:(1-fade.getColor().a)*10);
            shapeRenderer.rect(SIDE_BORDER, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS  , GAME_WIDTH-2* SIDE_BORDER, GAME_HEIGHT-2*(GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS));
            shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
            shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2+ MESSAGE_BOX_HEIGHT /2 -ROUNDNESS_RADIUS, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
            shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,180,90);
            shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2+ MESSAGE_BOX_HEIGHT /2 -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,90,90);
            shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT/2+ MESSAGE_BOX_HEIGHT /2 -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,0,90);
            shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,-90,90);

            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);

            batch.begin();
            tutorialFont.setColor(0.2f, 0.2f, 0.1f, fadeInShop2?fade.getColor().a/targetFade1Alpha:(1-fade.getColor().a)*10);
            tutorialFont.draw(batch,"Get enough bubbles", GAME_WIDTH / 2 - 96, GAME_HEIGHT/2 + 20);
            tutorialFont.draw(batch,"and buy upgrades", GAME_WIDTH / 2 - 88, GAME_HEIGHT/2 + 5);
            tutorialFont.draw(batch,"in the shop", GAME_WIDTH / 2 - 58, GAME_HEIGHT/2 - 10);
            tutorialFont.setColor(0.9f, 0.9f, 0.9f, fadeInShop2?5*(fade.getColor().a-4f*targetFade1Alpha/5)/targetFade1Alpha:(1-fade.getColor().a-(1-targetFade1Alpha)/2)*10);

            tutorialFont.draw(batch,"-click to continue-", GAME_WIDTH / 2 - 95, GAME_HEIGHT/2 - MESSAGE_BOX_HEIGHT /2 - 5);
            batch.end();

            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.65f, 0.9f, 1);
        shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        shapeRenderer.setColor(0.929f, 0.827f, 0.087f, 1);
        shapeRenderer.circle(GAME_WIDTH / 2, GAME_HEIGHT / 2 + 30, 67);
        shapeRenderer.end();

        batch.enableBlending();

        batch.begin();
        batch.draw(cloud, (GAME_WIDTH - 154) / 2, GAME_HEIGHT / 2 - 67 + 30, 154, 35);
        batch.draw(cloud2, (GAME_WIDTH) / 2 - 67 / 0.8f, GAME_HEIGHT / 2 - 67 / 1.5f + 30, 154f / 3, 35 / 2);
        batch.draw(landscape, 0, 50, GAME_WIDTH, 100);
        if (state > 3) {
            if (displayScore < 10) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 22, GAME_HEIGHT / 2 + 62);
            } else if (displayScore < 100) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 48, GAME_HEIGHT / 2 + 62);
            } else {
                fontS.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 55, GAME_HEIGHT / 2 + 56);
            }
        }
        titleFont.draw(batch, "Tutorial", GAME_WIDTH / 2 - 40, GAME_HEIGHT - 24);
        if (isDead) {
            tutorialFont.setColor(0.2f, 0.2f, 0.2f, 1);
            if (!isReviving) {
                if (isDrowned) {
                    tutorialFont.draw(batch, "Whoops! You drowned", GAME_WIDTH / 2 - 102, 2 * GAME_HEIGHT / 5);
                } else {
                    tutorialFont.draw(batch, "Whoops! You were", GAME_WIDTH / 2 - 90, 2 * GAME_HEIGHT / 5);
                    tutorialFont.draw(batch, "killed by a crab", GAME_WIDTH / 2 - 85, 2 * GAME_HEIGHT / 5 - 15);
                }
            } else {
                tutorialFont.draw(batch, "Try again :]", GAME_WIDTH / 2 - 57, 2 * GAME_HEIGHT / 5);
            }
        } else {
            switch (state) {
                case 0:
                    break;
                case 1:
                    tutorialFont.setColor(0.2f, 0.2f, 0.2f, 1);
                    tutorialFont.draw(batch, "Tap to jump!", GAME_WIDTH / 2 - 60, 2 * GAME_HEIGHT / 5);
                    break;
                case 2:
                    tutorialFont.setColor(0.2f, 0.2f, 0.2f, 1);
                    tutorialFont.draw(batch, "Avoid crabs", GAME_WIDTH / 2 - 60, 2 * GAME_HEIGHT / 5);
                    break;
                case 3:
                    tutorialFont.setColor(0.2f, 0.2f, 0.2f, 1);
                    tutorialFont.draw(batch, "Collect " + (BUBBLES_TO_COLLECT-collectedBubbles) + " bubbles", GAME_WIDTH / 2 - 85, 2 * GAME_HEIGHT / 5);
                    tutorialFont.draw(batch, "Tubes spawn bubbles", GAME_WIDTH / 2 - 97, 2 * GAME_HEIGHT / 5 - 25);
                    break;
                case 4:
                    tutorialFont.setColor(0.2f, 0.2f, 0.2f, 1);
                    tutorialFont.draw(batch, "Great job!", GAME_WIDTH / 2 - 55, 2 * GAME_HEIGHT / 5);
                    tutorialFont.draw(batch, "Now try to survive", GAME_WIDTH / 2 - 92, 2 * GAME_HEIGHT / 5 - 25);
            }
        }

        sea.renderBack(batch, delta);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.13f, 0.49f, 0.757f, 1);
        shapeRenderer.rect(0, 0, GAME_WIDTH, SEA_Y);
        shapeRenderer.end();

        batch.begin();
        for (Bubble bubble : bubbles) {
            bubble.draw(batch);
        }
        for (Tube tube : leftTubes) {
            tube.draw(batch);
        }
        for (Tube tube : rightTubes) {
            tube.draw(batch);
        }
        leftWall.draw(batch);
        rightWall.draw(batch);
        player.draw(batch);

        for (Crab crab : leftCrabs) {
            crab.draw(batch);
        }
        for (Crab crab : rightCrabs) {
            crab.draw(batch);
        }

        sea.renderFront(batch, delta);
        batch.end();

        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0.27f, 0.74f, sea.getFrontAlpha());
        shapeRenderer.rect(0, 0, GAME_WIDTH, SEA_Y);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        if (fadingOut) {
            fadeOutTimer-= delta;

            if (fadeOutTimer < 0) {
                finishedFadeOut();
            }
            fade.act(delta);

            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fadingColor);
            shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);

            batch.begin();
            if (displayScore < 10) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 22, GAME_HEIGHT / 2 + 62);
            } else if (displayScore < 100) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 48, GAME_HEIGHT / 2 + 62);
            } else {
                fontS.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 55, GAME_HEIGHT / 2 + 56);
            }
            batch.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(57, GAME_HEIGHT - 33, 4);
        shapeRenderer.circle(57, GAME_HEIGHT - 22, 4);

        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.circle(57, GAME_HEIGHT - 33, 3);
        shapeRenderer.circle(57, GAME_HEIGHT - 22, 3);

        /*shapeRenderer.circle(GAME_WIDTH/2 - 55, GAME_HEIGHT - 15, 10);
        shapeRenderer.circle(GAME_WIDTH/2 + 55, GAME_HEIGHT - 15, 10);
        shapeRenderer.circle(GAME_WIDTH/2 + 55, GAME_HEIGHT - 35, 10);
        shapeRenderer.circle(GAME_WIDTH/2 - 55, GAME_HEIGHT - 35, 10);
        shapeRenderer.rect(GAME_WIDTH/2 - 65, GAME_HEIGHT - 35, 130, 20);
        shapeRenderer.rect(GAME_WIDTH/2 - 55, GAME_HEIGHT - 45, 110, 40);*/

        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(0, GAME_HEIGHT - 37, 57, 19);
        shapeRenderer.rect(57, GAME_HEIGHT - 33, 4, 11);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(0, GAME_HEIGHT - 37, 57, GAME_HEIGHT - 37);
        shapeRenderer.line(0, GAME_HEIGHT - 18, 57, GAME_HEIGHT - 18);
        shapeRenderer.line(61, GAME_HEIGHT - 22, 61, GAME_HEIGHT - 33);
        shapeRenderer.end();

        batch.begin();
        //tutorialFont.draw(batch, "Tutorial", GAME_WIDTH/2 - 40, GAME_HEIGHT-24);
        //tutorialFont.draw(batch, "Tutorial", GAME_WIDTH/2 - 40, GAME_HEIGHT-24);
        if (state == 1) {
            tutorialFont.setColor(0.7f, 0.7f, 0.7f, 1);
            tutorialFont.draw(batch, "Don't sink too deep", GAME_WIDTH / 2 - 93, 0.35f * GAME_HEIGHT / 5);
        }

        batch.draw(bubble4score, 5, GAME_HEIGHT - 34, 13, 13);
        coinsFont.draw(batch, "" + coins, 23, GAME_HEIGHT - 23);
        batch.end();

    }

    private void updateSprites(float delta) {
        player.update(delta);
        for (Crab crab : rightCrabs) {
            crab.update(delta);
        }
        for (Crab crab : leftCrabs) {
            crab.update(delta);
        }
        for (Tube tube : rightTubes) {
            tube.update(delta);
        }
        for (Tube tube : leftTubes) {
            tube.update(delta);
        }
        for (Bubble bubble : bubbles) {
            bubble.update(delta);
        }
        leftWall.update(delta);
        rightWall.update(delta);


        if (removeCrabs) {
            gameScreen.removeCrabs();
            removeCrabs = false;
        }
        if (removeTubes) {
            gameScreen.removeTubes();
            removeTubes = false;
        }
        if (bubblesToRemove != 0) {
            gameScreen.removeBubbles();
            bubblesToRemove--;
        }
    }

    private boolean triggerSpawn(boolean isRight) {
        crabProb = (float) ((0.4) * Math.pow(0.8f, (isRight ? rightCrabs.size() : leftCrabs.size())));
        tubeProb = (float) (0.85f * Math.pow(0.6f, rightTubes.size() + leftTubes.size())) * (1);

        if (crabProb > tubeProb) {
            if (MathUtils.randomBoolean(crabProb)) {
                gameScreen.createCrab(isRight).setSpriteHandlerScreen(this);
                return false;
            } else if (MathUtils.randomBoolean(tubeProb * 0.6f)) {
                gameScreen.createTube(isRight).setSpriteHandlerScreen(this);
                return true;
            }
        } else if (MathUtils.randomBoolean(tubeProb)) {
            gameScreen.createTube(isRight).setSpriteHandlerScreen(this);
            return true;
        } else if (MathUtils.randomBoolean(crabProb * 0.6f)) {
            gameScreen.createCrab(isRight).setSpriteHandlerScreen(this);
            return false;
        }
        return false;
    }

    public void spawnBubble(boolean right) {
        for (Tube tube : right ? rightTubes : leftTubes) {
            if (MathUtils.randomBoolean(0.58f)) {
                if (bubbleCementery.size() == 0) {
                    final Bubble bubble = new Bubble((int) tube.getY() + 4, right);
                    bubbles.add(bubble);
                } else {
                    final Bubble bubble = bubbleCementery.get(0);
                    bubble.reset((int) tube.getY() + 4, right);
                    bubbles.add(bubble);
                    bubbleCementery.remove(0);
                }
            }
        }
    }

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {

    }

    @Override
    public void touchDown(float x, float y) {
        if (fade1IsFinished && !fadeInShop2) {
            startFade2();
            return;
        }
        player.jump();
        tapCount++;
        if (tapCount == 13) {
            state = 2;
            gameScreen.createCrab(false).setSpriteHandlerScreen(this);
        }
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

    public void setRemoveCrabs(boolean removeCrabs) {
        this.removeCrabs = removeCrabs;
    }

    public void setRemoveTubes(boolean removeTubes) {
        this.removeTubes = removeTubes;
    }

    public void addBubbleToRemove() {
        bubblesToRemove++;
    }

    @Override
    public void coinUp(int add) {
        player.makeHappy();
        coins += add;
        if (collectedBubbles < BUBBLES_TO_COLLECT) {
            collectedBubbles++;
            if (collectedBubbles == BUBBLES_TO_COLLECT && !isDead) {
                state = 4;
                displayScore = 0;
                player.setTutorialOn(false, this);
            }
        }
    }

    public void playerDied(boolean drowned) {
        isDead = true;
        isDrowned = drowned;
    }

    public void playerReviving(float revivalTime) {
        isReviving = true;
        revivalTimer = revivalTime;
        if (state == 2) {
            climbSpeed *= 5;
            Crab.setClimbSpeed(climbSpeed);
            Wall.speed = climbSpeed;
        }
    }

    public void finishedFadeOut() {
        fadeInShop1 = true;
        fade.reset();
        fade.setColor(fadingColor);
        fade.setEndColor(new Color(fadingColor.r,fadingColor.g,fadingColor.b, targetFade1Alpha));
        fade.setDuration(FADE_OUT_TIME);
        final MenuScreen menu = new MenuScreen(false);
        shop = new UpgradesScreen(menu);
        menu.setShop(shop);
        shop.setTutorialOn();
    }

    public void startFade2() {
        fadeInShop2 = true;
        fade.reset();
        fade.setColor(fadingColor);
        fade.setEndColor(new Color(fadingColor.r,fadingColor.g,fadingColor.b,0));
        fade.setDuration(0.25f);
    }

    public void fadeOut() {
        fadingOut = true;
        fade.setColor(fadingColor);
        fade.setEndColor(new Color(fadingColor.r,fadingColor.g,fadingColor.b,1));
        fade.setDuration(2.5f);
    }
}
