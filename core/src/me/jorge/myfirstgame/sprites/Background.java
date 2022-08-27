package me.jorge.myfirstgame.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.jorge.myfirstgame.MyGame;

public class Background {

    private final static float GAME_HEIGHT = MyGame.GAME_HEIGHT, GAME_WIDTH = me.jorge.myfirstgame.MyGame.GAME_WIDTH;

    /*  ----------STATE 0 (SUNSET)-----------
    The sky is composed by three rectangles. Top rectangle is color sky. 1st rectangle is underneath
    top rectangle. The top color of this 1st rectangle is sky and the bottom color will change as described
    below. This will create a gradient in the rectangle. The 2nd rectangle is just underneath the first
    rectangle. Its top color is the same as the bottom color of 1st rectangle (color1). Its bottom color
    is color2 and will also change to create a gradient.

    2nd rectangle goes from y=0 to y=y2. 1st rectangle goes from y=y2 to y=y1. Top rectangle goes from
    y=y2 to top.

    PHASE 0: 1st rectangle at half height. Color fade from sky to yellow.
    PHASE 1: 2nd rect fade from yellow to red and increase height from 0. 1st rect decrease height.
    PHASE 2: 2nd rect fade from red to orange. 1st rect fade from yellow to sky.
    PHASE 3: 2nd rect fade from orange to sky.

    -----------STATE 1 (MOON CYCLE)------------
    This state will represent a full cicle of the moon.

    PHASE 4: The mooncircle is drawn. On top draw a covercircle that passes through top and bottom points
    of the mooncircle. Its radius will grow until we can approximate circle with line.
    PHASE 5: Use code from covercircle but reflectd on the line x = GAME_WIDTH/2 and color moon. Now
    start at full radius and decrease. On top draw cover that siumulates circle hole in sky by approximating
    triangles.
    PHASE 6:  Reflect PHASE 5 on the line x = GAME_WIDTH/2 and invert time (start at low radius and increase)
    PHASE 7: Reflect PHASE 4 on the line x = GAME_WIDTH/2 and invert time (start at big radius and decrease)

    -----------STATE 2 (SUNRISE)----------------
    */

    private Color daySky = new Color (0.15f,0.65f,0.9f,1);
    private Color nightSky = new Color (0.062f,0.142f,0.306f,1);
    private Color sky = new Color(daySky);
    private int phase, state;


    private float phasePercentage, statePercentage, score;
    private float y1 = GAME_HEIGHT/2  + 30, y2 = 30;
    private Color yellow = new Color(0.84f,0.82f,0.07f,1),orange = new Color(0.84f,0.44f,0.16f,1),
            red = new Color(0.833f,0.22f,0.16f,1), orangeSun = new Color(0.929f,0.401f,0.025f,1),
            yellowSun = new Color(0.929f,0.827f,0.087f,1), moonColor = new Color(0.88f,0.88f,0.46f,1),
            pink = new Color(0.89f,0.03f,0.3f,1);
    private Color color1 = new Color(daySky), color2 = new Color(daySky);

    private float[] moonCoverVertices;
    private float moonAuxCircleX = GAME_WIDTH/2, moonAuxCircleRadius;
    private boolean approxSquare;
    public final static float SUN_RADIUS = 67;

    private Texture landscape, cloud;
    private Sprite cloud2;
    public static final float CLOUD_HEIGHT = 35, CLOUD_WIDTH = SUN_RADIUS*2+20;
    private float landscapeY, targetSunY = GAME_HEIGHT/2  + 30, sunY = targetSunY, sunSpeed = 3;
    private final float LANDSCAPE_HEIGHT = 100;

