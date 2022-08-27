package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.util.UpgradeButton;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class UpgradesScreen extends ScreenInputProcessor {

    private final MenuScreen menuScreen;

    private static final float ROUNDNESS_RADIUS = 13, SIDE_BORDER = 18, TOP_BORDER = GAME_HEIGHT/20;

    // The shop will have several subdivision screens represented by a state.
    // state 0 is the essentials shop
    // state -1 is the advanced shop
    // state 1 is the utility shop

    // In every state, there are 4 upgrade buttons that will be evenly distributed in the rectangle
    //
    private final BitmapFont title, coinsFont, descriptionFont;
    private final float DRAWING_RECT_HEIGHT = 320;

    private final Color BACK_BLUE = new Color(0f,0.27f,0.74f,1), BEIGE_CANVAS = new Color(0.9f,0.9f,0.75f,0.6f),
            GREEN_SHADE = new Color(0.15f,0.84f,0.1f,0.9f), RED_SHADE = new Color(0.85f,0.1f,0.1f,0.9f);
    private float alpha, xDisplacement, xDisplacementAdd, targetXDisplacement, yDisplacement, yChange;
    private boolean transition, isPanning, justFlinged, rewardEnabled;
    private Color shadeColor = GREEN_SHADE;
    private final ColorAction shadeFade = new ColorAction();

    private final Sprite sideArrowRight, sideArrowLeft;
    private float rightArrowAlpha = 1, leftArrowAlpha = 1;
    private final Button backButton;
    private Button adButton;
    private final UpgradeButton[] essentialButtons, advancedButtons, powerupButtons, utilityButtons;
    private int adsShownInDay;

    private final Texture bubble;

    // Powerups --> 1. Bubble rain 2. Lord of the seas (incy wincy crabby) 3. Fish King 4.
    // Utility --> 1. Zen 2. Swimming 3. Golden bubbles 4. Brother fish
    // Advanced --> 1. Holy fish 2.

    UpgradesScreen(MenuScreen menu) {
        System.out.println("We are in UpgradeScreen ");
        menuScreen = menu;

        bubble = admin.getAsset("bubble4score");

        if (!adLoaded) {
            myGame.loadVideoAd();
        }
        adsShownInDay = admin.getData("last date ad number");
        if (!admin.getString("last date ad").isEmpty()) {
            final long lastDateAd = Double.valueOf(admin.getString("last date ad")).longValue();
            if (TimeUtils.timeSinceMillis(lastDateAd) > 24*60*60*1000) {
                admin.saveData("last date ad number", 0);
                adsShownInDay = 0;
                admin.saveData("last date ad", Long.toString(TimeUtils.millis()));
                admin.flush();
                rewardEnabled = true;
            } else if (adsShownInDay < 5) {
                rewardEnabled = true;
            }
        } else {
            rewardEnabled = true;
            admin.saveData("last date ad", Float.toString(TimeUtils.millis()));
            admin.flush();
        }

        title = admin.getAsset("coolville30");
        final BitmapFont backFont = admin.getAsset("coolville30b");
        backFont.setColor(0.1f,0.1f,0.1f,1);
        coinsFont = admin.getAsset("khula10");
        descriptionFont = admin.getAsset("AcariItalic9");
        final BitmapFont nameFont = admin.getAsset("coolville15b");
        final BitmapFont levelFont = admin.getAsset("AcariItalic11");
        final BitmapFont maxLevelFont = admin.getAsset("AcariLight");
        descriptionFont.setColor(0,0,0,1);
        nameFont.setColor(0,0,0,1);
        levelFont.setColor(0,0,0,1);
        maxLevelFont.setColor(0,0,0,1);
        UpgradeButton.descriptionFont = descriptionFont;
        UpgradeButton.nameFont = nameFont;
        UpgradeButton.levelFont = levelFont;
        UpgradeButton.maxLevelFont = maxLevelFont;
        UpgradeButton.soundOff = soundOff;
        UpgradeButton.errorSound = admin.getAsset("error sound");
        UpgradeButton.buySound = admin.getAsset("buy sound");

        shadeFade.setColor(shadeColor);
        shadeFade.setEndColor(shadeColor);

        sideArrowRight = new Sprite((Texture) admin.getAsset("sidearrowup"));
        sideArrowRight.setBounds(GAME_WIDTH - SIDE_BORDER - 20 - 20, GAME_HEIGHT-TOP_BORDER-30-30,20,30);

        sideArrowLeft = new Sprite((Texture) admin.getAsset("sidearrowup"));
        sideArrowLeft.setBounds(SIDE_BORDER + 20, GAME_HEIGHT-TOP_BORDER-30-30,20,30);
        sideArrowLeft.flip(true,false);

        backButton = new Button(GAME_WIDTH/2-42, TOP_BORDER + 33, backFont,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.3f,0.3f,0.3f,1),"-back-") {
            @Override
            public void activate() {
                goToMenu();
            }
        };
        backButton.setBounds(GAME_WIDTH/2-45, TOP_BORDER + 12, 90, 26);

        if (rewardEnabled) {
            adButton = new Button(GAME_WIDTH - 50, GAME_HEIGHT - 50, 42, 42, (Texture) admin.getAsset("adbuttonup"), (Texture) admin.getAsset("adbuttondown")) {
                @Override
                public void activate() {
                    goToAdScreen();
                }
            };
        }
        // ~~~~~~~~~~~  COSTS  ~~~~~~~~~~~~
        final int[] lessCrabsCost = {60,145,325,585,900};                        // 2015
        final int[] moreTubesCost = {75,135,350,715,950};                        // 2225
        final int[] moreBubblesCost = {40,60,105,200,280,650,1000,1330};         // 3665
        final int[] fasterScoreCost = {50,70,115,250,355,560,900,1100};          // 3400
        final int[] holyFishCost = {410,720,1300,1800,2400};                     // 6630
        final int[] helperFishCost = {630};                                      //  630
        final int[] goldifyCost = {120,290,450,800,1100};                        // 2760
        final int[] nightVisionCost = {80,180,275,400,550};                      // 1485
        final int[] strongerTubesCost = {80,120,375,630,900};                    // 2105
        final int[] lazyPurpleCost = {70,115,140,170,285,380,490,650,800,900};   // 4000
        final int[] betterSwimmerCost = {70,90,115,200,260,305,500,600,700,800}; // 3640
        final int[] hardcoreModeCost = {2000};                                   // 2000
        final int[] pwuBubbleCost = {200,475,550,625,800};                       // 2650
        final int[] pwuShieldCost = {350,500,600,800,1200};                      // 3450
        final int[] pwuPoseidonCost = {125,370,550,700,900};                     // 2645
        final int[] pwuMissilesCost = {105,250,450,500,800};                     // 2105
                                                                                 // = 49650

        UpgradeButton.setUp(admin, this);
        essentialButtons = new UpgradeButton[4];
        for (int i = 0; i<4; i++) {
            essentialButtons[i] = new me.jorge.myfirstgame.util.UpgradeButton(SIDE_BORDER + 20, GAME_HEIGHT - TOP_BORDER - 135 - i*DRAWING_RECT_HEIGHT/4,
                    50);
        }
        essentialButtons[0].set("Less crabs", "Reduces the crab spawn probability",
                (Texture) admin.getAsset("upgradecrabs"), new Color(0f,0,0,1),5, lessCrabsCost);
        essentialButtons[1].set("More tubes", "Increases the tube spawn probability",
                (Texture) admin.getAsset("upgradetubes"), new Color(0f,0,0,1), 5, moreTubesCost);
        essentialButtons[2].set("More bubbles", "$$$",
                (Texture) admin.getAsset("upgradebubbles"),new Color(0f,0,0,1), 8,moreBubblesCost);
        essentialButtons[3].set("Faster score", "Score metre goes up quicker",
                (Texture) admin.getAsset("upgradescore"), new Color(0f,0,0,1), 8, fasterScoreCost);

        advancedButtons = new me.jorge.myfirstgame.util.UpgradeButton[4];
        for (int i = 0; i<3; i++) {
            advancedButtons[i] = new me.jorge.myfirstgame.util.UpgradeButton(SIDE_BORDER + 20 - 2*GAME_WIDTH, GAME_HEIGHT - TOP_BORDER - 135 - i*DRAWING_RECT_HEIGHT/4,
                    50);
        }
        advancedButtons[3] = new me.jorge.myfirstgame.util.UpgradeButton(SIDE_BORDER + 20 - 2*GAME_WIDTH, GAME_HEIGHT - TOP_BORDER - 135 - 3*DRAWING_RECT_HEIGHT/4,
                50) {
            @Override
            public void activate() {
                unlockHardmode();
            }
        };


        advancedButtons[0].set("Stronger tubes", "These bad boys can take more hits",
                (Texture) admin.getAsset("upgradestrongertubes"), new Color(0f,0,0,1), 5,strongerTubesCost);
        advancedButtons[1].set("Lazy purple", "Purple crabs find less joy in \n slashing",
                (Texture) admin.getAsset("upgradeslash"), new Color(0f,0,0,1), 10, lazyPurpleCost);
        advancedButtons[2].set("Holy fish", "Death is for the weak",
                (Texture) admin.getAsset("upgradeholy"), new Color(0f,0,0,1), 5,holyFishCost);
        advancedButtons[3].set("Hardcore mode", "Showcase your skill in this mode\n without upgrades. Will you make\n it to the top-10?",
                (Texture) admin.getAsset("upgradehardcore"), new Color(0f,0,0,1),1, hardcoreModeCost);

        utilityButtons = new me.jorge.myfirstgame.util.UpgradeButton[4];
        for (int i = 0; i<4; i++) {
            utilityButtons[i] = new me.jorge.myfirstgame.util.UpgradeButton(SIDE_BORDER + 20 - GAME_WIDTH, GAME_HEIGHT - TOP_BORDER - 135 - i*DRAWING_RECT_HEIGHT/4,
                    50);
        }
        utilityButtons[0].set("Night vision", "See through the night's fog",
                (Texture) admin.getAsset("upgradenightvision"), new Color(0f,0,0,1), 5,nightVisionCost);
        utilityButtons[1].set("Better swimmer", "Become fishier and swim faster",
                (Texture) admin.getAsset("upgradeswimmer"), new Color(0f,0,0,1), 10, betterSwimmerCost);
        utilityButtons[2].set("Goldify", "Chance of spawning expensive bubbles",
                (Texture) admin.getAsset("upgradegoldify"), new Color(0f,0,0,1), 5, goldifyCost);
        utilityButtons[3].set("Family business", "Bring your younger brother along\n" + "to pick up bubbles",
                (Texture) admin.getAsset("upgradehelper"), new Color(0f,0,0,1),1, helperFishCost);

        powerupButtons = new me.jorge.myfirstgame.util.UpgradeButton[4];
        for (int i = 0; i<4; i++) {
            powerupButtons[i] = new me.jorge.myfirstgame.util.UpgradeButton(SIDE_BORDER + 20 + GAME_WIDTH, GAME_HEIGHT - TOP_BORDER - 135 - i*DRAWING_RECT_HEIGHT/4,
                    50);
        }
        powerupButtons[0].set("Bubble shield", "Contrary to popular belief, bubbles\n are crab-proof",
                (Texture) admin.getAsset("upgradeshieldpwu"), new Color(0.898f,0.439f,0.451f,1), 5,pwuShieldCost);
        powerupButtons[1].set("Bubble rain", "Rich the fish",
                (Texture) admin.getAsset("upgradebubblepwu"), new Color(0.929f,0.878f,0.373f,1), 5, pwuBubbleCost);
        powerupButtons[2].set("Poseidon", "Incy Wincy crab ...",
                (Texture) admin.getAsset("upgradeposeidonpwu"), new Color(0.427f,0.953f,0.447f,1), 5, pwuPoseidonCost);
        powerupButtons[3].set("Fish lord", "Command a kamikaze fish troop.\n Long press to shoot",
                (Texture) admin.getAsset("upgrademissilespwu"), new Color(0.502f,0.537f,0.922f,1),5, pwuMissilesCost);
    }

    void goToAdScreen() {
        myGame.setScreen(new RewardedVideoScreen(this, "   Play an ad to\n   get 100 coins!", 100));
    }

    @Override
    public void show() {
        transition = false;
        //alpha = 0;
    }

    @Override
    public void render(float delta) {

        if (!isPanning && xDisplacement != targetXDisplacement) {
            xDisplacementAdd = 5*delta*(targetXDisplacement-xDisplacement);
            for (int i = 0; i<4; i++) {
                essentialButtons[i].addToXDisplacement(xDisplacementAdd);
                advancedButtons[i].addToXDisplacement(xDisplacementAdd);
                utilityButtons[i].addToXDisplacement(xDisplacementAdd);
                powerupButtons[i].addToXDisplacement(xDisplacementAdd);
            }
            xDisplacement += xDisplacementAdd;
        }
        if (Math.abs(xDisplacement - targetXDisplacement) < 20) {
            if (leftArrowAlpha != 0) leftArrowAlpha = 1;
            if (rightArrowAlpha != 0) rightArrowAlpha = 1;
        } else {
            rightArrowAlpha = 0.5f;
            leftArrowAlpha = 0.5f;
            if (targetXDisplacement == -GAME_WIDTH) {
                rightArrowAlpha = 0;
            } else if (targetXDisplacement == 2*GAME_WIDTH) {
                leftArrowAlpha = 0;
            }
        }

        if (!shadeFade.isComplete()) {
            shadeFade.act(delta);
        }

        if (alpha < 1f) {
            alpha += 2*delta;
        }

        //Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (!transition) {
            shapeRenderer.setColor(BACK_BLUE);
            shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }

        shapeRenderer.setColor(BEIGE_CANVAS.r,BEIGE_CANVAS.g,BEIGE_CANVAS.b,alpha);
        shapeRenderer.rect(SIDE_BORDER, TOP_BORDER +ROUNDNESS_RADIUS + yDisplacement, GAME_WIDTH-2* SIDE_BORDER, GAME_HEIGHT-2*(TOP_BORDER +ROUNDNESS_RADIUS));
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER + yDisplacement, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS + yDisplacement, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,180,90);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,90,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,0,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,-90,90);

        for (int i = 0; i < 4; i++) {
            essentialButtons[i].render(shapeRenderer);
            advancedButtons[i].render(shapeRenderer);
            utilityButtons[i].render(shapeRenderer);
            powerupButtons[i].render(shapeRenderer);
        }

        shapeRenderer.end();


        batch.enableBlending();
        batch.begin();

        for (int i = 0; i < 4; i++) {
            essentialButtons[i].render(batch);
            advancedButtons[i].render(batch);
            utilityButtons[i].render(batch);
            powerupButtons[i].render(batch);
        }

        title.setColor(0.1f,0.1f,0.1f,alpha);
        title.draw(batch, "Essentials",69 + xDisplacement, GAME_HEIGHT-35- TOP_BORDER + yDisplacement);
        title.draw(batch, "Advanced",78 + xDisplacement - 2*GAME_WIDTH, GAME_HEIGHT-35- TOP_BORDER + yDisplacement);
        title.draw(batch, "Utility",95 + xDisplacement - GAME_WIDTH, GAME_HEIGHT-35- TOP_BORDER + yDisplacement);
        title.draw(batch, "Abilities",84 + xDisplacement + GAME_WIDTH, GAME_HEIGHT-35- TOP_BORDER + yDisplacement);

        sideArrowRight.draw(batch,rightArrowAlpha);
        sideArrowLeft.draw(batch,leftArrowAlpha);

        backButton.render(batch);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(GAME_WIDTH / 2 - 20, GAME_HEIGHT - TOP_BORDER - 30 - 30 + yDisplacement, 3);
        shapeRenderer.circle(GAME_WIDTH / 2 - 10, GAME_HEIGHT - TOP_BORDER - 30 - 30 + yDisplacement, 3);
        shapeRenderer.circle(GAME_WIDTH / 2, GAME_HEIGHT - TOP_BORDER - 30 - 30 + yDisplacement, 3);
        shapeRenderer.circle(GAME_WIDTH / 2 + 10, GAME_HEIGHT - TOP_BORDER - 30 - 30 + yDisplacement, 3);
        shapeRenderer.end();


        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1.4f*Math.max(rightArrowAlpha,leftArrowAlpha));
        shapeRenderer.circle(GAME_WIDTH / 2 - 10*xDisplacement/ GAME_WIDTH, GAME_HEIGHT - TOP_BORDER - 30 - 30 + yDisplacement, 3);

        shapeRenderer.setColor(shadeColor);
        for (int i = 0; i < 4; i++) {
            essentialButtons[i].renderShade(shapeRenderer);
            advancedButtons[i].renderShade(shapeRenderer);
            utilityButtons[i].renderShade(shapeRenderer);
            powerupButtons[i].renderShade(shapeRenderer);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        fontBatch.begin();
        for (int i = 0; i < 4; i++) {
            essentialButtons[i].renderText(fontBatch);
            advancedButtons[i].renderText(fontBatch);
            utilityButtons[i].renderText(fontBatch);
            powerupButtons[i].renderText(fontBatch);
        }
        fontBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (!transition) {
            shapeRenderer.setColor(BACK_BLUE);
            shapeRenderer.rect(0, 0, SIDE_BORDER, GAME_HEIGHT);
            shapeRenderer.rect(GAME_WIDTH-SIDE_BORDER, 0, SIDE_BORDER, GAME_HEIGHT);
        }

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
        shapeRenderer.end();

        batch.begin();
        batch.draw(bubble,5, GAME_HEIGHT-34,13,13);
        coinsFont.draw(batch, ""+coins,23, GAME_HEIGHT-23);
        if (!transition && rewardEnabled && adLoaded) {
            adButton.render(batch);
        }
        batch.end();

        if (!transition && rewardEnabled && adLoaded) {
            fontBatch.begin();
            descriptionFont.draw(fontBatch, adsShownInDay + "/5", SCALE_X*(GAME_WIDTH - 50), SCALE_Y*(GAME_HEIGHT - 52), 42*SCALE_X, Align.center, true);
            fontBatch.end();
        }

        //exitButton.renderDebug(shapeRenderer);
    }

    public void uncheckButtons(UpgradeButton checkedButton) {
        for (int i = 0; i < 4; i++) {
            if (!essentialButtons[i].equals(checkedButton)) {
                essentialButtons[i].unCheck();
            }
            if (!advancedButtons[i].equals(checkedButton)) {
                advancedButtons[i].unCheck();
            }
            if (!utilityButtons[i].equals(checkedButton)) {
                utilityButtons[i].unCheck();
            }
            if (!powerupButtons[i].equals(checkedButton)) {
                powerupButtons[i].unCheck();
            }
        }
    }

    public void unsuccessfulBuy() {
        shadeColor = new Color(RED_SHADE);
        shadeFade.restart();
        shadeFade.setColor(shadeColor);
        shadeFade.setDuration(1);
        shadeFade.setEndColor(GREEN_SHADE);
    }

    void setYDisplacement(float add) {
        yChange = yDisplacement - add + GAME_HEIGHT;
        yDisplacement = add - GAME_HEIGHT;
        for (int i = 0; i < 4; i++) {
            essentialButtons[i].setYDisplacement(add - GAME_HEIGHT);
            advancedButtons[i].setYDisplacement(add - GAME_HEIGHT);
            utilityButtons[i].setYDisplacement(add - GAME_HEIGHT);
            powerupButtons[i].setYDisplacement(add - GAME_HEIGHT);
        }
        backButton.setYDisplacement(add - GAME_HEIGHT);
        sideArrowLeft.translateY(-yChange);
        sideArrowRight.translateY(-yChange);
    }

    void transition() {
        transition = true;
    }

    private void goToMenu() {
        myGame.setScreen(menuScreen);
        menuScreen.comeFromShop();
    }

    private void unlockHardmode() {
        menuScreen.unlockHard();
    }

    public void setTutorialOn() {
        yDisplacement = 0;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void changeSoundOff() {
        me.jorge.myfirstgame.util.UpgradeButton.soundOff = soundOff;
        Button.soundOff = soundOff;
    }

    @Override
    public void onRewarded() {
        rewardEnabled = false;
        adsShownInDay++;
        admin.saveData("last date ad number", adsShownInDay);
        admin.saveData("coins",coins);
        admin.flush();
    }

    @Override
    public void touchDown(float x, float y) {
        if (transition) {
            return;
        }
        if (rewardEnabled && adLoaded) {
            adButton.checkIfTouched(x,y);
        }
        backButton.checkIfTouched(x,y);
        for (int i = 0; i < 4; i++) {
            if (essentialButtons[i].checkIfTouched(x,y)) return;
            if (advancedButtons[i].checkIfTouched(x,y)) return;
            if (utilityButtons[i].checkIfTouched(x,y)) return;
            if (powerupButtons[i].checkIfTouched(x,y)) return;
        }
        uncheckButtons(null);
    }

    @Override
    public void touchUp(float x, float y) {
        if (transition) {
            return;
        }
        if (rewardEnabled && adLoaded) {
            adButton.checkIfReleased(x,y);
        }
        backButton.checkIfReleased(x,y);
        for (int i = 0; i < 4; i++) {
            essentialButtons[i].checkIfReleased(x,y);
            advancedButtons[i].checkIfReleased(x,y);
            utilityButtons[i].checkIfReleased(x,y);
            powerupButtons[i].checkIfReleased(x,y);
        }
    }

    @Override
    public void tap(float x, float y) {
        if (transition) {
            return;
        }
        if (leftArrowAlpha == 1 && sideArrowLeft.getBoundingRectangle().contains(x,y)) {
            flingRight(x,y);
        } else if (rightArrowAlpha == 1 && sideArrowRight.getBoundingRectangle().contains(x,y)) {
            flingLeft(x,y);
        }
    }

    @Override
    public void flingUp(float startX, float startY) {

    }

    @Override
    public void flingDown(float startX, float startY) {
        if (Math.abs(targetXDisplacement - xDisplacement) < 10) {
            goToMenu();
        }
    }

    @Override
    public void flingRight(float startX, float startY) {
        if (transition) {
            return;
        }
        if (targetXDisplacement < 2*GAME_WIDTH) {
            targetXDisplacement += GAME_WIDTH;
        }
        justFlinged = true;
    }

    @Override
    public void flingLeft(float startX, float startY) {
        if (transition) {
            return;
        }
        if (targetXDisplacement > -GAME_WIDTH) {
            targetXDisplacement -= GAME_WIDTH;
        }
        justFlinged = true;
    }

    @Override
    public void pan(float deltaX) {
        if (transition) {
            return;
        }
        isPanning = true;
        for (int i = 0; i < 4; i++) {
            essentialButtons[i].addToXDisplacement(1.5f*deltaX);
            advancedButtons[i].addToXDisplacement(1.5f*deltaX);
            utilityButtons[i].addToXDisplacement(1.5f*deltaX);
            powerupButtons[i].addToXDisplacement(1.5f*deltaX);
        }
        xDisplacement += 1.5f*deltaX;
    }

    @Override
    public void panStop(float deltaX) {
        if (transition) {
            return;
        }
        isPanning = false;
        if (!justFlinged) {
            if (-GAME_WIDTH / 2 < xDisplacement + deltaX * 20 && xDisplacement + deltaX * 20 < GAME_WIDTH / 2) {
                targetXDisplacement = 0;
            } else if (xDisplacement + deltaX * 20 < -GAME_WIDTH / 2) {
                targetXDisplacement = -GAME_WIDTH;
            } else if (xDisplacement + deltaX * 20 > 3*GAME_WIDTH / 2) {
                targetXDisplacement = 2 * GAME_WIDTH;
            } else {
                targetXDisplacement = GAME_WIDTH;
            }
        }
        justFlinged = false;
    }
}
