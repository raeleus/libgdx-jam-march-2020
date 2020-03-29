package com.ray3k.template.entities;

public interface Attachable {
    public void attachTo(PlayerEntity playerEntity);
    public void detach();
    public boolean checkForCollision();
}
