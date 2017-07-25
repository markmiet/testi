package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.uwsoft.editor.renderer.scripts.IScript;

/**
 * Created by mietmark on 19.7.2017.
 */
public class Door extends Box2dSprite implements IScript {
    private boolean open;

    public Door(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent) {
        super(playscreen, overlap2dIdentifier, parent);
    }
}
