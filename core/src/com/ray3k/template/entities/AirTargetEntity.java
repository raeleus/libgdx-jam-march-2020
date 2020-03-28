package com.ray3k.template.entities;

import com.ray3k.template.screens.GameScreen;

public class AirTargetEntity extends Entity {
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
        GameScreen.gameScreen.airTargets.removeValue(this, true);
        GameScreen.gameScreen.entityController.add(new AnimationEntity("spine/confetti.json", "animation", x, y));
        GameScreen.gameScreen.checkIfLevelComplete();
    }
}
