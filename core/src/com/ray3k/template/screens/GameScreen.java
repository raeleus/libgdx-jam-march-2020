package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.*;
import com.crashinvaders.vfx.VfxManager;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.OgmoReader;
import com.ray3k.template.OgmoReader.EntityNode;
import com.ray3k.template.OgmoReader.OgmoAdapter;
import com.ray3k.template.OgmoReader.OgmoValue;
import com.ray3k.template.entities.*;
import com.ray3k.template.screens.DialogPause.PauseListener;
import com.ray3k.template.transitions.Transitions;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public Color BG_COLOR = new Color();
    private Core core;
    public AssetManager assetManager;
    private Batch batch;
    public Stage stage;
    public Skin skin;
    public ShapeDrawer shapeDrawer;
    public EntityController entityController;
    private VfxManager vfxManager;
    public boolean paused;
    public CameraEntity cameraEntity;
    public Array<TerrainEntity> terrainEntities = new Array<>();
    public PlayerEntity playerEntity;
    public OgmoReader ogmoReader;
    public Array<LandTargetEntity> landTargets = new Array<>();
    public Array<AirTargetEntity> airTargets = new Array<>();
    public static final String[] levels = {"levels/tutorial.json", "levels/level1.json", "levels/test-level.json", "levels/test-level2.json"};
    public int levelIndex;
    public float levelWidth;
    public float levelHeight;
    private TextureRegion levelBackground;
    public OrthographicCamera backgroundCamera;
    public Viewport backgroundViewport;
    
    public GameScreen(int levelIndex) {
        this.levelIndex = levelIndex;
        gameScreen = this;
        core = Core.core;
        assetManager = core.assetManager;
        batch = core.batch;
        vfxManager = core.vfxManager;
        ogmoReader = new OgmoReader();
        
        BG_COLOR.set(Color.PINK);
    
        paused = false;
    
        final Music music = core.assetManager.get("bgm/game.mp3");
        music.setPosition(0);
        music.setVolume(core.bgm);
        music.play();
        
        stage = new Stage(new ExtendViewport(1024, 576), core.batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (!paused && keycode == Keys.ESCAPE) {
                    paused = true;
    
                    DialogPause dialogPause = new DialogPause(GameScreen.this);
                    dialogPause.show(stage);
                    dialogPause.addListener(new PauseListener() {
                        @Override
                        public void resume() {
                            paused = false;
                        }
        
                        @Override
                        public void quit() {
                            core.transition(new MenuScreen(), Transitions.slide(270, Interpolation.bounceOut), 1f);
                            music.pause();
                        }
                    });
                }
                return super.keyDown(event, keycode);
            }
        });
        
        skin = assetManager.get("skin/skin.json");
        shapeDrawer = new ShapeDrawer(core.batch, skin.getRegion("white"));
        shapeDrawer.setPixelSize(.5f);
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1024, 576, camera);
        
        backgroundCamera = new OrthographicCamera();
        backgroundViewport = new StretchViewport(1024, 576, backgroundCamera);
        backgroundViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        entityController = new EntityController();
        loadLevel(levels[levelIndex]);
    }
    
    @Override
    public void act(float delta) {
        if (!paused) {
            entityController.act(delta);
        }
        stage.act(delta);
    }
    
    @Override
    public void draw(float delta) {
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        vfxManager.cleanUpBuffers();
        vfxManager.beginCapture();
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        backgroundViewport.apply();
        batch.setProjectionMatrix(backgroundCamera.combined);
        batch.setColor(1, 1, 1, 1);
        batch.draw(levelBackground, 0, 0, backgroundViewport.getWorldWidth(), backgroundViewport.getWorldHeight());
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        entityController.draw(paused ? 0 : delta);
        batch.end();
        vfxManager.endCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
    
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        vfxManager.resize(width, height);
        viewport.update(width, height);
        backgroundViewport.update(width, height);
        
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
    
    @Override
    public void hide() {
        super.hide();
    }
    
    public void checkIfLevelComplete() {
        if (landTargets.size == 0 && airTargets.size == 0) {
            levelIndex++;
            if (levelIndex < levels.length) {
                entityController.add(new LoadGameScreenEntity(2f));
            } else {
                core.transition(new CreditsScreen(), Transitions.blinds(270, 5, Interpolation.linear), .5f);
            }
        }
    }
    
    public void loadLevel(String level) {
        gameScreen.ogmoReader.addListener(new OgmoAdapter() {
            int decalDepth;
    
            @Override
            public void level(String ogmoVersion, int width, int height, int offsetX, int offsetY,
                              ObjectMap<String, OgmoValue> valuesMap) {
                levelWidth = width;
                levelHeight = height;
                levelBackground = core.textureAtlas.findRegion(valuesMap.get("background").asString());
            }
    
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
                        float[] vertices = OgmoReader.nodesToVertices(nodes);
                        TerrainEntity terrainEntity = new TerrainEntity(vertices);
                        gameScreen.entityController.add(terrainEntity);
                        gameScreen.terrainEntities.add(terrainEntity);
                        break;
                    case "player":
                        PlayerEntity playerEntity = gameScreen.playerEntity = new PlayerEntity();
                        playerEntity.setPosition(x, y);
                        gameScreen.entityController.add(playerEntity);
                        gameScreen.entityController.add(gameScreen.cameraEntity = new CameraEntity());
                        break;
                    case "land-target":
                        LandTargetEntity landTargetEntity = new LandTargetEntity();
                        landTargetEntity.setPosition(x, y);
                        gameScreen.entityController.add(landTargetEntity);
                        gameScreen.landTargets.add(landTargetEntity);
                        break;
                    case "air-target":
                        AirTargetEntity airTargetEntity = new AirTargetEntity();
                        airTargetEntity.setPosition(x, y);
                        gameScreen.entityController.add(airTargetEntity);
                        gameScreen.airTargets.add(airTargetEntity);
                        break;
                }
            }
            
            @Override
            public void decal(int x, int y, float scaleX, float scaleY, float rotation, String texture, String folder) {
                AtlasRegion atlasRegion = core.textureAtlas.findRegion(texture);
                DecalEntity decalEntity = new DecalEntity(atlasRegion);
                decalEntity.depth = decalDepth;
                decalEntity.setPosition(x - atlasRegion.originalWidth / 2f, y - atlasRegion.originalHeight / 2f);
                decalEntity.rotation = rotation;
                gameScreen.entityController.add(decalEntity);
            }
        });
        gameScreen.ogmoReader.readFile(Gdx.files.internal(level));
    }
}
