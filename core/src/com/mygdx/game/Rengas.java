package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.CarMoves;
import com.tarashgames.car.GroundAreaType;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashSet;

/**
 * Created by mietmark on 6.7.2017.
 */

public class Rengas extends Sprite implements IScript {

    public PhysicsBodyComponent physicsBodyComponent;
    Entity entity;
    float w = 0;
    float h = 0;
    //    Body body;
    float maxForwardSpeed;
    float maxBackwardSpeed;
    float maxDriveForce;
    float maxLateralImpulse;
    Array<GroundAreaType> groundAreas;
    float currentTraction;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;
    private TextureRegionComponent textureRegionComponent;
    private Auto auto;
    private boolean left;
    private boolean front;
    private boolean shouldBeDestroyed = false;

    private SceneLoader sl;
    private ItemWrapper rootItem;
    public Rengas(PlayScreen playscreen, Auto auto, boolean front, boolean left, String overlap2dIdentifier) {
        this.playscreen = playscreen;
        this.auto = auto;
        this.front = front;
        this.left = left;
        sl=new SceneLoader();
        sl.loadScene("MainScene");
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild(overlap2dIdentifier).addScript(this);

    }



    public boolean isShouldBeDestroyed() {
        return shouldBeDestroyed;
    }

    public void setShouldBeDestroyed(boolean shouldBeDestroyed) {
        this.shouldBeDestroyed = shouldBeDestroyed;
    }
//

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
        currentTraction = 1;
        defineMario();
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    //    RevoluteJoint leftJoint, rightJoint;
    public void defineMario() {
        PhysicsBodyLoader instanssi =
                PhysicsBodyLoader.getInstance();
        physicsBodyComponent.body =
                instanssi.createBody(playscreen.world, this.entity, physicsBodyComponent, polygonComponent.vertices,
                        transformComponent);
        physicsBodyComponent.body.setUserData(this);
//        physicsBodyComponent.body.setAngularDamping(3);
//        physicsBodyComponent.body.setLinearDamping(2f);
        //autosta siirretety
        float maxForwardSpeed = 6650;
        float maxBackwardSpeed = -40;
        float backTireMaxDriveForce = 1300;
        float frontTireMaxDriveForce = 1500;
        float backTireMaxLateralImpulse = 8.5f;
        float frontTireMaxLateralImpulse = 7.5f;
//        RevoluteJointDef jointDef = new RevoluteJointDef();
//        jointDef.bodyA = this.auto.physicsBodyComponent.body;
//        jointDef.enableLimit = true;
//        jointDef.lowerAngle = 0;
//        jointDef.upperAngle = 0;
//        jointDef.localAnchorB.setZero();
        RevoluteJointDef jointDef = this.auto.jointDef;
        jointDef.bodyB = physicsBodyComponent.body;
        if (this.front && this.left) {
            jointDef.localAnchorA.set(-3, 3f);
            auto.leftJoint = (RevoluteJoint) this.playscreen.world.createJoint(jointDef);
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    frontTireMaxDriveForce, frontTireMaxLateralImpulse);

        } else if (this.front && !left) {
            jointDef.localAnchorA.set(3, 3f);
            auto.rightJoint = (RevoluteJoint) this.playscreen.world.createJoint(jointDef);
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    frontTireMaxDriveForce, frontTireMaxLateralImpulse);

        } else if (!this.front && this.left) {
            jointDef.localAnchorA.set(-3, -3f);
//            WeldJointDef weldJointDef = new WeldJointDef();
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    backTireMaxDriveForce, backTireMaxLateralImpulse);
            this.playscreen.world.createJoint(jointDef);
//            this.playscreen.world.destroyJoint();
        } else if (!this.front && !this.left) {
            jointDef.localAnchorA.set(3, -3f);
            setCharacteristics(maxForwardSpeed, maxBackwardSpeed,
                    backTireMaxDriveForce, backTireMaxLateralImpulse);
            this.playscreen.world.createJoint(jointDef);

        }
        //Tire luokasta..
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
        if (this.isShouldBeDestroyed()) {
            this.playscreen.world.destroyBody(this.physicsBodyComponent.body);
            this.physicsBodyComponent.body = null;
            this.auto.tires.remove(this);
        }

    }

    public TextureRegion getFrame(float dt) {
        stateTime += dt;
        return textureRegionComponent.region;

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
        Vector2 currentRightNormal = physicsBodyComponent.body.getWorldVector(new Vector2(1, 0));
        return CarMath.multiply(
                currentRightNormal.dot(physicsBodyComponent.body.getLinearVelocity()),
                currentRightNormal);
    }

    public Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = physicsBodyComponent.body.getWorldVector(new Vector2(0, 1));
        return CarMath.multiply(
                currentForwardNormal.dot(physicsBodyComponent.body.getLinearVelocity()),
                currentForwardNormal);
    }

    public void updateFriction() {
        Vector2 lat = CarMath.minus(getLateralVelocity());
        Vector2 impulse = CarMath.multiply(physicsBodyComponent.body.getMass(),
                CarMath.minus(getLateralVelocity()));
        if (impulse.len() > maxLateralImpulse) {
            impulse = CarMath.multiply(impulse,
                    maxLateralImpulse / impulse.len());
        }
        physicsBodyComponent.body.applyLinearImpulse(CarMath.multiply(currentTraction, impulse),
                physicsBodyComponent.body.getWorldCenter(), true);
        physicsBodyComponent.body.applyAngularImpulse(currentTraction * 0.1f * physicsBodyComponent.body.getInertia()
                * -physicsBodyComponent.body.getAngularVelocity(), true);
        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = CarMath.normalize(currentForwardNormal);
        float dragForceMagnitude = -2 * currentForwardSpeed;
        physicsBodyComponent.body.applyForce(CarMath.multiply(currentTraction * dragForceMagnitude,
                currentForwardNormal), physicsBodyComponent.body.getWorldCenter(), true);
//        System.out.println("physicsBodyComponent.body.getPosition().x=" + physicsBodyComponent.body.getPosition().x);
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
        Vector2 currentForwardNormal = physicsBodyComponent.body.getWorldVector(new Vector2(0, 1));
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);
        float force = 0;
        if (desiredSpeed > currentSpeed) {
            force = maxDriveForce;
        } else if (desiredSpeed < currentSpeed) {
            force = (-maxDriveForce);
        } else {
            return;
        }
        physicsBodyComponent.body.applyForce(
                CarMath.multiply(currentTraction * force, currentForwardNormal),
                physicsBodyComponent.body.getWorldCenter(), true);

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
//        if (this.front)
        physicsBodyComponent.body.applyTorque(desiredTorque, true);
    }


}