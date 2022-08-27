package me.jorge.myfirstgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

import me.jorge.myfirstgame.util.AssetAdmin;
import me.jorge.myfirstgame.util.Button;
import me.jorge.myfirstgame.MyGame;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;

public class NameSetScreen extends ScreenInputProcessor implements Input.TextInputListener {

    private final ScreenInputProcessor menu;

    private static final float SCALE_X = MyGame.SCREEN_WIDTH/ MyGame.GAME_WIDTH;
    private static final float SCALE_Y = MyGame.SCREEN_HEIGHT/ me.jorge.myfirstgame.MyGame.GAME_HEIGHT;

    private final BitmapFont textFont, userFont, errorFont;
    private String username;
    private final ArrayList<Character> usernameChars = new ArrayList<>();
    private final me.jorge.myfirstgame.util.Button submitButton;
    private boolean internetProblem, usernameUnavailable, keyboardShown, changeScreen;

    private final Color BEIGE_CANVAS = new Color(0.95f,0.7f,0.4f,0.6f);
    private static final float ROUNDNESS_RADIUS = 13, SIDE_BORDER = 18, TOP_BORDER = 180, BAR_HEIGHT = 20, BAR_SIDE_MARGIN = 10, BAR_ROUNDNESS = 5;

    NameSetScreen(ScreenInputProcessor menu) {

        this.menu = menu;

        textFont = admin.getAsset("coolville15c");
        textFont.setColor(0.1f,0.1f,0.1f,1);
        userFont = admin.getAsset("coolville15");
        errorFont = admin.getAsset("AcariItalic9");
        errorFont.setColor(0.1f,0.1f,0.1f,1);

        if (menu instanceof MenuScreen) {
            ((MenuScreen) menu).hideHighscore();
        }

        submitButton = new Button(GAME_WIDTH/2-25, TOP_BORDER + 33, userFont,new Color(0.1f,0.1f,0.1f,1),
                new Color(0.3f,0.3f,0.3f,1),"submit") {
            @Override
            public void activate() {
                System.out.println("submitting through button");
                submit();
            }
        };
        submitButton.setBounds(GAME_WIDTH/2-22, TOP_BORDER + 12, 90, 26);

        userFont.setColor(0.3f,0.3f,0.3f,1);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        menu.render(delta);

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


        shapeRenderer.setColor(BEIGE_CANVAS.r +0.2f,BEIGE_CANVAS.g +0.2f,BEIGE_CANVAS.b +0.2f,1);
        shapeRenderer.rect(SIDE_BORDER + BAR_SIDE_MARGIN, (GAME_HEIGHT-BAR_HEIGHT)/2 + BAR_ROUNDNESS, GAME_WIDTH-2* (BAR_SIDE_MARGIN+SIDE_BORDER), BAR_HEIGHT-2*BAR_ROUNDNESS);
        shapeRenderer.rect(SIDE_BORDER + BAR_SIDE_MARGIN + BAR_ROUNDNESS, (GAME_HEIGHT-BAR_HEIGHT)/2, GAME_WIDTH-2* (BAR_SIDE_MARGIN+SIDE_BORDER+BAR_ROUNDNESS),BAR_HEIGHT);
        shapeRenderer.circle(SIDE_BORDER + BAR_SIDE_MARGIN + BAR_ROUNDNESS, (GAME_HEIGHT-BAR_HEIGHT)/2 + BAR_ROUNDNESS,BAR_ROUNDNESS);
        shapeRenderer.circle(GAME_WIDTH - (SIDE_BORDER + BAR_SIDE_MARGIN + BAR_ROUNDNESS), (GAME_HEIGHT-BAR_HEIGHT)/2 + BAR_ROUNDNESS,BAR_ROUNDNESS);
        shapeRenderer.circle(SIDE_BORDER + BAR_SIDE_MARGIN + BAR_ROUNDNESS, (GAME_HEIGHT+BAR_HEIGHT)/2 - BAR_ROUNDNESS,BAR_ROUNDNESS);
        shapeRenderer.circle(GAME_WIDTH - (SIDE_BORDER + BAR_SIDE_MARGIN + BAR_ROUNDNESS), (GAME_HEIGHT+BAR_HEIGHT)/2 - BAR_ROUNDNESS,BAR_ROUNDNESS);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        batch.begin();
        if (username != null) {
            submitButton.render(batch);
        }
        textFont.draw(batch, "Hello! Choose your\n     username.", SIDE_BORDER + 20, GAME_HEIGHT-TOP_BORDER - 8);
        if (username == null) {
            userFont.draw(batch, "Type here...", SIDE_BORDER + BAR_SIDE_MARGIN, (GAME_HEIGHT + BAR_HEIGHT) / 2 - 5, GAME_WIDTH - 2 * (SIDE_BORDER + BAR_SIDE_MARGIN), Align.center, false);
        } else {
            userFont.draw(batch, username, SIDE_BORDER + BAR_SIDE_MARGIN, (GAME_HEIGHT + BAR_HEIGHT) / 2 - 5, GAME_WIDTH - 2 * (SIDE_BORDER + BAR_SIDE_MARGIN), Align.center, false);
        }
        batch.end();
        fontBatch.begin();
        if (internetProblem) {
            errorFont.draw(fontBatch, "Check your internet connection and try again",SCALE_X*(SIDE_BORDER + 30), SCALE_Y*(TOP_BORDER + 31));
        } else if (usernameUnavailable) {
            errorFont.draw(fontBatch, "Sorry! Username is not available :/",SCALE_X*(SIDE_BORDER + 56), SCALE_Y*(TOP_BORDER + 31));
        }
        fontBatch.end();
    }

