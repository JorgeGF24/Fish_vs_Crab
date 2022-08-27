package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;

import java.util.ArrayList;

import me.jorge.myfirstgame.sprites.Player;
import me.jorge.myfirstgame.util.AssetAdmin;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.util.PowerupButton;
import me.jorge.myfirstgame.sprites.Background;
import me.jorge.myfirstgame.sprites.Crab;
import me.jorge.myfirstgame.sprites.Bubble;
import me.jorge.myfirstgame.sprites.HelperFish;
import me.jorge.myfirstgame.sprites.Missile;
import me.jorge.myfirstgame.sprites.Sea;
import me.jorge.myfirstgame.sprites.Tube;
import me.jorge.myfirstgame.sprites.Wall;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class GameScreen extends ScreenInputProcessor implements SpriteHandlerScreen {

    private BitmapFont fontL, fontS, coinsFont;

    private float score, coinsConverted, scoreConversionSpeed = 1;
    private final float MAX_CONVERSION_SPEED = 50, CONVERSION_GRAVITY = 10;
    private int displayScore;

    private final Button pauseButton;
    private final PauseScreen pauseScreen;

    public static final float FADE_OUT_TIME = 3.4f;
    private boolean fadingOut, isScoreConverting, hardcoreOn, comingFromReward, shownVideoAd, rewarded;
    private final Color fadingColor = new Color(0,0,0,0);
    private final ColorAction fade = new ColorAction();

    private final Background background;

    private Player player;
    public static final float PLAYER_WIDTH = 55;
    public static final float PLAYER_HEIGHT = 40;
    public static final float X_SPEED = 172;
    public static final float JUMP = X_SPEED*1.75f;
    public static final float GRAVITY = -770f;
    private boolean isInWater, isDead, isReviving, touchJump;

    // The difficulty variable (0-easy up to 3 hard) will modify the crab spawn probability to make it easier for new players
    // and thus increase retention rates.
    private int highscore, highscore2, highscore3, milestoneLevel, milestoneReached, milestoneConversion, difficulty, gameCoins;
    private boolean newHighscore;
    private Texture bubble4score;
    private BitmapFont milestoneFont;
    public static final int[] milestones =        {0, 25,  50, 100, 150,  200, 225, 250, 300, 314, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800,  850, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2250, 2500, 2750, 3000, 4000, 5000, 7500, 10000, 15000, 20000, 1000000};
    private static final int[] milestoneRewards = {0, 30, 100, 210, 200,  280, 150, 200, 330, 200, 150, 500, 400, 900, 500, 800, 450, 900, 500, 1000, 500, 1500, 1000, 1200, 1400, 1600, 2000, 1700, 1800, 2000, 2100, 2700, 2300, 2500, 2700, 2800, 2900, 3100, 3000, 4000,   8000,  5000, 0};
  //                                                      150       325      450            750      1050      1400 1900      2550      3750      4850      6100       7600       9600  10600      13200       16800       20300        24400       29400            37400        43400       50400   58400  63400
    private final ArrayList<Crab> leftCrabs, rightCrabs, crabCementery;
    private float crabProb, slashProbability;
    public static final float CRAB_HEIGHT = 45;
    public static final float CRAB_WIDTH = 27;
    private boolean removeCrabs, isPurpulTime;

    private final ArrayList<Tube> leftTubes,rightTubes, tubeCementery;
    private float tubeProb;
    public static final float TUBE_HEIGHT = 36;
    public static final float TUBE_WIDTH = 30;
    private boolean removeTubes;

    private static final float MIN_CLIMB_SPEED = -30;
    private static final float MAX_CLIMB_SPEED = -140;
    private float normalClimbSpeed = MIN_CLIMB_SPEED;
    public static float climbSpeed;
    private static final float SPRITE_MARGIN = 8;
    private float leftSpawnClock = CRAB_HEIGHT + SPRITE_MARGIN;
    private float rightSpawnClock = CRAB_HEIGHT + SPRITE_MARGIN;
    // These dummy variables hold the crab/tube/bubble to remove. We cant remove a variable during a for loop
    // which iterates through our sprite arrays, so we need a holder variable to store it and remove it at the end.
    // The godDummy variables are used to reset our dummy variables.
    private final Sprite godDummySprite;
    private Sprite dummySprite;
    private final Bubble godBubble;
    private Bubble dumbBubble;

    private final ArrayList<Bubble> bubbles, bubbleCementery;
    public static final float BUBBLE_HEIGHT = 20;
    public static final float BUBBLE_WIDTH = 20;
    private int bubblesToRemove;
    private static final float BUBBLE_SPAWN_BASE_CHANCE = 0.5f;
    private static float bubbleProb;
    private static float goldenProbability;

    private boolean helperActive;
    private HelperFish helper;

    public final static float WALL_WIDTH = 13;
    private final Wall leftWall, rightWall;
    private Sea sea;
    private float darkScreenPercentage, moonshinePercentage, moonshineX;
    private int state;
    private final float WIND_SPEED = 24;
    private Sprite moonshine;

    private boolean tutorialOn;
    private TutorialScreen tutorialScreen;

    //UPGRADES

    private int lessCrabsLevel, moreTubesLevel, fasterScoreLevel, nightVisionLevel,
            bubblePwuLevel, shieldPwuLevel, poseidonPwuLevel, missilePwuLevel;

    //POWERUPS
    private final static float DRAWABLE_RECT_X = WALL_WIDTH + 5, DRAWABLE_RECT_Y = 7, BUTTON_SIDE = 45;
    private final static float DRAWABLE_RECT_WIDTH = GAME_WIDTH - 2*DRAWABLE_RECT_X;

    private float bubblePwuTimer, bubbleSpawnTimer;
    private boolean bubblePwuActive;
    private me.jorge.myfirstgame.util.PowerupButton bubblePwuButton;

    private float shieldPwuTimer, shieldHitTimer, shieldX, shieldY, shieldAlpha;
    private final float SHIELD_SIDE = 34*PLAYER_WIDTH/33, SHIELD_X_DISPLACEMENT = 1*PLAYER_WIDTH/33, SHIELD_Y_DISPLACEMENT = 5*PLAYER_HEIGHT/23;
    private Sprite bubbleShield;
    private boolean shieldPwuActive;
    private me.jorge.myfirstgame.util.PowerupButton shieldPwuButton;
    private Sound shieldHitSound;

    private float seaY, seaSpeed;
    private final float SHINE_SIDE = 50*PLAYER_WIDTH/33, SHINE_X_DISPLACEMENT = 10*PLAYER_WIDTH/33, SHINE_Y_DISPLACEMENT = 14*PLAYER_HEIGHT/23,
            TARGET_SEA_SPEED = 600;
    private Sprite poseidonBeam;
    private boolean poseidonPwuActive, isRising;
    private me.jorge.myfirstgame.util.PowerupButton poseidonPwuButton;
    private Sound waveSound;

    private ArrayList<Missile> missiles, missileCementery;
    private boolean missilePwuActive, removeMissiles, activeMissiles, longPressOn;
    private float missilePwuOutRunTimer, longpressTime, touchX, touchY;
    private BitmapFont tapToShootFont;
    private me.jorge.myfirstgame.util.PowerupButton missilePwuButton;

    public GameScreen() {

        if (!adLoaded) {
            myGame.loadVideoAd();
        }

        initAssets(admin);

        background = new Background((Texture) admin.getAsset("background"), (Texture) admin.getAsset("cloud"),
                (Texture) admin.getAsset("cloud2"));

        leftWall = new Wall((Texture) admin.getAsset("rock"));
        rightWall = new Wall((Texture) admin.getAsset("rock"));
        rightWall.flip(true,true);
        rightWall.setX(GAME_WIDTH - WALL_WIDTH);
        player.setPosition((GAME_WIDTH - PLAYER_WIDTH)/2,(GAME_HEIGHT - PLAYER_WIDTH)/2 +16);

        pauseScreen = new PauseScreen(this);

        pauseButton = new Button(GAME_WIDTH - 40, GAME_HEIGHT- 40,20,21.4f, (Texture) admin.getAsset("pausebuttonup"), (Texture) admin.getAsset("pausebuttondown")) {
            @Override
            public void activate() {
                pauseScreen.setScore(Math.round(score), highscore);
                myGame.setScreen(pauseScreen);
            }
        };
        pauseButton.setBounds(GAME_WIDTH - 60, GAME_HEIGHT- 60,60,61.4f);

        leftCrabs = new ArrayList<>();
        rightCrabs = new ArrayList<>();
        leftTubes = new ArrayList<>();
        rightTubes = new ArrayList<>();
        bubbles = new ArrayList<>();
        crabCementery = new ArrayList<>();
        tubeCementery = new ArrayList<>();
        bubbleCementery = new ArrayList<>();

        godDummySprite = new Crab(0,0,false, false,player,this);
        godBubble = new Bubble(0,false);
        dummySprite = godDummySprite;
        dumbBubble = godBubble;
    }

    private void initAssets(AssetAdmin admin) {
        Texture[] playerTextures = new Texture[8];
        playerTextures[0] = admin.getAsset("fish");
        playerTextures[1] = admin.getAsset("happyfish");
        playerTextures[2] = admin.getAsset("fishflap");
        playerTextures[3] = admin.getAsset("happyfishflap");
        playerTextures[4] = admin.getAsset("deadfish");
        playerTextures[5] = admin.getAsset("!");
        playerTextures[6] = admin.getAsset("holybeam");
        playerTextures[7] = admin.getAsset("poseidonfish");
        Texture[] loops = new Texture[5];
        loops[0] = admin.getAsset("holyloop");
        loops[1] = admin.getAsset("holyloopblue");
        loops[2] = admin.getAsset("holyloopgreen");
        loops[3] = admin.getAsset("holyloopred");
        loops[4] = admin.getAsset("holylooppurpul");
        player = new me.jorge.myfirstgame.sprites.Player(playerTextures,loops,admin.getData("Holy fish"),this);
        player.soundOff = soundOff;
        player.jumpSound = admin.getAsset("jump sound");
        player.splashSound = admin.getAsset("splash");
        player.soundOfLife = admin.getAsset("sound of life");
        player.soundOfDeath = admin.getAsset("sound of death");
        shieldHitSound = admin.getAsset("shield hit");

        final Texture[] seaTextures = new Texture[3];
        seaTextures[0] = admin.getAsset("seafront");
        seaTextures[1] = admin.getAsset("seamiddle");
        seaTextures[2] = admin.getAsset("seaback");
        sea = new Sea(seaTextures);

        Tube.soundOff = soundOff;
        Tube.tapSound = admin.getAsset("tap");

        final Texture[] popBubbleTextures = new Texture[6];
        popBubbleTextures[0] = admin.getAsset("airball");
        popBubbleTextures[1] = admin.getAsset("goldball");
        popBubbleTextures[2] = admin.getAsset("airballpop4");
        Bubble.setBubbleTextures(popBubbleTextures);
        Bubble.setUp(player,this, admin.getData("Goldify"));
        if (admin.getData("Family business") > 0) {
            helper = new HelperFish((Texture) admin.getAsset("helpermain"), (Texture) admin.getAsset("helperflap"), admin.getData("Family business"), this);
            Bubble.setUpHelper(helper);
            helperActive = true;
        }
        goldenProbability =  0.1f * admin.getData("Goldify");
        bubble4score = admin.getAsset("bubble4score");
        Bubble.soundOff = soundOff;
        Bubble.popSound = admin.getAsset("bubble pop");
        Bubble.coinSound = admin.getAsset("coin");

        final Texture [] redCrabTextures = new Texture[6];
        redCrabTextures[0] = admin.getAsset("crab");
        redCrabTextures[1] = admin.getAsset("crabblink");
        redCrabTextures[2] = admin.getAsset("crab2");
        redCrabTextures[3] = admin.getAsset("crab2blink");
        redCrabTextures[4] = admin.getAsset("crabreadystrike");
        redCrabTextures[5] = admin.getAsset("crabstrike");

        final Texture[] purpulCrabTextures = new Texture[6];
        purpulCrabTextures[0] = admin.getAsset("purpulcrab");
        purpulCrabTextures[1] = admin.getAsset("purpulcrabblink");
        purpulCrabTextures[2] = admin.getAsset("purpulcrab2");
        purpulCrabTextures[3] = admin.getAsset("purpulcrab2blink");
        purpulCrabTextures[4] = admin.getAsset("crabreadystrike");
        purpulCrabTextures[5] = admin.getAsset("crabstrike");

        Crab.setTextures(redCrabTextures, purpulCrabTextures, (Texture) admin.getAsset("deadcrab"), (Texture) admin.getAsset("!"));
        Crab.soundOff = soundOff;
        Crab.slashSound = admin.getAsset("slash");

        moonshine = new Sprite((Texture) admin.getAsset("moonshine"));
        moonshine.setBounds(0,0,GAME_WIDTH,GAME_HEIGHT);

        fontL = admin.getAsset("canonfont80");
        fontS = admin.getAsset("canonfont60");
        fontL.setColor(0.8f,0.6f,0.13f,1);
        fontS.setColor(0.8f,0.6f,0.13f,1);
        coinsFont = admin.getAsset("khula10");
        tapToShootFont = admin.getAsset("coolville30");
        milestoneFont = admin.getAsset("coolville15");
        milestoneLevel = admin.getData("milestoneLevel");
    }

    private void initPWUs() {
        if (bubblePwuLevel > 0) {
            bubblePwuButton = new me.jorge.myfirstgame.util.PowerupButton(DRAWABLE_RECT_X + DRAWABLE_RECT_WIDTH/4, DRAWABLE_RECT_Y, BUTTON_SIDE, (Texture) admin.getAsset("pwububble1"), (Texture) admin.getAsset("pwububble2")) {
                @Override
                public void activate() {
                    super.activate();
                    activateBubblePwu();
                }
            };
            bubblePwuButton.setUp(20 - bubblePwuLevel * 2);
        }

        if (shieldPwuLevel > 0) {
            bubbleShield = new Sprite((Texture) admin.getAsset("shield"));
            bubbleShield.setSize(SHIELD_SIDE,SHIELD_SIDE);
            shieldPwuButton = new me.jorge.myfirstgame.util.PowerupButton(DRAWABLE_RECT_X, DRAWABLE_RECT_Y, BUTTON_SIDE, (Texture) admin.getAsset("pwushield1"), (Texture) admin.getAsset("pwushield2")) {
                @Override
                public void activate() {
                    super.activate();
                    activateShieldPwu();
                }
            };
            shieldPwuButton.setUp(20 - shieldPwuLevel * 2);
        }

        if (poseidonPwuLevel > 0) {
            poseidonBeam = new Sprite((Texture) admin.getAsset("poseidonbeam"));
            poseidonBeam.setSize(PLAYER_WIDTH+40,GAME_HEIGHT);
            poseidonPwuButton = new me.jorge.myfirstgame.util.PowerupButton(DRAWABLE_RECT_X + DRAWABLE_RECT_WIDTH/2, DRAWABLE_RECT_Y, BUTTON_SIDE, (Texture) admin.getAsset("pwuposeidon1"), (Texture) admin.getAsset("pwuposeidon2")) {
                @Override
                public void activate() {
                    super.activate();
                    activatePoseidonPwu();
                }
            };
            poseidonPwuButton.setUp(30 - poseidonPwuLevel * 4);

            waveSound = admin.getAsset("wave");
        }

        if (missilePwuLevel > 0) {
            missilePwuButton = new me.jorge.myfirstgame.util.PowerupButton(DRAWABLE_RECT_X + 3*DRAWABLE_RECT_WIDTH/4, DRAWABLE_RECT_Y, BUTTON_SIDE, (Texture) admin.getAsset("pwumissiles0"), (Texture) admin.getAsset("pwumissiles0b")) {
                @Override
                public void activate() {
                    super.activate();
                    activateMissilePwu();
                }
            };
            int CHARGES;
            float explosionSize;
            switch (missilePwuLevel) {
                case 1:
                    CHARGES = 1;
                    explosionSize = 60;
                    break;
                case 2:
                    CHARGES = 1;
                    explosionSize = 65;
                    break;
                case 3:
                    CHARGES = 2;
                    explosionSize = 71;
                    break;
                case 4:
                    CHARGES = 2;
                    explosionSize = 82;
                    break;
                case 5:
                    CHARGES = 3;
                    explosionSize = 94;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + missilePwuLevel);
            }
            final Texture[] missileButtons = new Texture[6];
            for (int i=1; i < 7; i++) {
                missileButtons[i-1] = admin.getAsset("pwumissiles" + i);
            }
            missilePwuButton.setUp(7 - 0.9f*missilePwuLevel,CHARGES,missileButtons);

            /*final TextureRegion[] explosion = new TextureRegion[7];
            final Texture explosionstrip = admin.getAsset("explosion");
            for (int i=0; i < 7; i++) {
                explosion[i] = new TextureRegion(explosionstrip, 50*i,0,50,50);
            }*/
            Missile.setTextures((Texture) admin.getAsset("missile"), (Texture) admin.getAsset("explosion"), (Texture) admin.getAsset("crosshair"), explosionSize);
            Missile.soundOff = soundOff;
            Missile.explosionSound = admin.getAsset("explosion sound");
            missiles = new ArrayList<>();
            missileCementery = new ArrayList<>();
        }
    }

    private void pointUp(float add) {
        if ((score < 480)&&(score+add)%250<score%250) {
            state = (state +1)%3;
        } else if ((score+add)%250<score%250) {
            state = (state +1)%3;
        }

        if (!isPurpulTime) {
            if (score % 750 > 200 && score % 750<530) {
                isPurpulTime = true;
            }
        } else {
            if (score % 750 > 530) {
                isPurpulTime = false;
            }
        }
        if (score < 750) {
            normalClimbSpeed = MIN_CLIMB_SPEED + score*(MAX_CLIMB_SPEED - MIN_CLIMB_SPEED)/750;
            climbSpeedAdd(0);
        }
        /*
        if (score < 480) {
            score += 9*add;
        }*/
        score += add;
        if (score < 0) {
            score = 0;
        }
        displayScore = Math.round(score);
        background.updateSky(score);
    }

    public void coinUp(int add) {
        player.makeHappy();
        coins += add;
        gameCoins += add;
    }

    @Override
    public void show() {
        if (comingFromReward) {
            if (rewarded) {
                player.revive();
                fadingOut = false;
                shownVideoAd = true;
                fade.reset();
                fadingColor.a = 0;
            } else {
                finishedFading();
            }
            comingFromReward = false;
        } else if (displayScore == 0 && !hardcoreOn) {
            highscore = admin.getData("highscore");
            highscore2 = admin.getData("highscore2");
            highscore3 = admin.getData("highscore3");
            if (highscore > 310) {
                difficulty = 3;
            } else if (highscore > 250) {
                difficulty = 2;
            } else if (highscore > 190) {
                difficulty = 1;
            }
            Player.difficulty = difficulty;
            climbSpeedAdd(0);
            sea.setY(30);
            seaY = 30;
            player.setSeaLevel(seaY + Sea.HEIGHT);
            Bubble.setSeaLevel(seaY + Sea.HEIGHT + 5);

            Bubble.scoreConverting = false;
            if (missilePwuLevel > 0) {
                tapToShootFont.setColor(0.9f, 0.9f, 0.9f, 1);
            }

            lessCrabsLevel = admin.getData("Less crabs");
            moreTubesLevel = admin.getData("More tubes");
            final int moreBubblesLevel = admin.getData("More bubbles");
            bubbleProb = BUBBLE_SPAWN_BASE_CHANCE + moreBubblesLevel * 0.06f;
            fasterScoreLevel = admin.getData("Faster score");
            nightVisionLevel = admin.getData("Night vision");
            bubblePwuLevel = admin.getData("Bubble rain");
            shieldPwuLevel = admin.getData("Bubble shield");
            poseidonPwuLevel = admin.getData("Poseidon");
            missilePwuLevel = admin.getData("Fish lord");
            longPressOn = (1 == admin.getData("longPressOn"));
            player.setSwimmingLvL(admin.getData("Better swimmer"));
            Tube.set((Texture) admin.getAsset("tube"),(Texture) admin.getAsset("orangetube"),admin.getData("Stronger tubes"));
            slashProbability = 0.5f-0.035f*admin.getData("Lazy purple");

            initPWUs();

            if (!helperActive) {
                if (admin.getData("Family business") > 0) {
                    helper = new HelperFish((Texture) admin.getAsset("helpermain"), (Texture) admin.getAsset("helperflap"), admin.getData("Family business"), this);
                    Bubble.setUpHelper(helper);
                    helperActive = true;
                }
            }

            if (helperActive) {
                helper.setSeaLevel(seaY + Sea.HEIGHT + 7);
            }

            tapToShootFont.setColor(0.9f, 0.9f, 0.9f, 1);
            milestoneFont.setColor(0.5f, 0.5f, 0.5f, 1);
        } else if (hardcoreOn) {
            nightVisionLevel = 5;
        }
    }

    void setToHardcore() {
        hardcoreOn = true;

        highscore = admin.getData("hardcorehighscore");
        highscore2 = admin.getData("hardcorehighscore2");
        highscore3 = admin.getData("hardcorehighscore3");

        goldenProbability =  0.1f * admin.getData("Goldify");

        climbSpeedAdd(0);
        sea.setY(30);
        seaY = 30;
        player.setSeaLevel(seaY + Sea.HEIGHT);
        Bubble.setSeaLevel(seaY + Sea.HEIGHT + 5);

        Bubble.scoreConverting = false;
        Tube.set((Texture) admin.getAsset("tube"),(Texture) admin.getAsset("orangetube"),0);
        slashProbability = 0.5f;

        milestoneFont.setColor(0.5f, 0.5f, 0.5f, 1);

        Bubble.helperAlive = false;
        helper = null;
        helperActive = false;
    }

    @Override
    public void render(float delta) {
        if (isScoreConverting) {

            for (Bubble bubble : bubbles) {
                bubble.update(delta);
            }

            if (bubblesToRemove != 0) {
                removeBubbles();
            }

            if (scoreConversionSpeed < MAX_CONVERSION_SPEED) {
                scoreConversionSpeed += CONVERSION_GRAVITY*delta;
            }
            if (milestoneConversion - milestoneLevel > 0) {
                if (displayScore < milestones[milestoneConversion]) {
                    for (int i = 0; i < milestoneRewards[milestoneConversion]/25; i++) {
                        if (bubbles.size() < 25) {
                            if (bubbleCementery.size() == 0) {
                                Bubble bubble = new Bubble(GAME_HEIGHT + MathUtils.random(-70, 70), MathUtils.randomBoolean());
                                bubble.schedulePop(1.3f + MathUtils.random() / 2);
                                bubble.makeSuperGolden();
                                bubbles.add(bubble);
                            } else {
                                final Bubble bubble = bubbleCementery.get(0);
                                bubble.reset(GAME_HEIGHT + MathUtils.random(-70, 70), MathUtils.randomBoolean());
                                bubble.schedulePop(1.3f + MathUtils.random() / 2);
                                bubble.makeSuperGolden();
                                bubbles.add(bubble);
                                bubbleCementery.remove(0);
                            }
                        } else {
                            coinUp(25*(milestoneRewards[milestoneConversion]/25-i));
                            break;
                        }
                    }
                    milestoneConversion--;
                }
            }
            if (score>0.5f) {
                pointUp(-delta * scoreConversionSpeed);
                coinsConverted += delta*scoreConversionSpeed/10;
                if (coins + Math.round(coinsConverted) > coins) {
                    coinsConverted -= Math.round(coinsConverted);
                    if (bubbles.size() < 25) {
                        if (bubbleCementery.size() == 0) {
                            final Bubble bubble = new Bubble(GAME_HEIGHT, false);
                            bubble.schedulePop(1.3f + MathUtils.random() / 2);
                            bubbles.add(bubble);
                        } else {
                            final Bubble bubble = bubbleCementery.get(0);
                            bubble.reset(GAME_HEIGHT, false);
                            bubble.schedulePop(1.3f + MathUtils.random() / 2);
                            bubbles.add(bubble);
                            bubbleCementery.remove(0);
                        }
                    } else {
                        coinUp(MathUtils.randomBoolean(goldenProbability)?3:1);
                    }
                }
            } else if (bubbles.isEmpty()) {
                changeScreen();
            }
        } else {
            // SLOWDOWN time if missile targeting is active
            if (missilePwuActive) {
                missilePwuOutRunTimer -= delta;
                if (missilePwuOutRunTimer < 0) {
                    deactivateMissilePwu();
                }
                delta *= 0.1f;
            }

            // ================================ UPDATE GAME ============================================
            // Update spawn clocks. Add a point wih every rightSpawnClock update
            leftSpawnClock += climbSpeed * delta;
            if (leftSpawnClock < 0) {
                leftSpawnClock = triggerSpawn(true) ? TUBE_HEIGHT + SPRITE_MARGIN : CRAB_HEIGHT + SPRITE_MARGIN;
            }
            rightSpawnClock += climbSpeed * delta;
            if (rightSpawnClock < 0) {
                rightSpawnClock = triggerSpawn(false) ? TUBE_HEIGHT + SPRITE_MARGIN : CRAB_HEIGHT + SPRITE_MARGIN;
                if (!fadingOut) {
                    pointUp(1f * (1 + fasterScoreLevel * 0.25f));
                }
            }
            if (missilePwuLevel > 0 && touchJump && longPressOn) {
                longpressTime += delta;
                if (longpressTime > 0.15f) {
                    touchJump = false;
                    if (missilePwuButton.getCharges() > 0) {
                        missilePwuButton.removeCharge();
                        spawnMissile(touchX, touchY);
                    }
                }
            }

            updateMoonshine(delta);

            if (bubblePwuActive) {
                updateBubblePwu(delta);
            }

            if (poseidonPwuActive) {
                updatePoseidonPwu(delta);
            }

            // Update all sprites on the screen
            updateSprites(delta);

            if (shieldPwuActive) {
                updateShieldPwu(delta);
            }

            // Check if we have to remove any sprites
            if (removeCrabs) {
                removeCrabs();
                removeCrabs = false;
            }
            if (removeTubes) {
                removeTubes();
                removeTubes = false;
            }
            if (bubblesToRemove != 0) {
                removeBubbles();
            }
            if (removeMissiles) {
                removeMissiles();
                removeMissiles = false;
            }

            // Convert score to bubbles after death


            // ================================= RENDER GAME ===========================================
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.enableBlending();
            background.render(batch, shapeRenderer, delta);

            batch.begin();
            if (displayScore < 10) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 22, GAME_HEIGHT / 2 + 62);
            } else if (displayScore < 100) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 48, GAME_HEIGHT / 2 + 62);
            } else {
                fontS.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 55, GAME_HEIGHT / 2 + 56);
            }

            sea.renderBack(batch, delta);
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.13f, 0.49f, 0.757f, 1);
            shapeRenderer.rect(0, 0, GAME_WIDTH, seaY);
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
            if (shieldPwuActive) {
                bubbleShield.draw(batch);
            }
            if (helperActive) {
                helper.draw(batch);
            }
            for (Crab crab : leftCrabs) {
                crab.draw(batch);
            }
            for (Crab crab : rightCrabs) {
                crab.draw(batch);
            }

            if (activeMissiles) {
                for (Missile missile : missiles) {
                    missile.draw(batch);
                }
            }

            sea.renderFront(batch, delta);
            batch.end();

            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0.27f, 0.74f, isInWater ? 0.7f : 1);
            shapeRenderer.rect(0, 0, GAME_WIDTH, seaY);

            shapeRenderer.setColor(0, 0, 0, 0.75f * (darkScreenPercentage - Math.abs(1 - Math.abs(moonshinePercentage - 1)))/*+0.07f*Math.abs(moonShinePercentage)*/);
            shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);

            batch.begin();
            moonshine.draw(batch, Math.abs(1 - Math.abs(moonshinePercentage - 1)));
            moonshine.setX(moonshineX - GAME_WIDTH);
            moonshine.draw(batch, Math.abs(1 - Math.abs(moonshinePercentage - 1)));
            moonshine.setX(moonshineX + GAME_WIDTH);
            batch.end();

            if (poseidonPwuActive) {
                batch.begin();
                poseidonBeam.draw(batch);
                player.draw(batch);
                batch.end();

            }
            batch.begin();
            if (shieldPwuLevel > 0) {
                shieldPwuButton.render(batch);
            }
            if (bubblePwuLevel > 0) {
                bubblePwuButton.render(batch);
            }
            if (poseidonPwuLevel > 0) {
                poseidonPwuButton.render(batch);
            }
            if (missilePwuLevel > 0) {
                missilePwuButton.render(batch);
            }
            pauseButton.render(batch);
            batch.end();

            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (shieldPwuLevel > 0) {
                shieldPwuButton.render(shapeRenderer);
            }
            if (bubblePwuLevel > 0) {
                bubblePwuButton.render(shapeRenderer);
            }
            if (poseidonPwuLevel > 0) {
                poseidonPwuButton.render(shapeRenderer);
            }
            if (missilePwuLevel > 0) {
                missilePwuButton.render(shapeRenderer);
            }
            if (missilePwuActive) {
                shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 0.5f);
                shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            }
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);

            if (missilePwuActive) {
                batch.begin();
                tapToShootFont.draw(batch, "Tap to shoot", 60, GAME_HEIGHT - 30);
                batch.end();
            }
        }
