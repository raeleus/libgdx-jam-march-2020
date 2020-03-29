package com.ray3k.template.entities;

import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

import static com.ray3k.template.screens.GameScreen.gameScreen;

public class NukeEntity extends Entity implements Attachable {
    public static final float GRAVITY = 500;
    private GameScreen gameScreen;
    private boolean firstFrame = true;
    private MinimumTranslationVector mtv;
    public PlayerEntity attachEntity;
    public static final float MINIMUM_TARGET_DISTANCE = 200f;
    public static final float TARGET_KILL_RANGE = 400f;
    public boolean armed = false;
    
    public NukeEntity() {
        gameScreen = GameScreen.gameScreen;
        setSkeletonData(gameScreen.assetManager.get("spine/nuke.json"), gameScreen.assetManager.get("spine/nuke.json-animation"));
        animationState.setAnimation(0, "animation", false);
        
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
                
                if (armed) destroy = true;
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
            setPosition(attachEntity.ropeTarget.getWorldX() + attachEntity.deltaX * delta, attachEntity.ropeTarget.getWorldY() + attachEntity.deltaY * delta);
            skeleton.getRootBone().setRotation(attachEntity.skeleton.getRootBone().getRotation());
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
        if (Utils.pointDistance(x, y, gameScreen.playerEntity.x, gameScreen.playerEntity.y) < TARGET_KILL_RANGE) {
            gameScreen.playerEntity.destroy = true;
        }
        
        for (CityEntity city : gameScreen.cities) {
            if (Utils.pointDistance(x, y, city.x, city.y) < TARGET_KILL_RANGE) {
                city.destroy = true;
            }
        }
    
        AnimationEntity entity = new AnimationEntity("spine/nuke-explosion.json", "animation", x, y);
        gameScreen.entityController.add(entity);
    
        gameScreen.entityController.add(new EarthQuakeEntity(1f, .25f));
    }
    
    @Override
    public void attachTo(PlayerEntity playerEntity) {
        attachEntity = playerEntity;
        armed = true;
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
    
    @Override
    public boolean isAttached() {
        return attachEntity != null;
    }
}
