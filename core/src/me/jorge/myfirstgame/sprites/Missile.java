package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.jorge.myfirstgame.screens.GameScreen;
import me.jorge.myfirstgame.MyGame;

public class Missile extends Sprite {

    private me.jorge.myfirstgame.screens.GameScreen game;
    private static final float GAME_WIDTH = MyGame.GAME_WIDTH;
    private static final float GAME_HEIGHT = me.jorge.myfirstgame.MyGame.GAME_HEIGHT;

    private static final float WIDTH = 40, HEIGHT = 20, SPEED = 500;

    private float cruiseXVeloc, cruiseYVeloc, xVeloc, yVeloc, explosionTimer = 0.3f, rotationDegrees,
            startX,startY;
    private boolean remove, exploding, crosshairHidden;
    private double rotationRadians;

    private static Texture explosion, missileTexture, crosshairTexture;
    private static float explosionSize;
    private Sprite crosshair;
    private static final float CROSSHAIR_SIDE = 10;

    public static boolean soundOff;
    public static Sound explosionSound;

    public Missile(float startX, float startY, float targetX, float targetY, me.jorge.myfirstgame.screens.GameScreen game) {
        super(missileTexture);

        this.startX = startX;
        this.startY = startY;

        setSize(WIDTH,HEIGHT);
        setOriginCenter();
        setPosition(startX,startY);
        rotationRadians =  Math.atan((targetY - startY - HEIGHT/2)/(targetX - startX - WIDTH/2));
        rotationDegrees = (float) (360*rotationRadians /(2*Math.PI));
        setRotation(rotationDegrees);
        cruiseXVeloc = (float) (SPEED * Math.cos(rotationRadians));
        cruiseYVeloc = (float) (SPEED * Math.sin(rotationRadians));
        if (targetX < startX+WIDTH/2) {
            flip(true, false);
            cruiseXVeloc *= -1;
            cruiseYVeloc *= -1;
        }

        crosshair = new Sprite(crosshairTexture);
        crosshair.setBounds(targetX - CROSSHAIR_SIDE/2, targetY - CROSSHAIR_SIDE/2, CROSSHAIR_SIDE, CROSSHAIR_SIDE);
        crosshair.setOriginCenter();
        crosshairHidden = false;

        this.game = game;
    }

    public Missile reset(float startX, float startY, float targetX, float targetY, GameScreen game) {
        setRegion(missileTexture);

        this.startX = startX;
        this.startY = startY;

        setSize(WIDTH,HEIGHT);
        setOriginCenter();
        setPosition(startX,startY);
        rotationRadians =  Math.atan((targetY - startY - HEIGHT/2)/(targetX - startX - WIDTH/2));
        rotationDegrees = (float) (360*rotationRadians /(2*Math.PI));
        setRotation(rotationDegrees);
        cruiseXVeloc = (float) (SPEED * Math.cos(rotationRadians));
        cruiseYVeloc = (float) (SPEED * Math.sin(rotationRadians));
        if (targetX < startX+WIDTH/2) {
            flip(true, false);
            cruiseXVeloc *= -1;
            cruiseYVeloc *= -1;
        }

        crosshair.setPosition(targetX - CROSSHAIR_SIDE/2, targetY - CROSSHAIR_SIDE/2);
        crosshairHidden = false;

        this.game = game;

        remove = false;
        exploding  = false;
        xVeloc = 0;
        yVeloc = 0;
        explosionTimer = 0.3f;

        return this;
    }

    public void update(float delta) {
        if (!exploding) {
            if (Math.abs(xVeloc) < Math.abs(cruiseXVeloc)) {
                xVeloc += 5*cruiseXVeloc * delta;
            }
            if (Math.abs(yVeloc) < Math.abs(cruiseYVeloc)) {
                yVeloc += 5*cruiseYVeloc * delta;
            }
            setPosition(getX() + xVeloc * delta, getY() + yVeloc * delta);

            if (getY() > GAME_HEIGHT || getY() < -WIDTH) {
                remove = true;
                game.setRemoveMissiles(true);
            }
            if ((getX() + WIDTH/2 - Math.abs(WIDTH*Math.cos(rotationRadians)/2) < 15 && xVeloc<0)|| getX() + WIDTH/2 + Math.abs(WIDTH*Math.cos(rotationRadians)/2) > GAME_WIDTH - 15 && xVeloc>0 ) {
                exploding = true;
                crosshairHidden = true;
                setTexture(explosion);
                if (!soundOff) {
                    explosionSound.play(0.5f);
                }
                setRotation(0);
                System.out.println((xVeloc>0)?(GAME_WIDTH-4*explosionSize/5):explosionSize/5);
                setBounds((xVeloc>0)?(GAME_WIDTH-9*explosionSize/10):explosionSize/10,
                        (float) (getY()+ Math.abs(WIDTH*Math.sin(rotationRadians)/2))-explosionSize/2,explosionSize,explosionSize);
                setRegion(0, 0, 50, 50);
                game.missileExploded(this,xVeloc>0);
            }
        } else {
            if ((explosionTimer%(0.5f/7))<(explosionTimer-delta)%(0.5f/7)) {
                explosionTimer -= delta;
                for (int i = 6; i >= 0; i--) {
                    if (explosionTimer < 0.5f-i*0.5f/7) {
                        setRegion(50*i,0,50,50);
                        return;
                    }
                }
            } else {
                explosionTimer -= delta;
            }
            if (explosionTimer < 0) {
                remove = true;
                game.setRemoveMissiles(true);
            }
        }

        if (!crosshairHidden) {
            if (Math.signum(xVeloc) * (getX() + WIDTH / 2) > Math.signum(xVeloc) * (crosshair.getX() + CROSSHAIR_SIDE / 2)) {
                crosshairHidden = true;
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!crosshairHidden) {
            crosshair.draw(batch);
        }
        super.draw(batch);
    }

    public void drawDebug(ShapeRenderer renderer) {
        renderer.rect(getX(),getY(),WIDTH,HEIGHT);
        renderer.rect(crosshair.getX(),crosshair.getY(),CROSSHAIR_SIDE,CROSSHAIR_SIDE);
        renderer.rect(crosshair.getX() + WIDTH/2,crosshair.getY() + HEIGHT/2,CROSSHAIR_SIDE,CROSSHAIR_SIDE);
        renderer.circle(startX,startY, 5);
        renderer.setColor(Color.GREEN);
        renderer.circle(startX + WIDTH/2,startY + HEIGHT/2, 5);
    }

    public static void setTextures(Texture missile, Texture explosion, Texture crosshair, float explosionSize) {
        missileTexture = missile;
        System.out.println("setTextures");
        Missile.explosion = explosion;
        crosshairTexture = crosshair;
        Missile.explosionSize = explosionSize;
    }

    public boolean shouldRemove() {
        return remove;
    }
}
