package com.ray3k.template.entities;

import com.ray3k.template.Core;
import com.ray3k.template.Utils;

import static com.ray3k.template.screens.GameScreen.gameScreen;
import static com.ray3k.template.screens.GameScreen.soundExplosion;

public class MissileEntity extends Entity {
    public Entity target;
    public static final float TARGET_KILL_RANGE = 100f;
    
    public MissileEntity() {
        setSkeletonData(gameScreen.assetManager.get("spine/missile.json"), gameScreen.assetManager.get("spine/missile.json-animation"));
        animationState.setAnimation(0, "animation", true);
        depth = Core.DEPTH_MISSILE;
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (target != null) {
            float direction = Utils.pointDirection(x, y, target.x, target.y);
            setDirection(direction);
            skeleton.getRootBone().setRotation(direction);
            if (Utils.pointDistance(x, y, target.x, target.y) < TARGET_KILL_RANGE) {
                destroy = true;
                target.destroy = true;
            }
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
        AnimationEntity entity = new AnimationEntity("spine/explosion.json", "animation", x, y);
        gameScreen.entityController.add(entity);
        gameScreen.missiles.removeValue(this, true);
        gameScreen.entityController.add(new EarthQuakeEntity(1f, .25f));
        gameScreen.objectives.removeValue(this, true);
        gameScreen.checkIfLevelComplete();
        soundExplosion.play(Core.core.sfx);
    }
}
