package me.jorge.myfirstgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.lang.reflect.Array;
import java.util.Calendar;

import me.jorge.myfirstgame.screens.LoadingScreen;
import me.jorge.myfirstgame.screens.NameSetScreen;
import me.jorge.myfirstgame.screens.RewardedVideoScreen;
import me.jorge.myfirstgame.screens.ScreenInputProcessor;
import me.jorge.myfirstgame.util.manager;
import me.jorge.myfirstgame.util.AssetAdmin;
import me.jorge.myfirstgame.util.InputHandler;
import me.jorge.myfirstgame.util.RankingManager;

public class MyGame extends Game {

	private static SpriteBatch batch;
	private static SpriteBatch fontBatch;
	private static ShapeRenderer shapeRenderer;
	private static InputHandler inputHandler;
	private static manager manager;

	private static AssetAdmin assetAdmin;

	public static float SCREEN_WIDTH;
	public static float SCREEN_HEIGHT;
	public static final float GAME_WIDTH = 272;
	public static float GAME_HEIGHT;
	private static int sessionGamesPlayed, sessionTotalScore;

	private static final Calendar calendar= Calendar.getInstance();


	public MyGame(manager manager) {
		MyGame.manager = manager;
	}
	
	@Override
	public void create () {

		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		GAME_HEIGHT = SCREEN_HEIGHT * GAME_WIDTH/ SCREEN_WIDTH;

		OrthographicCamera cam = new OrthographicCamera();
		cam.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

		batch = new SpriteBatch();
		fontBatch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);

		AssetManager manager = new AssetManager();
        assetAdmin = new AssetAdmin(manager);
        InputHandler.DirectionListener directionListener = new InputHandler.DirectionListener();
		inputHandler = new InputHandler(directionListener);
		directionListener.setInputHandler(inputHandler);

		Gdx.input.setInputProcessor(inputHandler);

		System.out.println("SCREEN_WIDTH is " + SCREEN_WIDTH);
		System.out.println("SCREEN_HEIGHT is " + SCREEN_HEIGHT);
		System.out.println("GAME_WIDTH is " + GAME_WIDTH);
		System.out.println("GAME_HEIGHT is " + GAME_HEIGHT);

		this.setScreen(new LoadingScreen(this, assetAdmin));
	}

	public void usernameAvailableResult(int result) {
		if (screen instanceof NameSetScreen) {
			((NameSetScreen) screen).usernameAvailableResult(result);
		}
    }

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
		inputHandler.setScreen((ScreenInputProcessor) screen);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		assetAdmin.dispose();
	}

	public void anotherGamePlayed(int score) {
		sessionGamesPlayed++;
		sessionTotalScore += score;
		if (sessionGamesPlayed%3 == 0) {
			manager.show();
			sessionTotalScore = 0;
		} else if (sessionTotalScore > 1600) {
            manager.show();
		    sessionTotalScore = 0;
        }
	}

	public void showRewardedAd() {
		manager.showRewarded();
	}

	public void onRewarded() {
		((ScreenInputProcessor) screen).onRewarded();
	}

	public void loadVideoAd() {
		manager.loadVideoAd();
	}

	public void onRewardedVideoClosed() {
		if (screen instanceof RewardedVideoScreen) {
			((RewardedVideoScreen) screen).onAdClosed();
		}
	}

	public void onRewardedVideoFailed() {
		if (screen instanceof RewardedVideoScreen) {
			((RewardedVideoScreen) screen).onAdFail();
		}
	}

	public static boolean isTimeAutomatic() {
		return manager.isTimeAutomatic();
	}

	public int[] getDate() {
		int day = 0, month = 0, year = 0;
		System.out.println(day + "" + month + "" + year);
		if (isTimeAutomatic()) {
			 year=calendar.get(Calendar.YEAR);
			 month=calendar.get(Calendar.MONTH);
			 day=calendar.get(Calendar.DAY_OF_MONTH);
		}
		System.out.println("DATE IS " +day + "" + month + "" + year);
		return new int[]{day,month,year};
	}

	public void newHighscore(int highscore) {
		manager.newHighscore(highscore);
	}

	public void newHardcoreHighscore(int highscore) {
		manager.newHardcoreHighscore(highscore);
	}

	public int getHighscore() {
		return assetAdmin.getData("highscore");
	}

    public void getTopTen() {
        manager.getTopTen();
    }

	public void checkIfUsernameAvailable(String username) {
		manager.checkIfUsernameAvailable(username);
	}

	public void setUserName(String userName) {
		manager.setUserName(userName, getHighscore());
	}

	public void getRankingPosition(int highscore, int hardHighscore) {
		manager.getRankingPosition(highscore, hardHighscore);
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public static SpriteBatch getFontBatch() { return fontBatch; }

    public AssetAdmin getAssetAdmin() {
		return assetAdmin;
    }

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
}
