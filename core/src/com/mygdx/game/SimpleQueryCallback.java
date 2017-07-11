package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mietmark on 5.7.2017.
 */

public class SimpleQueryCallback implements QueryCallback {

    public List<Body> bodies = new ArrayList<Body>();

    private boolean returnValue;

    /**
     * @param returnValue {@code false} to find only one (random) body, {@code true} to
     *                    find all.
     */
    public SimpleQueryCallback(boolean returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        bodies.add(fixture.getBody());
        return returnValue;
    }

}