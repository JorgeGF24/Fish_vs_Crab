package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;

import java.lang.reflect.Array;
import java.util.Arrays;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.util.DailyRewardButton;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class DailyRewardScreen extends ScreenInputProcessor {

    private final MenuScreen menu;
    private final Button backButton;

    private static final Color BEIGE_CANVAS = new Color(0.95f,0.7f,0.4f,0.6f),
            TRANSPARENT = new Color(0,0,0,0), RED_SHADE = new Color(0.85f,0.1f,0.1f,0.9f);
    public static final Color FONT_COLOR = new Color(0.1f, 0.1f, 0.1f, 1);
    public static final float BUTTON_WIDTH = 40;
    private static final float ROUNDNESS_RADIUS = 13,  MESSAGE_BOX_HEIGHT = 220, SIDE_BORDER = 18,
            TOP_DRAWING_WIDTH = BUTTON_WIDTH*4 + 10*4, BOT_DRAWING_WIDTH = BUTTON_WIDTH*3 + 10*3;
    private float alpha;
    private boolean goingToMenu, comingFromMenu = true;
    private final float TRANSITION_TIME = 0.27f;

    private Color shadeColor = TRANSPARENT;
    private final ColorAction shadeFade = new ColorAction();

    private final BitmapFont titleFont, dayFont, backFont;

    private final DailyRewardButton[] topButtons, botButtons;
    private static int daysRedeemed = admin.getData("daysredeemed");
    private static int[] lastDateRedeemed = (admin.getString("lastdateredeemed").equals("")?new int[]{0,0,0}:new int[]{Integer.parseInt(admin.getString("lastdateredeemed").substring(0,2)),
            Integer.parseInt(admin.getString("lastdateredeemed").substring(2,4)),
            Integer.parseInt(admin.getString("lastdateredeemed").substring(4,8))});
    private final static int[] date = myGame.getDate();
    private static Texture tick;

    private final boolean isTimeAutomatic;

    DailyRewardScreen(MenuScreen menu) {
        System.out.println("Last day redeemed create " + lastDateRedeemed[0] + "" + lastDateRedeemed[1] + "" + lastDateRedeemed[2]);
        System.out.println("days redeemed create " + daysRedeemed);
        this.menu = menu;
        DailyRewardButton.setDailyRewardScreen(this);

        titleFont = admin.getAsset("coolville30");
        titleFont.setColor(FONT_COLOR);
        dayFont = admin.getAsset("coolville15");
        dayFont.setColor(FONT_COLOR);
        DailyRewardButton.dayFont = dayFont;
        DailyRewardButton.batch = batch;
        DailyRewardButton.errorSound = admin.getAsset("error sound");
        DailyRewardButton.buySound = admin.getAsset("buy sound");

        topButtons = new DailyRewardButton[4];
        botButtons = new DailyRewardButton[3];
        tick = admin.getAsset("tick");

        isTimeAutomatic = MyGame.isTimeAutomatic();

        shadeFade.setColor(shadeColor);
        shadeFade.setEndColor(shadeColor);

        boolean redemable = false;
        try {
            lastDateRedeemed = (admin.getString("lastdateredeemed").equals("") ? new int[]{0, 0, 0} : new int[]{Integer.parseInt(admin.getString("lastdateredeemed").substring(0, 2)),
                    Integer.parseInt(admin.getString("lastdateredeemed").substring(2, 4)),
                    Integer.parseInt(admin.getString("lastdateredeemed").substring(4, 8))});
            if (date[2] != 0 && date[2] == lastDateRedeemed[2]) {
                if (date[1] == lastDateRedeemed[1]) {
                    if (date[0] - lastDateRedeemed[0] == 1) {
                        redemable = true;
                    }
                } else if (date[1] - lastDateRedeemed[1] == 1) {
                    int[] DAYS_IN_MONTH = new int[]{31, (date[2] % 4 == 0) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                    if (date[0] == 1 && lastDateRedeemed[0] == DAYS_IN_MONTH[lastDateRedeemed[1]]) {
                        redemable = true;
                    }
                }
            }
        } catch (Exception e) {

        }

        System.out.println("redeemableeee " + redemable);

        if (redemable) {
            menu.dailyRewardIsAvailable();
            if (daysRedeemed == 7) {
                daysRedeemed = 0;
                admin.saveData("daysredeemed", 0);
                admin.flush();
            }
        } else if (!Arrays.equals(lastDateRedeemed, date)) {
            daysRedeemed = 0;
            admin.saveData("daysredeemed", 0);
            admin.flush();
        }

        topButtons[0] = new DailyRewardButton(GAME_WIDTH/2 - TOP_DRAWING_WIDTH/2, GAME_HEIGHT/2 + 10, 1,
                (Texture) admin.getAsset("day1reward"), daysRedeemed == 0) {
            @Override
            public void activate() {
                coins += 50;
                dailyRewardScreen.reward(1);
            }
        };
        topButtons[1] = new DailyRewardButton(GAME_WIDTH/2 - TOP_DRAWING_WIDTH/2 + TOP_DRAWING_WIDTH/4, GAME_HEIGHT/2 + 10, 2,
                (Texture) admin.getAsset("day2reward"), (1 == daysRedeemed) && redemable) {
            @Override
            public void activate() {
                hearts += 6;
                dailyRewardScreen.reward(2);
            }
        };
        topButtons[2] = new DailyRewardButton(GAME_WIDTH/2 - TOP_DRAWING_WIDTH/2 + 2*TOP_DRAWING_WIDTH/4, GAME_HEIGHT/2 + 10, 3,
                (Texture) admin.getAsset("day3reward"), 2 == daysRedeemed && redemable) {
            @Override
            public void activate() {
                coins += 200;
                dailyRewardScreen.reward(3);
            }
        };
        topButtons[3] = new DailyRewardButton(GAME_WIDTH/2 - TOP_DRAWING_WIDTH/2 + 3*TOP_DRAWING_WIDTH/4, GAME_HEIGHT/2 + 10, 4,
                (Texture) admin.getAsset("day4reward"), 3 == daysRedeemed && redemable) {
            @Override
            public void activate() {
                coins += 400;
                dailyRewardScreen.reward(4);
            }
        };
        botButtons[0] = new DailyRewardButton(GAME_WIDTH/2 - BOT_DRAWING_WIDTH/2 , GAME_HEIGHT/2 - 60, 5,
                (Texture) admin.getAsset("day5reward"), 4 == daysRedeemed && redemable) {
            @Override
            public void activate() {
                hearts += 12;
                dailyRewardScreen.reward(5);
            }
        };
        botButtons[1] = new DailyRewardButton(GAME_WIDTH/2 - BOT_DRAWING_WIDTH/2 + TOP_DRAWING_WIDTH/4, GAME_HEIGHT/2 - 60, 6,
                (Texture) admin.getAsset("day6reward"), 5 == daysRedeemed && redemable) {
            @Override
            public void activate() {
                coins += 1000;
                hearts += 20;
                dailyRewardScreen.reward(6);
            }
        };
        botButtons[2] = new DailyRewardButton(GAME_WIDTH/2 - BOT_DRAWING_WIDTH/2 + 2*TOP_DRAWING_WIDTH/4, GAME_HEIGHT/2 - 60, 7,
                (Texture) admin.getAsset("day7reward"), 6 == daysRedeemed && redemable) {
            @Override
            public void activate() {
                dailyRewardScreen.reward(7);
            }
        };

        backFont = admin.getAsset("coolville30b");
        backButton = new Button(GAME_WIDTH/2-42, GAME_HEIGHT/2-MESSAGE_BOX_HEIGHT/2 + 31, backFont,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.3f,0.3f,0.3f,1),"-back-") {
            @Override
            public void activate() {
                goToMenu();
            }
        };
        backButton.setBounds(GAME_WIDTH/2-45, GAME_HEIGHT/2-MESSAGE_BOX_HEIGHT/2+10, 90, 26);
    }

    private void goToMenu() {
        goingToMenu = true;
    }

    public void render(float delta) {
        if (comingFromMenu) {
            alpha += delta/TRANSITION_TIME;
            if (alpha > 1) {
                alpha = 1;
                comingFromMenu = false;
            }
        }

        if (goingToMenu) {
            alpha -= delta/TRANSITION_TIME;
            if (alpha < 0) {
                goingToMenu = false;
                alpha = 0;
                myGame.setScreen(menu);
                return;
            }
        }

        if (!shadeFade.isComplete()) {
            shadeFade.act(delta);
        }

        menu.render(delta);

        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f,0.1f,0.1f,0.3f*alpha);
        shapeRenderer.rect(0,0, GAME_WIDTH, GAME_HEIGHT);

        shapeRenderer.setColor(BEIGE_CANVAS.r,BEIGE_CANVAS.g,BEIGE_CANVAS.b,alpha);
        shapeRenderer.rect(SIDE_BORDER, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS  , GAME_WIDTH-2* SIDE_BORDER, MESSAGE_BOX_HEIGHT- 2*ROUNDNESS_RADIUS);
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2+ MESSAGE_BOX_HEIGHT /2 -ROUNDNESS_RADIUS, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,180,90);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT/2+ MESSAGE_BOX_HEIGHT /2 -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,90,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT/2+ MESSAGE_BOX_HEIGHT /2 -ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,0,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT/2- MESSAGE_BOX_HEIGHT /2 +ROUNDNESS_RADIUS,ROUNDNESS_RADIUS,-90,90);

        if (isTimeAutomatic) {
            for (int i = 0; i < 4; i++) {
                topButtons[i].setAlpha(alpha);
                topButtons[i].render(shapeRenderer);
            }
            for (int i = 0; i < 3; i++) {
                botButtons[i].setAlpha(alpha);
                botButtons[i].render(shapeRenderer);
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        batch.begin();

        dayFont.setColor(FONT_COLOR.r, FONT_COLOR.g, FONT_COLOR.b, FONT_COLOR.a*alpha);
        if (isTimeAutomatic) {
            for (int i = 0; i < 4; i++) {
                topButtons[i].render();
                if (i < daysRedeemed) {
                    batch.draw(tick, GAME_WIDTH / 2 - TOP_DRAWING_WIDTH / 2 + i * TOP_DRAWING_WIDTH / 4, GAME_HEIGHT / 2 + 10, BUTTON_WIDTH, BUTTON_WIDTH);
                }
            }
            for (int i = 0; i < 3; i++) {
                botButtons[i].render();
                if (i + 4 < daysRedeemed) {
                    batch.draw(tick, GAME_WIDTH / 2 - BOT_DRAWING_WIDTH / 2 + i * BOT_DRAWING_WIDTH / 3, GAME_HEIGHT / 2 - 60, BUTTON_WIDTH, BUTTON_WIDTH);
                }
            }
        }

        titleFont.setColor(FONT_COLOR.r, FONT_COLOR.g, FONT_COLOR.b, FONT_COLOR.a*alpha);
        backFont.setColor(FONT_COLOR.r, FONT_COLOR.g, FONT_COLOR.b, FONT_COLOR.a*alpha);
        titleFont.draw(batch, "Daily reward", GAME_WIDTH / 2 - 85, GAME_HEIGHT/2+97);
        backButton.render(batch);

        if (!isTimeAutomatic) {
            dayFont.draw(batch, "Set device time to", GAME_WIDTH / 2 - 92, GAME_HEIGHT/2+17);
            dayFont.draw(batch, "automatic to redeem", GAME_WIDTH / 2 - 100, GAME_HEIGHT/2-5);
        }
        batch.end();

        if (isTimeAutomatic) {
            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(shadeColor);
            for (int i = 0; i < 3; i++) {
                topButtons[i].renderShade(shapeRenderer);
                botButtons[i].renderShade(shapeRenderer);
            }
            topButtons[3].renderShade(shapeRenderer);

            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);
        }
    }

    @Override
    public void show() {
        super.show();
        comingFromMenu = true;
        DailyRewardButton.setDailyRewardScreen(this);
        System.out.println("date " + date[0] + "/" + date[1] + "/" + date[2]);
        System.out.println("lastdateredeemed " + lastDateRedeemed[0] + "/" + lastDateRedeemed[1] + "/" + lastDateRedeemed[2]);

        if (Arrays.equals(lastDateRedeemed, date)) {
            for (int i = 0; i < 4; i++) {
                topButtons[i].updateRedeemability(false);
            }

            for (int i = 0; i < 3; i++) {
                botButtons[i].updateRedeemability(false);
            }
        }
    }

    public void reward(int day) {
        daysRedeemed++;
        lastDateRedeemed = date;
        admin.saveData("daysredeemed", day);
        admin.saveData("lastdateredeemed", (date[0]<10?"0":"")+date[0]+(date[1]<10?"0":"")+date[1]+""+date[2]);
        admin.saveData("coins", coins);
        admin.flush();
        menu.claimedDailyReward();
    }

    public void unsuccessfulRedeem() {
        shadeColor = new Color(RED_SHADE);
        shadeFade.restart();
        shadeFade.setDuration(1);
        shadeFade.setColor(shadeColor);
        shadeFade.setEndColor(TRANSPARENT);
    }

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {

    }

    public void uncheckButtons(DailyRewardButton checkedButton) {
        for (int i = 0; i < 3; i++) {
            if (!topButtons[i].equals(checkedButton)) {
                topButtons[i].unCheck();
            }
            if (!botButtons[i].equals(checkedButton)) {
                botButtons[i].unCheck();
            }
        }
        if (!topButtons[3].equals(checkedButton)) {
            topButtons[3].unCheck();
        }
    }

    @Override
    public void touchDown(float x, float y) {
        if (isTimeAutomatic) {
            for (int i = 0; i < 4; i++) {
                if (topButtons[i].checkIfTouched(x, y)) {
                    return;
                }
            }
            for (int i = 0; i < 3; i++) {
                if (botButtons[i].checkIfTouched(x, y)) {
                    return;
                }
            }
        }
        backButton.checkIfTouched(x,y);
    }

    @Override
    public void touchUp(float x, float y) {
        backButton.checkIfReleased(x,y);
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
