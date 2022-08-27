package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Shark extends Sprite {

    private final float HEIGHT = 65, WIDTH = 180;
    private float xVeloc, yVeloc;

    private final float jump = 470;

    public Shark(Texture texture) {
        super(texture);

        setBounds(0,0,WIDTH,HEIGHT);
    }

    public void render(SpriteBatch batch) {
        setPosition(getX() + xVeloc, getY() + yVeloc);
        draw(batch);
    }
}
