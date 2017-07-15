package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.CarMoves;
import com.tarashgames.car.GroundAreaType;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;

import java.util.HashSet;

/**
 * Created by mietmark on 13.7.2017.
 */
public class Tire extends Box2dSprite implements IScript {
    float maxForwardSpeed;
    float maxBackwardSpeed;
    float maxDriveForce;
    float maxLateralImpulse;
    Array<GroundAreaType> groundAreas;
    float currentTraction;
    //    private Car auto;
    private boolean left;
    private boolean front;
    private boolean leftjoint;
    private boolean rightjoint;

    public Tire(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent) {
//        this.setActionsToHappenWhenCollision(collisionActions);
//        this.setPlayscreen(playscreen);
//        this.setOverlap2dIdentifier(overlap2dIdentifier);
//        this.auto = (Car) parent;
//        this.setSl(new SceneLoader());
//        this.getSl().loadScene("MainScene");
//        this.setRootItem(new ItemWrapper(this.getSl().getRoot()));
//        this.getRootItem().getChild(overlap2dIdentifier).addScript(this);
        super(playscreen, overlap2dIdentifier, parent);
        currentTraction = 1;
    }

    public boolean isLeftjoint() {
        return leftjoint;
    }

    public void setLeftjoint(boolean leftjoint) {
        this.leftjoint = leftjoint;
    }

    public boolean isRightjoint() {
        return rightjoint;
    }

    public void setRightjoint(boolean rightjoint) {
        this.rightjoint = rightjoint;
    }
//    public Car getAuto() {
//        return auto;
//    }

    public boolean isLeft() {
        return left;
    }

