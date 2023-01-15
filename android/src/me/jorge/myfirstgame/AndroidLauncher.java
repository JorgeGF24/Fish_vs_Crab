package me.jorge.myfirstgame;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.jorge.myfirstgame.screens.RankingScreen;
import me.jorge.myfirstgame.screens.ScreenInputProcessor;

public class AndroidLauncher extends AndroidApplication {

	private InterstitialAd mInterstitialAd;
	private RewardedAd mRewardedAd;
	private String userID;
	private FirebaseFirestore firestore;

	private String[] topNames, hardTopNames;
	private int[] topScores, hardTopScores;

	private MyGame game;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		System.out.println("LAUNCHING");
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useImmersiveMode = true;
		firestore = FirebaseFirestore.getInstance();
		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
			}
		});

		// TEST ca-app-pub-3940256099942544/8691691433
		// REAL ca-app-pub-8810945539727773/8819793873

		loadInterstitialAd();
		loadRewardAd();

		game = new MyGame(new AndroidManager(mInterstitialAd, mRewardedAd, this));
		initialize(game, config);
	}

	// TEST ca-app-pub-3940256099942544/5224354917
	// REAL ca-app-pub-8810945539727773/7982024773

	void loadRewardAd() {
		AdRequest adRequest = new AdRequest.Builder().build();
		RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
			adRequest, new RewardedAdLoadCallback() {
				@Override
				public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
					// Handle the error.
					Log.d("AD", loadAdError.toString());
					loadRewardAd();
					game.onRewardedVideoFailed();
					mRewardedAd = null;
				}

				@Override
				public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
					mRewardedAd = rewardedAd;
					ScreenInputProcessor.adLoaded = true;
					Log.d("AD", "Ad was loaded.");
				}
			});
	}

	void loadInterstitialAd(){
		AdRequest adRequest = new AdRequest.Builder().build();
		InterstitialAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest,
			new InterstitialAdLoadCallback() {
				@Override
				public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
					// The mInterstitialAd reference will be null until
					// an ad is loaded.
					mInterstitialAd = interstitialAd;
					Log.i("AD", "onAdLoaded");
				}

				@Override
				public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
					// Handle the error
					Log.d("AD", loadAdError.toString());
					mInterstitialAd = null;
				}
			}
		);
	}

	public void getTopTen() {
        final ArrayList<String> topNamesList = new ArrayList<>();
        final ArrayList<Long> topScoresList = new ArrayList<>();
        final ArrayList<String> hardTopNamesList = new ArrayList<>();
        final ArrayList<Long> hardTopScoresList = new ArrayList<>();
		System.out.println("get top names, top names was null");
        firestore.collection("users")
                .orderBy("Highscore", Query.Direction.DESCENDING).limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().size() == 10) {
                            topNames = new String[10];
                            topScores = new int[10];
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                topNamesList.add((String) document.get("User name"));
                                topScoresList.add((Long) document.get("Highscore"));
                            }
                            for (int i = 0; i < 10; i++) {
                                topNames[i] = topNamesList.get(i);
                                topScores[i] = Math.round(topScoresList.get(i));
                            }

                            RankingScreen.topNames = topNames;
							RankingScreen.topScores = topScores;
                        }
                    }
                });
        firestore.collection("users")
                .orderBy("HardcoreHighscore", Query.Direction.DESCENDING).limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().size() == 10) {
                            hardTopNames = new String[10];
                            hardTopScores = new int[10];
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                hardTopNamesList.add((String) document.get("User name"));
                                hardTopScoresList.add((Long) document.get("HardcoreHighscore"));
                            }
                            for (int i = 0; i < 10; i++) {
                                hardTopNames[i] = hardTopNamesList.get(i);
                                hardTopScores[i] = Math.round(hardTopScoresList.get(i));
                            }

							RankingScreen.hardTopNames = hardTopNames;
							RankingScreen.hardTopScores = hardTopScores;
                        }
                    }
                });
	}

	public void setUserName(String userName, int highscore) {
		if (userID == null) {
			FirebaseInstallations.getInstance().getId().addOnCompleteListener( task -> {
				if (!task.isSuccessful()) {
					Log.w("firebaseID", "getInstanceId failed", task.getException());
					return;
				}
				// Get new Instance ID
				userID = task.getResult();
				setUserName(userName,highscore);
			});
		} else {

			Map<String, Object> user = new HashMap<>();
			user.put("User name", userName);
			user.put("Highscore", highscore);
			user.put("HardcoreHighscore", 0);

			firestore.collection("users").document(userID)
					.set(user, SetOptions.merge());
		}
	}

	public void getRankingPosition(int highscore, int hardHighscore) {
        firestore.collection("users").whereGreaterThan("Highscore", highscore)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    RankingScreen.rankingPosition = "" + (1+task.getResult().size());
                }
            }
        });
        firestore.collection("users").whereGreaterThan("HardcoreHighscore", hardHighscore)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    me.jorge.myfirstgame.screens.RankingScreen.hardRankingPosition = "" + (1+task.getResult().size());
                }
            }
        });
	}

	public void checkIfUsernameAvailable(String username) {
		firestore.collection("users").whereEqualTo("User name", username)
				.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.getResult().isEmpty()) {
					if (!task.isSuccessful()) {
						usernameAvailableResult(-1);
						return;
					}
					usernameAvailableResult(1);
				} else {
					usernameAvailableResult(0);
				}
			}
		});
	}

	public void usernameAvailableResult(int result) {
		game.usernameAvailableResult(result);
	}

	public void newHigscore(int highscore) {
		if (userID==null) {
			FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
				if (!task.isSuccessful()) {
					Log.w("New highscore", "getInstanceId failed", task.getException());
					return;
				}
				// Get new Instance ID
				userID = task.getResult();
				newHigscore(highscore);
			});
		} else {
			firestore.collection("users").document(userID).update("Highscore", highscore);
		}
	}

	public void newHardcoreHighscore(int highscore) {
		if (userID==null) {
			FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
				if (!task.isSuccessful()) {
					Log.w("HARDCORE highscore", "getInstanceId failed", task.getException());
					return;
				}
				// Get new Instance ID
				userID = task.getResult();
			});
		} else {
			firestore.collection("users").document(userID).update("HardcoreHighscore", highscore);
		}
	}

	public void onRewarded(RewardItem rewardItem) {
		game.onRewarded();
	}

	public MyGame getGame() {
		return game;
	}
}
