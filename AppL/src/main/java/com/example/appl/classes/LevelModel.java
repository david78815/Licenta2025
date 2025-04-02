package com.example.appl.classes;

public class LevelModel {
    private final String levelName;
    private final String description;
    private final int score;
    private final String completionTime;
    private final boolean locked;

    public LevelModel(String levelName, String description, int score, String completionTime, boolean locked) {
        this.levelName = levelName;
        this.description = description;
        this.score = score;
        this.completionTime = completionTime;
        this.locked = locked;
    }

    public LevelModel(String levelName, String description){
        this(levelName, description, 0 , "--:--",true);
    }

    // Getters
    public String getLevelName() { return levelName; }
    public String getDescription() { return description; }
    public int getScore() { return score; }
    public String getCompletionTime() { return completionTime; }
    public boolean isLocked() { return locked; }
}

