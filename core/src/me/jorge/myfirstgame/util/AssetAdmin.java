package me.jorge.myfirstgame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import me.jorge.myfirstgame.MyGame;

public class AssetAdmin {

    private AssetManager manager;
    private Preferences prefs = Gdx.app.getPreferences("My Preferences");

    private static final float SCALE_X = MyGame.SCREEN_WIDTH/ me.jorge.myfirstgame.MyGame.GAME_WIDTH;

    public AssetAdmin(AssetManager manager) {
        this.manager = manager;

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    public void loadLoading() {
        //resources for loading screen should be loaded here
        /*manager.load();
        manager.finishLoading();*/
    }

    public void load() {

        // Big score font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont.fontFileName = "Fonts/Thirteen-Pixel-Fonts-2.ttf";
        myFont.fontParameters.size = 80;
        manager.load("Thirteen-Pixel-Fonts.ttf", BitmapFont.class, myFont);

        // Small score font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont1 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont1.fontFileName = "Fonts/Thirteen-Pixel-Fonts-2.ttf";
        myFont1.fontParameters.size = 60;
        manager.load("Thirteen-Pixel-Fonts60.ttf", BitmapFont.class, myFont1);

        // Mysc (Tap to shoot, tap to start, upgrades button)
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont2 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont2.fontFileName = "Fonts/Coolville.ttf";
        myFont2.fontParameters.size = 30;
        manager.load("Coolville30.ttf", BitmapFont.class, myFont2);

        // Mysc (Tap to shoot, tap to start, upgrades button)
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont3 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont3.fontFileName = "Fonts/Coolville.ttf";
        myFont3.fontParameters.size = 30;
        manager.load("Coolville30b.ttf", BitmapFont.class, myFont3);

        // Mysc smaller font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont4 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont4.fontFileName = "Fonts/Coolville.ttf";
        myFont4.fontParameters.size = 22;
        manager.load("Coolville22.ttf", BitmapFont.class, myFont4);

        // Mysc smaller font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFontC = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFontC.fontFileName = "Fonts/Coolville.ttf";
        myFontC.fontParameters.size = 22;
        manager.load("Coolville22c.ttf", BitmapFont.class, myFontC);

        FreetypeFontLoader.FreeTypeFontLoaderParameter myFontB = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFontB.fontFileName = "Fonts/Coolville.ttf";
        myFontB.fontParameters.size = Math.round(16*SCALE_X);
        manager.load("ScaledCoolville22.ttf", BitmapFont.class, myFontB);

        // Coins font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont5 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont5.fontFileName = "Fonts/Khula-Light.ttf";
        myFont5.fontParameters.size = 13;
        manager.load("Khula10.ttf", BitmapFont.class, myFont5);

        // Max level font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont7 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont7.fontFileName = "Fonts/AcariSans-Light.ttf";
        myFont7.fontParameters.size = Math.round(11*SCALE_X);
        manager.load("AcariLight.ttf", BitmapFont.class, myFont7);

        // Level font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont8 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont8.fontFileName = "Fonts/AcariSans-Italic.ttf";
        myFont8.fontParameters.size = Math.round(11*SCALE_X);
        manager.load("AcariItalic11.ttf", BitmapFont.class, myFont8);

        // Description font
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont8b = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont8b.fontFileName = "Fonts/AcariSans-Italic.ttf";
        myFont8b.fontParameters.size = Math.round(9*SCALE_X);
        manager.load("AcariItalic9.ttf", BitmapFont.class, myFont8b);

        manager.load("Fish/fish.png", Texture.class);
        manager.load("Fish/happyfish3.png", Texture.class);
        manager.load("Fish/fishflap.png", Texture.class);
        manager.load("Fish/happyfishflap3.png", Texture.class);
        manager.load("Fish/deadfish.png", Texture.class);
        manager.load("Fish/staring3.png", Texture.class);
        manager.load("Fish/poseidonfish.png", Texture.class);
        manager.load("Fish/fishpurpul.png", Texture.class);
        manager.load("Fish/happyfishpurpul.png", Texture.class);
        manager.load("Fish/fishflappurpul.png", Texture.class);
        manager.load("Fish/happyfishflappurpul.png", Texture.class);
        manager.load("Fish/deadfishpurpul.png", Texture.class);
        manager.load("Fish/staringpurpul.png", Texture.class);

        manager.load("Crab/purpulcrab1.png", Texture.class);
        manager.load("Crab/purpulcrab1blink.png", Texture.class);
        manager.load("Crab/purpulcrab2.png", Texture.class);
        manager.load("Crab/purpulcrab2blink.png", Texture.class);
        manager.load("Crab/crabreadystrike.png", Texture.class);
        manager.load("Crab/crabstrike.png", Texture.class);
        manager.load("Crab/deadcrab.png", Texture.class);

        manager.load("Background assets/rock5.png", Texture.class);
        manager.load("Background assets/hardwall.png", Texture.class);
        manager.load("Background assets/seafront6.png", Texture.class);
        manager.load("Background assets/seamiddle6.png", Texture.class);
        manager.load("Background assets/seaback6.png", Texture.class);
        manager.load("Background assets/background.png", Texture.class);
        manager.load("Background assets/cloud.png", Texture.class);
        manager.load("Background assets/cloud2.png", Texture.class);
        manager.load("Background assets/moonshine5.png", Texture.class);

        manager.load("Mysc/tubopixel2.png", Texture.class);
        manager.load("Mysc/orangetube.png", Texture.class);
        manager.load("Mysc/airball.png", Texture.class);
        manager.load("Mysc/airballpop1.png", Texture.class);
        manager.load("Mysc/airballpop2.png", Texture.class);
        manager.load("Mysc/airballpop3.png", Texture.class);
        manager.load("Mysc/airballpop4.png", Texture.class);
        manager.load("Mysc/goldball.png", Texture.class);
        manager.load("Mysc/exclamation.png", Texture.class);
        manager.load("Mysc/arrow.png", Texture.class);
        manager.load("Mysc/emptyheart.png", Texture.class);
        manager.load("Mysc/halfheart.png", Texture.class);
        manager.load("Mysc/fullheart.png", Texture.class);
        manager.load("Mysc/volume2On.png", Texture.class);
        manager.load("Mysc/volume2Off.png", Texture.class);
        manager.load("Mysc/sidearrowup.png", Texture.class);
        manager.load("Mysc/sidearrowdown.png", Texture.class);
        manager.load("Mysc/homebuttonup.png", Texture.class);
        manager.load("Mysc/homebuttondown.png", Texture.class);
        manager.load("Mysc/adbuttonup.png", Texture.class);
        manager.load("Mysc/adbuttondown.png", Texture.class);
        manager.load("Mysc/pausebuttonup.png", Texture.class);
        manager.load("Mysc/pausebuttondown.png", Texture.class);
        manager.load("Mysc/podiumup.png", Texture.class);
        manager.load("Mysc/podiumdown.png", Texture.class);
        manager.load("Mysc/pencilbuttonup.png", Texture.class);
        manager.load("Mysc/pencilbuttondown.png", Texture.class);

        manager.load("Upgrades/upgradecrabs3.png", Texture.class);
        manager.load("Upgrades/upgradetubes2.png", Texture.class);
        manager.load("Upgrades/upgradebubbles2.png", Texture.class);
        manager.load("Upgrades/upgradescore2.png", Texture.class);
        manager.load("Upgrades/upgradebetterswimmer.png", Texture.class);
        manager.load("Upgrades/upgradelazycrabs.png", Texture.class);
        manager.load("Upgrades/upgradestrongertubes.png", Texture.class);
        manager.load("Upgrades/upgradehardcore.png", Texture.class);
        manager.load("Upgrades/upgradeholy.png", Texture.class);
        manager.load("Upgrades/upgradenightvision2.png", Texture.class);
        manager.load("Upgrades/upgradehelper2.png", Texture.class);
        manager.load("Upgrades/upgradegoldify2.png", Texture.class);
        manager.load("Upgrades/upgradebubblepwu.png", Texture.class);
        manager.load("Upgrades/upgradeshieldpwu.png", Texture.class);
        manager.load("Upgrades/upgradeposeidonpwu.png", Texture.class);
        manager.load("Upgrades/upgrademissiles1pwu.png", Texture.class);
        manager.load("Upgrades/upgrademissiles2pwu.png", Texture.class);
        manager.load("Upgrades/upgrademissiles3pwu.png", Texture.class);

        manager.load("PWU assets/holyloop.png", Texture.class);
        manager.load("PWU assets/holyloopblue.png", Texture.class);
        manager.load("PWU assets/holyloopgreen.png", Texture.class);
        manager.load("PWU assets/holyloopred.png", Texture.class);
        manager.load("PWU assets/holylooppurpul.png", Texture.class);
        manager.load("PWU assets/holybeam.png", Texture.class);
        manager.load("PWU assets/helpermain.png", Texture.class);
        manager.load("PWU assets/helperflap.png", Texture.class);
        manager.load("PWU assets/shield.png", Texture.class);
        manager.load("PWU assets/poseidonbeam.png", Texture.class);
        manager.load("PWU assets/missile.png", Texture.class);
        manager.load("PWU assets/explosionstrip.png", Texture.class);
        manager.load("PWU assets/crosshair2.png", Texture.class);

        manager.load("PWU buttons/pwububble1.png", Texture.class);
        manager.load("PWU buttons/pwububble2.png", Texture.class);
        manager.load("PWU buttons/pwushield1.png", Texture.class);
        manager.load("PWU buttons/pwushield2.png", Texture.class);
        manager.load("PWU buttons/pwuposeidon1.png", Texture.class);
        manager.load("PWU buttons/pwuposeidon2.png", Texture.class);
        manager.load("PWU buttons/pwumissiles0.png", Texture.class);
        manager.load("PWU buttons/pwumissiles0b.png", Texture.class);
        manager.load("PWU buttons/pwumissiles1.png", Texture.class);
        manager.load("PWU buttons/pwumissiles2.png", Texture.class);
        manager.load("PWU buttons/pwumissiles3.png", Texture.class);
        manager.load("PWU buttons/pwumissiles4.png", Texture.class);
        manager.load("PWU buttons/pwumissiles5.png", Texture.class);
        manager.load("PWU buttons/pwumissiles6.png", Texture.class);

        for (int i = 1; i < 8; i++) {
            manager.load("Daily rewards/day"+i+"reward.png", Texture.class);
        }
        manager.load("Daily rewards/rewardtick2.png", Texture.class);
        manager.load("Daily rewards/openedchest.png", Texture.class);
        manager.load("Daily rewards/closedchest.png", Texture.class);

        manager.load("Sound/bubblepop.wav", Sound.class);
        manager.load("Sound/coin.wav", Sound.class);
        manager.load("Sound/explosion.wav", Sound.class);
        manager.load("Sound/jump.wav", Sound.class);
        manager.load("Sound/playerdie.wav", Sound.class);
        manager.load("Sound/holy sound.wav", Sound.class);
        manager.load("Sound/errorsound.wav", Sound.class);
        manager.load("Sound/glassbreak.wav", Sound.class);
        manager.load("Sound/tap.wav", Sound.class);
        manager.load("Sound/button.mp3", Sound.class);
        manager.load("Sound/slash.mp3", Sound.class);
        manager.load("Sound/splash.wav", Sound.class);
        manager.load("Sound/wave.wav", Sound.class);
        manager.load("Sound/bago'coins.mp3", Sound.class);
    }

    public boolean update() {
        return manager.update();
    }

    public <T> T getAsset(String asset) {
        switch (asset) {
            case "fish":
                return  manager.get("Fish/fish.png");
            case "happyfish":
                return  manager.get("Fish/happyfish3.png");
            case "fishflap":
                return  manager.get("Fish/fishflap.png");
            case "happyfishflap":
                return  manager.get("Fish/happyfishflap3.png");
            case "staring":
                return  manager.get("Fish/staring3.png");
            case "deadfish":
                return  manager.get("Fish/deadfish.png");
            case "poseidonfish":
                return  manager.get("Fish/poseidonfish.png");
            case "fishpurpul":
                return  manager.get("Fish/fishpurpul.png");
            case "happyfishpurpul":
                return  manager.get("Fish/happyfishpurpul.png");
            case "fishflappurpul":
                return  manager.get("Fish/fishflappurpul.png");
            case "happyfishflappurpul":
                return  manager.get("Fish/happyfishflappurpul.png");
            case "staringpurpul":
                return  manager.get("Fish/staringpurpul.png");
            case "deadfishpurpul":
                return  manager.get("Fish/deadfishpurpul.png");

            case "crab":
                return  manager.get("Crab/crab1.png");
            case "crab2":
                return  manager.get("Crab/crab2.png");
            case "crabblink":
                return  manager.get("Crab/crab1blink.png");
            case "crab2blink":
                return  manager.get("Crab/crab2blink.png");
            case "purpulcrab":
                return  manager.get("Crab/purpulcrab1.png");
            case "purpulcrab2":
                return  manager.get("Crab/purpulcrab2.png");
            case "purpulcrab2blink":
                return  manager.get("Crab/purpulcrab2blink.png");
            case "purpulcrabblink":
                return  manager.get("Crab/purpulcrab1blink.png");
            case "crabreadystrike":
                return  manager.get("Crab/crabreadystrike.png");
            case "crabstrike":
                return  manager.get("Crab/crabstrike.png");
            case "deadcrab":
                return manager.get("Crab/deadcrab.png");

            case "rock":
                return  manager.get("Background assets/rock5.png");
            case "hardrock":
                return  manager.get("Background assets/hardwall.png");
            case "seafront":
                return  manager.get("Background assets/seafront6.png");
            case "seamiddle":
                return  manager.get("Background assets/seamiddle6.png");
            case "seaback":
                return  manager.get("Background assets/seaback6.png");
            case "background":
                return  manager.get("Background assets/background.png");
            case "cloud":
                return  manager.get("Background assets/cloud.png");
            case "cloud2":
                return  manager.get("Background assets/cloud2.png");
            case "moonshine":
                return  manager.get("Background assets/moonshine5.png");

            case "tube":
                return  manager.get("Mysc/tubopixel2.png");
            case "orangetube":
                return  manager.get("Mysc/orangetube.png");
            case "airball":
                return  manager.get("Mysc/airball.png");
            case "goldball":
                return  manager.get("Mysc/goldball.png");
            case "bubble4score":
                if (getData("Goldify")>0)
                    return manager.get("Mysc/goldball.png");
                else
                    return  manager.get("Mysc/airball.png");
            case "airballpop1":
                return  manager.get("Mysc/airballpop1.png");
            case "airballpop2":
                return  manager.get("Mysc/airballpop2.png");
            case "airballpop3":
                return  manager.get("Mysc/airballpop3.png");
            case "airballpop4":
                return  manager.get("Mysc/airballpop4.png");
            case "!":
                return  manager.get("Mysc/exclamation.png");
            case "loadingbar":
                return  manager.get("Mysc/loadingbar.png");
            case "loadingjuice":
                return  manager.get("Mysc/loadingjuice.png");
            case "arrow":
                return  manager.get("Mysc/arrow.png");
            case "emptyheart":
                return  manager.get("Mysc/emptyheart.png");
            case "halfheart":
                return  manager.get("Mysc/halfheart.png");
            case "fullheart":
                return  manager.get("Mysc/fullheart.png");
            case "volumeOn":
                return  manager.get("Mysc/volume2On.png");
            case "volumeOff":
                return  manager.get("Mysc/volume2Off.png");
            case "sidearrowup":
                return  manager.get("Mysc/sidearrowup.png");
            case "sidearrowdown":
                return  manager.get("Mysc/sidearrowdown.png");
            case "homebuttonup":
                return  manager.get("Mysc/homebuttonup.png");
            case "adbuttondown":
                return  manager.get("Mysc/adbuttondown.png");
            case "adbuttonup":
                return  manager.get("Mysc/adbuttonup.png");
            case "homebuttondown":
                return  manager.get("Mysc/homebuttondown.png");
            case "pausebuttonup":
                return  manager.get("Mysc/pausebuttonup.png");
            case "pausebuttondown":
                return  manager.get("Mysc/pausebuttondown.png");
            case "podiumbuttonup":
                return  manager.get("Mysc/podiumup.png");
            case "podiumbuttondown":
                return  manager.get("Mysc/podiumdown.png");
            case "pencilbuttonup":
                return  manager.get("Mysc/pencilbuttonup.png");
            case "pencilbuttondown":
                return  manager.get("Mysc/pencilbuttondown.png");

            case "upgradecrabs":
                return  manager.get("Upgrades/upgradecrabs3.png");
            case "upgradetubes":
                return  manager.get("Upgrades/upgradetubes2.png");
            case "upgradebubbles":
                return  manager.get("Upgrades/upgradebubbles2.png");
            case "upgradescore":
                return  manager.get("Upgrades/upgradescore2.png");
            case "upgradeswimmer":
                return  manager.get("Upgrades/upgradebetterswimmer.png");
            case "upgradeslash":
                return  manager.get("Upgrades/upgradelazycrabs.png");
            case "upgradehardcore":
                return  manager.get("Upgrades/upgradehardcore.png");
            case "upgradestrongertubes":
                return  manager.get("Upgrades/upgradestrongertubes.png");
            case "upgradeholy":
                return  manager.get("Upgrades/upgradeholy.png");
            case "upgradehelper":
                return  manager.get("Upgrades/upgradehelper2.png");
            case "upgradenightvision":
                return  manager.get("Upgrades/upgradenightvision2.png");
            case "upgradegoldify":
                return  manager.get("Upgrades/upgradegoldify2.png");
            case "upgradebubblepwu":
                return  manager.get("Upgrades/upgradebubblepwu.png");
            case "upgradeshieldpwu":
                return  manager.get("Upgrades/upgradeshieldpwu.png");
            case "upgradeposeidonpwu":
                return  manager.get("Upgrades/upgradeposeidonpwu.png");
            case "upgrademissilespwu":
                if (getData("Fish lord") < 3) {
                    return manager.get("Upgrades/upgrademissiles1pwu.png");
                } else if (getData("Fish lord") < 5) {
                    return manager.get("Upgrades/upgrademissiles2pwu.png");
                } else {
                    return manager.get("Upgrades/upgrademissiles3pwu.png");
                }

            case "holyloop":
                return  manager.get("PWU assets/holyloop.png");
            case "holyloopblue":
                return  manager.get("PWU assets/holyloopblue.png");
            case "holyloopgreen":
                return  manager.get("PWU assets/holyloopgreen.png");
            case "holyloopred":
                return  manager.get("PWU assets/holyloopred.png");
            case "holylooppurpul":
                return  manager.get("PWU assets/holylooppurpul.png");
            case "holybeam":
                return  manager.get("PWU assets/holybeam.png");
            case "helpermain":
                return  manager.get("PWU assets/helpermain.png");
            case "helperflap":
                return  manager.get("PWU assets/helperflap.png");
            case "shield":
                return  manager.get("PWU assets/shield.png");
            case "poseidonbeam":
                return  manager.get("PWU assets/poseidonbeam.png");
            case "missile":
                return  manager.get("PWU assets/missile.png");
            case "explosion":
                return manager.get("PWU assets/explosionstrip.png");
            case "crosshair":
                return manager.get("PWU assets/crosshair2.png");

            case "pwububble1":
                return  manager.get("PWU buttons/pwububble1.png");
            case "pwububble2":
                return  manager.get("PWU buttons/pwububble2.png");
            case "pwushield1":
                return  manager.get("PWU buttons/pwushield1.png");
            case "pwushield2":
                return  manager.get("PWU buttons/pwushield2.png");
            case "pwuposeidon1":
                return  manager.get("PWU buttons/pwuposeidon1.png");
            case "pwuposeidon2":
                return  manager.get("PWU buttons/pwuposeidon2.png");
            case "pwumissiles0":
                return  manager.get("PWU buttons/pwumissiles0.png");
            case "pwumissiles0b":
                return  manager.get("PWU buttons/pwumissiles0b.png");
            case "pwumissiles1":
                return  manager.get("PWU buttons/pwumissiles1.png");
            case "pwumissiles2":
                return  manager.get("PWU buttons/pwumissiles2.png");
            case "pwumissiles3":
                return  manager.get("PWU buttons/pwumissiles3.png");
            case "pwumissiles4":
                return  manager.get("PWU buttons/pwumissiles4.png");
            case "pwumissiles5":
                return  manager.get("PWU buttons/pwumissiles5.png");
            case "pwumissiles6":
                return  manager.get("PWU buttons/pwumissiles6.png");

            case "day1reward":
                return manager.get("Daily rewards/day1reward.png");
            case "day2reward":
                return manager.get("Daily rewards/day2reward.png");
            case "day3reward":
                return manager.get("Daily rewards/day3reward.png");
            case "day4reward":
                return manager.get("Daily rewards/day4reward.png");
            case "day5reward":
                return manager.get("Daily rewards/day5reward.png");
            case "day6reward":
                return manager.get("Daily rewards/day6reward.png");
            case "day7reward":
                return manager.get("Daily rewards/day7reward.png");
            case "tick":
                return manager.get("Daily rewards/rewardtick2.png");
            case "openedchest":
                return manager.get("Daily rewards/openedchest.png");
            case "closedchest":
                return manager.get("Daily rewards/closedchest.png");

            case "bubble pop":
                return  manager.get("Sound/bubblepop.wav");
            case "coin":
                return  manager.get("Sound/coin.wav");
            case "buy sound":
                return  manager.get("Sound/bago'coins.mp3");
            case "error sound":
                return  manager.get("Sound/errorsound.wav");
            case "explosion sound":
                return  manager.get("Sound/explosion.wav");
            case "jump sound":
                return  manager.get("Sound/jump.wav");
            case "slash":
                return  manager.get("Sound/slash.mp3");
            case "splash":
                return  manager.get("Sound/splash.wav");
            case "shield hit":
                return  manager.get("Sound/glassbreak.wav");
            case "wave":
                return  manager.get("Sound/wave.wav");
            case "tap":
                return  manager.get("Sound/tap.wav");
            case "button sound":
                return  manager.get("Sound/button.mp3");
            case "sound of death":
                return  manager.get("Sound/playerdie.wav");
            case "sound of life":
                return  manager.get("Sound/holy sound.wav");

            case "canonfont80":
                return  manager.get("Thirteen-Pixel-Fonts.ttf");
            case "canonfont60":
                return  manager.get("Thirteen-Pixel-Fonts60.ttf");
            case "coolville30":
                return  manager.get("Coolville30.ttf");
            case "coolville30b":
                return  manager.get("Coolville30b.ttf");
            case "coolville15":
                return  manager.get("Coolville22.ttf");
            case "coolville15c":
                return  manager.get("Coolville22c.ttf");
            case "coolville15b":
                return  manager.get("ScaledCoolville22.ttf");
            case "khula10":
                return  manager.get("Khula10.ttf");
            case "AcariItalic11":
                return  manager.get("AcariItalic11.ttf");
            case "AcariItalic9":
                return  manager.get("AcariItalic9.ttf");
            case "AcariLight":
                return  manager.get("AcariLight.ttf");
            default:
                return null;
        }
    }

    public int getData(String key) {
        return prefs.getInteger(key);
    }

    public long getLong(String key) {
        return prefs.getLong(key);
    }

    public AssetManager getManager() { return manager; }

    public float getProgress() { return manager.getProgress(); }

    public String getString(String key) {
        return prefs.getString(key);
    }

    public void saveData(String key, int data) {
        prefs.putInteger(key,data);
    }

    public void saveData(String key, long data) {
        prefs.putLong(key,data);
    }

    public void saveData(String key, String data) {
        prefs.putString(key,data);
    }

    public void flush() {
        prefs.flush();
    }

    public void dispose() {
        manager.dispose();
    }
}
