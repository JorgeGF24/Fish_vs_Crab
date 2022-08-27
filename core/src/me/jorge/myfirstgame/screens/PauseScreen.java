package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import me.jorge.myfirstgame.util.AssetAdmin;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.MyGame;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class PauseScreen extends ScreenInputProcessor {

    private final BitmapFont titleFont, coinsFont, scoreFont;
    private final Button playButton, soundButton, homeButton;
    private final Texture bubbleTexture;
    private final Sprite crab;
    private final Texture[] crabTextures;
    private boolean goingToMenu, goingToGame, crabBlinking, crabInOne = true, longPressOn;
    private final boolean missilePwuOn;
    private float menuTimer = 1, gameTimer = 0.333f, crabAnimTimer, crabBlinkTimer, yDisplacement;
    private final float CRAB_ANIM = 0.65f, CRAB_BLINK_INTERVAL = 1.8f, CRAB_BLINK = 0.3f;

    private final GameScreen gameScreen;
    private int score;
    private int highscore;

    private float alpha;
    private final Color BEIGE_CANVAS = new Color(0.9f,0.9f,0.75f,0.6f);
    private static final float ROUNDNESS_RADIUS = 13, SIDE_BORDER = 18, TOP_BORDER = 18;

    private void goToMenu() {
        goingToMenu = true;
    }

    private void goToGame() {
        goingToGame = true;
    }

    PauseScreen(GameScreen gameScreen) {

        this.gameScreen = gameScreen;

        missilePwuOn = (0 < admin.getData("Fish lord"));
        longPressOn = (1 == admin.getData("longPressOn"));
        if (missilePwuOn) {
            yDisplacement = 30;
        }

        bubbleTexture = admin.getAsset("bubble4score");
        coinsFont = admin.getAsset("khula10");

        titleFont = admin.getAsset("coolville30");
        titleFont.setColor(0.1f,0.1f,0.1f,0);

        scoreFont = admin.getAsset("coolville15");
        scoreFont.setColor(0.1f,0.1f,0.1f,0);

        crabTextures = new Texture[4];
        crabTextures[0] = admin.getAsset("crab");
        crabTextures[1] = admin.getAsset("crab2");
        crabTextures[2] = admin.getAsset("crabblink");
        crabTextures[3] = admin.getAsset("crab2blink");
        crab = new Sprite(crabTextures[0]);
        crab.rotate90(false);
        crab.setBounds(GAME_WIDTH/2-25,GAME_HEIGHT/2 + 50 + yDisplacement, 50,31);

        playButton = new Button(GAME_WIDTH/2 - 17,GAME_HEIGHT/2-40 + yDisplacement,40, 65, (Texture) admin.getAsset("sidearrowup"), (Texture) admin.getAsset("sidearrowdown")) {
          @Override
          public void activate() {
              goToGame();
          }
        };
        homeButton = new Button(GAME_WIDTH/2 - 21,28,42, 39, (Texture) admin.getAsset("homebuttonup"), (Texture) admin.getAsset("homebuttondown")) {
            @Override
            public void activate() {
                goToMenu();
            }
        };
        soundButton = new Button(GAME_WIDTH - 48, 31, 23, (Texture) admin.getAsset("volumeOn"), (Texture) admin.getAsset("volumeOff"));
        soundButton.setIsChecked(soundOff);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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

        if (goingToMenu) {
            menuTimer -= delta;
            if (menuTimer < 0) {
                myGame.anotherGamePlayed(score);
                myGame.setScreen(new MenuScreen(false));
            }
        } else if (goingToGame) {
            gameTimer -= delta;
            alpha = 3*gameTimer;
            if (gameTimer < 0) {
                myGame.setScreen(gameScreen);
                alpha = 0;
            }
            homeButton.setAlpha(alpha);
            playButton.setAlpha(alpha);
            soundButton.setAlpha(alpha);
            crab.setAlpha(alpha);
            titleFont.setColor(0.1f,0.1f,0.1f,alpha);
            scoreFont.setColor(0.1f,0.1f,0.1f,alpha);
        } else if (alpha < 1) {
            alpha += 3*delta;
            if (alpha > 1) {
                alpha = 1;
            }
            homeButton.setAlpha(alpha);
            playButton.setAlpha(alpha);
            soundButton.setAlpha(alpha);
            crab.setAlpha(alpha);
            titleFont.setColor(0.1f,0.1f,0.1f,alpha);
            scoreFont.setColor(0.1f,0.1f,0.1f,alpha);
        }

        batch.enableBlending();
        gameScreen.render(0);

        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BEIGE_CANVAS.r,BEIGE_CANVAS.g,BEIGE_CANVAS.b,0.8f*alpha);
        shapeRenderer.rect(SIDE_BORDER, TOP_BORDER +ROUNDNESS_RADIUS , MyGame.GAME_WIDTH-2* SIDE_BORDER, MyGame.GAME_HEIGHT-2*(TOP_BORDER +ROUNDNESS_RADIUS));
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER, MyGame.GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, MyGame.GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS, MyGame.GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,180,90);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, MyGame.GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,90,90);
        shapeRenderer.arc(MyGame.GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, MyGame.GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,0,90);
        shapeRenderer.arc(MyGame.GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,-90,90);

        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(57,GAME_HEIGHT - 33, 4);
        shapeRenderer.circle(57,GAME_HEIGHT - 22, 4);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.circle(57,GAME_HEIGHT - 33, 3);
        shapeRenderer.circle(57,GAME_HEIGHT - 22, 3);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(0,GAME_HEIGHT - 37, 57,19);
        shapeRenderer.rect(57,GAME_HEIGHT - 33, 4,11);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(0,GAME_HEIGHT - 37,57,GAME_HEIGHT - 37);
        shapeRenderer.line(0,GAME_HEIGHT - 18,57,GAME_HEIGHT - 18);
        shapeRenderer.line(61,GAME_HEIGHT - 22,61,GAME_HEIGHT - 33);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        batch.enableBlending();
        batch.begin();
        batch.draw(bubbleTexture,5,GAME_HEIGHT-34,13,13);
        coinsFont.draw(batch, ""+coins,23,GAME_HEIGHT-23);
        titleFont.draw(batch, "Paused",96, GAME_HEIGHT-35-TOP_BORDER);
        scoreFont.draw(batch, "   Score: " + score + "\nHighscore: " + highscore, 61, GAME_HEIGHT / 2 - 79 + yDisplacement);
        if (missilePwuOn) {
            scoreFont.draw(batch, "Long press to shoot:\n       " + (longPressOn?" on":"off"), SIDE_BORDER + 20, GAME_HEIGHT / 2 - 110);
        }
        homeButton.render(batch);
        playButton.render(batch);
        soundButton.render(batch);
        crab.draw(batch);
        batch.end();
        batch.disableBlending();

        if (goingToMenu) {
            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0,0,0,1-menuTimer);
            shapeRenderer.rect(0,0,GAME_WIDTH,GAME_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);
        }
    }

    public void setScore(int score, int highscore) {
        this.score = score;
        this.highscore = highscore;
    }

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {

    }

    @Override
    public void touchDown(float x, float y) {
        if (y> GAME_HEIGHT / 2 - 146 && y< GAME_HEIGHT / 2 - 100) {
            longPressOn = !longPressOn;
            admin.saveData("longPressOn", longPressOn?1:0);
            admin.flush();
        }
        if (soundButton.checkIfTouched(x,y)) {
            soundOff = !soundOff;
            admin.saveData("soundOff", soundOff?1:0);
            admin.flush();
            Button.soundOff = !soundOff;
            gameScreen.changeSoundOff();
        } else if (playButton.checkIfTouched(x,y)) {
        } else {
            homeButton.checkIfTouched(x,y);
        }
    }

    @Override
    public void touchUp(float x, float y) {
        playButton.checkIfReleased(x,y);
        homeButton.checkIfReleased(x,y);
        soundButton.checkIfReleased(x,y);
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
