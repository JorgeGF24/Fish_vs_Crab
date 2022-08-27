package me.jorge.myfirstgame.util;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.DailyRewardScreen;

public class DailyRewardButton extends Button {

    private static final float SCALE_X = MyGame.SCREEN_WIDTH/MyGame.GAME_WIDTH;
    private static final float SCALE_Y = MyGame.SCREEN_HEIGHT/MyGame.GAME_HEIGHT;
    private static final Color BORDER_COLOR = DailyRewardScreen.FONT_COLOR;
    private static final float WIDTH = DailyRewardScreen.BUTTON_WIDTH;
    public static SpriteBatch batch;
    protected static DailyRewardScreen dailyRewardScreen;

    private final Sprite rewardPic;

    public static Sound buySound, errorSound;
    public static boolean soundOff;
    private static final Color redeemedColor = new Color(0,0,0,0);

    public static BitmapFont dayFont;
    private final int day;
    private boolean isReedemable;
    private static float alpha;

    public DailyRewardButton(float x, float y, int day, Texture rewardPic, boolean isReedemable) {
        super(x, y, 40, BORDER_COLOR, BORDER_COLOR, Color.CLEAR);
        this.day = day;
        this.rewardPic = new Sprite(rewardPic);
        this.rewardPic.setBounds(x + BORDER_THICKNESS,y + BORDER_THICKNESS,width- 2*BORDER_THICKNESS,
                width - 2*BORDER_THICKNESS);
        this.isReedemable = isReedemable;
    }

    @Override
    public boolean checkIfTouched(float x, float y) {
        if (bounds.contains(x,y)) {
            dailyRewardScreen.uncheckButtons(this);
            if (isReedemable) {
                activate();
                if (!soundOff) {
                    buySound.play();
                }
                isReedemable = false;
            } else {
                dailyRewardScreen.unsuccessfulRedeem();
                isChecked = true;
                if (!soundOff) {
                    errorSound.play();
                }
            }
        }
        return false;
    }

    @Override
    public void setAlpha(float newAlpha) {
        alpha = newAlpha;
        rewardPic.setAlpha(newAlpha);
        upColor.a = newAlpha;
        downColor.a = newAlpha;
        backColor.a = newAlpha;
    }

    public static void setDailyRewardScreen(DailyRewardScreen screen) {
        dailyRewardScreen = screen;
    }

    public void unCheck() {
        isChecked = false;
    }

    public void render() {
        rewardPic.draw(batch);
        dayFont.draw(batch, "Day" +  day, x, y+55);
    }

    public void renderShade(ShapeRenderer renderer) {
        if (isChecked) {
            renderer.rect(x+BORDER_THICKNESS + xDisplacement,y+BORDER_THICKNESS + yDisplacement, width-2*BORDER_THICKNESS, width-2*BORDER_THICKNESS);
        }
    }

    public void updateRedeemability(boolean isRedeemable) {
        this.isReedemable = isRedeemable;

    }
}