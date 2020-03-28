package com.ray3k.template.entities;

import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.ray3k.template.screens.GameScreen;

public class AnimationEntity extends Entity {
    private GameScreen gs;
    public AnimationEntity(String path, String animation, float x, float y) {
        gs = GameScreen.gameScreen;
        setSkeletonData(gs.assetManager.get(path), gs.assetManager.get(path += "-animation"));
        animationState.setAnimation(0, animation, false);
        animationState.addListener(new AnimationStateAdapter() {
            @Override
            public void end(TrackEntry entry) {
                destroy = true;
            }
        });
        
        this.x = x;
        this.y = y;
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