    public boolean isFront() {
        return front;
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    public void defineMario() {
        super.defineMario();
        MainItemComponent m = ComponentRetriever.get(this.entity, MainItemComponent.class);
        CustomVariables customVariables = new CustomVariables();
        customVariables.loadFromString(m.customVars);
        String l = customVariables.getStringVariable("left");
        String f = customVariables.getStringVariable("front");
        this.left = Boolean.parseBoolean(l);
        this.front = Boolean.parseBoolean(f);
        String rj = customVariables.getStringVariable("rightjoint");
        if (rj != null) {
            this.rightjoint = Boolean.parseBoolean(rj);
        }
        String lj = customVariables.getStringVariable("leftjoint");
        if (lj != null) {
            this.leftjoint = Boolean.parseBoolean(lj);
        }
//        float maxForwardSpeed = 6650;
//        float maxBackwardSpeed = -40;
//        float backTireMaxDriveForce = 1300;
//        float frontTireMaxDriveForce = 1500;
//        float backTireMaxLateralImpulse = 8.5f;
//        float frontTireMaxLateralImpulse = 7.5f;
        MainItemComponent mCar = ComponentRetriever.get(this.getParent().getEntity(), MainItemComponent.class);
        CustomVariables customVariablesCar = new CustomVariables();
        customVariablesCar.loadFromString(mCar.customVars);
        float maxForwardSpeed = customVariablesCar.getFloatVariable("maxForwardSpeed");
        float maxBackwardSpeed = customVariablesCar.getFloatVariable("maxBackwardSpeed");
        float backTireMaxDriveForce = customVariablesCar.getFloatVariable("backTireMaxDriveForce");
        float frontTireMaxDriveForce = customVariablesCar.getFloatVariable("frontTireMaxDriveForce");
        float backTireMaxLateralImpulse = customVariablesCar.getFloatVariable("backTireMaxLateralImpulse");
        float frontTireMaxLateralImpulse = customVariablesCar.getFloatVariable("frontTireMaxLateralImpulse");
//        jointDef.bodyB = getPhysicsBodyComponent().body;
        if (this.front && this.left) {
//            jointDef.localAnchorA.set(-3, 3f);
//            RevoluteJoint r = (RevoluteJoint) this.getPlayscreen().getWorld().createJoint(jointDef);
//            auto.setLeftJoint(r);
//            setJoint(this.getPlayscreen().getWorld().createJoint(jointDef));
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        } else if (this.front && !left) {
//            jointDef.localAnchorA.set(3, 3f);
//            auto.setRightJoint((RevoluteJoint) this.getPlayscreen().getWorld().createJoint(jointDef));
//            RevoluteJoint r = (RevoluteJoint) this.getPlayscreen().getWorld().createJoint(jointDef);
//            setJoint(this.getPlayscreen().getWorld().createJoint(jointDef));
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        } else if (!this.front && this.left) {
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    backTireMaxDriveForce, backTireMaxLateralImpulse);
//            setJoint(this.getPlayscreen().getWorld().createJoint(jointDef));
        } else if (!this.front && !this.left) {
//            jointDef.localAnchorA.set(3, -3f);
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    backTireMaxDriveForce, backTireMaxLateralImpulse);
//            setJoint(this.getPlayscreen().getWorld().createJoint(jointDef));
        }
    }

    public void addGroundArea(GroundAreaType item) {
        groundAreas.add(item);
        updateTraction();
    }

    public void removeGroundArea(GroundAreaType item) {
        groundAreas.removeValue(item, false);
        updateTraction();
    }

    void setCharacteristics(float maxForwardSpeed, float maxBackwardSpeed,
                            float maxDriveForce, float maxLateralImpulse) {
        this.maxForwardSpeed = maxForwardSpeed;
        this.maxBackwardSpeed = maxBackwardSpeed;
        this.maxDriveForce = maxDriveForce;
        this.maxLateralImpulse = maxLateralImpulse;
    }

    public void updateTraction() {
        if (groundAreas.size == 0) {
            currentTraction = 1;
            return;
        }
        currentTraction = 0;
        for (GroundAreaType groundType : groundAreas) {
            if (groundType.frictionModifier > currentTraction) {
                currentTraction = groundType.frictionModifier;
            }
        }
    }

    public Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(1, 0));
        return CarMath.multiply(
                currentRightNormal.dot(getPhysicsBodyComponent().body.getLinearVelocity()),
                currentRightNormal);
    }

    public Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(0, 1));
        return CarMath.multiply(
                currentForwardNormal.dot(getPhysicsBodyComponent().body.getLinearVelocity()),
                currentForwardNormal);
    }

    public void updateFriction() {
        Vector2 lat = CarMath.minus(getLateralVelocity());
        Vector2 impulse = CarMath.multiply(getPhysicsBodyComponent().body.getMass(),
                CarMath.minus(getLateralVelocity()));
        if (impulse.len() > maxLateralImpulse) {
            impulse = CarMath.multiply(impulse,
                    maxLateralImpulse / impulse.len());
        }
        getPhysicsBodyComponent().body.applyLinearImpulse(CarMath.multiply(currentTraction, impulse),
                getPhysicsBodyComponent().body.getWorldCenter(), true);
        getPhysicsBodyComponent().body.applyAngularImpulse(currentTraction * 0.1f * getPhysicsBodyComponent().body.getInertia()
                * -getPhysicsBodyComponent().body.getAngularVelocity(), true);
        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = CarMath.normalize(currentForwardNormal);
        float dragForceMagnitude = -2 * currentForwardSpeed;
        getPhysicsBodyComponent().body.applyForce(CarMath.multiply(currentTraction * dragForceMagnitude,
                currentForwardNormal), getPhysicsBodyComponent().body.getWorldCenter(), true);
    }

    public void updateDrive(HashSet<InputManager.Key> keys) {
        float desiredSpeed = 0;
        if (keys.contains(InputManager.Key.Up)) {
            desiredSpeed = maxForwardSpeed;
        } else if (keys.contains(InputManager.Key.Down)) {
            desiredSpeed = maxBackwardSpeed;
        } else {
            return;
        }
        Vector2 currentForwardNormal = getPhysicsBodyComponent().body.getWorldVector(new Vector2(0, 1));
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);
        float force = 0;
        if (desiredSpeed > currentSpeed) {
            force = maxDriveForce;
        } else if (desiredSpeed < currentSpeed) {
            force = (-maxDriveForce);
        } else {
            return;
        }
        getPhysicsBodyComponent().body.applyForce(
                CarMath.multiply(currentTraction * force, currentForwardNormal),
                getPhysicsBodyComponent().body.getWorldCenter(), true);
    }

    public void updateTurn(CarMoves moves) {
        float desiredTorque = 0;
        switch (moves) {
            case Left:
                desiredTorque = 15;
                break;
            case Right:
                desiredTorque = -15;
                break;
            default:
                return;
        }
        this.getPhysicsBodyComponent().body.applyTorque(desiredTorque, true);
    }
}