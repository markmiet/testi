package com.mygdx.game;

import com.uwsoft.editor.renderer.scripts.IScript;

/**
 * Created by mietmark on 25.7.2017.
 */
public class Painopiste extends Box2dSprite implements IScript {
    public Painopiste(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent) {
        super(playscreen, overlap2dIdentifier, parent);
    }
}
