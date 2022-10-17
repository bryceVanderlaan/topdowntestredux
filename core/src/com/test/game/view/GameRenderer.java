package com.test.game.view;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.test.game.TopDownTestRedux;
import com.test.game.ecs.ECSEngine;
import com.test.game.ecs.component.AnimationComponent;
import com.test.game.ecs.component.B2DComponent;
import com.test.game.map.CollisionArea;
import com.test.game.map.GameMap;
import com.test.game.map.MapListener;

import static com.test.game.TopDownTestRedux.BIT_GROUND;
import static com.test.game.TopDownTestRedux.UNIT_SCALE;

public class GameRenderer implements Disposable, MapListener {
    public static final String TAG = GameRenderer.class.getSimpleName();
    private final OrthographicCamera gameCamera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetManager assetManager;
    private final ImmutableArray<Entity> animatedEntities;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Array<TiledMapTileLayer> tiledMapLayers;
    private final GLProfiler profiler;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final World world;
    private Sprite dummySprite;

    public GameRenderer (final TopDownTestRedux context) {
        assetManager = context.getAssetManager();
        viewport = context.getScreenViewport();
        gameCamera = context.getGameCamera();
        spriteBatch = context.getSpriteBatch();

        animatedEntities = context.getEcsEngine().getEntitiesFor(Family.all(AnimationComponent.class, B2DComponent.class).get());

        mapRenderer = new OrthogonalTiledMapRenderer(null,UNIT_SCALE,spriteBatch);
        context.getMapManager().addMapListener(this);
        tiledMapLayers = new Array<TiledMapTileLayer>();

        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
        if(profiler.isEnabled()) {
            box2DDebugRenderer = new Box2DDebugRenderer();
            world = context.getWorld();
        } else {
            box2DDebugRenderer = null;
            world = null;
        }
    }

    public void render(final float alpha) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(false);
        spriteBatch.begin();
        if(mapRenderer.getMap() != null) {
            //AnimatedTiledMapTile.updateAnimationBaseTime();
            mapRenderer.setView(gameCamera);
            for(final TiledMapTileLayer layer:tiledMapLayers) {
                mapRenderer.renderTileLayer(layer);
            }
        }
        for(final Entity entity:animatedEntities) {
            renderEntity(entity,alpha);
        }
        spriteBatch.end();

        if(profiler.isEnabled()) {
            Gdx.app.debug("Render Info", "Bindings " + profiler.getTextureBindings());
            Gdx.app.debug("Render Info", "Draw Calls " + profiler.getDrawCalls());
            profiler.reset();

            box2DDebugRenderer.render(world,gameCamera.combined);
        }
    }

    private void renderEntity(final Entity entity,final float alpha) {
        final B2DComponent b2DComponent = ECSEngine.b2DCompMapper.get(entity);

        b2DComponent.renderPosition.lerp(b2DComponent.body.getPosition(),alpha);
        dummySprite.setBounds(b2DComponent.renderPosition.x - b2DComponent.width * 0.5f,b2DComponent.renderPosition.y - b2DComponent.height * 0.5f, b2DComponent.width, b2DComponent.height);
        dummySprite.draw(spriteBatch);
    }

    @Override
    public void dispose() {
        if(box2DDebugRenderer != null) {
            box2DDebugRenderer.dispose();
        }
        mapRenderer.dispose();
    }

    @Override
    public void mapChange(final GameMap gameMap) {
        mapRenderer.setMap(gameMap.getTiledMap());
        gameMap.getTiledMap().getLayers().getByType(TiledMapTileLayer.class,tiledMapLayers);

        if(dummySprite == null) {
            dummySprite = assetManager.get("charactersAndEffects/character_and_effect.atlas", TextureAtlas.class).createSprite("fireball");
            dummySprite.setOriginCenter();
        }
    }
}
