package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.ray3k.template.screens.GameScreen;
import com.ray3k.template.screens.MenuScreen;
import com.ray3k.template.transitions.Transitions;

public class QuitToMenuEntity extends Entity {
    private float timer;
    
    public QuitToMenuEntity(float timer) {
        this.timer = timer;
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        timer -= delta;
        if (timer < 0) {
            destroy = true;
            core.transition(new GameScreen(), Transitions.push(270, Color.BLACK, Interpolation.fastSlow), .5f);
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
