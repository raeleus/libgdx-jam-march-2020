package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ray3k.template.screens.GameScreen;

public class CameraEntity extends Entity {
    private GameScreen gameScreen;
    private OrthographicCamera camera;
    public float zoom;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        camera = gameScreen.camera;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        camera.position.set(x, y, 0);
        camera.zoom  = zoom;
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
