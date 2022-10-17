package com.test.game.audio;

public enum AudioType {
    INTRO("audio/poland.mp3",true,0.1f),
    SELECT("audio/select.wav",false,0.1f);

    private final String filePath;
    private final boolean isMusic;
    private final float volume;

    AudioType(final String filePath,final boolean isMusic,final float volume) {
        this.filePath = filePath;
        this.isMusic = isMusic;
        this.volume = volume;
    }

    public String getFilePath() {
        return filePath;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isMusic() {
        return isMusic;
    }
}
