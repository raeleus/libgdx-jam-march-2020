package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.VfxManager;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.entities.EntityController;
import com.ray3k.template.screens.DialogPause.PauseListener;
import com.ray3k.template.transitions.Transitions;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = new Color();
    private Core core;
    public AssetManager assetManager;
    private Batch batch;
    public Stage stage;
    public Skin skin;
    public ShapeDrawer shapeDrawer;
    public EntityController entityController;
    private VfxManager vfxManager;
    public boolean paused;
    
    public GameScreen() {
        gameScreen = this;
        core = Core.core;
        assetManager = core.assetManager;
        batch = core.batch;
        vfxManager = core.vfxManager;
        
        BG_COLOR.set(Color.PINK);
    
        paused = false;
    
        final Music music = core.assetManager.get("bgm/game.mp3");
        music.setPosition(0);
        music.play();
        
        stage = new Stage(new ScreenViewport(), core.batch);
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
        viewport = new FitViewport(1024, 576, camera);
        
        entityController = new EntityController();
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
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        vfxManager.cleanUpBuffers();
        vfxManager.beginCapture();
        batch.begin();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        entityController.draw(paused ? 0 : delta);
        batch.end();
        vfxManager.endCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        vfxManager.resize(width, height);
        viewport.update(width, height);
        
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
}
