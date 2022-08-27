package me.jorge.myfirstgame.util;

public interface RankingManager {

    public void getTopTen();

    public void newHighscore(int highscore);

    public void newHardcoreHighscore(int highscore);

    public void checkIfUsernameAvailable(String username);

    void setUserName(String userName, int highscore);

    void getRankingPosition(int highscore, int hardHighscore);
}
