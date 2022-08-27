package me.jorge.myfirstgame.util;

public interface manager {

    void show();

    void showRewarded();

    void loadVideoAd();

    public void getTopTen();

    public void newHighscore(int highscore);

    public void newHardcoreHighscore(int highscore);

    public void checkIfUsernameAvailable(String username);

    void setUserName(String userName, int highscore);

    void getRankingPosition(int highscore, int hardHighscore);

    boolean isTimeAutomatic();
}
