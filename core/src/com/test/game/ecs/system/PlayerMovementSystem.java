package com.test.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.test.game.TopDownTestRedux;
import com.test.game.ecs.ECSEngine;
import com.test.game.ecs.component.B2DComponent;
import com.test.game.ecs.component.PlayerComponent;
import com.test.game.input.GameKeyInputListener;
import com.test.game.input.GameKeys;
import com.test.game.input.InputManager;

public class PlayerMovementSystem extends IteratingSystem implements GameKeyInputListener {
    private boolean directionChange;
    private int xFactor;
    private int yFactor;

    public PlayerMovementSystem(final TopDownTestRedux context) {
        super(Family.all(PlayerComponent.class, B2DComponent.class).get());
        context.getInputManager().addInputListener(this);
        directionChange = false;
        xFactor = yFactor = 0;
    }
    @Override
    protected void processEntity(final Entity entity,final float deltaTime) {
        final PlayerComponent playerComponent = ECSEngine.playerCompMapper.get(entity);
        final B2DComponent b2DComponent = ECSEngine.b2DCompMapper.get(entity);

        if (directionChange) {
            directionChange = false;
            b2DComponent.body.applyLinearImpulse(
                    (xFactor * playerComponent.speed.x - b2DComponent.body.getLinearVelocity().x) * b2DComponent.body.getMass(),
                    (yFactor * playerComponent.speed.y - b2DComponent.body.getLinearVelocity().y) * b2DComponent.body.getMass(),
                    b2DComponent.body.getWorldCenter().x,
                    b2DComponent.body.getWorldCenter().y,
                    true
            );
        }
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        switch (key) {
            case UP:
                directionChange = true;
                yFactor = 1;
                break;
            case DOWN:
                directionChange = true;
                yFactor = -1;
                break;
            case LEFT:
                directionChange = true;
                xFactor = -1;
                break;
            case RIGHT:
                directionChange = true;
                xFactor = 1;
                break;
            default:
                return;
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
        switch (key) {
            case UP:
                directionChange = true;
                yFactor = manager.isKeyPressed(GameKeys.DOWN) ? -1 : 0;
                break;
            case DOWN:
                directionChange = true;
                yFactor = manager.isKeyPressed(GameKeys.UP) ? 1 : 0;
                break;
            case LEFT:
                directionChange = true;
                xFactor = manager.isKeyPressed(GameKeys.RIGHT) ? 1 : 0;
                break;
            case RIGHT:
                directionChange = true;
                xFactor = manager.isKeyPressed(GameKeys.LEFT) ? -1 : 0;
                break;
            default:
                return;
        }
    }
}
