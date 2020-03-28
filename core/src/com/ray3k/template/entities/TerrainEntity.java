package com.ray3k.template.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ShortArray;
import com.ray3k.template.Core;
import com.ray3k.template.OgmoReader;
import com.ray3k.template.OgmoReader.EntityNode;
import com.ray3k.template.OgmoReader.OgmoAdapter;
import com.ray3k.template.OgmoReader.OgmoValue;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TerrainEntity extends Entity {
    private Core core;
    private ShapeDrawer shapeDrawer;
    public float[] vertices;
    public ShortArray shortArray;
    public float[] sortedTriangles;
    private Color color;
    
    public TerrainEntity(float[] vertices) {
        core = Core.core;
        shapeDrawer = core.shapeDrawer;
        this.vertices = vertices;
    
        shortArray = new ShortArray();
        Utils.computeTriangles(vertices, shortArray);
        sortedTriangles = Utils.sortTriangles(vertices, shortArray);
        depth = Core.DEPTH_LANDSCAPE;
        
        color = new Color(54 / 255f, 71 / 255f, 0, 1);
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
        shapeDrawer.setColor(color);
        shapeDrawer.filledPolygon(vertices, shortArray);
    }
    
    @Override
    public void destroy() {
    
    }
}
