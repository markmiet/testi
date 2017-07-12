package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;
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

/**
 * Created by mietmark on 5.7.2017.
 */

public class TextureRegionSprite extends Sprite implements IScript {
    protected Entity entity;
    protected float w = 0;
    protected float h = 0;
    private PhysicsBodyComponent physicsBodyComponent;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;
    private TextureRegionComponent textureRegionComponent;
    private SceneLoader sl;
    private ItemWrapper rootItem;
    private String overlap2dIdentifier;
    private STATE currentstate = STATE.NORMAL;

    public TextureRegionSprite() {
    }


    public TextureRegionSprite(PlayScreen playscreen, String overlap2dIdentifier) {
        this.playscreen = playscreen;
        this.overlap2dIdentifier = overlap2dIdentifier;
        sl = new SceneLoader();
        sl.loadScene("MainScene");
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild(overlap2dIdentifier).addScript(this);

    }

    public STATE getCurrentstate() {
        return currentstate;
    }

    public void setCurrentstate(STATE currentstate) {
        this.currentstate = currentstate;
    }

    public PhysicsBodyComponent getPhysicsBodyComponent() {
        return physicsBodyComponent;
    }

    public void setPhysicsBodyComponent(PhysicsBodyComponent physicsBodyComponent) {
        this.physicsBodyComponent = physicsBodyComponent;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public TransformComponent getTransformComponent() {
        return transformComponent;
    }

    public void setTransformComponent(TransformComponent transformComponent) {
        this.transformComponent = transformComponent;
    }

    public DimensionsComponent getDimensionsComponent() {
        return dimensionsComponent;
    }

    public void setDimensionsComponent(DimensionsComponent dimensionsComponent) {
        this.dimensionsComponent = dimensionsComponent;
    }

    public PolygonComponent getPolygonComponent() {
        return polygonComponent;
    }

    public void setPolygonComponent(PolygonComponent polygonComponent) {
        this.polygonComponent = polygonComponent;
    }

    public PlayScreen getPlayscreen() {
        return playscreen;
    }

    public void setPlayscreen(PlayScreen playscreen) {
        this.playscreen = playscreen;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public TextureRegionComponent getTextureRegionComponent() {
        return textureRegionComponent;
    }

    public void setTextureRegionComponent(TextureRegionComponent textureRegionComponent) {
        this.textureRegionComponent = textureRegionComponent;
    }

    public SceneLoader getSl() {
        return sl;
    }

    public void setSl(SceneLoader sl) {
        this.sl = sl;
    }

    public ItemWrapper getRootItem() {
        return rootItem;
    }

    public void setRootItem(ItemWrapper rootItem) {
        this.rootItem = rootItem;
    }

    public String getOverlap2dIdentifier() {
        return overlap2dIdentifier;
    }

    public void setOverlap2dIdentifier(String overlap2dIdentifier) {
        this.overlap2dIdentifier = overlap2dIdentifier;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        polygonComponent = ComponentRetriever.get(entity, PolygonComponent.class);
        physicsBodyComponent = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        textureRegionComponent = ComponentRetriever.get(entity, TextureRegionComponent.class);
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
                instanssi.createBody(playscreen.getWorld(), this.entity, physicsBodyComponent, polygonComponent.vertices,
                        transformComponent);
        physicsBodyComponent.body.setUserData(this);
        physicsBodyComponent.body.setAngularDamping(3);
        physicsBodyComponent.body.setLinearDamping(2f);
        if (w == 0) {
            BoundingBox boundingBox = PhysicsUtil.calculateBoundingBox(physicsBodyComponent.body);
            w = (boundingBox.max.x - boundingBox.min.x);
            h = (boundingBox.max.y - boundingBox.min.y);
        }
    }

    public void update(float dt) {
        if (!handleDestroy(dt)) {
            setBounds(physicsBodyComponent.body.getPosition().x - getWidth() / 2, physicsBodyComponent.body.getPosition().y - getHeight() / 2, w, h);
            this.setOriginCenter();
            float deg = physicsBodyComponent.body.getAngle() * MathUtils.radDeg;
            this.setRotation(deg);
            setRegion(getFrame(dt));
        }
    }

    private boolean handleDestroy(float dt) {
        if (this.currentstate == STATE.TO_BE_DESTROYED) {
            this.getPlayscreen().getWorld().destroyBody(this.getPhysicsBodyComponent().body);
            this.getPhysicsBodyComponent().body = null;
            this.currentstate = STATE.DESTROYED;
            return true;
        }
        if (this.currentstate == STATE.DESTROYED) {
            return true;
        }
        return false;
    }

    public TextureRegion getFrame(float dt) {
        stateTime += dt;
        return textureRegionComponent.region;

    }


    public enum STATE {NORMAL, TO_BE_DESTROYED, DESTROYED, JOINT_TO_BE_DESTROYED, JOINT_DESTROYED}


}