    public Background (Texture landscape, Texture cloud, Texture cloud2) {
        this.landscape = landscape;

        moonCoverVertices = new float[26];
        moonCoverVertices[0] = GAME_WIDTH;
        moonCoverVertices[1] = GAME_HEIGHT + 30;
        moonCoverVertices[2] = GAME_WIDTH;
        moonCoverVertices[3] = 30;
        double approxAngle = 2*Math.PI/(moonCoverVertices.length - 6);
        for (int i = 0; 5+2*i< moonCoverVertices.length; i++) {
            moonCoverVertices[4+2*i] = GAME_WIDTH/2-(float) (SUN_RADIUS*Math.cos(Math.PI/2 + approxAngle*i));
            moonCoverVertices[5+2*i] = GAME_HEIGHT/2-(float) (SUN_RADIUS*Math.sin(Math.PI/2 + approxAngle*i)) + 30;
        }

        /*double approxAngle = 2*Math.PI/(sunCoverVertices.length - 6);
        for (int i = 0; 5+2*i<sunCoverVertices.length;i++) {
            sunCoverVertices[4+2*i] = GAME_WIDTH/2-(float) (SUN_RADIUS*Math.cos(approxAngle*i));
            sunCoverVertices[5+2*i] = GAME_HEIGHT/2-(float) (SUN_RADIUS*Math.sin(approxAngle*i));
        }
        sunCover = new Polygon(sunCoverVertices);*/

        this.cloud = cloud;
        this.cloud2 = new Sprite(cloud2);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, float delta) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        switch (state) {
            case 0:
            case 2:
                drawSun(shapeRenderer, delta);
                break;
            case 1:
                drawMoon(shapeRenderer);

        }

        shapeRenderer.end();

