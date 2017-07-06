package com.tarashgames.car;

import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.tarashgames.handlers.InputManager.Key;


public class Car {
	public Body body;
	Array<Tire> tires;
	RevoluteJoint leftJoint, rightJoint;


	public Car(World world) {
		
		tires = new Array<Tire>();

		BodyDef bodyDef = new BodyDef();

		bodyDef.type = BodyType.DynamicBody;

		bodyDef.position.set(new Vector2(3, 3));
		
		body = world.createBody(bodyDef);
		body.setAngularDamping(3);
		
		Vector2[] vertices = new Vector2[8];

		vertices[0] = new Vector2(1.5f, 0);
		vertices[1] = new Vector2(3, 2.5f);
		vertices[2] = new Vector2(2.8f, 5.5f);
		vertices[3] = new Vector2(1, 10);
		vertices[4] = new Vector2(-1, 10);
		vertices[5] = new Vector2(-2.8f, 5.5f);
		vertices[6] = new Vector2(-3, 2.5f);
		vertices[7] = new Vector2(-1.5f, 0);

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.set(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 0.1f;
		fixtureDef.filter.categoryBits = Constants.CAR;
		fixtureDef.filter.maskBits = Constants.GROUND;
		
		body.createFixture(fixtureDef);

		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = body;
		jointDef.enableLimit = true;
		jointDef.lowerAngle = 0;
		jointDef.upperAngle = 0;
		jointDef.localAnchorB.setZero();

		float maxForwardSpeed = 250;
		float maxBackwardSpeed = -40;
		float backTireMaxDriveForce = 300;
		float frontTireMaxDriveForce = 500;
		float backTireMaxLateralImpulse = 8.5f;
		float frontTireMaxLateralImpulse = 7.5f;

		Tire tire = new Tire(world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				backTireMaxDriveForce, backTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(-3, 0.75f);
		world.createJoint(jointDef);
		tires.add(tire);

		tire = new Tire(world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				backTireMaxDriveForce, backTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(3, 0.75f);
		world.createJoint(jointDef);
		tires.add(tire);

		tire = new Tire(world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				frontTireMaxDriveForce, frontTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(-3, 8.5f);
		leftJoint = (RevoluteJoint)world.createJoint(jointDef);
		tires.add(tire);

		tire = new Tire(world);
		tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
				frontTireMaxDriveForce, frontTireMaxLateralImpulse);
		jointDef.bodyB = tire.body;
		jointDef.localAnchorA.set(3, 8.5f);
		rightJoint = (RevoluteJoint)world.createJoint(jointDef);
		tires.add(tire);
	}

	public void update(HashSet<Key> keys) {
		for (Tire tire : tires) {
			tire.updateFriction();
		}
		for (Tire tire : tires) {
			tire.updateDrive(keys);
		}

		float lockAngle = 35 * Constants.DEGTORAD;
		float turnSpeedPerSec = 160 * Constants.DEGTORAD;
		float turnPerTimeStep = turnSpeedPerSec / 60.0f;
		float desiredAngle = 0;

		if(keys.contains(Key.Left)){
			desiredAngle = lockAngle;
		} else if(keys.contains(Key.Right)){
			desiredAngle = -lockAngle;
		}
		
		float angleNow = leftJoint.getJointAngle();
		float angleToTurn = desiredAngle - angleNow;
		angleToTurn = CarMath.clamp(angleToTurn, -turnPerTimeStep, turnPerTimeStep);
		float newAngle = angleNow + angleToTurn;
		
		leftJoint.setLimits(newAngle, newAngle);
		rightJoint.setLimits(newAngle, newAngle);
	}
}
