package me.jorge.myfirstgame.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PowerupButton extends Button {

    private float cooldown, cooldownTimer;
    private boolean active, chargero;
    private int maxCharge = 1, charges = 1;

    private Texture[] chargesTexture;

    public PowerupButton(float x, float y, float width, Texture available, Texture active) {
        super(x, y, width, width, available, active);
    }

    public void setUp(float cooldown) {
        this.cooldown = cooldown;
        cooldownTimer = cooldown;
    }

    public void setUp(float cooldown, int maxCharge, Texture[] chargesTextures) {
        this.cooldown = cooldown;
        cooldownTimer = cooldown;

        this.maxCharge = maxCharge;

        this.chargesTexture = chargesTextures;

        chargero = true;
    }

    public void update(float delta) {
        if (charges < maxCharge && !active) {
            cooldownTimer -= delta;
            if (cooldownTimer < 0) {
                charges++;
                cooldownTimer = cooldown;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!chargero || charges == 0) {
            super.render(batch);
        } else {
            batch.draw(active?chargesTexture[(charges-1)*2 + 1]:chargesTexture[(charges-1)*2], x + xDisplacement, y + yDisplacement, width, height);
        }
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (charges < maxCharge && !active) {
            renderer.setColor(1,1,1,0.5f);
            renderer.arc(x + width/2, y + width/2, width/2-1,90,360*cooldownTimer/cooldown);
        }
    }

    public void deactivate() {
        active = false;
        isDown = false;
        cooldownTimer = cooldown;
    }

    @Override
    public void checkIfReleased(float x, float y) {
    }

    @Override
    public boolean checkIfTouched(float x, float y) {
        if (super.checkIfTouched(x,y)) {
            if (charges>0) {
                charges--;
                active = true;
                activate();
            }
            isDown = active;
            return true;
        }
        return false;
    }

    public void touched() {
        isDown = true;
        if (charges>0) {
            charges--;
            active = true;
            activate();
        }
        isDown = active;
    }

    public int getCharges() {
        return charges;
    }

    public void removeCharge() {
        charges--;
    }
}
