package com.test.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.test.game.TopDownTestRedux;
import com.test.game.audio.AudioType;
import com.test.game.input.GameKeys;
import com.test.game.input.InputManager;
import com.test.game.view.LoadingUI;

public class LoadingScreen extends AbstractScreen<LoadingUI> {
    private final AssetManager assetManager;
    private boolean isMusicLoaded;

    public LoadingScreen(final TopDownTestRedux context) {
        super(context);
        this.assetManager = context.getAssetManager();
        assetManager.load("samplemap.tmx", TiledMap.class);

        assetManager.load("charactersAndEffects/character_and_effect.atlas", TextureAtlas.class);

        //load audio
        isMusicLoaded = false;
        for(final AudioType audioType:AudioType.values()) {
            assetManager.load(audioType.getFilePath(),audioType.isMusic() ? Music.class : Sound.class);
        }
    }

    @Override
    protected LoadingUI getScreenUI(final TopDownTestRedux context) {
        return new LoadingUI(context);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        assetManager.update();

        if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
            isMusicLoaded = true;
            audioManager.playAudio(AudioType.INTRO);
        }
        screenUI.setProgress(assetManager.getProgress());
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide(){
        super.hide();
        audioManager.stopCurrentMusic();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        if (assetManager.getProgress() >= 1) {
            audioManager.playAudio(AudioType.SELECT);
            context.setScreen(ScreenType.GAME);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {

    }
}
