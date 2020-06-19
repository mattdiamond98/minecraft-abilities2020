package com.gmail.mattdiamond98.coronacraft.data;

public enum PlayerDataPath {

    NAME("name"),

    RATING("rating"),
    RATING_MEAN("rating.mean"),
    RATING_STANDARD_DEVIATION("rating.standard_deviation"),
    RATING_CONSERVATIVE_MULTIPLIER("rating.conservative_multiplier");

    private String path;

    PlayerDataPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
