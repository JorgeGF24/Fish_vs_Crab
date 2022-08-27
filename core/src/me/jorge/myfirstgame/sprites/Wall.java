package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import me.jorge.myfirstgame.MyGame;
import me.jorge.myfirstgame.screens.GameScreen;

public class Wall extends Sprite {

    private final static float HEIGHT = MyGame.GAME_HEIGHT;
    public static float speed;

    public Wall(Texture texture) {
        super(texture);
        setSize(GameScreen.WALL_WIDTH, HEIGHT);
    }

    public void update(float delta) {
        setY(getY() + speed *delta);
    }

    @Override
    public void draw(Batch batch) {
        if (getY() < -HEIGHT) {
            setY(getY() + HEIGHT);
        }

        super.draw(batch);
        setY(getY() + HEIGHT);
        super.draw(batch);
        setY(getY() - HEIGHT);
    }

    public void drawUp(Batch batch) {
        if (getY() < 0) {
            setY(getY() + HEIGHT);
        }

        super.draw(batch);
        setY(getY() - HEIGHT);
        super.draw(batch);
        setY(getY() + HEIGHT);
    }
}
