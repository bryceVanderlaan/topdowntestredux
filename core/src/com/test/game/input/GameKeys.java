package com.test.game.input;

import com.badlogic.gdx.Input;

public enum GameKeys {
    UP(Input.Keys.W,Input.Keys.UP),
    DOWN(Input.Keys.S,Input.Keys.DOWN),
    LEFT(Input.Keys.A,Input.Keys.LEFT),
    RIGHT(Input.Keys.D,Input.Keys.RIGHT),
    INCREASE_ZOOM(Input.Keys.Q),
    DECREASE_ZOOM(Input.Keys.E),
    SELECT(Input.Keys.ENTER,Input.Keys.SPACE),
    BACK(Input.Keys.ESCAPE,Input.Keys.BACKSPACE);

    final int[] keyCode;

    GameKeys(final int... keyCode) {
        this.keyCode = keyCode;
    }

    public int[] getKeyCode() {
        return keyCode;
    }
}
