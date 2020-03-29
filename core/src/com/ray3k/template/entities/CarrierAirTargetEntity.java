package com.ray3k.template.entities;

import com.ray3k.template.screens.GameScreen;

import static com.ray3k.template.screens.GameScreen.gameScreen;

public class CarrierAirTargetEntity extends Entity {
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
        GameScreen.gameScreen.carrierAirTargets.removeValue(this, true);
        GameScreen.gameScreen.entityController.add(new AnimationEntity("spine/confetti.json", "animation", x, y));
        gameScreen.objectives.removeValue(this, true);
        GameScreen.gameScreen.checkIfLevelComplete();
    }
}
