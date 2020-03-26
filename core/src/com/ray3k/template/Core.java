package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.crashinvaders.vfx.VfxManager;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.ray3k.template.AnimationStateDataLoader.AnimationStateDataParameter;
import com.ray3k.template.screens.LoadScreen;
import com.ray3k.template.transitions.Transitions;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Core extends JamGame {
    public static final String PROJECT_NAME = "VTOL Vertical Take-Off and Landing";
    public static Core core;
    public Skin skin;
    public SkeletonRenderer skeletonRenderer;
    public ChangeListener sndChangeListener;
    public VfxManager vfxManager;
    public CrossPlatformWorker crossPlatformWorker;
    public static enum Binding {
        ROTATE_LEFT, ROTATE_RIGHT, THRUST, CLAW_SHOOT;
    }
    public float bgm;
    public float sfx;
    public Preferences preferences;
    public ShapeDrawer shapeDrawer;
    
    @Override
    public void create() {
        super.create();
        core = this;
        
        preferences = Gdx.app.getPreferences(PROJECT_NAME);
        
        bgm = preferences.getFloat("bgm", 1.0f);
        sfx = preferences.getFloat("sfx", 1.0f);
        
        setDefaultBindings();
        JamScreen.loadBindings();
        
        crossPlatformWorker.create();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                assetManager.get("sfx/click.mp3", Sound.class).play();
            }
        };
        
        setScreen(new LoadScreen(() -> {
            core.skin = core.assetManager.get("skin/skin.json");
            shapeDrawer = new ShapeDrawer(batch, skin.getRegion("white"));
        }));
        defaultTransition = Transitions.wipe(45);
        defaultTransitionDuration = .5f;
    }
    
    @Override
    public void dispose() {
        vfxManager.dispose();
        assetManager.dispose();
        
        super.dispose();
    }
    
    @Override
    public void loadAssets() {
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        assetManager.setLoader(Skin.class, new SkinFreeTypeLoader(assetManager.getFileHandleResolver()));
        assetManager.setLoader(AnimationStateData.class, new AnimationStateDataLoader(assetManager.getFileHandleResolver()));
        
        FileHandle fileHandle = Gdx.files.internal("skin.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Skin.class);
        }
    
        fileHandle = Gdx.files.internal("bgm.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Music.class);
        }
    
        fileHandle = Gdx.files.internal("sfx.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Sound.class);
        }
    
        fileHandle = Gdx.files.internal("spine-atlas.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            fileHandle = Gdx.files.internal("spine.txt");
            if (fileHandle.exists()) for (String path2 : fileHandle.readString().split("\\n")) {
                assetManager.load(path2 + "-animation", AnimationStateData.class, new AnimationStateDataParameter(path2, path));
            }
            break;
        }
    }
    
    public void setDefaultBindings() {
        JamScreen.addKeyBinding(Binding.ROTATE_LEFT, Input.Keys.LEFT);
        JamScreen.addKeyBinding(Binding.ROTATE_RIGHT, Input.Keys.RIGHT);
        JamScreen.addKeyBinding(Binding.THRUST, Input.Keys.UP);
        JamScreen.addKeyBinding(Binding.CLAW_SHOOT, Input.Keys.Z);
    }
}