    public void keyTyped(char character) {
        System.out.println((int) character);
        if (character == 8 || character == 127) {
            if (username != null) {
                usernameChars.remove(username.length()-1);
                usernameChars.trimToSize();
                if (usernameChars.size()>0) {
                    username = null;
                    for (int i = 0; i < usernameChars.size(); i++) {
                        if (username == null) {
                            username = ""+ usernameChars.get(i);
                        } else {
                            username += usernameChars.get(i);
                        }
                    }
                } else {
                    username = null;
                    userFont.setColor(0.3f,0.3f,0.3f,1);
                }
            }
        } else if (((int)character<58 && (int)character>47) ||((int)character<91 && (int)character>64) || ((int)character<123 && (int)character>96) || (int) character == 46 || (int) character == 95 || (int) character == 31) {
            if (usernameChars.size() < 10) {
                usernameChars.add(character);
                if (username == null) {
                    username = "" + character;
                    userFont.setColor(0.13f, 0.13f, 0.13f, 1);
                } else {
                    username += character;
                }
            }
        } else if ((int)character == 10 && username != null) {
            submit();
        }
    }

    public void usernameAvailableResult(int result) {
        if (result == 1) {
            final AssetAdmin admin = myGame.getAssetAdmin();
            System.out.println("Username after checking availability is " + username);
            admin.saveData("longPressOn", 1);
            admin.saveData("userName", username);
            admin.flush();
            if (username != null) {
                myGame.setUserName(username.toUpperCase());
                myGame.setScreen(menu);
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
        } else if (result == 0) {
            usernameUnavailable = true;
            username = null;
            usernameChars.clear();
        } else {
            internetProblem = true;
            username = null;
            usernameChars.clear();
        }
    }

    private void submit() {
        usernameUnavailable = false;
        internetProblem = false;
        if (username.equals(myGame.getAssetAdmin().getString("userName"))) {
            myGame.setScreen(menu);
            Gdx.input.setOnscreenKeyboardVisible(false);
        } if (username != null) {
            myGame.checkIfUsernameAvailable(username.toUpperCase());
        }
    }

    /*public void delete() {
        System.out.println("delete");
        if (username != null) {
            usernameChars.remove(username.length()-1);
            usernameChars.trimToSize();
            System.out.println(username.length());
            System.out.println(usernameChars.size());
            for (int i = 1; i < usernameChars.size(); i++) {
                System.out.println(usernameChars.get(i));
            }
            if (username.length()>0) {
                username = "" + usernameChars.get(0);
                for (int i = 1; i < usernameChars.size(); i++) {
                    username += usernameChars.get(i);
                }
            } else {
                username = null;
                userFont.setColor(0.3f,0.3f,0.3f,1);
            }
        }
    }*/

    @Override
    public void changeSoundOff() {

    }

    @Override
    public void onRewarded() {

    }

    @Override
    public void touchDown(float x, float y) {
        if (y > (GAME_HEIGHT-BAR_HEIGHT)/2 - 10 && y < (GAME_HEIGHT+BAR_HEIGHT)/2 + 10) {
            Gdx.input.setOnscreenKeyboardVisible(true);
            keyboardShown = true;
            usernameUnavailable = false;
            internetProblem = false;
        } else if ((y < TOP_BORDER || y>GAME_HEIGHT-TOP_BORDER)  && menu instanceof RankingScreen) {
            if (!keyboardShown) {
                changeScreen = true;
            }
            submitButton.checkIfTouched(x,y);
        } else {
            submitButton.checkIfTouched(x,y);
        }
    }

    @Override
    public void touchUp(float x, float y) {
        submitButton.checkIfReleased(x,y);

        if (changeScreen) {
            myGame.setScreen(menu);
            Gdx.input.setOnscreenKeyboardVisible(false);
            changeScreen = false;
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

    @Override
    public void input(String text) {

    }

    @Override
    public void canceled() {

    }

    @Override
    public void pause() {
        super.pause();
        Gdx.input.setOnscreenKeyboardVisible(false);
    }
}
