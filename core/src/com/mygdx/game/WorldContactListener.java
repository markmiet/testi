package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by mietmark on 7.7.2017.
 */

public class WorldContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    PlayScreen screen;

    public WorldContactListener(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {
//        System.out.println(contact.getTangentSpeed());
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        Rengas r = null;
        if (fixA != null && fixA.getBody() != null && fixA.getBody().getUserData() instanceof Rengas) {
            r = (Rengas) fixA.getBody().getUserData();
        } else if (fixB != null && fixB.getBody() != null && fixB.getBody().getUserData() instanceof Rengas) {
            r = (Rengas) fixB.getBody().getUserData();

        }
        if (r != null) {
//            if (r.isFront() && r.isLeft()) {
//                r.getAuto().getPlayscreen().getWorld().destroyJoint(r.getAuto().getLeftJoint());
                if (r.getCurrentstate() != Box2dSprite.STATE.JOINT_DESTROYED) {
                    r.setCurrentstate(Box2dSprite.STATE.JOINT_TO_BE_DESTROYED);
                }

//            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
