package com.mygdx.game;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.Constants;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by mietmark on 6.7.2017.
 */

public class Auto extends Box2dSprite implements IScript, Serializable {

    private ArrayList<Rengas> tires;
    //    private RevoluteJoint leftJoint, rightJoint;
    private RevoluteJointDef jointDef = new RevoluteJointDef();

    public Auto(PlayScreen playscreen, String overlap2dIdentifier) {
        this.setPlayscreen(playscreen);
        this.setOverlap2dIdentifier(overlap2dIdentifier);
        this.setSl(new SceneLoader());
        this.getSl().loadScene("MainScene");
        this.setRootItem(new ItemWrapper(this.getSl().getRoot()));
        this.getRootItem().getChild(overlap2dIdentifier).addScript(this);
//        super(playscreen, overlap2dIdentifier);
    }

    public RevoluteJoint getLeftJoint() {
        for (Rengas r : tires) {
            if (r.isLeft() && r.isFront()) {
                return (RevoluteJoint) r.getJoint();
            }
        }
        return null;
//        return leftJoint;
    }
//
//    public void setLeftJoint(RevoluteJoint leftJoint) {
//        this.leftJoint = leftJoint;
//    }

    public RevoluteJoint getRightJoint() {
        for (Rengas r : tires) {
            if (!r.isLeft() && r.isFront()) {
                return (RevoluteJoint) r.getJoint();
            }
        }
//        return rightJoint;
        return null;
    }
//
//    public void setRightJoint(RevoluteJoint rightJoint) {
//        this.rightJoint = rightJoint;
//    }

    public RevoluteJointDef getJointDef() {
        return jointDef;
    }

    public void setJointDef(RevoluteJointDef jointDef) {
        this.jointDef = jointDef;
    }

    public ArrayList<Rengas> getTires() {
        return tires;
    }

    public void setTires(ArrayList<Rengas> tires) {
        this.tires = tires;
    }


    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void defineMario() {
        //
        PhysicsBodyLoader instanssi =
                PhysicsBodyLoader.getInstance();
        getPhysicsBodyComponent().body =
                instanssi.createBody(getPlayscreen().getWorld(), this.entity, getPhysicsBodyComponent(), this.getPolygonComponent().vertices,
                        this.getTransformComponent());
        getPhysicsBodyComponent().body.setUserData(this);
        getPhysicsBodyComponent().body.setAngularDamping(3);
        getPhysicsBodyComponent().body.setLinearDamping(2f);
        if (w == 0) {
            BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(getPhysicsBodyComponent().body);
            w = (boundingBox.max.x - boundingBox.min.x);
            h = (boundingBox.max.y - boundingBox.min.y);
        }
        tires = new ArrayList<Rengas>();
        jointDef.bodyA = getPhysicsBodyComponent().body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.localAnchorB.setZero();
        float maxForwardSpeed = 6650;
        float maxBackwardSpeed = -40;
        float backTireMaxDriveForce = 1300;
        float frontTireMaxDriveForce = 1500;
        float backTireMaxLateralImpulse = 8.5f;
        float frontTireMaxLateralImpulse = 7.5f;
        Rengas tire;
        tire = new Rengas(this.getPlayscreen(), this, true, true, "vaseneturengas");
        tire.setParent(this);
        tires.add(tire);
        Rengas tire2 = new Rengas(this.getPlayscreen(), this, true, false, "oikeaeturengas");
        tire2.setParent(this);
        tires.add(tire2);
        Rengas vasentakarengas = new Rengas(this.getPlayscreen(), this, false, true, "vasentakarengas");
        vasentakarengas.setParent(this);
        tires.add(vasentakarengas);
        Rengas oikeatakarengas = new Rengas(this.getPlayscreen(), this, false, false, "oikeatakarengas");
        oikeatakarengas.setParent(this);
        tires.add(oikeatakarengas);
        this.getChildren().addAll(tires);
    }

    public void update(HashSet<InputManager.Key> keys) {
        for (Rengas tire : tires) {
//            if (tire.getCurrentstate() != STATE.DESTROYED && tire.getCurrentstate() != STATE.TO_BE_DESTROYED)
//                tire.updateFriction();
            if (tire.getCurrentstate() != STATE.DESTROYED && tire.getCurrentstate() != STATE.TO_BE_DESTROYED &&
                    tire.getCurrentstate() != STATE.JOINT_DESTROYED && tire.getCurrentstate() != STATE.JOINT_TO_BE_DESTROYED
                    ) {
                tire.updateFriction();
            }
        }
        for (Rengas tire : tires) {
            if (tire.getCurrentstate() != STATE.DESTROYED && tire.getCurrentstate() != STATE.TO_BE_DESTROYED &&
                    tire.getCurrentstate() != STATE.JOINT_DESTROYED && tire.getCurrentstate() != STATE.JOINT_TO_BE_DESTROYED
                    ) {
                tire.updateDrive(keys);
            }
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
            if (rightJoint!=null)
            rightJoint.setLimits(newAngle, newAngle);
        } else if (rightJoint != null) {
            float angleNow = rightJoint.getJointAngle();
            float angleToTurn = desiredAngle - angleNow;
            angleToTurn = CarMath.clamp(angleToTurn, -turnPerTimeStep, turnPerTimeStep);
            float newAngle = angleNow + angleToTurn;
            if (leftJoint!=null)
            leftJoint.setLimits(newAngle, newAngle);
            rightJoint.setLimits(newAngle, newAngle);
        }


    }
//    public void update(float dt) {
//        super.update(dt);
//        for (Rengas r:this.getTires()) {
//            r.update(dt);
//        }
//    }


}
