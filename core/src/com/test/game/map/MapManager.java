package com.test.game.map;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.test.game.TopDownTestRedux;

import java.util.EnumMap;

import static com.test.game.TopDownTestRedux.BIT_GROUND;


public class MapManager {
    public static final String TAG = MapManager.class.getSimpleName();
    private final World world;
    private final Array<Body> bodies;
    private final TopDownTestRedux context;
    private final AssetManager assetManager;
    private MapType currentMapType;
    private GameMap currentMap;
    private final EnumMap<MapType,GameMap> mapCache;
    private final Array<MapListener> listeners;

    public MapManager(final TopDownTestRedux context) {
        currentMapType = null;
        currentMap = null;

        this.context = context;
        world = context.getWorld();
        assetManager = context.getAssetManager();
        bodies = new Array<Body>();
        mapCache = new EnumMap<MapType, GameMap>(MapType.class);
        listeners = new Array<MapListener>();
    }

    public void addMapListener(final MapListener listener) {
        listeners.add(listener);
    }

    public void setMap(final MapType type) {
        if(currentMapType == type) {
            return;
        }

        if(currentMap != null) {
            world.getBodies(bodies);
            destroyCollisionAreas();
        }

        Gdx.app.debug(TAG,"Changing to map" + type);
        currentMap = mapCache.get(type);
        if(currentMap==null){
            Gdx.app.debug(TAG,"Creating new map of type" + type);
            final TiledMap tiledMap = assetManager.get(type.getFilePath(), TiledMap.class);
            currentMap = new GameMap(tiledMap);
            mapCache.put(type,currentMap);
        }

        spawnCollisionAreas();

        for(final MapListener listener:listeners) {
            listener.mapChange(currentMap);
        }
    }

    private void destroyCollisionAreas() {
        for(final Body body:bodies) {
            if("GROUND".equals(body.getUserData())) {
                world.destroyBody(body);
            }
        }
    }

    private void spawnCollisionAreas() {
        context.resetBodyAndFixtureDefinition();
        for (final CollisionArea collisionArea: currentMap.getCollisionAreas()) {
            //create room
            TopDownTestRedux.BODY_DEF.position.set(collisionArea.getX(),collisionArea.getY());
            TopDownTestRedux.BODY_DEF.fixedRotation = true;
            final Body body = world.createBody(TopDownTestRedux.BODY_DEF);

            TopDownTestRedux.FIXTURE_DEF.filter.categoryBits = BIT_GROUND;
            TopDownTestRedux.FIXTURE_DEF.filter.maskBits = -1;
            ChainShape chainShape = new ChainShape();
            chainShape.createChain(collisionArea.getVertices());
            TopDownTestRedux.FIXTURE_DEF.shape = chainShape;
            body.createFixture(TopDownTestRedux.FIXTURE_DEF);
            chainShape.dispose();
        }
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }
}
