package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.util.AssetAdmin;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.sprites.Wall;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class RankingScreen extends ScreenInputProcessor {

    //private static boolean isSignedIn;

    private boolean transition;
    private final MenuScreen menu;
    private String userName;
    public static String rankingPosition = "?";
    public static String hardRankingPosition = "?";
    private static int highscore;
    private static int hardHighscore;

    private BitmapFont titleFont;
    public float menuFontAlpha;
    private final BitmapFont textFont;
    public static String[] topNames = {"Unable", "To", "Connect", "to", "the", "internet", "try", "again", "later", ":/", "sorry"};
    public static int[] topScores = {0,0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static String[] hardTopNames = {"Unable", "To", "Connect", "to", "the", "internet", "try", "again", "later", ":/", "sorry"};
    public static int[] hardTopScores = {0,0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final float DRAWING_RECT_HEIGHT = 250;

    private final Color BEIGE_CANVAS = new Color(0.9f,0.9f,0.75f,0.6f);
    private static final float ROUNDNESS_RADIUS = 13, SIDE_BORDER = 18, TOP_BORDER = 18;
    private final Texture background;
    private final Wall rightWall, leftWall;

    private final Sprite sideArrowRight, sideArrowLeft;
    private float rightArrowAlpha = 1, leftArrowAlpha;
    private boolean justFlinged, isPanning;

    private final Button /*signInButton,*/ backButton, nameSetButton;

    private float yDisplacement, yChange, xDisplacement, targetXDisplacement, xDisplacementAdd;

    RankingScreen(MenuScreen menu, Texture landscape, Wall right, Wall left) {
        this.menu = menu;

        final BitmapFont backFont = admin.getAsset("coolville30b");
        backFont.setColor(0.1f,0.1f,0.1f,1);
        textFont = admin.getAsset("coolville15c");
        textFont.setColor(0.2f,0.2f,0.2f, 1);
        titleFont = admin.getAsset("coolville30b");

        highscore = admin.getData("highscore");
        hardHighscore = admin.getData("hardcorehighscore");
        userName = admin.getString("userName");
        myGame.getTopTen();
        myGame.getRankingPosition(highscore, hardHighscore);

        background = landscape;
        rightWall = right;
        leftWall = left;

        sideArrowRight = new Sprite((Texture) admin.getAsset("sidearrowup"));
        sideArrowRight.setBounds(GAME_WIDTH - SIDE_BORDER - 20 - 20, GAME_HEIGHT-TOP_BORDER-30-30,20,30);

        sideArrowLeft = new Sprite((Texture) admin.getAsset("sidearrowup"));
        sideArrowLeft.setBounds(SIDE_BORDER + 20, GAME_HEIGHT-TOP_BORDER-30-30,20,30);
        sideArrowLeft.flip(true,false);

        backButton = new me.jorge.myfirstgame.util.Button(GAME_WIDTH/2-42, TOP_BORDER + 33, backFont ,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.3f,0.3f,0.3f,1),"-back-") {
            @Override
            public void activate() {
                goToMenu();
            }
        };
        backButton.setBounds(GAME_WIDTH/2-45, TOP_BORDER + 12, 90, 26);

        nameSetButton = new Button(GAME_WIDTH/2+82, GAME_HEIGHT -TOP_BORDER - 353, 19, (Texture) admin.getAsset("pencilbuttonup") , (Texture) admin.getAsset("pencilbuttondown")) {
            @Override
            public void activate() {
                goToNameSetScreen();
            }
        };
        nameSetButton.setBounds(GAME_WIDTH/2+79, GAME_HEIGHT -TOP_BORDER - 356, 26, 26);

        //isSignedIn = (admin.getData("signed in")==1);
        /*signInButton = new Button(GAME_WIDTH/2-42, GAME_HEIGHT/2, backFont ,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.3f,0.3f,0.3f,1), "sign in") {
            @Override
            public void activate() {
                signInManager.signIn();
            }
        };
        signInButton.setBounds(GAME_WIDTH/2-42, GAME_HEIGHT/2 - 20, 90, 26);*/
    }

    private void goToNameSetScreen() {
        myGame.setScreen(new NameSetScreen(this));
    }

    @Override
    public void show() {
        transition = false;
        titleFont.setColor(0.1f,0.1f,0.1f, 1);

        userName = admin.getString("userName");
        System.out.println("Ranking show username is " + userName);
    }

    @Override
    public void render(float delta) {

        if (!isPanning && xDisplacement != targetXDisplacement) {
            xDisplacementAdd = 5*delta*(targetXDisplacement-xDisplacement);
            nameSetButton.addToXDisplacement(xDisplacementAdd);
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
            } else if (targetXDisplacement == 0) {
                leftArrowAlpha = 0;
            }
        }

        if (!transition) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.15f,0.65f,0.9f,1);
            shapeRenderer.rect(0, 0,GAME_WIDTH,GAME_HEIGHT);

            shapeRenderer.setColor(0.929f,0.827f,0.087f,1);
            shapeRenderer.circle(GAME_WIDTH/2, GAME_HEIGHT/2 + 30,67);
            shapeRenderer.end();

            batch.begin();
            batch.draw(background,0,0, GAME_WIDTH, 100);
            batch.end();

        }

        Gdx.gl.glEnable(GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(BEIGE_CANVAS.r,BEIGE_CANVAS.g,BEIGE_CANVAS.b,1);
        shapeRenderer.rect(SIDE_BORDER, TOP_BORDER +ROUNDNESS_RADIUS + yDisplacement , GAME_WIDTH-2* SIDE_BORDER, GAME_HEIGHT-2*(TOP_BORDER +ROUNDNESS_RADIUS));
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER + yDisplacement, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.rect(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS + yDisplacement, GAME_WIDTH-2*(SIDE_BORDER +ROUNDNESS_RADIUS),ROUNDNESS_RADIUS);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,180,90);
        shapeRenderer.arc(SIDE_BORDER +ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,90,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, GAME_HEIGHT- TOP_BORDER -ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,0,90);
        shapeRenderer.arc(GAME_WIDTH- SIDE_BORDER -ROUNDNESS_RADIUS, TOP_BORDER +ROUNDNESS_RADIUS + yDisplacement,ROUNDNESS_RADIUS,-90,90);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        batch.enableBlending();
        batch.begin();
        sideArrowRight.draw(batch,rightArrowAlpha);
        sideArrowLeft.draw(batch,leftArrowAlpha);

        backButton.render(batch);
        nameSetButton.render(batch);

        titleFont.draw(batch, "TOP 10", 100 + xDisplacement, GAME_HEIGHT-28- TOP_BORDER + yDisplacement);
        titleFont.draw(batch, "Hardcore", 90 + GAME_WIDTH + xDisplacement, GAME_HEIGHT-28- TOP_BORDER + yDisplacement);
        titleFont.draw(batch, "Your position", 35 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 337 + yDisplacement);
        titleFont.draw(batch, "Your position", 35 + GAME_WIDTH + xDisplacement, GAME_HEIGHT -TOP_BORDER - 337 + yDisplacement);
        for (int i = 0; i < 10; i++) {
            textFont.draw(batch,(1+i) + ". " + topNames[i], SIDE_BORDER + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 72 - DRAWING_RECT_HEIGHT/10*i + yDisplacement);
            textFont.draw(batch,""+topScores[i], SIDE_BORDER + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 72 - DRAWING_RECT_HEIGHT/10*i + yDisplacement, GAME_WIDTH - 2*SIDE_BORDER -20, Align.right,false);

            textFont.draw(batch,(1+i) + ". " + hardTopNames[i], SIDE_BORDER + GAME_WIDTH + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 72 - DRAWING_RECT_HEIGHT/10*i + yDisplacement);
            textFont.draw(batch,""+ hardTopScores[i], SIDE_BORDER + GAME_WIDTH + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 72 - DRAWING_RECT_HEIGHT/10*i + yDisplacement, GAME_WIDTH - 2*SIDE_BORDER -20, Align.right,false);
        }

        textFont.draw(batch,rankingPosition + ". " + userName, SIDE_BORDER + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 367 + yDisplacement);
        textFont.draw(batch, ""+highscore, SIDE_BORDER + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 367 + yDisplacement, GAME_WIDTH - 2*SIDE_BORDER -20, Align.right,false);

        textFont.draw(batch,hardRankingPosition + ". " + userName, SIDE_BORDER + GAME_WIDTH + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 367 + yDisplacement);
        textFont.draw(batch, ""+hardHighscore, SIDE_BORDER + GAME_WIDTH + 10 + xDisplacement, GAME_HEIGHT -TOP_BORDER - 367 + yDisplacement, GAME_WIDTH - 2*SIDE_BORDER -20, Align.right,false);

        /*if (!isSignedIn) {
            signInButton.render(batch);
        }*/

        batch.end();

        if (!transition) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.15f,0.65f,0.9f,1);
            shapeRenderer.rect(0,80,SIDE_BORDER,GAME_HEIGHT-80);
            shapeRenderer.rect(GAME_WIDTH-SIDE_BORDER,80,SIDE_BORDER,GAME_HEIGHT-80);
            shapeRenderer.end();
            batch.begin();
            leftWall.draw(batch);
            rightWall.draw(batch);
            batch.end();
        }
    }

    private void goToMenu() {
        myGame.setScreen(menu);
        menu.comeFromRanking();
    }

    void transition() {
        transition = true;
    }

    void setYDisplacement(float add) {
        yChange = yDisplacement - add - GAME_HEIGHT;
        yDisplacement = add + GAME_HEIGHT;
        backButton.setYDisplacement(add + GAME_HEIGHT);
        nameSetButton.setYDisplacement(add + GAME_HEIGHT);
        //signInButton.setYDisplacement(add + GAME_HEIGHT);
        sideArrowLeft.translateY(-yChange);
        sideArrowRight.translateY(-yChange);
    }

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {

    }

    @Override
    public void touchDown(float x, float y) {
        backButton.checkIfTouched(x, y);
        nameSetButton.checkIfTouched(x,y);
        /*if (!isSignedIn) {
            signInButton.checkIfTouched(x, y);
        }*/
        System.out.println(sideArrowLeft.getY());
    }

    @Override
    public void touchUp(float x, float y) {
        backButton.checkIfReleased(x,y);
        nameSetButton.checkIfReleased(x,y);
        /*if (!isSignedIn) {
            signInButton.checkIfReleased(x, y);
        }*/
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
        if (Math.abs(targetXDisplacement - xDisplacement) < 10) {
            goToMenu();
        }
    }

    @Override
    public void flingDown(float startX, float startY) {
    }

    @Override
    public void flingRight(float startX, float startY) {
        if (transition) {
            return;
        }
        if (targetXDisplacement < 0) {
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
        xDisplacement += 1.5f*deltaX;
        nameSetButton.addToXDisplacement(1.5f*deltaX);
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
            }
        }
        justFlinged = false;
    }
}
