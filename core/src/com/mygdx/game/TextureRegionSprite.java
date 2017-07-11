package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by mietmark on 5.7.2017.
 */

public class TextureRegionSprite extends Sprite implements IScript {

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

    public TextureRegionSprite(PlayScreen playscreen) {
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


}
