package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.util.UpgradeButton;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class RewardedVideoScreen extends ScreenInputProcessor {

    private final ScreenInputProcessor backScreen;

    private final BitmapFont textFont, userFont, errorFont;
    private boolean internetProblem;
    private float blockTotalTime = 1.5f, blockTimer = blockTotalTime;

    private final Button backButton, adButton;

    private static final float SCALE_X = MyGame.SCREEN_WIDTH/ MyGame.GAME_WIDTH;
    private static final float SCALE_Y = MyGame.SCREEN_HEIGHT/ MyGame.GAME_HEIGHT;

    private final Color BEIGE_CANVAS = new Color(0.95f,0.7f,0.4f,0.6f);
    private static final float ROUNDNESS_RADIUS = 13, SIDE_BORDER = 18, TOP_BORDER = 180, BLOCK_CIRCLE_RADIUS = 16;
    private final int REWARD_QUANTITY, REWARD_TYPE; // 0 to revive, 1 for coins, 2 for hearts

    private final String rewardMessage;

    RewardedVideoScreen(final ScreenInputProcessor backScreen, String rewardMessage, final int REWARD_QUANTITY, final int REWARD_TYPE) {
        this.backScreen = backScreen;

        this.REWARD_QUANTITY = REWARD_QUANTITY;
        this.REWARD_TYPE = REWARD_TYPE;

        textFont = admin.getAsset("coolville15c");
        textFont.setColor(0.1f,0.1f,0.1f,1);
        userFont = admin.getAsset("coolville15");
        errorFont = admin.getAsset("AcariItalic9");
        errorFont.setColor(0.1f,0.1f,0.1f,1);

        userFont.setColor(0.3f,0.3f,0.3f,1);

        this.rewardMessage = rewardMessage;

        backButton = new Button(GAME_WIDTH/2-60, TOP_BORDER + 58, userFont,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.3f,0.3f,0.3f,1),"back") {
            @Override
            public void activate() {
                System.out.println("submitting through button");
                myGame.setScreen(backScreen);
            }
        };
        backButton.setBounds(GAME_WIDTH/2-65, TOP_BORDER + 45, 55, 36);

        adButton = new Button(GAME_WIDTH/2 +30, TOP_BORDER + 40,20, 30f, (Texture) admin.getAsset("sidearrowup"), (Texture) admin.getAsset("sidearrowdown")) {
            @Override
            public void activate() {
                internetProblem = false;
                showAd();
            }
        };
    }

    void setBlockTimer(float time) {
        blockTimer = time;
        blockTotalTime = time;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        if (blockTimer > 0) {
            blockTimer -= delta;
        }

        backScreen.render(0);

        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f,0.1f,0.1f,0.3f);
        shapeRenderer.rect(0,0, GAME_WIDTH, GAME_HEIGHT);

        shapeRenderer.setColor(BEIGE_CANVAS.r,BEIGE_CANVAS.g,BEIGE_CANVAS.b,1);
        shapeRenderer.rect(SIDE_BORDER, TOP_BORDER +ROUNDNESS_RADIUS  , GAME_WIDTH-2* SIDE_BORDER, GAME_HEIGHT-2*(TOP_BORDER +ROUNDNESS_RADIUS));
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,180,90);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,90,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,0,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,-90,90);

        if (blockTimer > 0) {
            shapeRenderer.setColor(BEIGE_CANVAS.r ,BEIGE_CANVAS.g- 0.2f,BEIGE_CANVAS.b- 0.2f,1);
            shapeRenderer.arc(GAME_WIDTH/2-40, TOP_BORDER + 55,BLOCK_CIRCLE_RADIUS,90,360*blockTimer/blockTotalTime);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        batch.begin();
        textFont.draw(batch, rewardMessage, SIDE_BORDER + 20, GAME_HEIGHT-TOP_BORDER - 32);
        if (blockTimer <= 0) {
            backButton.render(batch);
        }
        adButton.render(batch);
        batch.end();
        fontBatch.begin();
        if (internetProblem) {
            errorFont.draw(fontBatch, "Check your internet connection and try again",SCALE_X*(SIDE_BORDER + 30), SCALE_Y*(TOP_BORDER + 71));
        }
        fontBatch.end();
    }

    private void showAd() {
        if (adLoaded) {
            myGame.showRewardedAd();
        } else {
            myGame.loadVideoAd();
        }
    }

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {
        backScreen.onRewarded();
        if (REWARD_TYPE == 1) {
            coins += REWARD_QUANTITY;
            UpgradeButton.updateCoins(coins);
        } else if (REWARD_TYPE == 2) {
            hearts += REWARD_QUANTITY;
        }
    }

    public void onAdClosed() {
        myGame.setScreen(backScreen);
    }

    public void onAdFail() {
        internetProblem = true;
    }

    @Override
    public void touchDown(float x, float y) {
        if (blockTimer <= 0) {
            backButton.checkIfTouched(x, y);
        }
        adButton.checkIfTouched(x,y);
    }

    @Override
    public void touchUp(float x, float y) {
        backButton.checkIfReleased(x,y);
        adButton.checkIfReleased(x,y);
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
