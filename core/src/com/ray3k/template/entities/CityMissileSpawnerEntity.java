package com.ray3k.template.entities;

import static com.ray3k.template.screens.GameScreen.gameScreen;

public class CityMissileSpawnerEntity extends  Entity {
    public int count;
    public float speed;
    public float delay;
    private float timer;
    
    @Override
    public void create() {
        timer = delay;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (gameScreen.cities.size > 0) {
            timer -= delta;
            if (timer < 0) {
                timer = delay;
                MissileEntity missileEntity = new MissileEntity();
                missileEntity.setPosition(x, y);
                missileEntity.setSpeed(speed);
                missileEntity.target = gameScreen.cities.random();
                gameScreen.entityController.add(missileEntity);
                gameScreen.missiles.add(missileEntity);
            }
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
