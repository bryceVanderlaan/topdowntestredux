package com.test.game.screen.map;

public enum MapType {
    MAP_1("samplemap.tmx"),
    MAP_2("samplemap2.tmx");

    private final String filePath;

    MapType(final String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
