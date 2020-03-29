package com.ray3k.template.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class CarrierEntity extends Entity implements Attachable {
    public static final float GRAVITY = 500;
    private GameScreen gameScreen;
    private boolean firstFrame = true;
    private MinimumTranslationVector mtv;
    public PlayerEntity attachEntity;
    public static final float MINIMUM_TARGET_DISTANCE = 200f;
    
    public CarrierEntity() {
        gameScreen = GameScreen.gameScreen;
        setSkeletonData(gameScreen.assetManager.get("spine/carrier.json"), gameScreen.assetManager.get("spine/carrier.json-animation"));
        animationState.setAnimation(0, "fill3", false);
        
        this.x = x;
        this.y = y;
        depth = Core.DEPTH_ENTITY;
        setGravity(GRAVITY, 270);
        mtv = new MinimumTranslationVector();
    }
    
    private boolean checkForCollision(MinimumTranslationVector mtv) {
        float[] f1 = Utils.skeletonBoundsToTriangles(skeletonBounds);
        for (TerrainEntity terrain : gameScreen.terrainEntities) {
            if (Utils.overlapSortedTriangles(f1, terrain.sortedTriangles, mtv)) return true;
        }
        return false;
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
        if (!firstFrame) {
            if (attachEntity == null && checkForCollision(mtv)) {
                mtv.normal.setLength(mtv.depth + .1f);
                x += mtv.normal.x;
                y += mtv.normal.y;
                deltaX = 0;
                deltaY = 0;
                setGravity(0, 270);
                
                for (CarrierLandTargetEntity target : gameScreen.carrierLandTargets) {
                    if (Utils.pointDistance(x, y, target.x, target.y) < MINIMUM_TARGET_DISTANCE) {
                        destroy = true;
                        target.destroy = true;
                        break;
                    }
                }
            }
        } else {
            firstFrame = false;
        }
    }
    
    @Override
    public void act(float delta) {
        if (x < 0) {
            x = 0;
            deltaX = 0;
        } else if (x > gameScreen.levelWidth) {
            x = gameScreen.levelWidth;
            deltaX = 0;
        }
        
        if (attachEntity != null) {
            setPosition(attachEntity.ropeTarget.getWorldX(), attachEntity.ropeTarget.getWorldY());
            skeleton.getRootBone().setRotation(attachEntity.skeleton.getRootBone().getRotation());
        } else {
            for (CarrierAirTargetEntity target : gameScreen.carrierAirTargets) {
                if (Utils.pointDistance(x, y, target.x, target.y) <  MINIMUM_TARGET_DISTANCE) {
                    destroy = true;
                    target.destroy = true;
                    break;
                }
            }
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void attachTo(PlayerEntity playerEntity) {
        attachEntity = playerEntity;
    }
    
    @Override
    public void detach() {
        setGravity(GRAVITY, 270);
        setMotion(attachEntity.getSpeed(), attachEntity.getDirection());
        attachEntity = null;
    }
    
    @Override
    public boolean checkForCollision() {
        return checkForCollision(mtv);
    }
}
