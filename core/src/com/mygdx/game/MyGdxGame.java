package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends Game {
    SpriteBatch batch;
    //	Texture img;
    public static final int V_WIDTH = 1920;
    public static final int V_HEIGHT = 1200;
    public static final float PPM = 10;


    @Override
    public void create() {
        batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
        log=new FPSLogger();
        setScreen(new PlayScreen(this));

    }
private FPSLogger log;
    @Override
    public void dispose() {
        super.dispose();
//		manager.dispose();
        batch.dispose();
    }

    @Override
    public void render()
    {
//        log.log();
    super.render();
    }
}
