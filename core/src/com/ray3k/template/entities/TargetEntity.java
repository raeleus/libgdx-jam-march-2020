package com.ray3k.template.entities;

import static com.ray3k.template.screens.GameScreen.gameScreen;

public class TargetEntity extends MissileEntity {
    public TargetEntity() {
        setSkeletonData(gameScreen.assetManager.get("spine/target.json"), gameScreen.assetManager.get("spine/target.json-animation"));
        animationState.setAnimation(0, "animation", true);
    }
}
