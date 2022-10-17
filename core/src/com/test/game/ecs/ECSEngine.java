package com.test.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.test.game.TopDownTestRedux;
import com.test.game.ecs.component.AnimationComponent;
import com.test.game.ecs.component.B2DComponent;
import com.test.game.ecs.component.PlayerComponent;
import com.test.game.ecs.system.PlayerCameraSystem;
import com.test.game.ecs.system.PlayerMovementSystem;

import static com.test.game.TopDownTestRedux.*;

public class ECSEngine extends PooledEngine {
    public static final ComponentMapper<PlayerComponent> playerCompMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<B2DComponent> b2DCompMapper = ComponentMapper.getFor(B2DComponent.class);
    private final World world;

    private final TopDownTestRedux context;

    public ECSEngine(final TopDownTestRedux context) {
        super();

        this.context = context;

        world = context.getWorld();

        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
    }

    public void createPlayer(final Vector2 playerSpawnLocation, final float width,final float height) {
        final Entity player = this.createEntity();

        final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
        playerComponent.speed.set(3,3);
        player.add(playerComponent);

        context.resetBodyAndFixtureDefinition();

        //create a player
        final B2DComponent b2DComponent = this.createComponent(B2DComponent.class);
        BODY_DEF.position.set(playerSpawnLocation.x,playerSpawnLocation.y + height * 0.5f);
        BODY_DEF.fixedRotation = true;
        BODY_DEF.type = BodyDef.BodyType.DynamicBody;
        b2DComponent.body = world.createBody(BODY_DEF);
        b2DComponent.body.setUserData("Player");
        b2DComponent.width = width;
        b2DComponent.height = height;
        b2DComponent.renderPosition.set(b2DComponent.body.getPosition());

        FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
        FIXTURE_DEF.filter.maskBits = BIT_GROUND;
        PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(width * 0.5f,height * 0.5f);
        FIXTURE_DEF.shape = pShape;
        b2DComponent.body.createFixture(FIXTURE_DEF);
        pShape.dispose();

        player.add(b2DComponent);

        //animation component
        final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
        player.add(animationComponent);

        this.addEntity(player);
    }

}
