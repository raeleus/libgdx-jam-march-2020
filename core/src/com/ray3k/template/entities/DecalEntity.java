package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;

public class DecalEntity extends Entity {
    private AtlasSprite sprite;
    public DecalEntity(AtlasRegion atlasRegion) {
        sprite = new AtlasSprite(atlasRegion);
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        sprite.setX(x);
        sprite.setY(y);
    }
    
    @Override
    public void draw(float delta) {
        sprite.draw(batch);
    }
    
    @Override
    public void destroy() {
    
    }
}
