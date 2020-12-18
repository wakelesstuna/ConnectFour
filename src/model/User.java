package model;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

    // Design pattern Fluent interface

    private String userName;
    private String password;
    private GameStats gameStats;


    public User() {
        this.gameStats = new GameStats();
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    public void resetGameStats() {
        this.gameStats = new GameStats();
    }

    @Override
    public String toString() {
        return userName;
    }


    @Override
    public int compareTo(User o) {
        return gameStats.getWins() - o.gameStats.getWins();
    }
}


