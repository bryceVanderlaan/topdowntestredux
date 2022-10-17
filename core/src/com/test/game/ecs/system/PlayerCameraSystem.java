package com.test.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.test.game.TopDownTestRedux;
import com.test.game.ecs.ECSEngine;
import com.test.game.ecs.component.B2DComponent;
import com.test.game.ecs.component.PlayerComponent;


public class PlayerCameraSystem extends IteratingSystem {
    private final OrthographicCamera gameCamera;
    private boolean zoomChange;
    private int zoomFactor;

    public PlayerCameraSystem(final TopDownTestRedux context) {
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        gameCamera = context.getGameCamera();
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        gameCamera.position.set(ECSEngine.b2DCompMapper.get(entity).renderPosition,0);

        if(Gdx.input.isKeyPressed(Input.Keys.Q) && gameCamera.zoom >= 0.12000038f) {
            gameCamera.zoom -= 0.02f;
            Gdx.app.debug("Current Camera Zoom Level: ", String.valueOf(gameCamera.zoom));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.E) && gameCamera.zoom <= 3.3399978f) {
            gameCamera.zoom += 0.02f;
        }
    }
}
