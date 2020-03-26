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

public class LandscapeEntity extends Entity {
    private Core core;
    private ShapeDrawer shapeDrawer;
    private GameScreen gameScreen;
    private float[] vertices;
    private ShortArray shortArray;
    
    @Override
    public void create() {
        core = Core.core;
        shapeDrawer = core.shapeDrawer;
        gameScreen = GameScreen.gameScreen;
        
        gameScreen.ogmoReader.addListener(new OgmoAdapter() {
            int decalDepth;
            
            @Override
            public void layer(String name, int gridCellWidth, int gridCellHeight, int offsetX, int offsetY) {
                switch (name) {
                    case "decals-front":
                        decalDepth = Core.DEPTH_DECAL_FRONT;
                        break;
                    case "decals-back":
                        decalDepth = Core.DEPTH_DECAL_BACK;
                        break;
                }
            }
    
            @Override
            public void entity(String name, int id, int x, int y, int width, int height, boolean flippedX,
                               boolean flippedY, int originX, int originY, int rotation, Array<EntityNode> nodes,
                               ObjectMap<String, OgmoValue> valuesMap) {

                switch(name) {
                    case "terrain":
                        nodes.insert(0, new EntityNode(x, y));
                        vertices = OgmoReader.nodesToVertices(nodes);
                        break;
                    case "player":
                        gameScreen.playerEntity.setPosition(x, y);
                        break;
                }
            }
    
            @Override
            public void decal(int x, int y, float scaleX, float scaleY, int rotation, String texture, String folder) {
                AtlasRegion atlasRegion = core.textureAtlas.findRegion(texture);
                DecalEntity decalEntity = new DecalEntity(atlasRegion);
                decalEntity.depth = decalDepth;
                decalEntity.setPosition(x - atlasRegion.originalWidth / 2f, y - atlasRegion.originalHeight / 2f);
                gameScreen.entityController.add(decalEntity);
            }
        });
        gameScreen.ogmoReader.readFile(Gdx.files.internal("levels/test-level.json"));
        shortArray = Utils.computeTriangles(vertices);
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
        shapeDrawer.setColor(Color.GREEN);
        shapeDrawer.filledPolygon(vertices, shortArray);
    }
    
    @Override
    public void destroy() {
    
    }
}
