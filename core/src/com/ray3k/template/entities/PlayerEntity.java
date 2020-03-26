package com.ray3k.template.entities;

public class PlayerEntity extends Entity {
    public PlayerEntity() {
        setSkeletonData(core.assetManager.get("spine/ship.json"), core.assetManager.get("spine/ship.json-animation"));
        animationState.setAnimation(0, "start", false);
        animationState.apply(skeleton);
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
