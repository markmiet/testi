package com.tarashgames.car;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tarashgames.handlers.InputManager.Key;

import java.util.HashSet;

public class Tire {
    Body body;
    float maxForwardSpeed;
    float maxBackwardSpeed;
    float maxDriveForce;
    float maxLateralImpulse;
    Array<GroundAreaType> groundAreas;
    float currentTraction;

    public Tire(World world) {
        groundAreas = new Array<GroundAreaType>();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 1.25f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = Constants.TIRE;
        fixtureDef.filter.maskBits = Constants.GROUND;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new CarTireType());
        body.setUserData(this);
        currentTraction = 1;
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

    void updateTraction() {
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

    Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1, 0));
        return CarMath.multiply(
                currentRightNormal.dot(body.getLinearVelocity()),
                currentRightNormal);
    }

    Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        return CarMath.multiply(
                currentForwardNormal.dot(body.getLinearVelocity()),
                currentForwardNormal);
    }

    public void updateFriction() {
        Vector2 lat = CarMath.minus(getLateralVelocity());
        Vector2 impulse = CarMath.multiply(body.getMass(),
                CarMath.minus(getLateralVelocity()));
        if (impulse.len() > maxLateralImpulse) {
            impulse = CarMath.multiply(impulse,
                    maxLateralImpulse / impulse.len());
        }
        body.applyLinearImpulse(CarMath.multiply(currentTraction, impulse),
                body.getWorldCenter(), true);
        body.applyAngularImpulse(currentTraction * 0.1f * body.getInertia()
                * -body.getAngularVelocity(), true);
        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = CarMath.normalize(currentForwardNormal);
        float dragForceMagnitude = -2 * currentForwardSpeed;
        body.applyForce(CarMath.multiply(currentTraction * dragForceMagnitude,
                currentForwardNormal), body.getWorldCenter(), true);
    }

    void updateDrive(HashSet<Key> keys) {
        float desiredSpeed = 0;
        if (keys.contains(Key.Up)) {
            desiredSpeed = maxForwardSpeed;
        } else if (keys.contains(Key.Down)) {
            desiredSpeed = maxBackwardSpeed;
        } else {
            return;
        }
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);
        float force = 0;
        if (desiredSpeed > currentSpeed) {
            force = maxDriveForce;
        } else if (desiredSpeed < currentSpeed) {
            force = (-maxDriveForce);
        } else {
            return;
        }
        body.applyForce(
                CarMath.multiply(currentTraction * force, currentForwardNormal),
                body.getWorldCenter(), true);
    }

    void updateTurn(CarMoves moves) {
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
        body.applyTorque(desiredTorque, true);
    }
}
