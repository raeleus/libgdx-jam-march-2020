package com.ray3k.template.entities;

public class PlayerEntity extends Entity {
    @Override
    public void create() {
        setSkeletonData(core.assetManager.get("spine/ship.json"), core.assetManager.get("spine/ship.json-animation"));
        animationState.apply(skeleton);
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
