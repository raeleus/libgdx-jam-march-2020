package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.ray3k.template.screens.GameScreen;

public class WreckEntity extends Entity {
    private static final float MIN_TIME = .001f;
    private static final float MAX_TIME = .2f;
    private static final int NUMBER_OF_EXPLOSIONS = 2;
    private float timer;
    
    @Override
    public void create() {
        timer = MathUtils.random(MIN_TIME, MAX_TIME);
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        timer -= delta;
        if (timer < 0) {
            timer = MathUtils.random(MIN_TIME, MAX_TIME);
            for (int i = 0; i < MathUtils.random(NUMBER_OF_EXPLOSIONS); i++) {
                AnimationEntity entity = new AnimationEntity("spine/explosion.json", "animation",
                         MathUtils.random(skeletonBounds.getMinX(), skeletonBounds.getMaxX()),
                        MathUtils.random(skeletonBounds.getMinY(), skeletonBounds.getMaxY()));
                GameScreen.gameScreen.entityController.add(entity);
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
