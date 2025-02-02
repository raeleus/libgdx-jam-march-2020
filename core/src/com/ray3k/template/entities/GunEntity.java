package com.ray3k.template.entities;

import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Bone;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

import static com.ray3k.template.screens.GameScreen.musicMinigun;

public class GunEntity extends Entity implements Attachable {
    public static final float GRAVITY = 500;
    private GameScreen gameScreen;
    private boolean firstFrame = true;
    private MinimumTranslationVector mtv;
    public PlayerEntity attachEntity;
    public static final float MINIMUM_TARGET_DISTANCE = 200f;
    public static final float FIRING_RATE = .01f;
    private static final float BULLET_SPEED = 10000f;
    private static final float BULLET_ANGLE_RANGE = 10f;
    public float firingTimer;
    private Bone rotator;
    
    public GunEntity() {
        gameScreen = GameScreen.gameScreen;
        setSkeletonData(gameScreen.assetManager.get("spine/turret.json"), gameScreen.assetManager.get("spine/turret.json-animation"));
        animationState.setAnimation(0, "cease-fire", false);
        
        this.x = x;
        this.y = y;
        depth = Core.DEPTH_ENTITY;
        setGravity(GRAVITY, 270);
        mtv = new MinimumTranslationVector();
        firingTimer = FIRING_RATE;
        rotator = skeleton.findBone("rotator");
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
                mtv.normal.setLength(mtv.depth + 10f);
                x += mtv.normal.x;
                y += mtv.normal.y;
                deltaX = 0;
                deltaY = 0;
                setGravity(0, 270);
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
            rotator.setRotation(attachEntity.skeleton.getRootBone().getRotation());
    
            firingTimer -= delta;
            if (firingTimer < 0) {
                firingTimer = FIRING_RATE;
                BulletEntity bullet = new BulletEntity();
                bullet.setPosition(rotator.getWorldX(), rotator.getWorldY());
                bullet.skeleton.getRootBone().setRotation(rotator.getWorldRotationX());
                bullet.setMotion(BULLET_SPEED, rotator.getWorldRotationX() - BULLET_ANGLE_RANGE / 2f + MathUtils.random(BULLET_ANGLE_RANGE));
                bullet.deltaX += attachEntity.deltaX;
                bullet.deltaY += attachEntity.deltaY;
                gameScreen.entityController.add(bullet);
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
        animationState.setAnimation(0, "fire", true);
        musicMinigun.setLooping(true);
        musicMinigun.setVolume(Core.core.sfx);
        musicMinigun.play();
    }
    
    @Override
    public void detach() {
        setGravity(GRAVITY, 270);
        setMotion(attachEntity.getSpeed(), attachEntity.getDirection());
        attachEntity = null;
        animationState.setAnimation(0, "cease-fire", false);
        musicMinigun.stop();
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
