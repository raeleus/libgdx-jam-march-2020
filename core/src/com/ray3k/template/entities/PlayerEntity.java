package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.MathUtils;
import com.ray3k.template.Core.Binding;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class PlayerEntity extends Entity {
    private GameScreen gameScreen;
    private float rotation;
    public static final float ZOOM_MINIMUM = 1f;
    public static final float ZOOM_MAXIMUM = 5f;
    public static final float PLAYER_ZOOM_SPEED_MAXIMUM = 1000f;
    public static final float ZOOM_ACCELERATION = .25f;
    public static final float GRAVITY = 500;
    public static MinimumTranslationVector mtv;
    public static final float MAX_LANDING_SPEED = 300f;
    public static final float MAX_LANDING_ROTATION = 10f;
    public static final int LAND_DETECTION_DISTANCE = 100;
    private boolean firstFrame;
    
    public PlayerEntity() {
        gameScreen = GameScreen.gameScreen;
        
        setSkeletonData(core.assetManager.get("spine/ship.json"), core.assetManager.get("spine/ship.json-animation"));
        animationState.setAnimation(0, "thruster-none", true);
        animationState.setAnimation(1, "propellers", true);
        animationState.setAnimation(2, "claw-hide", false);
        animationState.setAnimation(3, "landing-gear-retract", false);
        animationState.apply(skeleton);
        skeletonBounds.update(skeleton, true);
        
        setGravity(GRAVITY, 270);
        mtv = new MinimumTranslationVector();
        firstFrame = true;
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
        if (!firstFrame) {
            if (checkForCollision(mtv)) {
                if (Math.abs(deltaY) > MAX_LANDING_SPEED || !Utils.isEqual360(rotation, 0,
                        MAX_LANDING_ROTATION) || !Intersector.isPointInPolygon(gameScreen.landscapeEntity.vertices, 0,
                        gameScreen.landscapeEntity.vertices.length, x, y - LAND_DETECTION_DISTANCE)) {
                    destroy = true;
                } else {
                    mtv.normal.setLength(mtv.depth + .1f);
                    x += mtv.normal.x;
                    y += mtv.normal.y;
                    deltaX = 0;
                    deltaY = 0;
                    setGravity(0, 270);
                }
            }
        } else {
            firstFrame = false;
        }
    }
    
    private boolean checkForCollision(MinimumTranslationVector mtv) {
        float[] f1 = Utils.skeletonBoundsToTriangles(skeletonBounds);
        return Utils.overlapSortedTriangles(f1, gameScreen.landscapeEntity.sortedTriangles, mtv);
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
        
        gameScreen.cameraEntity.setPosition(x, y);
        gameScreen.cameraEntity.zoom = Utils.approach(gameScreen.cameraEntity.zoom, ZOOM_MINIMUM + (ZOOM_MAXIMUM - ZOOM_MINIMUM) * Interpolation.fastSlow.apply(MathUtils.clamp(getSpeed() / PLAYER_ZOOM_SPEED_MAXIMUM,0, 1)), ZOOM_ACCELERATION * delta);
        
        if (y < 0) {
            y = 0;
            if (deltaY < 0) {
                deltaY = 0;
            }
            deltaX = 0;
        }
    
        boolean isLanding;
        if (Intersector.isPointInPolygon(gameScreen.landscapeEntity.vertices, 0, gameScreen.landscapeEntity.vertices.length, x, y - 150)) {
            isLanding = true;
            if (!animationState.getCurrent(3).getAnimation().getName().equals("landing-gear")) {
                animationState.setAnimation(3, "landing-gear", false);
            }
        } else {
            isLanding = false;
            if (!animationState.getCurrent(3).getAnimation().getName().equals("landing-gear-retract")) {
                animationState.setAnimation(3, "landing-gear-retract", false);
            }
        }
    
        boolean canRotate;
        if (isLanding && Intersector.isPointInPolygon(gameScreen.landscapeEntity.vertices, 0, gameScreen.landscapeEntity.vertices.length, x, y - LAND_DETECTION_DISTANCE)) {
            canRotate = false;
        } else {
            setGravity(GRAVITY, 270);
            canRotate = true;
        }
        
        if (canRotate) {
            float previousRotation = rotation;
    
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
            skeleton.updateWorldTransform();
            skeletonBounds.update(skeleton, true);
            if (checkForCollision(null)) {
                rotation = previousRotation;
                skeleton.getRootBone().setRotation(rotation);
                skeleton.updateWorldTransform();
                skeletonBounds.update(skeleton, true);
            }
        }
    }
    
    @Override
    public void draw(float delta) {
        core.shapeDrawer.setColor(Color.YELLOW);
        core.shapeDrawer.filledRectangle(x, y - 100, 10, 10);
    }
    
    @Override
    public void destroy() {
        WreckEntity wreckEntity = new WreckEntity();
        wreckEntity.setSkeletonData(core.assetManager.get("spine/ship.json"), core.assetManager.get("spine/ship.json-animation"));
        wreckEntity.animationState.setAnimation(0, "wreck", false);
        wreckEntity.skeleton.getRootBone().setRotation(rotation);
        wreckEntity.setPosition(x, y);
        gameScreen.entityController.add(wreckEntity);
        gameScreen.entityController.add(new QuitToMenuEntity(2f));
    }
}
