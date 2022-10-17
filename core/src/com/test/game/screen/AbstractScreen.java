package com.test.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.test.game.TopDownTestRedux;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.test.game.audio.AudioManager;
import com.test.game.input.GameKeyInputListener;
import com.test.game.input.InputManager;


public abstract class AbstractScreen<T extends Table> implements Screen, GameKeyInputListener {
    protected final TopDownTestRedux context;
    protected final FitViewport viewport;
    protected final World world;
    protected final Box2DDebugRenderer box2DDebugRenderer;
    protected final Stage stage;
    protected final T screenUI;
    protected final InputManager inputManager;
    protected final AudioManager audioManager;

    public AbstractScreen(final TopDownTestRedux context) {
        this.context = context;
        viewport = context.getScreenViewport();
        this.world = context.getWorld();
        this.box2DDebugRenderer = context.getBox2DDebugRenderer();
        inputManager = context.getInputManager();
        stage = context.getStage();
        screenUI = getScreenUI(context);
        audioManager = context.getAudioManager();
    }

    protected abstract T getScreenUI(final TopDownTestRedux context);

    @Override
    public void resize (final int width, final int height) {
        viewport.update(width,height);
        stage.getViewport().update(width, height,true);
    }

    @Override
    public void show(){
        inputManager.addInputListener(this);
        stage.addActor(screenUI);
    }

    @Override
    public void hide() {
        inputManager.removeInputListener(this);
        stage.getRoot().removeActor(screenUI);
    }
}
