package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class IndicatorEntity extends Entity {
    private Entity target;
    private final static Vector2 temp = new Vector2();
    private static final float BORDER = 30;
    
    public IndicatorEntity(Entity target) {
        this.target = target;
    }
    
    @Override
    public void create() {
        setSkeletonData(Core.core.assetManager.get("spine/indicator.json"), Core.core.assetManager.get("spine/indicator.json-animation"));
        animationState.setAnimation(0, "animation", true);
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (target.destroy) {
            destroy = true;
        } else {
            OrthographicCamera camera = GameScreen.gameScreen.camera;
            float rotation = Utils.pointDirection(camera.position.x, camera.position.y, target.x, target.y);
            float distance = Utils.pointDistance(camera.position.x, camera.position.y, target.x, target.y);
            float cameraDistance = Utils.min((camera.viewportWidth / 2 - BORDER) * camera.zoom, (camera.viewportHeight / 2 - BORDER) * camera.zoom, distance);
            skeleton.getRootBone().setRotation(rotation);
            
            if (cameraDistance < distance) {
                temp.set(cameraDistance, 0);
                temp.rotate(rotation);
                x = camera.position.x + temp.x;
                y = camera.position.y + temp.y;
            } else {
                x = target.x;
                y = target.y;
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
