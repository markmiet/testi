package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.Constants;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by mietmark on 13.7.2017.
 */
public class Car extends Box2dSprite implements IScript, Serializable {

    public Car(PlayScreen playscreen, String overlap2dIdentifier) {
        this.setPlayscreen(playscreen);
        this.setOverlap2dIdentifier(overlap2dIdentifier);
        this.setSl(new SceneLoader());
        this.getSl().loadScene("MainScene");
        this.setRootItem(new ItemWrapper(this.getSl().getRoot()));
        this.getRootItem().getChild(overlap2dIdentifier).addScript(this);
        generateChilds();
    }
    @Override
    public void generateChilds() {
        NodeComponent nc = ComponentRetriever.get(this.getSl().getRoot(), NodeComponent.class);
        for (Entity c : nc.children) {
            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
            CustomVariables customVariables = new CustomVariables();
            customVariables.loadFromString(m.customVars);
            String parentname = customVariables.getStringVariable("parentname");
            if (this.getOverlap2dIdentifier().equals(parentname)) {
                //child found
                String luokka = customVariables.getStringVariable("class");
                if ("Rengas".equals(luokka)) {
                    Tire r = new Tire(this.getPlayscreen(), m.itemIdentifier, this);
                } else {
                    Box2dSprite children = new Box2dSprite(this.getPlayscreen(), m.itemIdentifier, this);
                }
            }
        }
    }

    public RevoluteJoint getLeftJoint() {
        for (Box2dSprite r : this.getChildren()) {
            if (((Tire) r).isLeftjoint()) {
                return (RevoluteJoint) r.getJoint();
            }
        }
        return null;
    }

    public RevoluteJoint getRightJoint() {
        for (Box2dSprite r : this.getChildren()) {
            if (((Tire) r).isRightjoint()) {
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

    public void update(HashSet<InputManager.Key> keys) {
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Tire && ((Tire) r).getJoint() != null)
                ((Tire) r).updateFriction();
        }
        for (Box2dSprite r : this.getChildren()) {
            if (r instanceof Tire && ((Tire) r).getJoint() != null)
                ((Tire) r).updateDrive(keys);
        }
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
            if (leftJoint != null)
                leftJoint.setLimits(newAngle, newAngle);
            rightJoint.setLimits(newAngle, newAngle);
        }
    }
}