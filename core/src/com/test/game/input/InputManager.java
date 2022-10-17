package com.test.game.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class InputManager implements InputProcessor {
    private final GameKeys[] keyMapping;
    private final boolean[] keyState;
    private final Array<GameKeyInputListener> listenerArray;

    public InputManager(){
        this.keyMapping = new GameKeys[256];
        for(final GameKeys gameKey: GameKeys.values()) {
            for(final int code:gameKey.keyCode) {
                keyMapping[code] = gameKey;
            }
        }
        keyState = new boolean[GameKeys.values().length];
        listenerArray = new Array<GameKeyInputListener>();
    }

    public void addInputListener(final GameKeyInputListener listener) {
        listenerArray.add(listener);
    }

    public void removeInputListener(final GameKeyInputListener listener) {
        listenerArray.removeValue(listener,true);
    }

    @Override
    public boolean keyDown(int keycode) {
        final GameKeys gameKey = keyMapping[keycode];
        if (gameKey == null) {
            return false;
        }

        notifyKeyDown(gameKey);
        return false;
    }

    public void notifyKeyDown (final GameKeys gameKey) {
        keyState[gameKey.ordinal()] = true;
        for (final GameKeyInputListener gameKeyInputListener :listenerArray) {
            gameKeyInputListener.keyPressed(this,gameKey);
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        final GameKeys gameKey = keyMapping[keycode];
        if (gameKey == null) {
            return false;
        }

        notifyKeyUp(gameKey);
        return false;
    }

    public void notifyKeyUp (final GameKeys gameKey) {
        keyState[gameKey.ordinal()] = false;
        for (final GameKeyInputListener gameKeyInputListener :listenerArray) {
            gameKeyInputListener.keyUp(this,gameKey);
        }
    }

    public boolean isKeyPressed(final GameKeys gameKey) {
        return keyState[gameKey.ordinal()];
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
