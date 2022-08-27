package me.jorge.myfirstgame.util;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.ScreenInputProcessor;
import me.jorge.myfirstgame.screens.UpgradesScreen;

public class UpgradeButton extends Button {

    private static final float SCALE_X = MyGame.SCREEN_WIDTH/ MyGame.GAME_WIDTH;
    private static final float SCALE_Y = MyGame.SCREEN_HEIGHT/ me.jorge.myfirstgame.MyGame.GAME_HEIGHT;
    private static final Color BORDER_COLOR = new Color(0.12f,0.11f,0.09f,1);
    private static UpgradesScreen shop;

    private Texture upgradePic;
    private static Texture bubble;
    private static AssetAdmin admin;

    public static Sound buySound, errorSound;
    public static boolean soundOff;
    private Color maxColor;

    private static int coins;
    private int level, maxLevel;
    private int[] cost;

    public static BitmapFont nameFont, descriptionFont, levelFont, maxLevelFont;
    private String name, description = "", sadString = " :/";

    public UpgradeButton(float x, float y, float side) {
        super(x, y, side, BORDER_COLOR, Color.BLACK, Color.BLACK);
        coins = ScreenInputProcessor.coins;
    }

    public void set(String name, String description, Texture upgradePic, Color maxColor, int maxLevel, int[] cost) {
        this.upgradePic = upgradePic;
        this.description = description;
        this.name = name;

        level = admin.getData(name);
        this.maxLevel = maxLevel;
        this.maxColor = maxColor;

        if (level == maxLevel) {
            downColor = maxColor;
            upColor = maxColor;
        }

        this.cost = cost;

    }

    public void unCheck() {
        isChecked = false;
    }

    @Override
    public void checkIfReleased(float x, float y) {
        if (isDown) {
            isDown = false;
            if (bounds.contains(x,y)) {
                if (!soundOff) {
                    clickSound.play();
                }
                if (isChecked) {
                    buyUpgrade();
                } else {
                    isChecked = true;
                    shop.uncheckButtons(this);
                    switch (MathUtils.random(4)){
                        case 0:
                            sadString = " :/";
                            break;
                        case 1:
                            sadString = ":'[";
                            break;
                        case 2:
                            sadString = "X_X";
                            break;
                        case 3:
                            sadString = "...";
                            break;
                        case 4:
                            sadString = " :(";
                            break;
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(upgradePic,x + BORDER_THICKNESS + xDisplacement,y + BORDER_THICKNESS + yDisplacement,width- 2*BORDER_THICKNESS,
                width - 2*BORDER_THICKNESS);
    }

    public void renderText(SpriteBatch fontBatch) {
        nameFont.draw(fontBatch, name,SCALE_X*(x + width + 5 + xDisplacement), SCALE_Y*(y + width - 6 + yDisplacement));
        descriptionFont.draw(fontBatch, description,SCALE_X*(x + width + 5 + xDisplacement), SCALE_Y*(y + width/2 + 2 + yDisplacement));
        if (level < maxLevel) {
            levelFont.draw(fontBatch, "Level " + level, SCALE_X * (9 + x + xDisplacement), SCALE_Y * (y + width + 11 + yDisplacement));
        } else {
            maxLevelFont.draw(fontBatch, "MAX", SCALE_X * (15 + x + xDisplacement), SCALE_Y * (y + width + 11 + yDisplacement));
        }

        if (isChecked) {
            if (level == maxLevel) {
                nameFont.draw(fontBatch, "MAX", SCALE_X * (x + width / 2 - 15 + xDisplacement), SCALE_Y * (y + width / 2 + 15 + yDisplacement));
            } else {
                nameFont.draw(fontBatch, "" + cost[level], SCALE_X * (x + width / 2 - 15 + xDisplacement - (cost[level]>999?10:0)), SCALE_Y * (y + width / 2 + 15 + yDisplacement));
                fontBatch.draw(bubble,SCALE_X*(x + width/2 + 8 + xDisplacement), SCALE_Y*(y + width/2 + 6 + yDisplacement),SCALE_X*10, SCALE_Y*10);
                if (cost[level] <= coins) {
                    nameFont.draw(fontBatch, "Buy?", SCALE_X * (x + width / 2 - 15 + xDisplacement), SCALE_Y * (y + width / 2 + yDisplacement));
                } else {
                    nameFont.draw(fontBatch, sadString, SCALE_X * (x + width / 2 - 11 + xDisplacement), SCALE_Y * (y + width / 2 - 2 + yDisplacement));
                }
            }
        }
    }

    public void renderShade(ShapeRenderer renderer) {
        if (isChecked) {
            renderer.rect(x+BORDER_THICKNESS + xDisplacement,y+BORDER_THICKNESS + yDisplacement, width-2*BORDER_THICKNESS, width-2*BORDER_THICKNESS);
        }
    }

    private void buyUpgrade() {
        if (level < maxLevel) {
            if (cost[level] <= coins) {
                coins -= cost[level];
                if (!soundOff) {
                    buySound.play();
                }
                level++;
                activate();

                if (level == maxLevel) {
                    downColor = maxColor;
                    upColor = maxColor;
                }

                admin.saveData(name, level);
                admin.saveData("coins", coins);
                admin.flush();
                ScreenInputProcessor.coins = coins;
                return;
            }
        }
        shop.unsuccessfulBuy();
        if (!soundOff) {
            errorSound.play();
        }
    }

    public static void setUp(AssetAdmin admin, UpgradesScreen screen) {
        UpgradeButton.admin = admin;
        bubble = admin.getAsset("airball");
        shop = screen;
    }

    public static void updateCoins(int newCoins) {
        coins = newCoins;
    }
}
