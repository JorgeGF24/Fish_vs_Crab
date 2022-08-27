package me.jorge.myfirstgame.util;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import me.jorge.myfirstgame.screens.ScreenInputProcessor;

public class Button {

    public static boolean soundOff;
    public static Sound clickSound;

    protected final float BORDER_THICKNESS = 3.5f, ROUND_CORNER_RADIUS = 3;

    protected float x, xDisplacement;
    protected float y, yDisplacement;
    protected float width;
    protected float height;
    protected Rectangle bounds;
    protected Sprite up, down;
    private BitmapFont font;
    protected Color upColor, downColor, backColor;
    private String text;

    protected boolean isDown, isChecked;
    private int style; // 0 for image button, 1 for text button, 2 for square button, 3 for checked button

    public Button(float x, float y, float width, float height, Texture up, Texture down) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.up = new Sprite(up);
        this.down = new Sprite(down);
        this.up.setBounds(x,y,width,height);
        this.down.setBounds(x,y,width,height);

        bounds = new Rectangle(x,y,width,height);
    }

    public Button(float x, float y, BitmapFont font, Color up, Color down, String text) {
        this.x = x;
        this.y = y + height;
        upColor = up;
        downColor = down;
        this.text = text;
        this.font = font;
        this.font.setColor(upColor);

        bounds = new Rectangle(x,y,20,20);

        style = 1;
    }

    Button(float x, float y, float side, Color upColor, Color downColor, Color backColor) {
        this.x = x;
        this.y = y + height;
        width = side;
        this.upColor = upColor;
        this.downColor = downColor;
        this.backColor = backColor;
        bounds = new Rectangle(x,y,side,side);

        style = 2;
    }

    public Button(float x, float y, float width, Texture unChecked, Texture checked) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = width;
        this.up = new Sprite(unChecked);
        this.down = new Sprite(checked);
        this.up.setBounds(x,y,width,height);
        this.down.setBounds(x,y,width,height);

        bounds = new Rectangle(x,y,width,width);

        style = 3;
    }

    public void render(SpriteBatch batch) {
        if (style == 1)
            font.draw(batch,text,x + xDisplacement,y + yDisplacement);
        else if (style == 0) {
            if (isDown) {
                down.draw(batch);
            } else {
                up.draw(batch);
            }
        } else if (style == 3) {
            if (isChecked) {
                down.draw(batch);
            } else {
                up.draw(batch);
            }
        }
    }

    public void render(ShapeRenderer renderer) {
        renderer.setColor(isDown?downColor:upColor);
        renderer.rect(x + xDisplacement+ROUND_CORNER_RADIUS,y + yDisplacement,width - 2*ROUND_CORNER_RADIUS,width);
        renderer.rect(x + xDisplacement,y + yDisplacement+ROUND_CORNER_RADIUS,width,width - 2*ROUND_CORNER_RADIUS);
        renderer.circle(x + xDisplacement+ROUND_CORNER_RADIUS,y + yDisplacement+ROUND_CORNER_RADIUS,ROUND_CORNER_RADIUS);
        renderer.circle(x + xDisplacement+ROUND_CORNER_RADIUS,y + yDisplacement+width-ROUND_CORNER_RADIUS,ROUND_CORNER_RADIUS);
        renderer.circle(x + xDisplacement+width-ROUND_CORNER_RADIUS,y + yDisplacement+ROUND_CORNER_RADIUS,ROUND_CORNER_RADIUS);
        renderer.circle(x + xDisplacement+width-ROUND_CORNER_RADIUS,y + yDisplacement+width-ROUND_CORNER_RADIUS,ROUND_CORNER_RADIUS);

        renderer.setColor(backColor);
        renderer.rect(x + BORDER_THICKNESS + xDisplacement, y + BORDER_THICKNESS + yDisplacement, width - 2*BORDER_THICKNESS, width - 2*BORDER_THICKNESS);
    }

    public void renderDebug(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1,0,0,1);
        renderer.rect(bounds.x,bounds.y,bounds.width,bounds.height);
        renderer.end();
    }

    public void setBounds(float x, float y, float width, float height) {
        bounds.set(x,y,width,height);
    }

    public boolean checkIfTouched(float x, float y) {
        if (bounds.contains(x,y)) {
            isDown = true;
            if (style == 1) {
                font.setColor(downColor);
            }
            return true;
        } else
            return false;
    }

    public void checkIfReleased(float x, float y) {
        if (isDown) {
            isDown = false;
            if (style == 1) {
                font.setColor(upColor);
            }
            if (bounds.contains(x,y)) {
                activate();
                isChecked = !isChecked;
                if (!soundOff) {
                    clickSound.play();
                }
                return;
            }
            isChecked = !isChecked;
        }
    }

    public void activate() {

    }

    public void setAlpha(float newAlpha) {
        up.setAlpha(newAlpha);
        down.setAlpha(newAlpha);
    }

    public void setYDisplacement(float yDisplacement) {
        this.yDisplacement = yDisplacement;
        if (style == 0 || style == 3) {
            up.setY(y + yDisplacement);
            down.setY(y + yDisplacement);
        }
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
        style = 3;
    }

    public void addToXDisplacement(float add) {
        bounds.setX(bounds.getX() + add);
        xDisplacement += add;
        if (style == 0 || style == 3) {
            up.setX(x + xDisplacement);
            down.setX(x + xDisplacement);
        }
    }
}
