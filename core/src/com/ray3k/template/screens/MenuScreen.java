package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.transitions.TransitionFlyThrough;
import com.ray3k.template.transitions.Transitions;

public class MenuScreen extends JamScreen {
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    private Array<Actor> focusables;
    
    public MenuScreen() {
        focusables = new Array<>();
    }
    
    @Override
    public void show() {
        super.show();
        
        core = Core.core;
        skin = core.skin;
        
        final Music bgm = core.assetManager.get("bgm/menu.mp3");
        if (!bgm.isPlaying()) {
            bgm.play();
            bgm.setVolume(core.bgm);
            bgm.setLooping(true);
        }
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
        
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                boolean shifting = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
                switch (keycode) {
                    case Keys.TAB:
                        if (shifting) {
                            previous();
                        } else {
                            next();
                        }
                        break;
                    case Keys.RIGHT:
                    case Keys.D:
                    case Keys.DOWN:
                    case Keys.S:
                        next();
                        break;
                    case Keys.LEFT:
                    case Keys.A:
                    case Keys.UP:
                    case Keys.W:
                        previous();
                        break;
                    case Keys.SPACE:
                    case Keys.ENTER:
                        activate();
                }
                return super.keyDown(event, keycode);
            }
            
            public void next() {
                Actor focused = stage.getKeyboardFocus();
                if (focused == null) {
                    stage.setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focused, true) + 1;
                    if (index >= focusables.size) index = 0;
                    stage.setKeyboardFocus(focusables.get(index));
                }
            }
            
            public void previous() {
                Actor focused = stage.getKeyboardFocus();
                if (focused == null) {
                    stage.setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focused, true) - 1;
                    if (index < 0) index = focusables.size - 1;
                    stage.setKeyboardFocus(focusables.get(index));
                }
            }
            
            public void activate() {
                Actor focused = stage.getKeyboardFocus();
                if (focused != null) {
                    focused.fire(new ChangeEvent());
                } else {
                    stage.setKeyboardFocus(focusables.first());
                }
            }
        });
        
        InputListener mouseEnterListener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setKeyboardFocus(null);
            }
        };
        
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("bg-10"));
        root.pad(20);
        stage.addActor(root);
        
        Image image = new Image(skin, "logo");
        image.setScaling(Scaling.fit);
        root.add(image).minSize(0);
        
        root.row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("window-10"));
        root.add(table).space(10);
        
        Button button = new Button(skin, "start");
        table.add(button);
        focusables.add(button);
        button.addListener(core.sndChangeListener);
        button.addListener(mouseEnterListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                core.transition(new GameScreen(), Transitions.flyThrough(2, Interpolation.exp10), 1f);
                bgm.stop();
            }
        });
        
        button = new Button(skin, "options");
        table.add(button);
        focusables.add(button);
        button.addListener(core.sndChangeListener);
        button.addListener(mouseEnterListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                core.transition(new OptionsScreen());
            }
        });
        
        button = new Button(skin, "credits");
        table.add(button);
        focusables.add(button);
        button.addListener(core.sndChangeListener);
        button.addListener(mouseEnterListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                core.transition(new CreditsScreen());
            }
        });
        
        root.row();
        Label label = new Label("Copyright Raymond \"Raeleus\" Buckley © 2020", skin);
        root.add(label);
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        core.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}