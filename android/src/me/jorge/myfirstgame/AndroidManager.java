package me.jorge.myfirstgame;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

import me.jorge.myfirstgame.screens.ScreenInputProcessor;
import me.jorge.myfirstgame.util.manager;

public class AndroidManager implements manager {

    private final AndroidLauncher launcher;

    public AndroidManager(InterstitialAd interstitialAd, RewardedAd rewardAd, AndroidLauncher launcher) {
        this.launcher = launcher;
        this.interstitialAd = interstitialAd;
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

    private InterstitialAd interstitialAd;
    private RewardedAd rewardAd;
    private final Activity activity;
    private final Context context;

    @Override
    public void showRewarded() {
        activity.runOnUiThread(() -> {
            if (rewardAd != null) {

                rewardAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d("AD", "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d("AD", "Ad dismissed fullscreen content.");
                        rewardAd = null;

                        ScreenInputProcessor.adLoaded = false;
                        loadRewardAd();
                        launcher.getGame().onRewardedVideoClosed();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.e("AD", "Ad failed to show fullscreen content.");
                        rewardAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d("AD", "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("AD", "Ad showed fullscreen content.");
                    }
                });
                rewardAd.show(launcher, rewardItem -> {
                    // Handle the reward.
                    Log.d("AD", "The user earned the reward.");
                    launcher.onRewarded(rewardItem);
                });
            } else {
                loadRewardAd();
                Log.d("AD", "The rewarded ad wasn't ready yet.");
            }
        });
    }

    @Override
    public void showInterstitial() {
        activity.runOnUiThread(() -> {
            if (interstitialAd != null) {
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d("inter AD", "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d("inter AD", "Ad dismissed fullscreen content.");
                        interstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        Log.e("inter AD", "Ad failed to show fullscreen content.");
                        interstitialAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d("inter AD", "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("inter AD", "Ad showed fullscreen content.");
                    }
                });

                interstitialAd.show(launcher);
            } else {
                loadInterstitialAd();
                Log.d("inter AD", "The interstitial ad wasn't ready yet.");
            }
        });
    }

    @Override
    public void loadRewardAd() {
        activity.runOnUiThread(() -> {
            if (rewardAd == null) {
                ((AndroidLauncher) activity).loadRewardAd();
            }
        });
    }

    @Override
    public void loadInterstitialAd() {
        activity.runOnUiThread(() -> {
            if (interstitialAd == null) {
                ((AndroidLauncher) activity).loadInterstitialAd();
            }
        });
    }

    @Override
    public boolean isTimeAutomatic() {
        return (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0)) == 1 && (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1);
    }

    public void setInterstitialAd(InterstitialAd interstitialAd) {
        this.interstitialAd = interstitialAd;
    }

    public void setRewardAd(RewardedAd rewardAd) {
        this.rewardAd = rewardAd;
    }
}
