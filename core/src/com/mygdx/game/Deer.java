package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by mietmark on 4.7.2017.
 */

public class Deer extends Sprite implements IScript {

    public PhysicsBodyComponent physicsBodyComponent;
    Entity entity;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private SpriteAnimationComponent saComponent;
    private SpriteAnimationStateComponent sasComponent;
    private float stateTime = 0;
    private com.badlogic.gdx.graphics.g2d.Animation walkAnimation;


    public Deer(PlayScreen playscreen) {
        this.playscreen = playscreen;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;

        transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        polygonComponent = ComponentRetriever.get(entity, PolygonComponent.class);
        physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        saComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        sasComponent = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        walkAnimation = sasComponent.currentAnimation;
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
//        this.setRotation(physicsBodyComponent.body.getAngle());

        BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);
        boundingBox.min.scl(MyGdxGame.PPM);
        boundingBox.max.scl(MyGdxGame.PPM);
        setBounds(boundingBox.min.x / MyGdxGame.PPM, boundingBox.min.y / MyGdxGame.PPM, (boundingBox.max.x - boundingBox.min.x) / MyGdxGame.PPM,
                (boundingBox.max.y - boundingBox.min.y) / MyGdxGame.PPM);
        setOrigin(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
        setRegion(getFrame(dt));
//System.out.println("angle="+ physicsBodyComponent.body.getAngle() );


    }

    public TextureRegion getFrame(float dt) {
        stateTime += dt;
        TextureRegion region;

        region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);


        return region;
    }

    public void draw(Batch batch, float delta) {
        float deg = physicsBodyComponent.body.getAngle() * MathUtils.radDeg;
        TextureRegion keyFrame = getFrame(delta);
        batch.draw(keyFrame, getX(), getY(),
                getWidth() / 2.0f,
                getHeight() / 2.0f, getWidth(), getHeight(),
                1f, 1f, deg - 90, false);

//        physicsBodyComponent.body.setFixedRotation(true);
    }

}
