package com.ray3k.template.entities;

import com.ray3k.template.Core;

import static com.ray3k.template.screens.GameScreen.gameScreen;

public class CityEntity extends Entity {
    public CityEntity() {
        setSkeletonData(gameScreen.assetManager.get("spine/city.json"), gameScreen.assetManager.get("spine/city.json-animation"));
        animationState.setAnimation(0, "animation", true);
        depth = Core.DEPTH_DECAL_FRONT;
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
        WreckEntity wreckEntity = new WreckEntity();
        wreckEntity.setSkeletonData(core.assetManager.get("spine/city.json"), core.assetManager.get("spine/city.json-animation"));
        wreckEntity.animationState.setAnimation(0, "wreck", false);
        wreckEntity.setPosition(x, y);
        gameScreen.entityController.add(wreckEntity);
        gameScreen.entityController.add(new EarthQuakeEntity(5f, .5f));
        gameScreen.cities.removeValue(this, true);
        if (gameScreen.cities.size == 0) {
            gameScreen.entityController.add(new LoadGameScreenEntity(2f));
        }
    }
}
