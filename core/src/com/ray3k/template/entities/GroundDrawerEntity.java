package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ray3k.template.Core;
import com.ray3k.template.screens.GameScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GroundDrawerEntity extends Entity {
    private GameScreen gameScreen;
    private ShapeDrawer shapeDrawer;
    private OrthographicCamera camera;
    private float[] vertices = new float[8];
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        shapeDrawer = gameScreen.shapeDrawer;
        camera = gameScreen.camera;
        depth = Core.DEPTH_LANDSCAPE;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
        shapeDrawer.setColor(gameScreen.groundColor);
        float drawX = camera.position.x - camera.viewportWidth / 2 * camera.zoom;
        float drawWidth = camera.viewportWidth * camera.zoom;
        float drawY = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
        float drawHeight = 0 - drawY;
        
        if (drawHeight > 0) {
            int i = 0;
            vertices[i++] = drawX;
            vertices[i++] = drawY;
            vertices[i++] = drawX;
            vertices[i++] = drawY + drawHeight;
            vertices[i++] = drawX + drawWidth;
            vertices[i++] = drawY + drawHeight;
            vertices[i++] = drawX + drawWidth;
            vertices[i++] = drawY;
            shapeDrawer.filledPolygon(vertices);
        }
    }
    
    @Override
    public void destroy() {
    
    }
}
