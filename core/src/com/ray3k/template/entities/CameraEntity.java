package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.ray3k.template.screens.GameScreen;

public class CameraEntity extends Entity {
    private GameScreen gameScreen;
    private OrthographicCamera camera;
    private OrthographicCamera backgroundCamera;
    public float zoom;
    public float backgroundZoom;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        camera = gameScreen.camera;
        backgroundCamera = gameScreen.backgroundCamera;
        zoom = 1f;
        backgroundZoom = .75f;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        camera.position.set(MathUtils.clamp(x,camera.viewportWidth / 2 * camera.zoom, gameScreen.levelWidth - camera.viewportWidth / 2 * camera.zoom), y, 0);
        camera.zoom = zoom;
        backgroundCamera.zoom = backgroundZoom;
        float bgWorldWidth = gameScreen.backgroundViewport.getWorldWidth();
        float bgWorldHeight = gameScreen.backgroundViewport.getWorldHeight();
        backgroundCamera.position.set((bgWorldWidth - (1 - backgroundZoom) * bgWorldWidth + (1 - backgroundZoom) * 2 * bgWorldWidth * x / gameScreen.levelWidth) / 2, (bgWorldHeight - (1 - backgroundZoom) * bgWorldHeight + (1 - backgroundZoom) * 2 * bgWorldHeight * Math.min(y / gameScreen.levelHeight, 1)) / 2, 0);
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
