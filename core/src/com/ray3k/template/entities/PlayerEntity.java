package com.ray3k.template.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.ray3k.template.Core.Binding;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class PlayerEntity extends Entity {
    private GameScreen gameScreen;
    private float rotation;
    public static final float ZOOM_MINIMUM = 1f;
    public static final float ZOOM_MAXIMUM = 5f;
    public static final float PLAYER_ZOOM_SPEED_MINIMUM = 0f;
    public static final float PLAYER_ZOOM_SPEED_MAXIMUM = 1000f;
    public static final float ZOOM_ACCELERATION = .25f;
    
    public PlayerEntity() {
        gameScreen = GameScreen.gameScreen;
        
        setSkeletonData(core.assetManager.get("spine/ship.json"), core.assetManager.get("spine/ship.json-animation"));
        animationState.setAnimation(0, "thruster-none", true);
        animationState.setAnimation(1, "propellers", true);
        animationState.setAnimation(2, "claw-hide", false);
        animationState.setAnimation(3, "landing-gear", false);
        animationState.apply(skeleton);
        
        setGravity(500, 270);
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (gameScreen.isBindingPressed(Binding.THRUST)) {
            if (!animationState.getCurrent(0).getAnimation().getName().equals("thruster-both")) {
                animationState.setAnimation(0, "thruster-both", true);
            }
            
            addMotion(8, rotation + 90);
        }
        
        if (!gameScreen.isAnyBindingPressed(Binding.ROTATE_LEFT, Binding.ROTATE_RIGHT, Binding.THRUST)) {
            if (!animationState.getCurrent(0).getAnimation().getName().equals("thruster-none")) {
                animationState.setAnimation(0, "thruster-none", true);
            }
        }
        
        if (y < 50) {
            if (!animationState.getCurrent(3).getAnimation().getName().equals("landing-gear")) {
                animationState.setAnimation(3, "landing-gear", false);
            }
        } else {
            if (!animationState.getCurrent(3).getAnimation().getName().equals("landing-gear-retract")) {
                animationState.setAnimation(3, "landing-gear-retract", false);
            }
        }
        
        gameScreen.cameraEntity.setPosition(x, y);
        gameScreen.cameraEntity.zoom = Utils.approach(gameScreen.cameraEntity.zoom, ZOOM_MINIMUM + (ZOOM_MAXIMUM - ZOOM_MINIMUM) * Interpolation.fastSlow.apply(MathUtils.clamp(getSpeed() / PLAYER_ZOOM_SPEED_MAXIMUM,0, 1)), ZOOM_ACCELERATION * delta);
        
        if (y < 0) {
            y = 0;
            if (deltaY < 0) {
                deltaY = 0;
            }
            deltaX = 0;
        }
    
        if (gameScreen.isBindingPressed(Binding.ROTATE_LEFT)) {
            if (!MathUtils.isZero(y)) {
                if (!animationState.getCurrent(0).getAnimation().getName().equals("thruster-left")) {
                    animationState.setAnimation(0, "thruster-right", true);
                }
                
                rotation += 100 * delta;
            }
        } else if (gameScreen.isBindingPressed(Binding.ROTATE_RIGHT)) {
            if (!MathUtils.isZero(y)) {
                if (!animationState.getCurrent(0).getAnimation().getName().equals("thruster-right")) {
                    animationState.setAnimation(0, "thruster-left", true);
                }
            
                rotation -= 100 * delta;
            }
        }
    
        rotation %= 360;
        skeleton.getRootBone().setRotation(rotation);
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