/*
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        if (activeMissiles) {
            for (Missile missile: missiles) {
                missile.drawDebug(shapeRenderer);
            }
        }
        shapeRenderer.end();*/

        if (fadingOut) {
            fade.act(delta);

            Gdx.gl.glEnable(GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fadingColor);
            shapeRenderer.rect(0,0,GAME_WIDTH,GAME_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL_BLEND);

            batch.begin();
            if (displayScore < 10) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 22, GAME_HEIGHT / 2 + 62);
            } else if (displayScore <100) {
                fontL.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 48, GAME_HEIGHT / 2 + 62);
            } else {
                fontS.draw(batch, "" + displayScore, GAME_WIDTH / 2 - 55, GAME_HEIGHT / 2 + 56);
            }
            if (isScoreConverting) {
                //conversionFont.draw(batch, "score to bubbles", GAME_WIDTH / 2 - 115, GAME_HEIGHT / 2 + 56);
                tapToShootFont.draw(batch, "Skip",GAME_WIDTH - 70, 40);
                milestoneFont.draw(batch, "Next milestone: " + milestones[Math.max(milestoneLevel+1,milestoneReached+1)], GAME_WIDTH/2 - 90, GAME_HEIGHT/2 - 35);
                if (milestoneReached > milestoneLevel) {
                    for (int i = 0; i < milestoneReached - milestoneLevel; i++) {
                        milestoneFont.draw(batch, "Reached " + milestones[milestoneLevel + i + 1], GAME_WIDTH / 2 - 90, GAME_HEIGHT / 2 - 55 - i*20);
                        milestoneFont.draw(batch, "+" + milestoneRewards[milestoneLevel + i + 1], GAME_WIDTH / 2 + 40, GAME_HEIGHT / 2 - 55 - i * 20);
                        batch.draw(bubble4score,GAME_WIDTH / 2 + 80,GAME_HEIGHT / 2 - 68 - i * 20,13,13);
                    }
                }
                for (Bubble bubble : bubbles) {
                    bubble.draw(batch);
                }
            }
            batch.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(57,GAME_HEIGHT - 33, 4);
        shapeRenderer.circle(57,GAME_HEIGHT - 22, 4);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.circle(57,GAME_HEIGHT - 33, 3);
        shapeRenderer.circle(57,GAME_HEIGHT - 22, 3);
        shapeRenderer.rect(0,GAME_HEIGHT - 37, 57,19);
        shapeRenderer.rect(57,GAME_HEIGHT - 33, 4,11);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(0,GAME_HEIGHT - 37,57,GAME_HEIGHT - 37);
        shapeRenderer.line(0,GAME_HEIGHT - 18,57,GAME_HEIGHT - 18);
        shapeRenderer.line(61,GAME_HEIGHT - 22,61,GAME_HEIGHT - 33);
        shapeRenderer.end();

        batch.begin();
        batch.draw(bubble4score,5,GAME_HEIGHT-34,13,13);
        coinsFont.draw(batch, ""+coins,23,GAME_HEIGHT-23);
        batch.end();

    }

    private void updateMoonshine(float delta) {
        switch (state) {
            case 1:
                moonshinePercentage = (1 - (375 - score%750) / 125);
                moonshineX += WIND_SPEED * delta;
                if (moonshineX > GAME_WIDTH) {
                    moonshineX -= GAME_WIDTH;
                }
                moonshine.setX(moonshineX);
                darkScreenPercentage = 1;
                break;
            case 0:
                darkScreenPercentage = score%250/250;
                break;
            case 2:
                darkScreenPercentage =  1-score%250/250;
                break;
        }

        moonshinePercentage *= (1-nightVisionLevel*0.12f);
        darkScreenPercentage *= (1-nightVisionLevel*0.15f);
    }

    private void updateSprites(float delta) {
        if (helperActive) {
            helper.update(delta);
        }
        player.update(delta);
        for (Crab crab: rightCrabs) {
            crab.update(delta);
        }
        for (Crab crab: leftCrabs) {
            crab.update(delta);
        }
        for (Tube tube: rightTubes) {
            tube.update(delta);
        }
        for (Tube tube: leftTubes) {
            tube.update(delta);
        }
        for (Bubble bubble : bubbles) {
            bubble.update(delta);
        }
        leftWall.update(delta);
        rightWall.update(delta);

        if (activeMissiles) {
            for (Missile missile:missiles) {
                missile.update(delta);
            }
        }

        if (bubblePwuLevel > 0 && !poseidonPwuActive) {
            bubblePwuButton.update(delta);
        }

        if (shieldPwuLevel > 0 && !poseidonPwuActive) {
            shieldPwuButton.update(delta);
        }

        if (poseidonPwuLevel > 0) {
            poseidonPwuButton.update(delta);
        }

        if (missilePwuLevel > 0 && !poseidonPwuActive) {
            missilePwuButton.update(delta);
        }
    }

    private void updateBubblePwu(float delta) {
        bubblePwuTimer -= delta;
        if (bubblePwuTimer < 0) {
            bubblePwuActive = false;
            bubblePwuButton.deactivate();
            return;
        }
        bubbleSpawnTimer -= delta;
        if (bubbleSpawnTimer < 0) {
            spawnBubble(MathUtils.randomBoolean());
            bubbleSpawnTimer = 0.3f;
        }
    }

    private void updateShieldPwu(float delta) {
        shieldPwuTimer -= delta;
        if (shieldHitTimer > 0) {
            shieldHitTimer -= delta;
            if (shieldHitTimer < 0) {
                bubbleShield.setColor(Color.WHITE);
                if (shieldPwuTimer < 0) {
                    shieldPwuActive = false;
                    shieldPwuButton.deactivate();
                    player.setInmune(true, 0.3f);
                    return;
                }
            }
        } else if (shieldPwuTimer < 0) {
            shieldPwuActive = false;
            shieldPwuButton.deactivate();
            player.setInmune(true, 0.3f);
            return;
        }
        if (shieldPwuTimer < 2) {
            bubbleShield.setAlpha(player.getColor().a/3);
        }
        bubbleShield.setPosition(player.getX() - SHIELD_X_DISPLACEMENT,player.getY() - SHIELD_Y_DISPLACEMENT);
    }

    private void updatePoseidonPwu(float delta) {
        if (seaY < 31 && !isRising) {
            deactivatePoseidonPwu();
            poseidonPwuButton.deactivate();
            return;
        } else if (!isRising) {
            if (seaY > 600) {
                seaY -= delta*640;
            } else {
                seaY -= delta * (50 + seaY);
            }
        } else {
            if (seaSpeed < TARGET_SEA_SPEED) {
                seaSpeed += 200*delta;
            }
            seaY += seaSpeed *delta;
            if (seaY > GAME_HEIGHT+200) {
                isRising = false;
                leftCrabs.clear();
                rightCrabs.clear();
            }
        }
        sea.setY(seaY);
        if (helperActive) {
            helper.setSeaLevel(seaY + Sea.HEIGHT + 7);
        }
        Bubble.setSeaLevel(seaY + Sea.HEIGHT + 5);
    }

    public void spawnBubble(boolean right) {
        if (tutorialOn) {
            tutorialScreen.spawnBubble(right);
            return;
        }
        for (Tube tube : right ? rightTubes : leftTubes) {
            if (MathUtils.randomBoolean(bubbleProb)) {
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

    // This function will return true if it has spawned a tube, and false if it spawns a crab or nothing
    private boolean triggerSpawn(boolean isRight) {
        crabProb = (float) ((1- lessCrabsLevel *0.1f)*((hardcoreOn?((score<280)?score:280):(score<750)?score:750)*0.004f + 0.5f) * Math.pow(0.61f+difficulty*0.08,(isRight?rightCrabs.size():leftCrabs.size())));
        tubeProb = (float) (0.7f * Math.pow(0.7f,rightTubes.size() + leftTubes.size()))*(1+ moreTubesLevel *0.1f);

        if (crabProb>tubeProb) {
            if (MathUtils.randomBoolean(crabProb)) {
                createCrab(isRight);
                return false;
            } else  if (MathUtils.randomBoolean(tubeProb*0.6f)) {
                createTube(isRight);
                return true;
            }
        } else if (MathUtils.randomBoolean(tubeProb)) {
            createTube(isRight);
            return true;
        } else if (MathUtils.randomBoolean(crabProb*0.6f)) {
            createCrab(isRight);
            return false;
        }
        return false;
    }

    Tube createTube(boolean isRight) {
        Tube tube;
        if (tubeCementery.size() == 0) {
            tube = new Tube(isRight ? GAME_WIDTH - TUBE_WIDTH - WALL_WIDTH + 5 : WALL_WIDTH - 5, GAME_HEIGHT, isRight, this);

            if (isRight) {
                rightTubes.add(tube);
            } else {
                leftTubes.add(tube);
            }
        } else {
            tube = tubeCementery.get(0);
            tube.reset(isRight ? GAME_WIDTH - TUBE_WIDTH - WALL_WIDTH + 5 : WALL_WIDTH - 5, GAME_HEIGHT, isRight, this);

            if (isRight) {
                rightTubes.add(tube);
            } else {
                leftTubes.add(tube);
            }

            tubeCementery.remove(0);
        }
        return tube;
    }

    Crab createCrab(boolean isRight) {
        Crab crab;
        if (crabCementery.size() == 0) {
            crab = new Crab(isRight ? GAME_WIDTH - CRAB_WIDTH - WALL_WIDTH + 2 : WALL_WIDTH - 2, GAME_HEIGHT, isRight, isPurpulTime, player, this);

            if (isRight) {
                rightCrabs.add(crab);
            } else {
                leftCrabs.add(crab);
            }
        } else {
            crab = crabCementery.get(0);
            crab.reset(isRight ? GAME_WIDTH - CRAB_WIDTH - WALL_WIDTH + 2 : WALL_WIDTH - 2, GAME_HEIGHT, isRight, isPurpulTime, player, this);
            if (isRight) {
                rightCrabs.add(crab);
            } else {
                leftCrabs.add(crab);
            }
            crabCementery.remove(0);
        }
        return crab;
    }


    public boolean checkCrabCollisions(boolean isRight) {
        if (isRight) {
            for (Crab crab : rightCrabs) {
                if (crab.isAlive() && crab.getCollisionRect().overlaps(player.getCollisionRect())) {
                    if (shieldPwuActive) {
                        shieldHit();
                    }
                    return true;
                }
            }
        } else {
            for (Crab crab : leftCrabs) {
                if (crab.isAlive() && crab.getCollisionRect().overlaps(player.getCollisionRect())) {
                    if (shieldPwuActive) {
                        shieldHit();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkTubeCollisions(boolean isRight) {
        if (isRight) {
            for (Tube tube : rightTubes) {
                if (tube.getCollisionRect().overlaps(player.getCollisionRect())) {
                    if (!tube.isPushed()) {
                        pointUp(3*(1+ fasterScoreLevel *0.25f));
                        if (shieldPwuActive) {
                            shieldPwuTimer += 0.5;
                        }
                    }
                    tube.push();
                    return true;
                }
            }
        } else {
            for (Tube tube : leftTubes) {
                if (tube.getCollisionRect().overlaps(player.getCollisionRect())) {
                    if (!tube.isPushed()) {
                        pointUp(3*(1+ fasterScoreLevel *0.25f));
                        if (shieldPwuActive) {
                            shieldPwuTimer += 2f;
                            player.setInmune(true, shieldPwuTimer);
                        }
                    }
                    tube.push();
                    return true;
                }
            }
        }
        return false;
    }

    public void playerInWater(boolean inWater) {
        isInWater = inWater;
        if (inWater) {
            sea.changeFrontAlpha(0.7f);
            /*if (helperActive) {
                if (player.getCollisionRect().overlaps(helper.getBoundingRectangle())) {
                    helper.die();
                }
            }*/
        } else {
            sea.changeFrontAlpha(1);
        }
    }

    void removeCrabs() {
        dummySprite = godDummySprite;
        for (Crab crab: rightCrabs) {
            if (crab.shouldRemove()) {
                dummySprite = crab;
                crabCementery.add((Crab) dummySprite);
                break;
            }
        }
        rightCrabs.remove(dummySprite);

        dummySprite = godDummySprite;
        for (Crab crab : leftCrabs) {
            if (crab.shouldRemove()) {
                dummySprite = crab;
                crabCementery.add((Crab) dummySprite);
                break;
            }
        }
        leftCrabs.remove(dummySprite);
    }

    void removeTubes() {
        dummySprite = godDummySprite;
        for (Tube tube: rightTubes) {
            if (tube.shouldRemove()) {
                dummySprite = tube;
                tubeCementery.add((Tube) dummySprite);
                break;
            }
        }
        rightTubes.remove(dummySprite);

        dummySprite = godDummySprite;
        for (Tube tube : leftTubes) {
            if (tube.shouldRemove()) {
                dummySprite = tube;
                tubeCementery.add((Tube) dummySprite);
                break;
            }
        }
        leftTubes.remove(dummySprite);
    }

    void removeBubbles() {
        dumbBubble = godBubble;
        for (Bubble bubble : bubbles) {
            if (bubble.shouldRemove()) {
                dumbBubble = bubble;
                bubbleCementery.add(bubble);
                break;
            }
        }
        bubbles.remove(dumbBubble);
        bubblesToRemove--;
    }

    private void removeMissiles() {
        dummySprite = godDummySprite;
        for (Missile missile : missiles) {
            if (missile.shouldRemove()) {
                dummySprite = missile;
                missileCementery.add(missile);
                break;
            }
        }
        missiles.remove(dummySprite);
        if (missiles.size() == 0) {
            activeMissiles = false;
        }
    }

    public void playerBounced(boolean isRight) {
        if (isPurpulTime) {
            for (int i = 0; i<(isRight?leftCrabs.size():rightCrabs.size()); i++) {
                if (MathUtils.randomBoolean(slashProbability)) {
                    //
                    (isRight?leftCrabs:rightCrabs).get(i).setReadyStrike(1.1f);
                    i++;
                }
            }
        }
    }

    private void activateBubblePwu() {
        bubblePwuActive = true;
        bubblePwuTimer = 5 + 1.5f*bubblePwuLevel;
    }

    private void activateShieldPwu() {
        shieldPwuActive = true;
        shieldPwuTimer = 8 + 2*shieldPwuLevel;
        player.setInmune(true, shieldPwuTimer);
        bubbleShield.setAlpha(1);
    }

    private void shieldHit() {
        if (shieldHitTimer <= 0) {
            shieldPwuTimer -= 5f;
            shieldHitTimer = 0.14f;
            bubbleShield.setColor(new Color(1,0.1f,0.1f,1));
            if (!soundOff) {
                shieldHitSound.play();
            }
        }
    }

    private void activatePoseidonPwu() {
        poseidonPwuActive = true;
        isRising = true;
        isInWater = false;
        player.activatePoseidon(true);
        poseidonBeam.setX(player.getX()-20);
        sea.changeFrontAlpha(1);
        seaSpeed = 0;
        climbSpeed = 0;
        Crab.setClimbSpeed(0);
        Tube.setSpeed(0);
        Wall.speed = 0;
        if (!soundOff) {
            waveSound.play();
        }
    }

    private void deactivatePoseidonPwu() {
        poseidonPwuActive = false;
        player.activatePoseidon(false);
        climbSpeedAdd(0);
        seaY = 30;
        sea.setY(30);
    }

    private void activateMissilePwu() {
        missilePwuActive = true;
        missilePwuOutRunTimer = 3;
    }

    private void deactivateMissilePwu() {
        missilePwuActive = false;
        missilePwuButton.deactivate();
    }

    public void missileExploded(Missile missile, boolean isRight) {
        for (Crab crab: isRight?rightCrabs:leftCrabs) {
            if (crab.getBoundingRectangle().overlaps(missile.getBoundingRectangle())) {
                crab.kill();
            }
        }
    }

    public void climbSpeedAdd(float yVeloc) {
        climbSpeed = normalClimbSpeed - Math.abs(yVeloc);

        Crab.setClimbSpeed(climbSpeed);
        Tube.setSpeed(climbSpeed);
        Wall.speed = climbSpeed;

        if (tutorialOn) {
            TutorialScreen.climbSpeed = climbSpeed;
        }
    }

    public void activateHelper() {
        helperActive = true;
    }

    Player getPlayer() {
        return player;
    }

    Wall getLeftWall() {
        return leftWall;
    }

    Wall getRightWall() {
        return rightWall;
    }

    Sea getSea() {
        return sea;
    }

    ArrayList<Crab> getLeftCrabs() {
        return leftCrabs;
    }

    ArrayList<Crab> getRightCrabs() {
        return rightCrabs;
    }

    ArrayList<Tube> getLeftTubes() {
        return leftTubes;
    }

    ArrayList<Tube> getRightTubes() {
        return rightTubes;
    }

    ArrayList<Bubble> getBubbles() {
        return bubbles;
    }

    ArrayList<Bubble> getBubbleCementery() {
        return bubbleCementery;
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

    public void setTutorialOn(boolean state) { tutorialOn = state; }

    public void setTutorialScreen(TutorialScreen tutorialScreen) {
        this.tutorialScreen = tutorialScreen;
    }

    public void setRemoveCrabs(boolean removeCrabs) {
        this.removeCrabs = removeCrabs;
    }

    public void setRemoveTubes(boolean removeTubes) {
        this.removeTubes = removeTubes;
    }

    public void addBubbleToRemove() {
        if (tutorialOn) {
            tutorialScreen.addBubbleToRemove();
        }
        bubblesToRemove++;
    }

    public void setRemoveMissiles(boolean removeMissile) {
        this.removeMissiles = removeMissile;
    }

    public void setIsDead(boolean lifeState) {
        isDead = lifeState;
        missilePwuActive = false;
    }

    public void setIsReviving(boolean lifeState) {
        isReviving = lifeState;
    }

    public void fadeOut() {
        fadingOut = true;
        fade.setColor(fadingColor);
        fade.setEndColor(new Color(fadingColor.r,fadingColor.g,fadingColor.b,1));
        fade.setDuration(FADE_OUT_TIME);
    }

    public void finishedFading() {
        if (!hardcoreOn && !shownVideoAd && adLoaded) {
            final RewardedVideoScreen rewardedVideoScreen = new RewardedVideoScreen(this, "  Play an ad to\n     revive!", 0);
            rewardedVideoScreen.setBlockTimer(2.5f);
            myGame.setScreen(rewardedVideoScreen);
            shownVideoAd = true;
            comingFromReward = true;
        } else {
            milestoneFont.setColor(0.5f, 0.5f, 0.5f, 1);
            isScoreConverting = true;
            Bubble.scoreConverting = true;
            player.setX(-100);
            //conversionFont.setColor(1,1,1,1);
            tapToShootFont.setColor(0.9f, 0.9f, 0.9f, 1);
            Bubble.setSeaLevel(-50);
            bubbleCementery.addAll(bubbles);
            bubbles.clear();
            if (displayScore > highscore) {
                highscore3 = highscore2;
                highscore2 = highscore;
                highscore = displayScore;
                saveHighscores(3, hardcoreOn);
                if (hardcoreOn) {
                    myGame.newHardcoreHighscore(highscore);
                } else {
                    myGame.newHighscore(highscore);
                }
            } else if (displayScore > highscore2) {
                highscore3 = highscore2;
                highscore2 = displayScore;
                saveHighscores(2, hardcoreOn);
            } else if (displayScore > highscore3) {
                highscore3 = displayScore;
                saveHighscores(1, hardcoreOn);
            }

            while (milestones[milestoneReached] < displayScore) {
                milestoneReached++;
                milestoneConversion = milestoneReached;
            }
            if (milestoneReached > 0) {
                milestoneReached--;
                milestoneConversion--;
            }
        }
    }

    private void saveHighscores(int num, boolean hardcore) {
        for (int i = 3; i > 3-num; i--) {
            admin.saveData((hardcore?"hard":"")+"highscore" + (num==1?"":i), highscore3);
        }
        admin.flush();
    }

    private void changeScreen() {

        admin.saveData("coins",coins);
        if (milestoneReached > milestoneLevel) {
            admin.saveData("milestoneLevel", milestoneReached);
        }
        admin.flush();
        myGame.anotherGamePlayed(displayScore);
        if (adLoaded && gameCoins > 29 + difficulty*8) {
            myGame.setScreen(new RewardedVideoScreen(new MenuScreen(false), "You won " + gameCoins +
                    " coins \nWant to double this?", gameCoins));
        } else {
            myGame.setScreen(new MenuScreen(false));
        }
    }

    @Override
    public void changeSoundOff() {
        player.soundOff = soundOff;
        Tube.soundOff = soundOff;
        Crab.soundOff = soundOff;
        Bubble.soundOff = soundOff;
        Missile.soundOff = soundOff;
        PowerupButton.soundOff = soundOff;
    }

    @Override
    public void onRewarded() {
        rewarded = true;
    }

    @Override
    public void touchDown(float x, float y) {
        if (pauseButton.checkIfTouched(x,y)) {

        } else if (missilePwuActive) {
            spawnMissile(x, y);
            deactivateMissilePwu();
        } else if (!(isReviving || isDead || poseidonPwuActive)) {
            if (shieldPwuLevel > 0) {
                if (shieldPwuButton.checkIfTouched(x, y)) {
                    return;
                }
            }
            if (poseidonPwuLevel > 0) {
                if (poseidonPwuButton.checkIfTouched(x, y)) {
                    return;
                }
            }
            if (bubblePwuLevel > 0) {
                if (bubblePwuButton.checkIfTouched(x, y)) {
                    return;
                }
            }
            if (missilePwuLevel > 0) {
                if (missilePwuButton.checkIfTouched(x, y)) {
                    return;
                }
            }
            player.jump();
            touchJump = true;
            longpressTime = 0;
            touchX = x;
            touchY = y;

        }
    }

    private void spawnMissile(float x, float y) {
        if (missileCementery.size() == 0) {
            missiles.add(new Missile(player.getX(), player.getY(), x, y, this));
        } else {
            missiles.add(missileCementery.get(0).reset(player.getX(), player.getY(), x, y, this));
            missileCementery.remove(0);
        }
        activeMissiles = true;
    }

    @Override
    public void touchUp(float x, float y) {
        if (touchJump) {
            touchJump = false;

        } else if (isScoreConverting && displayScore > 0) {
            if (x > 2*GAME_WIDTH/3 && y < 50) {
                for (int i = 0; i <= Math.round(score/30); i++) {
                    if (bubbles.size() < 25) {
                        if (bubbleCementery.size() == 0) {
                            Bubble bubble = new Bubble(GAME_HEIGHT + MathUtils.random(50), false);
                            bubble.makeGolden();
                            bubble.schedulePop(1.3f + MathUtils.random() / 2);
                            bubbles.add(bubble);
                        } else {
                            final Bubble bubble = bubbleCementery.get(0);
                            bubble.reset(GAME_HEIGHT + MathUtils.random(50), false);
                            bubble.schedulePop(1.3f + MathUtils.random() / 2);
                            bubble.makeGolden();
                            bubbles.add(bubble);
                            bubbleCementery.remove(0);
                        }
                    } else {
                        coinUp((Math.round(score/30) - i) * 3);
                        break;
                    }
                }
                score = 0;
                displayScore = 0;
            }
        } else {
            pauseButton.checkIfReleased(x,y);
        }
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
