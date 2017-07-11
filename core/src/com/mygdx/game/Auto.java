package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.tarashgames.car.CarMath;
import com.tarashgames.car.Constants;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by mietmark on 6.7.2017.
 */

public class Auto extends Sprite implements IScript, Serializable {

    public PhysicsBodyComponent physicsBodyComponent;
    public String overlap2dIdentifier = null;
    Entity entity;
    float w = 0;
    float h = 0;
    //
    ArrayList<Rengas> tires;
    RevoluteJoint leftJoint, rightJoint;
    RevoluteJointDef jointDef = new RevoluteJointDef();
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private MainItemComponent mainItemComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;
    private TextureRegionComponent textureRegionComponent;
    private SceneLoader sl;
    private ItemWrapper rootItem;
    public Auto(PlayScreen playscreen,String overlap2dIdentifier) {
        this.playscreen = playscreen;
        this.overlap2dIdentifier = overlap2dIdentifier;
        sl=new SceneLoader();
        sl.loadScene("MainScene");
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild(overlap2dIdentifier).addScript(this);


    }

    public ArrayList<Rengas> getTires() {
        return tires;
    }

    public void setTires(ArrayList<Rengas> tires) {
        this.tires = tires;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        mainItemComponent = ComponentRetriever.get(entity, MainItemComponent.class);
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
//        float deg = physicsBodyComponent.body.getAngle() * MathUtils.radDeg;
        if (w == 0) {
            BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);
            w = (boundingBox.max.x - boundingBox.min.x);
            h = (boundingBox.max.y - boundingBox.min.y);
        }

        tires = new ArrayList<Rengas>();

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
        Rengas tire;


        tire = new Rengas(this.playscreen, this, true, true, "vaseneturengas");
//        rootItem.getChild("vaseneturengas").addScript(tire);
        tires.add(tire);
        Rengas tire2 = new Rengas(this.playscreen, this, true, false, "oikeaeturengas");
//        rootItem.getChild("oikeaeturengas").addScript(tire2);
        tires.add(tire2);
        Rengas vasentakarengas = new Rengas(this.playscreen, this, false, true, "vasentakarengas");
//        rootItem.getChild("vasentakarengas").addScript(vasentakarengas);
        tires.add(vasentakarengas);
        Rengas oikeatakarengas = new Rengas(this.playscreen, this, false, false, "oikeatakarengas");
//        rootItem.getChild("oikeatakarengas").addScript(oikeatakarengas);
        tires.add(oikeatakarengas);
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
        for (Rengas tire : tires) {
            tire.updateFriction();
        }
        for (Rengas tire : tires) {
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