        batch.begin();
        batch.draw(cloud, (GAME_WIDTH-CLOUD_WIDTH)/2, GAME_HEIGHT/2 -SUN_RADIUS + 30, CLOUD_WIDTH, CLOUD_HEIGHT);
        batch.draw(cloud2, (GAME_WIDTH)/2 - SUN_RADIUS/0.8f, GAME_HEIGHT/2 - SUN_RADIUS/1.5f + 30, CLOUD_WIDTH/3, CLOUD_HEIGHT/2);
        batch.draw(landscape,0,50, GAME_WIDTH, LANDSCAPE_HEIGHT);
        batch.end();
    }

    private void drawSun(ShapeRenderer shapeRenderer,float delta) {
        shapeRenderer.rect(0,3*LANDSCAPE_HEIGHT/4 +y2,GAME_WIDTH,y1-y2,color1,color1,sky,sky);
        shapeRenderer.setColor(sky);
        shapeRenderer.rect(0,3*LANDSCAPE_HEIGHT/4 + y1,GAME_WIDTH,GAME_HEIGHT-y1);
        shapeRenderer.rect(0,3*LANDSCAPE_HEIGHT/4,GAME_WIDTH,y2,color2,color2,color1,color1);

        if (state==0?(statePercentage < 0.43):(statePercentage > 0.67)) {
            shapeRenderer.setColor(yellowSun);
        } else {
            shapeRenderer.setColor(mixColors(yellowSun,orangeSun,state==0?(statePercentage*250 - 107.5f)/142.5f:((1-statePercentage)*250 - 82.5f)/82.5f));
        }

        if (targetSunY > sunY) {
            sunY += sunSpeed*delta*(targetSunY - sunY);
        } else {
            sunY -= sunSpeed*delta*(sunY - targetSunY);
        }
        shapeRenderer.circle(GAME_WIDTH/2, sunY,67);
    }

    private void drawMoon(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(sky);
        shapeRenderer.rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        if (approxSquare && phase<6) {
            shapeRenderer.setColor(moonColor);
            shapeRenderer.circle(GAME_WIDTH / 2, GAME_HEIGHT / 2 + 30, 67);
            shapeRenderer.setColor(sky);
            shapeRenderer.rect(GAME_WIDTH / 2 - SUN_RADIUS, GAME_HEIGHT / 2 - SUN_RADIUS + 30, SUN_RADIUS, 2 * SUN_RADIUS);
        } else if (approxSquare) {
            shapeRenderer.setColor(moonColor);
            shapeRenderer.circle(GAME_WIDTH / 2, GAME_HEIGHT / 2 + 30, 67);
            shapeRenderer.setColor(sky);
            shapeRenderer.rect(GAME_WIDTH / 2, GAME_HEIGHT / 2 - SUN_RADIUS + 30, SUN_RADIUS, 2 * SUN_RADIUS);
        } else {
            switch (phase) {
                case 4:
                case 7:
                    shapeRenderer.setColor(moonColor);
                    shapeRenderer.circle(GAME_WIDTH / 2, GAME_HEIGHT / 2 + 30, 67);
                    shapeRenderer.setColor(sky);
                    shapeRenderer.circle(moonAuxCircleX, GAME_HEIGHT / 2 + 30, moonAuxCircleRadius);
                    break;
                case 5:
                case 6:
                    shapeRenderer.setColor(moonColor);
                    shapeRenderer.circle(moonAuxCircleX, GAME_HEIGHT / 2 + 30, moonAuxCircleRadius);

                    // Triangle covering
                    shapeRenderer.setColor(sky);
                    for (int i = 1; 2*i+1 < moonCoverVertices.length/2; i++) {
                        shapeRenderer.triangle(moonCoverVertices[2], moonCoverVertices[3], moonCoverVertices[2*i+2], moonCoverVertices[2*i+3], moonCoverVertices[2*i+4], moonCoverVertices[2*i+5]);
                    }
                    for (int i = 0; 2*i+3 + moonCoverVertices.length/2 < moonCoverVertices.length; i++) {
                        shapeRenderer.triangle(moonCoverVertices[0], moonCoverVertices[1], moonCoverVertices[1+ moonCoverVertices.length/2 + 2*i], moonCoverVertices[moonCoverVertices.length/2+2*i+2], moonCoverVertices[2*i+ moonCoverVertices.length/2+3], moonCoverVertices[2*i+ moonCoverVertices.length/2+4]);
                    }
                    shapeRenderer.triangle(moonCoverVertices[0], moonCoverVertices[1], moonCoverVertices[2], moonCoverVertices[3], moonCoverVertices[moonCoverVertices.length/2+1], moonCoverVertices[moonCoverVertices.length/2+2]);
                    shapeRenderer.triangle(moonCoverVertices[2], moonCoverVertices[3],GAME_WIDTH/2,0, moonCoverVertices[4], moonCoverVertices[5]);
                    shapeRenderer.triangle(moonCoverVertices[0], moonCoverVertices[1],GAME_WIDTH/2,GAME_HEIGHT, moonCoverVertices[moonCoverVertices.length-2], moonCoverVertices[moonCoverVertices.length-1]);
                    break;
            }
        }
    }

    public void updateSky(float score) {
        score %= 750;
        statePercentage = (score%250)/250;
        /*System.out.println("State and phase is " + state+ " " + phase);
        System.out.println("phase% and state percentage " + phasePercentage + " " + statePercentage);
        System.out.println("y1 is " + y1 + " and y2 is " + y2);
        System.out.println("score " + score + " and this.score " + this.score);*/

        switch (state) {
            case 0:
                targetSunY = GAME_HEIGHT / 2 - 4 * SUN_RADIUS * statePercentage + 30;
                sky.set((1 - statePercentage) * daySky.r + statePercentage * nightSky.r, (1 - statePercentage) * daySky.g
                        + statePercentage * nightSky.g, (1 - statePercentage) * daySky.b + statePercentage * nightSky.b, 1);
                /*System.out.println("sky " + sky + " and this.score " + this.score);
                System.out.println("daySky " + daySky + " and nightSky " + nightSky);*/
                break;
            case 1:
                break;
            case 2:
                if (this.score > score) {
                    phase = 0;
                    state = 0;
                    statePercentage = 0;
                    phasePercentage = 0;
                    y1 = 30;
                    color2 = new Color(daySky);
                    color1 = new Color(daySky);
                    break;
                }
                targetSunY = GAME_HEIGHT / 2 - 4 * SUN_RADIUS * (1-statePercentage) + 30;
                    //pseudoStatePercentage = (statePercentage*250 - 100)/150;
                    sky.set(statePercentage * daySky.r + (1 - statePercentage) * nightSky.r, statePercentage * daySky.g
                            + (1 - statePercentage) * nightSky.g, statePercentage * daySky.b + (1 - statePercentage) * nightSky.b, 1);

                break;
        }

        switch (phase) {
            case 0:
                if (score > 149) {
                    phase = 1;
                    break;
                }
                phasePercentage = score/149;
                color1.set(mixColors(sky,yellow,phasePercentage));
                break;
            case 1:
                if (score > 199) {
                    y1 = (3f/7)*GAME_HEIGHT + 30;
                    y2 = (1.5f/7)*GAME_HEIGHT + 30;
                    phase = 2;
                    break;
                }
                phasePercentage = (score-149)/50;
                //color1.set((1-phasePercentage)*color1.r + phasePercentage*o.r,(1-phasePercentage)*color1.g + phasePercentage*yellow.g,(1-phasePercentage)*color1.b + phasePercentage*yellow.b,1);
                color2.set(mixColors(yellow,red,phasePercentage));
                y1 = GAME_HEIGHT/2 - (0.5f/7)*GAME_HEIGHT*phasePercentage + 30;
                y2 = (1.5f/7)*GAME_HEIGHT*phasePercentage + 30;
                break;
            case 2:
                if (score>224) {
                    phase = 3;
                    break;
                }
                phasePercentage = (score - 199) / 25;
                color2.set(mixColors(red,orange,phasePercentage));
                color1.set(mixColors(yellow,sky,phasePercentage));

                break;
                //y1 = (3f/7)*GAME_HEIGHT*(1-phasePercentage);
                //y2 = (1.5f/7)*GAME_HEIGHT*(1-phasePercentage);
            case 3:
                if (score>249) {
                    phase = 4;
                    sky = new Color(nightSky);
                    state = 1;
                    moonAuxCircleX = GAME_WIDTH/2;
                    approxSquare = false;
                    moonAuxCircleRadius =SUN_RADIUS;
                    statePercentage = 0;
                    break;
                }
                phasePercentage = (score - 224) / 25;
                color2.set(mixColors(orange,sky,phasePercentage));
                break;
            case 4:
                //              g                  -d                  -f                       h
                // GAME_WIDTH/2 + SUN_RADIUS - moonAuxCircleX - moonAuxCircleRadius = SUN_RADIUS*phasePercentage
                //       a                 b / +- c                  d               e                   f
                // (GAME_WIDTH/2, GAME_HEIGHT/2 +- SUN_RADIUS), (auxMoonCircleX,GAME_HEIGHT/2), moonAuxCircleRadius
                // sqrt((a-d)^2 + (b-e)^2) = sqrt((a-d)^2+(c-e)^2) = f
                // a - sqrt(f^2 - (b-e)^2) = d

                // d = g - f - h = g - sqrt((a-d)^2+(c-e)^2) - h
                // d^2 + g^2 + h^2 - 2dg + 2 dh - 2 gh = a^2 - 2ad + d^2 + (c-e)^2
                // d (2a - 2g + 2h) = a^2 + (c-e)^2 - g^2 - h^2 + 2gh
                // d = (a^2 + (c-e)^2 - g^2 - h^2 + 2gh) / (2a - 2g + 2h)

                if (score>311.5) {
                    approxSquare = true;
                    phase = 5;
                    break;
                }
                phasePercentage = (score - 250) / 62.5f;
                if (phasePercentage < 0.99f) {
                    moonAuxCircleX = (GAME_WIDTH + phasePercentage * SUN_RADIUS * phasePercentage
                            - 2 * phasePercentage * (GAME_WIDTH / 2 + SUN_RADIUS)) / (4 - 2 * (1+phasePercentage));
                    moonAuxCircleRadius = (float) Math.sqrt((GAME_WIDTH / 2 - moonAuxCircleX) * (GAME_WIDTH / 2 - moonAuxCircleX)
                            + SUN_RADIUS * SUN_RADIUS);
                } else {
                    approxSquare = true;
                }
                break;
            case 5:
                if (score>375) {
                    phase = 6;
                    for (int i = 0; i<moonCoverVertices.length/2;i++) {
                        moonCoverVertices[2*i] = GAME_WIDTH - moonCoverVertices[2*i];
                    }
                    break;
                }
                phasePercentage = (score - 312.5f) / 62.5f;
                if (phasePercentage > 0.01f) {
                    approxSquare = false;
                    moonAuxCircleX = GAME_WIDTH-(GAME_WIDTH + (1-phasePercentage) * SUN_RADIUS * (1-phasePercentage)
                            - 2 * (1-phasePercentage) * (GAME_WIDTH / 2 + SUN_RADIUS)) / (4 - 2 * (2-phasePercentage));
                    moonAuxCircleRadius = (float) Math.sqrt((GAME_WIDTH / 2 - moonAuxCircleX) * (GAME_WIDTH / 2 - moonAuxCircleX)
                            + SUN_RADIUS * SUN_RADIUS);
                }
                break;
            case 6:
                if (score>436.5f) {
                    phase = 7;
                    approxSquare = true;
                    for (int i = 0; i<moonCoverVertices.length/2;i++) {
                        moonCoverVertices[2*i] = GAME_WIDTH - moonCoverVertices[2*i];
                    }
                    break;
                }
                phasePercentage = (score - 375) / 62.5f;
                if (phasePercentage < 0.99f) {
                    moonAuxCircleX = (GAME_WIDTH + phasePercentage * SUN_RADIUS * phasePercentage
                            - 2 * phasePercentage * (GAME_WIDTH / 2 + SUN_RADIUS)) / (4 - 2 * (1+phasePercentage));
                    moonAuxCircleRadius = (float) Math.sqrt((GAME_WIDTH / 2 - moonAuxCircleX) * (GAME_WIDTH / 2 - moonAuxCircleX)
                            + SUN_RADIUS * SUN_RADIUS);
                } else {
                    approxSquare = true;
                }
                break;
            case 7:
                if (score>499) {
                    phase = 8;
                    state = 2;
                    statePercentage = 0;
                    phasePercentage = 0;
                    targetSunY = GAME_HEIGHT / 2 - 4 * SUN_RADIUS + 30;
                    break;
                }
                phasePercentage = (score - 437.5f) / 62.5f;
                if (phasePercentage > 0.01f) {
                    approxSquare = false;
                    moonAuxCircleX = GAME_WIDTH-(GAME_WIDTH + (1-phasePercentage) * SUN_RADIUS * (1-phasePercentage)
                            - 2 * (1-phasePercentage) * (GAME_WIDTH / 2 + SUN_RADIUS)) / (4 - 2 * (2-phasePercentage));
                    moonAuxCircleRadius = (float) Math.sqrt((GAME_WIDTH / 2 - moonAuxCircleX) * (GAME_WIDTH / 2 - moonAuxCircleX)
                            + SUN_RADIUS * SUN_RADIUS);
                }
                break;

            case 8:
                if (score>524){
                    phase = 9;
                    break;
                }
                phasePercentage = (score - 500) / 25;
                color2.set(mixColors(sky,orange,phasePercentage));
                break;
            case 9:
                if (score>569) {
                    phase = 10;
                    break;
                }
                phasePercentage = (score-525)/45;
                color2.set(mixColors(orange,red,phasePercentage));
                color1.set(mixColors(sky,yellow,phasePercentage));
                break;
            case 10:
                if (score> 604) {
                    phase = 11;
                    y1 = (2f/7)*GAME_HEIGHT + 30;
                    y2 = (1f/7)*GAME_HEIGHT + 30;
                    color1 = new Color(orange);
                    color2 = new Color(pink);
                    break;
                }
                phasePercentage = (score-570)/35;
                color2.set(mixColors(red,pink,phasePercentage));
                color1.set(mixColors(yellow,orange,phasePercentage));
                y1 = (3f/7)*GAME_HEIGHT - (1f/7)*GAME_HEIGHT*phasePercentage + 30;
                y2 = (1.5f/7)*GAME_HEIGHT- (0.5f/7)*GAME_HEIGHT*phasePercentage + 30;
                break;
            case 11:
                if (score >649) {
                    phase = 12;
                    y2 =  30;
                    break;
                }
                phasePercentage = (score-605)/45;

                color2.set(mixColors(pink,orange,phasePercentage));
                color1.set(mixColors(orange,mixColors(yellow,sky,0.5f),phasePercentage));
                y2 = (1f/7)*GAME_HEIGHT- (1f/7)*GAME_HEIGHT*phasePercentage + 30;
                break;
            case 12:
                phasePercentage = (score - 650)/100;

                y1 = (2f/7)*GAME_HEIGHT + (1.5f/7)*GAME_HEIGHT*phasePercentage + 30;
                color1.set(mixColors(mixColors(yellow,sky,0.5f),sky,phasePercentage));
                break;
            default:
                break;
        }
        this.score = score;
    }

    private Color mixColors(Color colorA, Color colorB, float proportion) {
        return new Color((1-proportion)*colorA.r + proportion*colorB.r,(1-proportion)*colorA.g + proportion*colorB.g,(1-proportion)*colorA.b + proportion*colorB.b,(1-proportion)*colorA.a + proportion*colorB.a);
    }
}
