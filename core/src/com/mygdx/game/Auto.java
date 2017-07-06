package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.Constants;
import com.tarashgames.car.Tire;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.HashSet;

/**
 * Created by mietmark on 6.7.2017.
 */

public class Auto extends Sprite implements IScript {

    public PhysicsBodyComponent physicsBodyComponent;
    Entity entity;
    float w = 0;
    float h = 0;
    //
    Array<Tire> tires;
    RevoluteJoint leftJoint, rightJoint;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;
    private TextureRegionComponent textureRegionComponent;

    public Auto(PlayScreen playscreen) {
        this.playscreen = playscreen;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;

        transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        polygonComponent = ComponentRetriever.get(entity, PolygonComponent.class);
        physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);


        textureRegionComponent = ComponentRetriever.get(entity, TextureRegionComponent.class);
        if (textureRegionComponent.polygonSprite != null) {
            System.out.println("pologyonsrpite!=NUll");
        }

        defineMario();
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }

    public void defineMario() {

        //

        PhysicsBodyLoader instanssi =
                PhysicsBodyLoader.getInstance();
        physicsBodyComponent.body =
                instanssi.createBody(playscreen.world, this.entity, physicsBodyComponent, polygonComponent.vertices,
                        transformComponent);
        physicsBodyComponent.body.setUserData(this);


        physicsBodyComponent.body.setAngularDamping(3);
        physicsBodyComponent.body.setLinearDamping(2f);
        //
        float deg = physicsBodyComponent.body.getAngle() * MathUtils.radDeg;
        System.out.println("deg="+deg);

        if (w == 0) {
            BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);

            w = (boundingBox.max.x - boundingBox.min.x);

            h = (boundingBox.max.y - boundingBox.min.y);
        }


//        if (w == 0) {
//            BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);
//
//            w = (boundingBox.max.x - boundingBox.min.x);
//
//            h = (boundingBox.max.y - boundingBox.min.y);
//        }

        tires = new Array<Tire>();
/*
        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(new Vector2(3, 3));

        physicsBodyComponent.body = this.playscreen.world.createBody(bodyDef);
        physicsBodyComponent.body.setAngularDamping(3);

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
//        fixtureDef.filter.categoryBits = Constants.CAR;
//        fixtureDef.filter.maskBits = Constants.GROUND;

        physicsBodyComponent.body.createFixture(fixtureDef);
*/

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = this.physicsBodyComponent.body;
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
//        Tire tire;

        Tire tire = new Tire(this.playscreen.world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(-3, -3f);
        this.playscreen.world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(this.playscreen.world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(3, -3f);
        this.playscreen.world.createJoint(jointDef);
        tires.add(tire);


        tire = new Tire(this.playscreen.world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(-3, 3f);
        leftJoint = (RevoluteJoint) this.playscreen.world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(this.playscreen.world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(3, 3f);
        rightJoint = (RevoluteJoint) this.playscreen.world.createJoint(jointDef);
        tires.add(tire);


//
//        this.setRotation(0);
//        this.physicsBodyComponent.body.getPosition().setAngle(0);
    }

    public void update(float dt) {
        if (physicsBodyComponent == null) {
            System.out.println("physicsBodyComponent==null");
        }
        if (physicsBodyComponent.body == null) {
            System.out.println("physicsBodyComponent.body==null");
        }

        if (w == 0) {
            BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);

            w = (boundingBox.max.x - boundingBox.min.x);

            h = (boundingBox.max.y - boundingBox.min.y);
        }
        setBounds(physicsBodyComponent.body.getPosition().x - getWidth() / 2, physicsBodyComponent.body.getPosition().y - getHeight() / 2, w, h);
        this.setOriginCenter();
        float deg = physicsBodyComponent.body.getAngle() * MathUtils.radDeg;
        this.setRotation(deg);
        setRegion(getFrame(dt));

    }

    public TextureRegion getFrame(float dt) {
        stateTime += dt;

        return textureRegionComponent.region;

    }

    public void update(HashSet<InputManager.Key> keys) {

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

        if (keys.contains(InputManager.Key.Left)) {
            desiredAngle = lockAngle;
        } else if (keys.contains(InputManager.Key.Right)) {
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
