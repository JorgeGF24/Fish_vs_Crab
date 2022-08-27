package me.jorge.myfirstgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import me.jorge.myfirstgame.MyGame;

public class DesktopLauncher {
	public static void main(String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 4;
		config.height = 675;
		config.width = 380;
		config.resizable = false;
		new LwjglApplication(new MyGame(new DesktopManager()), config);
	}
}
