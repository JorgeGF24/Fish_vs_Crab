package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;

import java.util.concurrent.TimeUnit;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.sprites.Player;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.sprites.Background;
import me.jorge.myfirstgame.sprites.Sea;
import me.jorge.myfirstgame.sprites.Wall;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class MenuScreen extends ScreenInputProcessor {

    private final GameScreen gameScreen;
    private UpgradesScreen shop;
    private RankingScreen rankingScreen;
    private final DailyRewardScreen dailyRewardScreen;

    private final float fadeInTime;
    private float transitionTimer;
    private boolean fadingIn, fontFading, highscoreHidden;
    public static boolean gamemodeHard, hardUnlocked;
    private final Color fadingColor, fontColor = new Color(0.1f,0.1f,0.1f,1);
    private final ColorAction fade = new ColorAction(), fontFade = new ColorAction();

    private final Texture background, cloud, cloud2, bubble, wall, hardWall;
    private Texture[] playerTextures, purpulplayerTextures;
    private final Sprite arrow;
    private final Button soundButton, rankingButton, dailyRewardButton;
    private Button gamemodeButton;

    private final float ARRO_CAP_SPEED = 40;
    private final float ARROW_Y_CAP = 5;
    private float arroY = 10, arroSpeed;
    private boolean arrowGoingUp;

    private final Player player;
    private final Sea sea;
    private final Wall leftWall, rightWall;

    private float flashAlpha, flashTimer;
    private int flashStage; // 1 is alha up torwards first  flash. 2 is first flash done, going for 2nd. 3 is gradual deflashing

    private final BitmapFont font, fontS, coinsFont;

    //backgroundY - BACKGROUND_SPEED*t = 20
    // 150 - INIT*t = 0
    //INIT = seaY/t = BACKGROUND_SPEED*seaY/(backgroundY -20)
    //backgroundY = 20 + BACKGROUND_SPEED*t = 20 + BACKGROUND_SPEED * (150/INITIAL_CLIMB_SPEED)


    private final static float BACKGROUND_SPEED = 30;
    private final float CLIMB_CONSTANT =10;
    private float climbSpeed;
    private float scoreXDisplacement;
    private final float INITIAL_CLIMB_SPEED = 235;
    private final float CLOUD_HEIGHT = Background.CLOUD_HEIGHT, CLOUD_WIDTH = Background.CLOUD_WIDTH,
            SUN_RADIUS = Background.SUN_RADIUS, SEA_HEIGHT = Sea.HEIGHT;
    private final float DEFAULT_BACKGROUND_Y = 20 + BACKGROUND_SPEED * (150/INITIAL_CLIMB_SPEED);
    private float backgroundY = DEFAULT_BACKGROUND_Y, seaY = GAME_HEIGHT/2 - SEA_HEIGHT - 15;

    private int highscore, highscore2, highscore3;

    private int state;
    //private boolean isStarting  1, goingToShop  2, comingFromShop  -2, goingToRanking  3, comingFromRanking  -3,
    private boolean tutotialOn;

    //private Button shopButton;

    // hearts
    private float timerDisplacement, testFloat = 0;
    private final Texture[] heartTextures;
    private final BitmapFont timerFont;
    private long heartTimer, lastHeartTime;
    private final long HEART_TIMER_MIN = 30, HEART_TIMER = HEART_TIMER_MIN * 60 * 1000;
    private String timerToPrint;
    private int minutesToPrint, secondsToPrint;
    private final SpriteBatch fontBatch = MyGame.getFontBatch();

    MenuScreen(boolean fromLoading)  {
        System.out.println("We are in menuScreen "+ GAME_HEIGHT/2 + " seaY is " + seaY);

        gameScreen = new GameScreen();
        player = gameScreen.getPlayer();
        player.setSeaLevel(seaY + SEA_HEIGHT);
        leftWall = gameScreen.getLeftWall();
        rightWall = gameScreen.getRightWall();
        wall = admin.getAsset("rock");
        hardWall = admin.getAsset("hardrock");

        highscore = admin.getData("highscore");
        highscore2 = admin.getData("highscore2");
        highscore3 = admin.getData("highscore3");
        System.out.println("Highscore is " + highscore);
        hearts = admin.getData("hearts");
        lastHeartTime = admin.getLong("lasthearttime");
        if (lastHeartTime == 0) {
            lastHeartTime = System.currentTimeMillis();
            admin.saveData("lasthearttime", lastHeartTime);
            admin.flush();
        }

        fadeInTime = fromLoading?1.5f:0.3f;
        final int i = fromLoading?1:0;
        fadingColor = new Color(i,i,i,1);

        background = admin.getAsset("background");
        cloud = admin.getAsset("cloud");
        cloud2 = admin.getAsset("cloud2");
        arrow =  new Sprite((Texture) admin.getAsset("arrow"));
        final float ARRO_HEIGHT = 25, ARRO_WIDTH = 30, ARRO_X = GAME_WIDTH / 2 - ARRO_WIDTH / 2;
        arrow.setBounds(ARRO_X,0, ARRO_WIDTH, ARRO_HEIGHT);

        font = admin.getAsset("coolville30");
        font.setColor(fontColor);
        fontS = admin.getAsset("coolville15");
        fontS.setColor(fontColor);
        fontFade.setColor(fontColor);
        coinsFont = admin.getAsset("khula10");
        coinsFont.setColor(0,0,0,1);

        final Texture staringPlayer = admin.getAsset("staring");
        bubble = admin.getAsset("bubble4score");
        player.setTexture(staringPlayer);
        heartTextures = new Texture[3];
        heartTextures[0] = admin.getAsset("emptyheart");
        heartTextures[1] = admin.getAsset("halfheart");
        heartTextures[2] = admin.getAsset("fullheart");
        timerFont = admin.getAsset("AcariItalic9");
        timerFont.setColor(0,0,0,1);

        timerDisplacement = Math.max((Math.round(hearts/2.1f))*20, 0);

        soundButton = new Button(GAME_WIDTH - 54, GAME_HEIGHT - 49, 33, (Texture) admin.getAsset("volumeOn"), (Texture) admin.getAsset("volumeOff"));
        soundButton.setIsChecked(soundOff);

        rankingButton = new Button(GAME_WIDTH - 57,GAME_HEIGHT - 90, 36,36*13/17f, (Texture) admin.getAsset("podiumbuttonup"),(Texture) admin.getAsset("podiumbuttondown")) {
            @Override
            public void activate() {
                goToRanking();
            }
        };

        dailyRewardButton = new Button(GAME_WIDTH - 57,GAME_HEIGHT - 136, 33, (Texture) admin.getAsset("closedchest"),(Texture) admin.getAsset("openedchest")) {
            @Override
            public void activate() {
                myGame.setScreen(dailyRewardScreen);
            }

            @Override
            public void checkIfReleased(float x, float y) {
                if (isDown) {
                    isDown = false;
                    if (bounds.contains(x,y)) {
                        activate();
                        if (!soundOff) {
                            clickSound.play();
                        }
                    }
                }
            }
        };

        dailyRewardScreen = new DailyRewardScreen(this);

        if (hardUnlocked) {
            gamemodeButton = new me.jorge.myfirstgame.util.Button(GAME_WIDTH - 60, GAME_HEIGHT - 174, 40, 27, (Texture) admin.getAsset("fishpurpul"), (Texture) admin.getAsset("fish")) {
                @Override
                public void activate() {
                    changeGamemode();
                }
            };
            gamemodeButton.setIsChecked(gamemodeHard);

            playerTextures = new Texture[8];
            playerTextures[0] = admin.getAsset("fish");
            playerTextures[1] = admin.getAsset("happyfish");
            playerTextures[2] = admin.getAsset("fishflap");
            playerTextures[3] = admin.getAsset("happyfishflap");
            playerTextures[4] = admin.getAsset("deadfish");
            playerTextures[5] = admin.getAsset("!");
            playerTextures[6] = admin.getAsset("holybeam");
            playerTextures[7] = admin.getAsset("poseidonfish");

            purpulplayerTextures = new Texture[8];
            purpulplayerTextures[0] = admin.getAsset("fishpurpul");
            purpulplayerTextures[1] = admin.getAsset("happyfishpurpul");
            purpulplayerTextures[2] = admin.getAsset("fishflappurpul");
            purpulplayerTextures[3] = admin.getAsset("happyfishflappurpul");
            purpulplayerTextures[4] = admin.getAsset("deadfishpurpul");
            purpulplayerTextures[5] = admin.getAsset("!");
            purpulplayerTextures[6] = admin.getAsset("holybeam");
            purpulplayerTextures[7] = admin.getAsset("fishflappurpul");

            if (gamemodeHard) {
                player.setTexturePack(purpulplayerTextures);
            }
        }

        /*shopButton = new Button(GAME_WIDTH/2-73, 50, font2,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.5f,0.4f,0.3f,1),"-upgrades-") {
            @Override
            public void activate() {
                ((MenuScreen) parent).goToShop();
            }

        };
        shopButton.setParent(this);
        shopButton.setBounds(0, 0, GAME_WIDTH, 46);*/

        sea = gameScreen.getSea();
        Wall.speed = 0;

        sea.setY(seaY);
        sea.changeFrontAlpha(0.8f);
    }

    @Override
    public void show() {
        highscore = admin.getData("highscore");
        highscore2 = admin.getData("highscore2");
        highscore3 = admin.getData("highscore3");
        fadingIn = true;
        highscoreHidden = false;
        fontS.setColor(fontColor);
        font.setColor(fontColor);
        //This next line checks if the fading color is already transparent, i.e. this is not the first time the menu appears so we are coming from the shop
        if (fadingColor.a > 0.1f) {
            fade.setColor(fadingColor);
            fade.setEndColor(new Color(fadingColor.r,fadingColor.g,fadingColor.b,0));
            fade.setDuration(fadeInTime);
        }
        if (admin.getData("Holy fish") > 0) {
            player.lifeUpgrade(admin.getData("Holy fish"));
        }
        if (gamemodeHard) {
            leftWall.setTexture(hardWall);
            rightWall.setTexture(hardWall);
            System.out.println("Rock set to hardcore");
            player.lifeUpgrade(0);
            highscore = admin.getData("hardcorehighscore");
            highscore2 = admin.getData("hardcorehighscore2");
            highscore3 = admin.getData("hardcorehighscore3");
        }
        timerDisplacement = Math.max((Math.round(hearts/2.1f))*20, 0);
        System.out.println("displacement " + timerDisplacement);

        if (MyGame.isTimeAutomatic()) {
            heartTimer=System.currentTimeMillis()-lastHeartTime;
            final boolean SAVE = hearts < 10 && heartTimer > HEART_TIMER;
            while (hearts < 10 && heartTimer > HEART_TIMER) {
                lastHeartTime += HEART_TIMER;
                hearts++;
                timerDisplacement = Math.max((Math.round(hearts/2.1f))*20, 0);
            }
            if (SAVE) {
                admin.saveData("lasthearttime", lastHeartTime);
                admin.saveData("hearts", hearts);
                admin.flush();
            }
        }
    }

    void comeFromShop() {
        state = -2;
        shop.transition();
        sea.changeFrontAlpha(0.8f);
        leftWall.setY(0);
        rightWall.setY(0);

        fontFade.restart();
        fontColor.a = -1.3f;
        fontFade.setDuration(1);
        fontFade.setEndColor(new Color(fontColor.r,fontColor.g,fontColor.b,1));
        fontFading = true;
    }

    void comeFromRanking() {
        state = -3;
        rankingScreen.transition();

        fontFade.restart();
        fontColor.a = -1.3f;
        fontFade.setDuration(1);
        fontFade.setEndColor(new Color(fontColor.r,fontColor.g,fontColor.b,1));
        fontFading = true;
    }

    @Override
    public void render(float delta) {
        if (delta > 0.02f) delta = 0.02f;
        switch(state) {
            case -2:  // coming from shop
                transitionTimer += delta;
                climbSpeed = CLIMB_CONSTANT*(seaY-GAME_HEIGHT/2 + SEA_HEIGHT + 15)*transitionTimer/2;
                seaY -= delta*climbSpeed;
                sea.setY(seaY);
                player.addToY(-delta*climbSpeed);
                player.setSeaLevel(seaY + SEA_HEIGHT);

                Wall.speed = -climbSpeed;

                shop.setYDisplacement(seaY - GAME_HEIGHT/2 + SEA_HEIGHT + 15);

                leftWall.update(delta);
                rightWall.update(delta);

                if (transitionTimer > 2) {
                    state = 0;
                    transitionTimer = 0;
                }
                break;

            case 2:  // Going to shop
                transitionTimer += delta;
                climbSpeed = CLIMB_CONSTANT*(3*GAME_HEIGHT/2 - SEA_HEIGHT - 15 -seaY)*transitionTimer/2;
                seaY += delta*climbSpeed;
                sea.setY(seaY);
                player.addToY(delta*climbSpeed);
                player.setSeaLevel(seaY + SEA_HEIGHT);

                Wall.speed = climbSpeed;

                shop.setYDisplacement(seaY - GAME_HEIGHT/2 + SEA_HEIGHT + 15);

                leftWall.update(delta);
                rightWall.update(delta);

                if (transitionTimer > 2) {
                    goToShop();
                    if (state != 2) {
                        return;
                    }
                }
                break;
            case -3:   // Coming from ranking
                transitionTimer += delta;
                climbSpeed = CLIMB_CONSTANT*(GAME_HEIGHT/2 - SEA_HEIGHT - 15 - seaY)*transitionTimer/2;
                seaY += climbSpeed *delta;
                sea.setY(seaY);
                player.addToY(climbSpeed*delta);
                player.setSeaLevel(seaY + SEA_HEIGHT);

                if (seaY > -SEA_HEIGHT) {
                    backgroundY += 0.5f* CLIMB_CONSTANT * delta * (DEFAULT_BACKGROUND_Y - backgroundY + 20) * transitionTimer;
                }

                Wall.speed = climbSpeed;
                scoreXDisplacement += 4*delta*(0 - scoreXDisplacement)*transitionTimer;

                rankingScreen.setYDisplacement(seaY - GAME_HEIGHT/2 + SEA_HEIGHT + 15);

                leftWall.update(delta);
                rightWall.update(delta);

                if (transitionTimer > 2) {
                    state = 0;
                    leftWall.setY(leftWall.getY() - GAME_HEIGHT);
                    rightWall.setY(rightWall.getY() - GAME_HEIGHT);
                    transitionTimer = 0;
                }
                break;
            case 3:   // Going to ranking
                transitionTimer += delta;
                climbSpeed = CLIMB_CONSTANT*(-SEA_HEIGHT - 15 - seaY - GAME_HEIGHT/2)*transitionTimer/2;
                seaY += delta*climbSpeed;
                sea.setY(seaY);
                player.addToY(delta*climbSpeed);
                player.setSeaLevel(seaY + SEA_HEIGHT);

                Wall.speed = climbSpeed;

                rankingScreen.setYDisplacement(seaY - GAME_HEIGHT/2 + SEA_HEIGHT + 15);

                backgroundY -= 3* CLIMB_CONSTANT *delta*(backgroundY+30)*transitionTimer;

                scoreXDisplacement += 3*delta*(-75 - scoreXDisplacement)*transitionTimer;

                leftWall.update(delta);
                rightWall.update(delta);

                if (transitionTimer > 2) {
                    goToRanking();
                    if (state != 3) {
                        return;
                    }
                }
                break;
        }
        if (state != 1) {
            switch (flashStage) {
                case 1:
                    if (flashAlpha > 0.8f) {
                        flashStage = 2;
                        flashAlpha = 0.3f;
                    }
                    flashAlpha += delta*4;
                    break;
                case 2:
                    if (flashAlpha > 0.8f) {
                        flashStage = 3;
                        flashAlpha = 0.8f;
                        changeGamemode();
                    }
                    flashAlpha += delta*4;
                    break;
                case 3:
                    flashAlpha -= 1.4f*delta;
                    if (flashAlpha < 0) {
                        flashAlpha = 0;
                        flashStage = 0;
                    }
                    break;
                default:
            }
            player.updateInMenu(delta);
            if (arrowGoingUp) {
                if (arroSpeed < ARRO_CAP_SPEED) {
                    arroSpeed += ARRO_CAP_SPEED*8*delta;
                }
                if (arroY > ARROW_Y_CAP) {
                    arrowGoingUp = false;
                }
            } else {
                if (arroSpeed > -ARRO_CAP_SPEED) {
                    arroSpeed -= ARRO_CAP_SPEED*8*delta;
                }
                if (arroY < 0) {
                    arrowGoingUp = true;
                }
            }
            arroY += arroSpeed*delta;
            arrow.setY(arroY);
        } else {
            transitionTimer += delta;
            /*if (climbSpeed < INITIAL_CLIMB_SPEED) {
                climbSpeed += 1600*delta;
                System.out.println(seaY - climbSpeed * delta);
            } else {
                climbSpeed = INITIAL_CLIMB_SPEED;
            }*/
            seaY -= CLIMB_CONSTANT *delta*(seaY-30)*transitionTimer;
            sea.setY(seaY);

            Wall.speed = -CLIMB_CONSTANT * (seaY-30) * transitionTimer;

            leftWall.update(delta);
            rightWall.update(delta);

            backgroundY -= CLIMB_CONSTANT *delta*(backgroundY-20)*transitionTimer;
            if (transitionTimer > 1) {
                if (gamemodeHard) {
                    gameScreen.setToHardcore();
                }
                if (tutotialOn) {
                    gameScreen.setTutorialOn(true);
                    myGame.setScreen(new TutorialScreen(gameScreen));
                } else {
                    myGame.setScreen(gameScreen);
                    hearts--;
                    if (hearts == 9) {
                        lastHeartTime = System.currentTimeMillis();
                        admin.saveData("lasthearttime", lastHeartTime);
                    }
                    admin.saveData("hearts", hearts);
                    admin.flush();
                }
            }

            player.update(delta);
        }


        /*if (Gdx.input.isTouched() && !isStarting) {
            isStarting = true;
            player.startJump();
            sea.changeFrontAlpha(1);
        }*/

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f,0.65f,0.9f,1);
        shapeRenderer.rect(0, 0,GAME_WIDTH,GAME_HEIGHT);

        shapeRenderer.setColor(0.929f,0.827f,0.087f,1);
        shapeRenderer.circle(GAME_WIDTH/2, GAME_HEIGHT/2 + 30,67);
        shapeRenderer.end();


        batch.enableBlending();
        batch.begin();

        batch.draw(background,0,backgroundY + 30, GAME_WIDTH, 100);
        batch.draw(cloud, (GAME_WIDTH-CLOUD_WIDTH)/2, GAME_HEIGHT/2 -SUN_RADIUS + 30, CLOUD_WIDTH, CLOUD_HEIGHT);
        batch.draw(cloud2, (GAME_WIDTH)/2 - SUN_RADIUS/0.8f, GAME_HEIGHT/2 - SUN_RADIUS/1.5f + 30, CLOUD_WIDTH/3, CLOUD_HEIGHT/2);


        sea.renderBack(batch,delta);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.13f, 0.49f, 0.757f, 1);
        shapeRenderer.rect(0,0, GAME_WIDTH,seaY);
        shapeRenderer.end();

        batch.begin();
        if (state == 2 || state == -3) {
            leftWall.drawUp(batch);
            rightWall.drawUp(batch);
        } else {
            leftWall.draw(batch);
            rightWall.draw(batch);
        }


        player.draw(batch);

        sea.renderFront(batch, delta);

        batch.end();

        if (state*state == 9) { // any of the ranking states
            rankingScreen.render(delta);
        }


        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (!(state == 1 || state*state == 4)) {
            shapeRenderer.setColor(0, 0.27f, 0.74f, 0.8f);
        } else {
            shapeRenderer.setColor(0, 0.27f, 0.74f, 0.8f+0.2f*(1-fontColor.a));
        }

        shapeRenderer.rect(0,0, GAME_WIDTH,seaY);

        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(57 + scoreXDisplacement,GAME_HEIGHT - 33, 4);
        shapeRenderer.circle(57 + scoreXDisplacement,GAME_HEIGHT - 22, 4);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.circle(57 + scoreXDisplacement,GAME_HEIGHT - 33, 3);
        shapeRenderer.circle(57 + scoreXDisplacement,GAME_HEIGHT - 22, 3);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(0 + scoreXDisplacement,GAME_HEIGHT - 37, 57,19);
        shapeRenderer.rect(57 + scoreXDisplacement,GAME_HEIGHT - 33, 4,11);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(0 + scoreXDisplacement,GAME_HEIGHT - 37,57 + scoreXDisplacement,GAME_HEIGHT - 37);
        shapeRenderer.line(0 + scoreXDisplacement,GAME_HEIGHT - 18,57 + scoreXDisplacement,GAME_HEIGHT - 18);
        shapeRenderer.line(61 + scoreXDisplacement,GAME_HEIGHT - 22,61 + scoreXDisplacement,GAME_HEIGHT - 33);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        batch.enableBlending();
        batch.begin();
        batch.draw(bubble,5 + scoreXDisplacement,GAME_HEIGHT-34,13,13);

        coinsFont.draw(batch, ""+coins,23 + scoreXDisplacement,GAME_HEIGHT-23);

        if (fontFading) {
            fontFade.act(delta);
            font.setColor(fontColor);
            fontS.setColor(fontColor);
            arrow.setAlpha(fontColor.a);
            sea.changeFrontAlpha(0.8f+0.2f*(1-fontColor.a));
            rankingButton.setAlpha(fontColor.a);
            dailyRewardButton.setAlpha(fontColor.a);
            if (hardUnlocked) {
                gamemodeButton.setAlpha(fontColor.a);
            }
            soundButton.setAlpha(fontColor.a);
            if (fontFade.isComplete()) fontFading = false;
        }
        if (fontColor.a > 0.1f) {
            fontS.setColor(fontColor);
            font.setColor(fontColor);
            if (!tutotialOn) {
                font.draw(batch, "Upgrades", GAME_WIDTH / 2 - 57, 50 + arroY);
                if (!highscoreHidden) fontS.draw(batch, "    Highscore " + highscore +
                        "\n2nd Highscore " + highscore2 + "\n3rd Highscore " + highscore3, 47, GAME_HEIGHT / 2 - 50);

            }
            font.draw(batch, "Tap to start!", 50, GAME_HEIGHT / 2 + 52);
        }
        if (!tutotialOn) {
            arrow.draw(batch);
            soundButton.render(batch);
            rankingButton.render(batch);
            dailyRewardButton.render(batch);
            if (hardUnlocked) {
                gamemodeButton.render(batch);
            }
        } else {
            font.setColor(0.2f,0.2f,0.2f,1);
            font.draw(batch, "Tutorial", GAME_WIDTH/2 - 40, GAME_HEIGHT-24);
        }

        batch.end();

        if (!tutotialOn) {
            drawHearts();
        }

        if (state*state == 4) {
            shop.render(delta);
        }
        if (flashStage>0) {
            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1,1,1,flashAlpha);
            shapeRenderer.rect(0,0,GAME_WIDTH,GAME_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);
        }

        if (fadingIn) {
            fade.act(delta);

            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fadingColor);
            shapeRenderer.rect(0,0,GAME_WIDTH,GAME_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);

            if (fade.isComplete()) fadingIn = false;
        }
    }

    private void drawHearts() {

        if (state*state > 1) {
            return;
        }

        batch.begin();

        for (int i = 0; i < 5; i++) {
            batch.draw(heartTextures[heartIndex(2*i)],GAME_WIDTH/2 - 48 + i*20,GAME_HEIGHT-34,16,8f*16/10);
        }

        for (int i = 0; i < Math.min(Math.round((hearts-10)/2f), 5); i++) {
            batch.draw(heartTextures[heartIndex(10 + i*2)],GAME_WIDTH/2 - 48 + i*20,GAME_HEIGHT-34-8f*16/10-8,16,8f*16/10);
        }

        for (int i = 0; i < Math.round((hearts-20)/2f); i++) {
            batch.draw(heartTextures[heartIndex(20 + i*2)],GAME_WIDTH/2 - 48 + i*20,GAME_HEIGHT-34-2*(8f*16/10+8),16,8f*16/10);
        }

        batch.end();

        if (hearts < 10) {
            heartTimer=System.currentTimeMillis()-lastHeartTime;
            if (heartTimer > HEART_TIMER && lastHeartTime != 0) {
                if (MyGame.isTimeAutomatic()) {
                    lastHeartTime += HEART_TIMER;
                    hearts++;
                    timerDisplacement = Math.max((Math.round(hearts/2.1f))*20, 0);
                    admin.saveData("lasthearttime", lastHeartTime);
                    admin.saveData("hearts", hearts);
                    admin.flush();
                }
            }
            minutesToPrint = (int) (HEART_TIMER_MIN-1-TimeUnit.MILLISECONDS.toMinutes(heartTimer));
            secondsToPrint = (int) (60-TimeUnit.MILLISECONDS.toSeconds(heartTimer) + TimeUnit.MINUTES.toSeconds(HEART_TIMER_MIN-1-minutesToPrint));
            if (secondsToPrint == 0) {
                minutesToPrint++;
            }
            if (minutesToPrint < 0) {
                minutesToPrint = 0;
                secondsToPrint = 0;
            }
            timerToPrint = "" + (minutesToPrint < 10?0+""+minutesToPrint:minutesToPrint) + ":" +
                    (secondsToPrint<10?0+""+secondsToPrint:secondsToPrint);
            fontBatch.begin();
            timerFont.draw(fontBatch, timerToPrint, SCALE_X * (GAME_WIDTH / 2 - 54 + timerDisplacement), SCALE_Y * (GAME_HEIGHT - 38));
            fontBatch.end();
        }

    }

    private int heartIndex(int min) {
        if (hearts-min<=0) {
            return 0;
        } else return Math.min(hearts - min, 2);
    }

    private void changeGamemode() {
        if (flashStage == 0) {
            flashStage++;
        } else {
            gamemodeHard = !gamemodeHard;
            admin.saveData("gamemodeHard", gamemodeHard ? 1 : 0);
            admin.flush();
            if (gamemodeHard) {
                leftWall.setTexture(hardWall);
                rightWall.setTexture(hardWall);
                player.lifeUpgrade(0);
                highscore = admin.getData("hardcorehighscore");
                highscore2 = admin.getData("hardcorehighscore2");
                highscore3 = admin.getData("hardcorehighscore3");
                player.setTexturePack(purpulplayerTextures);
                player.setHardcore(true);
            } else {
                leftWall.setTexture(wall);
                rightWall.setTexture(wall);
                player.lifeUpgrade(admin.getData("Holy fish"));
                highscore = admin.getData("highscore");
                highscore2 = admin.getData("highscore2");
                highscore3 = admin.getData("highscore3");
                player.setTexturePack(playerTextures);
                player.setHardcore(false);
            }
        }
    }

    void hideHighscore() {
        highscoreHidden = true;
    }

    private void goToRanking() {
        if (state == 3) {
            myGame.setScreen(rankingScreen);
            state = 0;
            transitionTimer = 0;
        } else {
            if (rankingScreen == null) {
                rankingScreen = new RankingScreen(this, background, rightWall, leftWall);
            }
            state = 3;
            fontFade.restart();
            fontFade.setDuration(0.2f);
            fontFade.setEndColor(new Color(fontColor.r, fontColor.g, fontColor.b, 0));
            fontFading = true;
            rankingScreen.transition();
        }
    }

    private void goToShop() {
        if (state == 2) {
            myGame.setScreen(shop);
            state = 0;
            transitionTimer = 0;
        } else {
            if (shop == null) {
                shop = new UpgradesScreen(this);
            }
            state = 2;
            sea.changeFrontAlpha(1);
            fontFade.restart();
            fontFade.setDuration(0.2f);
            fontFade.setEndColor(new Color(fontColor.r, fontColor.g, fontColor.b, 0));
            fontFading = true;
            shop.transition();
        }
    }

    public void unlockHard() {
        hardUnlocked = true;
        gamemodeButton = new Button(GAME_WIDTH - 48, GAME_HEIGHT - 130, 40, 27, (Texture) admin.getAsset("fish"), (Texture) admin.getAsset("fishpurpul")) {
            @Override
            public void activate() {
                changeGamemode();
            }
        };
        gamemodeButton.setIsChecked(gamemodeHard);

        playerTextures = new Texture[8];
        playerTextures[0] = admin.getAsset("fish");
        playerTextures[1] = admin.getAsset("happyfish");
        playerTextures[2] = admin.getAsset("fishflap");
        playerTextures[3] = admin.getAsset("happyfishflap");
        playerTextures[4] = admin.getAsset("deadfish");
        playerTextures[5] = admin.getAsset("!");
        playerTextures[6] = admin.getAsset("holybeam");
        playerTextures[7] = admin.getAsset("poseidonfish");

        purpulplayerTextures = new Texture[8];
        purpulplayerTextures[0] = admin.getAsset("fishpurpul");
        purpulplayerTextures[1] = admin.getAsset("happyfishpurpul");
        purpulplayerTextures[2] = admin.getAsset("fishflappurpul");
        purpulplayerTextures[3] = admin.getAsset("happyfishflappurpul");
        purpulplayerTextures[4] = admin.getAsset("deadfishpurpul");
        purpulplayerTextures[5] = admin.getAsset("!");
        purpulplayerTextures[6] = admin.getAsset("holybeam");
        purpulplayerTextures[7] = admin.getAsset("fishflappurpul");
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

    }

    @Override
    public void onRewarded() {

    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setShop(UpgradesScreen shop) {
        this.shop = shop;
    }

    public void setTutotialOn(boolean tutotialOn) {
        this.tutotialOn = tutotialOn;
    }

    @Override
    public void touchDown(float x, float y) {
        if (tutotialOn || state != 0) {
            return;
        }
        rankingButton.checkIfTouched(x, y);
        dailyRewardButton.checkIfTouched(x, y);
        if (hardUnlocked) {
            gamemodeButton.checkIfTouched(x, y);
        }
    }

    @Override
    public void touchUp(float x, float y) {
        if (state == 0 &! tutotialOn) {
            soundButton.checkIfReleased(x, y);
            rankingButton.checkIfReleased(x, y);
            dailyRewardButton.checkIfReleased(x, y);
            if (hardUnlocked) {
                gamemodeButton.checkIfReleased(x, y);
            }
        }
    }

    @Override
    public void tap(float x, float y) {
        if (tutotialOn) {
            if (state != 1) {
                start();
            } else {
                player.jump();
            }
        } else if (soundButton.checkIfTouched(x,y)) {
            soundOff = !soundOff;
            admin.saveData("soundOff", soundOff ? 1 : 0);
            admin.flush();
            gameScreen.changeSoundOff();
            if (shop != null) {
                shop.changeSoundOff();
            }
        } else if (!(rankingButton.checkIfTouched(x,y) || dailyRewardButton.checkIfTouched(x,y)) && state == 0) {
            if (hardUnlocked) {
                if (!gamemodeButton.checkIfTouched(x,y)) {
                    if (y > ARROW_Y_CAP + 70) {
                        start();
                    } else {
                        goToShop();
                    }
                }
            } else {
                if (y > ARROW_Y_CAP + 70) {
                    start();
                } else {
                    goToShop();
                }
            }
        } else if (state == 1) {
            player.jump();
        }
    }

    void start() {
        state = 1;
        fontFade.restart();
        fontFade.setDuration(0.2f);
        fontFade.setEndColor(new Color(fontColor.r, fontColor.g, fontColor.b, 0));
        fontFading = true;
        sea.changeFrontAlpha(1);
        player.setSeaLevel(0);
        player.startJump();
    }

    void dailyRewardIsAvailable() {
        dailyRewardButton.setIsChecked(false);
    }

    void claimedDailyReward() {
        dailyRewardButton.setIsChecked(true);
    }

    @Override
    public void flingUp(float startX, float startY) {
        if (startY < GAME_HEIGHT/2 && !( state>0 || tutotialOn)) {
            goToShop();
        }
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