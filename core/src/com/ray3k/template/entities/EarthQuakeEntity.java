package com.ray3k.template.entities;

import com.crashinvaders.vfx.effects.EarthquakeEffect;
import com.ray3k.template.screens.GameScreen;

public class EarthQuakeEntity extends Entity {
    private EarthquakeEffect earthquakeEffect;
    private float amount;
    private float maxTime;
    private float time;
    
    public EarthQuakeEntity(float amount, float maxTime) {
        this.amount = amount;
        this.maxTime = maxTime;
        time = maxTime;
    }
    
    @Override
    public void create() {
        earthquakeEffect = new EarthquakeEffect(amount, 2f);
        GameScreen.gameScreen.vfxManager.addEffect(earthquakeEffect);
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        time -= delta;
        if (time < 0) {
            destroy = true;
        } else {
            earthquakeEffect.setAmount(amount * time / maxTime);
            earthquakeEffect.rebind();
            earthquakeEffect.update(delta);
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
        GameScreen.gameScreen.vfxManager.removeEffect(earthquakeEffect);
    }
}
