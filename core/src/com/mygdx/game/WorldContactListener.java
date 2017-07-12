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
        Box2dSprite r = null;
        Box2dSprite r2 = null;
        if (fixA != null && fixA.getBody() != null && fixA.getBody().getUserData() != null) {
            r = (Box2dSprite) fixA.getBody().getUserData();
        }
        if (fixB != null && fixB.getBody() != null && fixB.getBody().getUserData() != null) {
            r2 = (Box2dSprite) fixB.getBody().getUserData();
        }
        if (r != null) {
            if (r.getActionsToHappenWhenCollision() != null && !r.getActionsToHappenWhenCollision().isEmpty()) {
                for (Action a : r.getActionsToHappenWhenCollision())
                    r.addToActions(a);
            }


        }
        if (r2 != null) {
            if (r2.getActionsToHappenWhenCollision() != null && !r2.getActionsToHappenWhenCollision().isEmpty()) {
                for (Action a : r2.getActionsToHappenWhenCollision())
                    r2.addToActions(a);
            }

        }
//        if (r2!=null) {
//            if (r2.isRemoveFromParentInCollision()) {
//                r2.setCurrentstate(Box2dSprite.STATE.JOINT_TO_BE_DESTROYED);
//            }
//            else if (r2.isDestroyInCollision()) {
//                r2.setCurrentstate(Box2dSprite.STATE.TO_BE_DESTROYED);
//            }
//        }
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
