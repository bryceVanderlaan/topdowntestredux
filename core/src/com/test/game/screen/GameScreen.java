package com.test.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.test.game.TopDownTestRedux;
import com.test.game.input.GameKeys;
import com.test.game.input.InputManager;
import com.test.game.map.*;
import com.test.game.view.GameRenderer;
import com.test.game.view.GameUI;

import static com.test.game.TopDownTestRedux.*;

public class GameScreen extends AbstractScreen<GameUI> implements MapListener {
    private final MapManager mapManager;

    public GameScreen (final TopDownTestRedux context) {
        super(context);

        mapManager = context.getMapManager();
        mapManager.addMapListener(this);
        mapManager.setMap(MapType.MAP_1);

        context.getEcsEngine().createPlayer(mapManager.getCurrentMap().getStartLocation(),0.75f,0.75f);
    }

    @Override
    protected GameUI getScreenUI(final TopDownTestRedux context) {
        return new GameUI(context);
    }


    @Override
    public void render(final float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            mapManager.setMap(MapType.MAP_1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            mapManager.setMap(MapType.MAP_2);
        }
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

    public void keyPressed(InputManager manager, GameKeys key) {

    }

    public void keyUp(InputManager manager, GameKeys key) {

    }

    @Override
    public void mapChange(GameMap gameMap) {

    }
}
