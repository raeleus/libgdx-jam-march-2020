package com.ray3k.template.entities;

import com.badlogic.gdx.math.Intersector;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

import static com.ray3k.template.screens.GameScreen.gameScreen;

public class BulletEntity extends Entity {
    public static final float MISSILE_KILL_RANGE = 100f;
    
    public BulletEntity() {
        GameScreen gameScreen = GameScreen.gameScreen;
        setSkeletonData(gameScreen.assetManager.get("spine/bullet.json"), gameScreen.assetManager.get("spine/bullet.json-animation"));
        animationState.setAnimation(0, "animation", false);
        depth = Core.DEPTH_BULLETS;
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (x < 0 || x > gameScreen.levelWidth || y < 0 || y > gameScreen.levelHeight || checkCollisionTerrain()) {
            destroy = true;
        } else {
            for (MissileEntity missile : gameScreen.missiles) {
                if (Utils.pointDistance(x, y, missile.x, missile.y) < MISSILE_KILL_RANGE) {
                    destroy = true;
                    missile.destroy = true;
                }
            }
        }
    }
    
    public boolean checkCollisionTerrain() {
        for (TerrainEntity terrainEntity : gameScreen.terrainEntities) {
            if (Intersector.isPointInPolygon(terrainEntity.vertices, 0, terrainEntity.vertices.length, x,y)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
