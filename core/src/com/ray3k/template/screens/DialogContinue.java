package com.ray3k.template.screens;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class DialogContinue extends Dialog {
    private Core core;
    private Skin skin;
    
    public DialogContinue() {
        super("", Core.core.skin);
        core = Core.core;
        skin = getSkin();
    
        Table root = getContentTable();
        
        root.pad(10);
        root.defaults().space(10);
        Label label = new Label("CONTINUE", skin);
        root.add(label);
        
        root.row();
        label = new Label("Resume game from save?", skin);
        label.setAlignment(Align.center);
        root.add(label);
        
        root.row();
        Table table = new Table();
        root.add(table);
    
        table.defaults().space(10);
        TextButton textButton = new TextButton("Continue Game", skin);
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fire(new ContinueEvent());
            }
        });
        
        textButton = new TextButton("New Game", skin);
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fire(new NewEvent());
            }
        });
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
    }
    
    public static class ContinueEvent extends Event {
    
    }
    
    public static class NewEvent extends Event {
    
    }
    
    public static abstract class ContinueListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof ContinueEvent) {
                continueGame();
                return true;
            } else if (event instanceof NewEvent) {
                newGame();
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void continueGame();
        public abstract void newGame();
    }
}
