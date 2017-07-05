package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by mietmark on 5.7.2017.
 */

public class Ruoho extends Sprite implements IScript {

    public PhysicsBodyComponent physicsBodyComponent;
    Entity entity;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;

    private  TextureRegionComponent textureRegionComponent;

    public Ruoho(PlayScreen playscreen) {
        this.playscreen = playscreen;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;

        transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        polygonComponent = ComponentRetriever.get(entity, PolygonComponent.class);
        physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
//        saComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
//        sasComponent = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
//        walkAnimation = sasComponent.currentAnimation;

        textureRegionComponent= ComponentRetriever.get(entity, TextureRegionComponent.class);

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

    }

    public void update(float dt) {
        if (physicsBodyComponent==null) {
            System.out.println("physicsBodyComponent==null");
        }
        if (physicsBodyComponent.body==null) {
            System.out.println("physicsBodyComponent.body==null");
        }
        BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);
        boundingBox.min.scl(MyGdxGame.PPM);
        boundingBox.max.scl(MyGdxGame.PPM);
        setBounds(boundingBox.min.x / MyGdxGame.PPM, boundingBox.min.y / MyGdxGame.PPM, (boundingBox.max.x - boundingBox.min.x) / MyGdxGame.PPM,
                (boundingBox.max.y - boundingBox.min.y) / MyGdxGame.PPM);
        setOrigin(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
        setRegion(getFrame(dt));

    }

    public TextureRegion getFrame(float dt) {
        stateTime += dt;

        return textureRegionComponent.region;

    }

}
