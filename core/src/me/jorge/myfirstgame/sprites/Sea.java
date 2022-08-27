package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.jorge.myfirstgame.MyGame;

public class Sea {

    private static final float GAME_WIDTH = MyGame.GAME_WIDTH;

    private static final float FRONT_SPEED = 31f, MIDDLE_SPEED = -15f, BACK_SPEED = 9f;
    public static final float HEIGHT = 45;

    private Sprite front, middle, back;
    private Texture[] textures;

    public Sea(Texture[] textures) {
        this.textures = textures;

        front = new Sprite(textures[0]);
        middle = new Sprite(textures[1]);
        back = new Sprite(textures[2]);

        front.setSize(MyGame.GAME_WIDTH,HEIGHT);
        middle.setSize(MyGame.GAME_WIDTH,HEIGHT);
        back.setSize(me.jorge.myfirstgame.MyGame.GAME_WIDTH,HEIGHT);
    }

    public Sea(Texture[] textures, float y) {
        new Sea(textures);
        front.setY(y);
        middle.setY(y);
        back.setY(y);
    }

    public void renderFront(SpriteBatch batch, float delta) {
        update(front, FRONT_SPEED, delta);

        drawScroll(batch, front);
    }

    public void renderBack(SpriteBatch batch, float delta) {
        update(back, BACK_SPEED, delta);
        update(middle, MIDDLE_SPEED, delta);

        drawScroll(batch, back);
        drawScroll(batch, middle);
    }

    private void drawScroll(SpriteBatch batch, Sprite sprite) {
        sprite.draw(batch);
        if (sprite.getX() < 0) {
            sprite.setX(sprite.getX() + sprite.getWidth());
            sprite.draw(batch);
            sprite.setX(sprite.getX() - sprite.getWidth());
        } else {
            sprite.setX(sprite.getX() - sprite.getWidth());
            sprite.draw(batch);
            sprite.setX(sprite.getX() + sprite.getWidth());
        }
    }

    public void update(Sprite sprite, float speed, float delta) {
        sprite.setX(sprite.getX() + speed*delta);
        if (sprite.getX() > sprite.getWidth()) {
            sprite.setX(sprite.getX() - sprite.getWidth());
        } else if (sprite.getX() < -sprite.getWidth()) {
            sprite.setX(sprite.getX() + sprite.getWidth());
        }
    }

    public void setY(float y) {
        front.setY(y);
        middle.setY(y);
        back.setY(y);
    }

    public void changeFrontAlpha(float alpha) {
        front.setAlpha(alpha);
    }

    public float getFrontAlpha() {
        return front.getColor().a;
    }
}
