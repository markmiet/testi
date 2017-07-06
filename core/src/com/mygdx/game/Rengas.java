package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.CarMoves;
import com.tarashgames.car.CarTireType;
import com.tarashgames.car.Constants;
import com.tarashgames.car.GroundAreaType;
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

public class Rengas extends Sprite implements IScript {

    public PhysicsBodyComponent physicsBodyComponent;
    Entity entity;
    float w = 0;
    float h = 0;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;
    private TextureRegionComponent textureRegionComponent;

    public Rengas(PlayScreen playscreen) {
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
        PhysicsBodyLoader instanssi =
                PhysicsBodyLoader.getInstance();
        physicsBodyComponent.body =
                instanssi.createBody(playscreen.world, this.entity, physicsBodyComponent, polygonComponent.vertices,
                        transformComponent);
        physicsBodyComponent.body.setUserData(this);


        physicsBodyComponent.body.setAngularDamping(3);
        physicsBodyComponent.body.setLinearDamping(2f);


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
//


    Body body;
    float maxForwardSpeed;
    float maxBackwardSpeed;
    float maxDriveForce;
    float maxLateralImpulse;

    Array<GroundAreaType> groundAreas;

    float currentTraction;




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

    void updateDrive(HashSet<InputManager.Key> keys) {
        float desiredSpeed = 0;

        if(keys.contains(InputManager.Key.Up)){
            desiredSpeed = maxForwardSpeed;
        } else if(keys.contains(InputManager.Key.Down)){
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

    void updateTurn(CarMoves moves){
        float desiredTorque = 0;

        switch(moves){
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