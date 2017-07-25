package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.Constants;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by mietmark on 13.7.2017.
 */
public class Car extends Box2dSprite implements IScript, Serializable {
//    public Car(PlayScreen playscreen, String overlap2dIdentifier) {
//
//    }

    float previousSpeed = 0;
//    @Override
//    public void generateChilds() {
//        NodeComponent nc = ComponentRetriever.get(this.getSl().getRoot(), NodeComponent.class);
//        for (Entity c : nc.children) {
//            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
//            CustomVariables customVariables = new CustomVariables();
//            customVariables.loadFromString(m.customVars);
//            String parentname = customVariables.getStringVariable("parentname");
//            if (this.getOverlap2dIdentifier().equals(parentname)) {
//                //child found
//                String luokka = customVariables.getStringVariable("class");
//                if ("Rengas".equals(luokka)) {
//                    Rengas r = new Rengas(this.getPlayscreen(), m.itemIdentifier, this);
//                } else {
//                    Box2dSprite children = new Box2dSprite(this.getPlayscreen(), m.itemIdentifier, this);
//                }
//            }
//        }
//    }
    long previousSpeedTimeStamp;
    float acc;

    public Car(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent) {
        this.setPlayscreen(playscreen);
        this.setOverlap2dIdentifier(overlap2dIdentifier);
        this.setSl(new SceneLoader());
        this.getSl().loadScene("MainScene");
        this.setRootItem(new ItemWrapper(this.getSl().getRoot()));
        this.getRootItem().getChild(overlap2dIdentifier).addScript(this);
        generateChilds();
    }

    public RevoluteJoint getLeftJoint() {
        for (Box2dSprite r : this.getChildren()) {
            if (((Rengas) r).isLeftjoint()) {
                return (RevoluteJoint) r.getJoint();
            }
        }
        return null;
    }

    public RevoluteJoint getRightJoint() {
        for (Box2dSprite r : this.getChildren()) {
            if (((Rengas) r).isRightjoint()) {
                return (RevoluteJoint) r.getJoint();
            }
        }
        return null;
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void defineMario() {
        super.defineMario();
        getJointDef().bodyA = getPhysicsBodyComponent().body;
    }

    public float getLeftFromtMaxLateralImpulse() {
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Rengas && ((Rengas) r).isFront() && ((Rengas) r).isLeft()) {
                return ((Rengas) r).getMaxLateralImpulse();
            }
        }
        return 0;
    }

    public void setLeftFromtMaxLateralImpulse(float f) {
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Rengas && ((Rengas) r).isFront() && ((Rengas) r).isLeft()) {
                ((Rengas) r).setMaxLateralImpulse(f);
            }
        }
    }

    public void setRightFromtMaxLateralImpulse(float f) {
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Rengas && ((Rengas) r).isFront() && !((Rengas) r).isLeft()) {
                ((Rengas) r).setMaxLateralImpulse(f);
            }
        }
    }

    public float getCurrentSpeed() {
        Vector2 currentForwardNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(0, 1));
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);
        return currentSpeed;
    }

    public float getAcceleration() {
        long timestamp = System.currentTimeMillis();
        float currentspeed = getCurrentSpeed();
        acc = (currentspeed - previousSpeed) / (System.currentTimeMillis() - previousSpeedTimeStamp);
//        float acc=(currentspeed-previousSpeed)/60;
        acc = acc * 100;
        previousSpeed = currentspeed;
        previousSpeedTimeStamp = timestamp;
//        this.getPhysicsBodyComponent().body.getMassData().center.set(0,acc);
        return acc;
    }

    public Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(0, 1));
        return CarMath.multiply(
                currentForwardNormal.dot(getPhysicsBodyComponent().body.getLinearVelocity()),
                currentForwardNormal);
    }

    public Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(1, 0));
        return CarMath.multiply(
                currentRightNormal.dot(getPhysicsBodyComponent().body.getLinearVelocity()),
                currentRightNormal);
    }
//    public float getLateralSpeed() {
//        Vector2 currentForwardNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(1, 0));
//        float currentSpeed = getLateralVelocity().dot(currentForwardNormal);
//        return currentSpeed;
//    }


    public void update(HashSet<InputManager.Key> keys) {
//        this.getPhysicsBodyComponent().body.getMassData().center.
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Rengas && ((Rengas) r).getJoint() != null)
                ((Rengas) r).updateFriction();
        }
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Rengas && ((Rengas) r).getJoint() != null)
                ((Rengas) r).updateDrive(keys);
//            if (r instanceof Painopiste) {
//
//                ((Painopiste)r).getJoint().getAnchorA().set(r.getOriginalJointX()+10,r.getOriginalJointY()+acc);
//            }

        }
        this.getPhysicsBodyComponent().body.getMassData().center.set( 0,acc

                );

        float lockAngle = 35 * Constants.DEGTORAD;
        float turnSpeedPerSec = 160 * Constants.DEGTORAD;
        float turnPerTimeStep = turnSpeedPerSec / 60.0f;
        float desiredAngle = 0;
        if (keys.contains(InputManager.Key.Left)) {
            desiredAngle = lockAngle;
        } else if (keys.contains(InputManager.Key.Right)) {
            desiredAngle = -lockAngle;
        }
        RevoluteJoint leftJoint = this.getLeftJoint();
        RevoluteJoint rightJoint = this.getRightJoint();
        if (leftJoint != null) {
            float angleNow = leftJoint.getJointAngle();
            float angleToTurn = desiredAngle - angleNow;
            angleToTurn = CarMath.clamp(angleToTurn, -turnPerTimeStep, turnPerTimeStep);
            float newAngle = angleNow + angleToTurn;
            leftJoint.setLimits(newAngle, newAngle);
            if (rightJoint != null) {
                rightJoint.setLimits(newAngle, newAngle);
            }
        } else if (rightJoint != null) {
            float angleNow = rightJoint.getJointAngle();
            float angleToTurn = desiredAngle - angleNow;
            angleToTurn = CarMath.clamp(angleToTurn, -turnPerTimeStep, turnPerTimeStep);
            float newAngle = angleNow + angleToTurn;
            if (leftJoint != null) {
                leftJoint.setLimits(newAngle, newAngle);
            }
            rightJoint.setLimits(newAngle, newAngle);
        }
    }
}