package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
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
    public VfxManager vfxManager;
    public boolean paused;
    public CameraEntity cameraEntity;
    public Array<TerrainEntity> terrainEntities = new Array<>();
    public PlayerEntity playerEntity;
    public OgmoReader ogmoReader;
    public Array<Entity> objectives = new Array<>();
    public Array<LandTargetEntity> landTargets = new Array<>();
    public Array<AirTargetEntity> airTargets = new Array<>();
    public Array<CarrierEntity> carriers = new Array<>();
    public Array<GunEntity> guns = new Array<>();
    public Array<MissileEntity> missiles = new Array<>();
    public Array<CityEntity> cities = new Array<>();
    public Array<NukeEntity> nukes = new Array<>();
    public Array<CityMissileSpawnerEntity> cityMissileSpawners = new Array<>();
    public Array<CarrierLandTargetEntity> carrierLandTargets = new Array<>();
    public Array<CarrierAirTargetEntity> carrierAirTargets = new Array<>();
    public static final String[] levels = {"levels/tutorial.json", "levels/level1.json", "levels/level2.json", "levels/level3.json", "levels/tutorial2.json", "levels/level4.json", "levels/level5.json", "levels/tutorial3.json", "levels/level6.json", "levels/level7.json", "levels/level8.json", "levels/level9.json", "levels/level10.json", "levels/end.json"};
    public int levelIndex;
    public float levelWidth;
    public float levelHeight;
    private AtlasSprite levelBackground;
    public OrthographicCamera backgroundCamera;
    public Viewport backgroundViewport;
    public Color groundColor;
    public static Music musicGame;
    public static Sound soundExplosion;
    public static Sound soundNuke;
    public static Sound soundWreck;
    public static Sound soundSmallExplosion;
    public static Music musicMinigun;
    public static Music musicThrust;
    public static Sound soundChime;
    public static Sound soundContainer;
    public static Sound soundLand;
    public static Sound soundClaw;
    public static Sound soundRelease;
    public static Sound soundError;
    public static Sound soundWinch;
    
    public GameScreen(int levelIndex) {
        this.levelIndex = levelIndex;
        gameScreen = this;
        core = Core.core;
        assetManager = core.assetManager;
        batch = core.batch;
        vfxManager = core.vfxManager;
        ogmoReader = new OgmoReader();
        soundExplosion = assetManager.get("sfx/explosion.mp3");
        soundNuke = assetManager.get("sfx/nuke.mp3");
        soundWreck = assetManager.get("sfx/wreck.mp3");
        soundSmallExplosion = assetManager.get("sfx/small-explosion.mp3");
        musicMinigun = assetManager.get("bgm/minigun.mp3");
        musicThrust = assetManager.get("bgm/thrust.mp3");
        musicThrust.setLooping(true);
        musicThrust.setVolume(Core.core.sfx);
        soundChime = assetManager.get("sfx/chime.mp3");
        soundContainer = assetManager.get("sfx/container.mp3");
        soundLand = assetManager.get("sfx/land.mp3");
        soundClaw = assetManager.get("sfx/claw.mp3");
        soundRelease = assetManager.get("sfx/release.mp3");
        soundError = assetManager.get("sfx/error.mp3");
        soundWinch = assetManager.get("sfx/winch.mp3");
        
        BG_COLOR.set(Color.PINK);
    
        paused = false;
    
        musicGame = core.assetManager.get("bgm/game.mp3");
        musicGame.setVolume(core.bgm * .25f);
        musicGame.setLooping(true);
        musicGame.play();
        
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
                            musicGame.pause();
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
        backgroundViewport = new FillViewport(1024, 576, backgroundCamera);
        backgroundViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    
        groundColor = new Color(54 / 255f, 71 / 255f, 0, 1);
        
        entityController = new EntityController();
        entityController.add(new GroundDrawerEntity());
        loadLevel(levels[levelIndex]);
        
        Core.core.preferences.putInteger("levelIndex", levelIndex);
        Core.core.preferences.flush();
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
        levelBackground.setPosition(0, 0);
        levelBackground.setSize(backgroundViewport.getWorldWidth(), backgroundViewport.getWorldHeight());
        levelBackground.draw(batch);
        batch.flush();
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
        System.out.println("resize");
        
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        vfxManager.removeAllEffects();
    }
    
    @Override
    public void hide() {
        super.hide();
        vfxManager.removeAllEffects();
        musicMinigun.stop();
        musicThrust.stop();
    }
    
    public void checkIfLevelComplete() {
        if (objectives.size == 0) {
            levelIndex++;
            if (levelIndex < levels.length) {
                entityController.add(new LoadGameScreenEntity(2f));
            } else {
                core.transition(new CreditsScreen(), Transitions.blinds(270, 5, Interpolation.linear), .5f);
                musicGame.stop();
                Core.core.preferences.remove("levelIndex");
                Core.core.preferences.flush();
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
                levelBackground = new AtlasSprite(core.textureAtlas.findRegion(valuesMap.get("background").asString()));
                groundColor.set(valuesMap.get("groundColor").asColor());
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
                        objectives.add(landTargetEntity);
                        gameScreen.entityController.add(new IndicatorEntity(landTargetEntity));
                        break;
                    case "air-target":
                        AirTargetEntity airTargetEntity = new AirTargetEntity();
                        airTargetEntity.setPosition(x, y);
                        gameScreen.entityController.add(airTargetEntity);
                        gameScreen.airTargets.add(airTargetEntity);
                        objectives.add(airTargetEntity);
                        gameScreen.entityController.add(new IndicatorEntity(airTargetEntity));
                        break;
                    case "carrier":
                        CarrierEntity carrierEntity = new CarrierEntity();
                        carrierEntity.setPosition(x, y);
                        gameScreen.entityController.add(carrierEntity);
                        carriers.add(carrierEntity);
                        gameScreen.entityController.add(new IndicatorEntity(carrierEntity));
                        break;
                    case "carrier-target":
                        CarrierLandTargetEntity carrierLandTargetEntity = new CarrierLandTargetEntity();
                        carrierLandTargetEntity.setPosition(x, y);
                        gameScreen.entityController.add(carrierLandTargetEntity);
                        gameScreen.carrierLandTargets.add(carrierLandTargetEntity);
                        objectives.add(carrierLandTargetEntity);
                        gameScreen.entityController.add(new IndicatorEntity(carrierLandTargetEntity));
                        break;
                    case "carrier-target-air":
                        CarrierAirTargetEntity carrierAirTargetEntity = new CarrierAirTargetEntity();
                        carrierAirTargetEntity.setPosition(x, y);
                        gameScreen.entityController.add(carrierAirTargetEntity);
                        gameScreen.carrierAirTargets.add(carrierAirTargetEntity);
                        objectives.add(carrierAirTargetEntity);
                        gameScreen.entityController.add(new IndicatorEntity(carrierAirTargetEntity));
                        break;
                    case "nuke":
                        NukeEntity nuke = new NukeEntity();
                        nuke.setPosition(x, y);
                        gameScreen.entityController.add(nuke);
                        gameScreen.entityController.add(new IndicatorEntity(nuke));
                        nukes.add(nuke);
                        break;
                    case "gun":
                        GunEntity gunEntity = new GunEntity();
                        gunEntity.setPosition(x, y);
                        gameScreen.entityController.add(gunEntity);
                        guns.add(gunEntity);
                        gameScreen.entityController.add(new IndicatorEntity(gunEntity));
                        break;
                    case "missile":
                        MissileEntity missileEntity = new MissileEntity();
                        missileEntity.setPosition(x, y);
                        gameScreen.entityController.add(missileEntity);
                        missiles.add(missileEntity);
                        break;
                    case "target":
                        missileEntity = new TargetEntity();
                        missileEntity.setPosition(x, y);
                        gameScreen.entityController.add(missileEntity);
                        missiles.add(missileEntity);
                        objectives.add(missileEntity);
                        gameScreen.entityController.add(new IndicatorEntity(missileEntity));
                        break;
                    case "city-missile-spawner":
                        CityMissileSpawnerEntity cityMissileSpawnerEntity = new CityMissileSpawnerEntity();
                        cityMissileSpawnerEntity.setPosition(x, y);
                        cityMissileSpawnerEntity.speed = valuesMap.get("speed", new OgmoReader.OgmoValue("speed", 200f)).asFloat();
                        cityMissileSpawnerEntity.delay = valuesMap.get("delay", new OgmoReader.OgmoValue("delay", 5f)).asFloat();
                        cityMissileSpawnerEntity.count = valuesMap.get("count", new OgmoReader.OgmoValue("delay", 10)).asInt();
                        gameScreen.entityController.add(cityMissileSpawnerEntity);
                        if (cityMissileSpawnerEntity.count > -1) objectives.add(cityMissileSpawnerEntity);
                        cityMissileSpawners.add(cityMissileSpawnerEntity);
                        break;
                    case "city":
                        CityEntity cityEntity = new CityEntity();
                        cityEntity.setPosition(x, y);
                        cityEntity.evil = valuesMap.get("evil", new OgmoReader.OgmoValue("evil", false)).asBoolean();
                        if (cityEntity.evil) objectives.add(cityEntity);
                        gameScreen.entityController.add(cityEntity);
                        cities.add(cityEntity);
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
