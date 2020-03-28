package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ShortArray;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TerrainEntity extends Entity {
    private Core core;
    private ShapeDrawer shapeDrawer;
    public float[] vertices;
    public ShortArray shortArray;
    public float[] sortedTriangles;
    
    public TerrainEntity(float[] vertices) {
        core = Core.core;
        shapeDrawer = core.shapeDrawer;
        this.vertices = vertices;
    
        shortArray = new ShortArray();
        Utils.computeTriangles(vertices, shortArray);
        sortedTriangles = Utils.sortTriangles(vertices, shortArray);
        depth = Core.DEPTH_LANDSCAPE;
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
        shapeDrawer.setColor(GameScreen.gameScreen.groundColor);
        shapeDrawer.filledPolygon(vertices, shortArray);
    }
    
    @Override
    public void destroy() {
    
    }
}
