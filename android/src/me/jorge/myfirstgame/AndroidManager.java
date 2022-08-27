package me.jorge.myfirstgame;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import me.jorge.myfirstgame.util.manager;

public class AndroidManager implements manager {

    private final AndroidLauncher launcher;

    public AndroidManager(InterstitialAd ad, RewardedVideoAd rewardAd, AndroidLauncher launcher) {
        this.launcher = launcher;
        this.ad = ad;
        this.rewardAd = rewardAd;
        this.activity = launcher;

        context = launcher.getContext();
    }

    @Override
    public void getTopTen() {
        launcher.getTopTen();
    }

    @Override
    public void newHighscore(int highscore) {
        launcher.newHigscore(highscore);
    }

    @Override
    public void newHardcoreHighscore(int highscore) {
        launcher.newHardcoreHighscore(highscore);
    }

    public void checkIfUsernameAvailable(String username) {
        launcher.checkIfUsernameAvailable(username);
    }

    public void setUserName(String userName, int highscore) {
        System.out.println("Setting username in AndroidRanking");
        launcher.setUserName(userName, highscore);
    }

    @Override
    public void getRankingPosition(int highscore, int hardHighscore) {
        launcher.getRankingPosition(highscore, hardHighscore);
    }

    private final InterstitialAd ad;
    private final RewardedVideoAd rewardAd;
    private final Activity activity;
    private final Context context;

    @Override
    public void show() {
        System.out.println("Show ad");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (ad.isLoaded()) {
                    ad.show();
                } else {
                    ad.loadAd(new AdRequest.Builder().build());
                    //Log.d(TAG, "Interstitial ad is not loaded yet");
                }
            }
        });
    }

    @Override
    public void showRewarded() {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (rewardAd.isLoaded()) {
                    rewardAd.show();
                } else {
                    ((AndroidLauncher) activity).loadRewardedVideoAd();
                    //Log.d(TAG, "Interstitial ad is not loaded yet");
                }
            }
        });
    }

    @Override
    public void loadVideoAd() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!rewardAd.isLoaded()) {
                    ((AndroidLauncher) activity).loadRewardedVideoAd();
                }
            }
        });
    }

    @Override
    public boolean isTimeAutomatic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0)) == 1 && (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1);
        } else {
            return (android.provider.Settings.System.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1) && (android.provider.Settings.System.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1);
        }
    }
}
