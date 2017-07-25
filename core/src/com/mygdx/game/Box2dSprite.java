package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mietmark on 12.7.2017.
 */
public class Box2dSprite extends Sprite implements IScript {
    public static float DEGTORAD = 0.0174532925199432957f;
    public MainItemComponent mainItemComponent;
    protected Entity entity;
    protected float w = 0;
    protected float h = 0;
    private PhysicsBodyComponent physicsBodyComponent;
    private TransformComponent transformComponent;
    private DimensionsComponent dimensionsComponent;
    private SpriteAnimationComponent saComponent;
    private SpriteAnimationStateComponent sasComponent;
    private com.badlogic.gdx.graphics.g2d.Animation walkAnimation;
    private PolygonComponent polygonComponent;
    private PlayScreen playscreen;
    private float stateTime = 0;
    private TextureRegionComponent textureRegionComponent;
    private SceneLoader sl;
    private ItemWrapper rootItem;
    private String overlap2dIdentifier;
    //    private STATE currentstate = STATE.NORMAL;
    private ArrayList<Box2dSprite> children = new ArrayList<Box2dSprite>();
    private Joint joint;
    private Box2dSprite parent;
    private CopyOnWriteArrayList<Action> actionsToAct = new CopyOnWriteArrayList<Action>();//eli tuonne aina lisätään
    private HashSet<Action> alreadyActedActions = new HashSet<Action>();
    private ArrayList<Action> actionsToHappenWhenCollision = new ArrayList<Action>();
    private RevoluteJointDef jointDef = new RevoluteJointDef();
    private boolean rvj;
    private boolean draw = true;

    public Float getUpperAngleAfterCollision() {
        return upperAngleAfterCollision;
    }

    public void setUpperAngleAfterCollision(Float upperAngleAfterCollision) {
        this.upperAngleAfterCollision = upperAngleAfterCollision;
    }

    private Float upperAngleAfterCollision=null;
    public Box2dSprite() {
    }

    public Box2dSprite(PlayScreen playscreen, String overlap2dIdentifier) {
        this(playscreen, overlap2dIdentifier, null);
    }

    public Box2dSprite(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent) {
        initMjm(playscreen, overlap2dIdentifier, parent);
    }

    public Box2dSprite(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent, boolean rvj) {
        this.rvj = rvj;
        initMjm(playscreen, overlap2dIdentifier, parent);
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }




    public boolean isRvj() {
        return rvj;
    }

    public void setRvj(boolean rvj) {
        this.rvj = rvj;
    }

    public void initMjm(PlayScreen playscreen, String overlap2dIdentifier, Box2dSprite parent) {
        this.playscreen = playscreen;
        this.overlap2dIdentifier = overlap2dIdentifier;
        this.parent = parent;
        if (this.parent != null)
            this.parent.getChildren().add(this);
        sl = new SceneLoader();
        sl.loadScene("MainScene");
        rootItem = new ItemWrapper(sl.getRoot());
        //System.out.println("overlap2dIdentifier=" + overlap2dIdentifier);
        rootItem.getChild(overlap2dIdentifier).addScript(this);
        //ja childs
        generateChilds();
    }

    public RevoluteJointDef getJointDef() {
        return jointDef;
    }

    public void setJointDef(RevoluteJointDef jointDef) {
        this.jointDef = jointDef;
    }

    public void generateChilds() {
        NodeComponent nc = ComponentRetriever.get(sl.getRoot(), NodeComponent.class);
        for (Entity c : nc.children) {
            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
            CustomVariables customVariables = new CustomVariables();
            customVariables.loadFromString(m.customVars);
            String parentname = customVariables.getStringVariable("parentname");
            if (this.overlap2dIdentifier.equals(parentname)) {
                //child found
                String luokka = customVariables.getStringVariable("class");
                try {
                    if (luokka == null) {
                        Box2dSprite children = new Box2dSprite(this.playscreen, m.itemIdentifier, this);
                    } else {
                        Class<?> clazz = Class.forName("com.mygdx.game." + luokka);
                        Constructor<?> constructor = clazz.getConstructor(PlayScreen.class, String.class, Box2dSprite.class);
                        Object instance = constructor.newInstance(this.playscreen, m.itemIdentifier, this);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Action> getActionsToHappenWhenCollision() {
        return actionsToHappenWhenCollision;
    }

    public void setActionsToHappenWhenCollision(ArrayList<Action> actionsToHappenWhenCollision) {
        this.actionsToHappenWhenCollision = actionsToHappenWhenCollision;
    }

    public Box2dSprite getParent() {
        return parent;
    }

    public void setParent(Box2dSprite parent) {
        this.parent = parent;
    }

    public Joint getJoint() {
        return joint;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    public ArrayList<Box2dSprite> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Box2dSprite> children) {
        this.children = children;
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
        saComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        sasComponent = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        mainItemComponent = ComponentRetriever.get(entity, MainItemComponent.class);
//        mainItemComponent.itemIdentifier
        if (sasComponent != null) {
            walkAnimation = sasComponent.currentAnimation;
        }
        defineMario();
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    public Box2dSprite get(String itemIdentifier) {
        if (this.getParent() != null) {
            if (this.getParent().mainItemComponent.itemIdentifier.equals(itemIdentifier))
                return this.getParent();
            for (Box2dSprite c : this.getParent().getChildren()) {
                if (c.mainItemComponent.itemIdentifier.equals(itemIdentifier)) {
                    return c;
                }
            }
        }
        return null;
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
        CustomVariables customVariables = new CustomVariables();
        customVariables.loadFromString(this.mainItemComponent.customVars);
        String draws = customVariables.getStringVariable("draw");
        if ("false".equals(draws)) {
            draw = false;
        }

        if (this.isRvj()) {
            String BID = customVariables.getStringVariable("BID");
            String AID = customVariables.getStringVariable("AID");
            Box2dSprite a = this.playscreen.getBox2dSprite(AID);
            Box2dSprite b = this.playscreen.getBox2dSprite(BID);
            //eli a ja tämä
            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.bodyA = a.getPhysicsBodyComponent().body;
            jointDef.bodyB = b.getPhysicsBodyComponent().body;
            jointDef.enableLimit = true;
            jointDef.lowerAngle = 0;
            jointDef.upperAngle = 0;
            float x =
                    this.getPhysicsBodyComponent().body.getPosition().x - a.getPhysicsBodyComponent().body.getPosition().x;
            float y =
                    this.getPhysicsBodyComponent().body.getPosition().y - a.getPhysicsBodyComponent().body.getPosition().y;
            jointDef.localAnchorA.set(x, y);
            float xb =
                    this.getPhysicsBodyComponent().body.getPosition().x - b.getPhysicsBodyComponent().body.getPosition().x;
            float yb =
                    this.getPhysicsBodyComponent().body.getPosition().y - b.getPhysicsBodyComponent().body.getPosition().y;
            jointDef.localAnchorB.set(xb, yb);
            setCustomvarsToRevoluteJointDef(customVariables, jointDef);
            b.setJoint(this.getPlayscreen().getWorld().createJoint(jointDef));

            String upperAngleAfterCollisions=customVariables.getStringVariable("upperAngleAfterCollision");
            if (upperAngleAfterCollisions!=null) {
                b.setUpperAngleAfterCollision(Float.parseFloat(upperAngleAfterCollisions));
            }

            this.getPlayscreen().getWorld().destroyBody(this.physicsBodyComponent.body);
        } else if (parent != null && !this.playscreen.isInRvjs(overlap2dIdentifier)) {
            //jointien teko...
//            CustomVariables customVariables = new CustomVariables();
//            customVariables.loadFromString(mainItemComponent.customVars);
            String jPosFromEdit = customVariables.getStringVariable("jPosFromEdit");
            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.bodyA = parent.getPhysicsBodyComponent().body;
            jointDef.bodyB = getPhysicsBodyComponent().body;
            jointDef.enableLimit = true;
            jointDef.lowerAngle = 0;
            jointDef.upperAngle = 0;
            float x =
                    this.getPhysicsBodyComponent().body.getPosition().x - parent.getPhysicsBodyComponent().body.getPosition().x;
            float y =
                    this.getPhysicsBodyComponent().body.getPosition().y - parent.getPhysicsBodyComponent().body.getPosition().y;
            jointDef.localAnchorA.set(x, y);
            setCustomvarsToRevoluteJointDef(customVariables, jointDef);

            setJoint(this.getPlayscreen().getWorld().createJoint(jointDef));

        }
        setActions();
    }

    private void setCustomvarsToRevoluteJointDef(CustomVariables customVariables, RevoluteJointDef jointDef) {
        String localAnchorA = customVariables.getStringVariable("localAnchorA");
        if (localAnchorA != null) {
            String xy[] = localAnchorA.split(",");
            jointDef.localAnchorA.set(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]));
        }
        String localAnchorB = customVariables.getStringVariable("localAnchorB");
        if (localAnchorB != null) {
            String xy[] = localAnchorB.split(",");
            jointDef.localAnchorB.set(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]));
        }
        String enableLimit = customVariables.getStringVariable("enableLimit");
        if (enableLimit != null) {
            jointDef.enableLimit = Boolean.parseBoolean(enableLimit);
        }
        String collideConnected = customVariables.getStringVariable("collideConnected");
        if (collideConnected != null) {
            jointDef.collideConnected = Boolean.parseBoolean(collideConnected);
        }
        String lowerAngle = customVariables.getStringVariable("lowerAngle");
        if (lowerAngle != null) {
            jointDef.lowerAngle = Float.parseFloat(lowerAngle) * DEGTORAD;
        }
        String upperAngle = customVariables.getStringVariable("upperAngle");
        if (upperAngle != null) {
            jointDef.upperAngle = Float.parseFloat(upperAngle) * DEGTORAD;
        }
        String referenceAngle = customVariables.getStringVariable("referenceAngle");
        if (referenceAngle != null) {
            jointDef.referenceAngle = Float.parseFloat(referenceAngle) * DEGTORAD;
        }
    }

    public void update(float dt) {
        handleActions();
//        if (!handleDestroy(dt)) {
        setBounds(physicsBodyComponent.body.getPosition().x - getWidth() / 2, physicsBodyComponent.body.getPosition().y - getHeight() / 2, w, h);
        this.setOriginCenter();
        float deg = physicsBodyComponent.body.getAngle() * MathUtils.radDeg;
        this.setRotation(deg);
        setRegion(getFrame(dt));
//        }
        for (Box2dSprite c : this.getChildren()) {
            c.update(dt);
        }
    }
//    private boolean handleDestroy(float dt) {
//        if (this.currentstate == STATE.TO_BE_DESTROYED) {
//            this.currentstate = STATE.DESTROYED;
//            this.getPlayscreen().getWorld().destroyBody(this.getPhysicsBodyComponent().body);
//            this.getPhysicsBodyComponent().body = null;
//            return true;
//        }
//        if (this.currentstate == STATE.JOINT_TO_BE_DESTROYED && this.getJoint() != null) {
//            this.currentstate = STATE.JOINT_DESTROYED;
//            this.getPlayscreen().getWorld().destroyJoint(this.getJoint());
//            this.setJoint(null);
//            return false;
//        } else if (this.currentstate == STATE.DESTROYED) {
//            return true;
//        }
//        return false;
//    }

    private void handleActions() {
        if (!getActionsToAct().isEmpty()) {
            for (Action c : this.getActionsToAct()) {
                if (c.getAction() == Action.ACTION.DESTROY && !c.isJoint()) {
                    this.getPlayscreen().getWorld().destroyBody(this.getPhysicsBodyComponent().body);
                    this.getPhysicsBodyComponent().body = null;
                } else if (c.getAction() == Action.ACTION.DESTROY && c.isJoint()) {
                    System.out.println("c == COLLISION_ACTION.JOINT_DESTROY");
                    if (this.getJoint() != null) {
                        this.getPlayscreen().getWorld().destroyJoint(this.getJoint());
                        this.setJoint(null);
                    }
                }
                this.getActionsToAct().remove(c);
            }
        }
    }

    private TextureRegion getNormalFrame(float dt) {
        stateTime += dt;
        return textureRegionComponent.region;
    }

    public TextureRegion getFrame(float dt) {
        if (walkAnimation != null) {
            return getAnimationFrame(dt);
        } else {
            return getNormalFrame(dt);
        }
    }

    private TextureRegion getAnimationFrame(float dt) {
        stateTime += dt;
        TextureRegion region;
        region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
        return region;
    }

    public void draw(Batch batch) {
        super.draw(batch);
        for (Box2dSprite c : this.getChildren()) {
//            if (c.isDraw())
            c.draw(batch);
        }
    }

    public CopyOnWriteArrayList<Action> getActionsToAct() {
        return actionsToAct;
    }

    public void setActionsToAct(CopyOnWriteArrayList<Action> actionsToAct) {
        this.actionsToAct = actionsToAct;
    }

    public HashSet<Action> getAlreadyActedActions() {
        return alreadyActedActions;
    }

    public void setAlreadyActedActions(HashSet<Action> alreadyActedActions) {
        this.alreadyActedActions = alreadyActedActions;
    }

    public void addToActions(Action action) {
        if (!action.isCanRecur()) {
            if (!alreadyActedActions.contains(action)) {
                actionsToAct.add(action);
            }
        } else {
            actionsToAct.add(action);
        }
        alreadyActedActions.add(action);
    }

    private void setActions() {
        MainItemComponent m = ComponentRetriever.get(this.entity, MainItemComponent.class);
        CustomVariables customVariables = new CustomVariables();
        customVariables.loadFromString(m.customVars);
        String str = customVariables.getStringVariable("CA");
        if (str != null) {
            String split[] = str.split(";");
            for (String s : split) {
                Action a = Action.getAction(s);
                if (a != null) {
                    this.getActionsToHappenWhenCollision().add(a);
                }
            }
        }
    }
    public void setUpperAngleAfterCollision() {
        if (upperAngleAfterCollision!=null) {
            if (this.getJoint() instanceof RevoluteJoint) {

                ((RevoluteJoint)this.getJoint()).setLimits(((RevoluteJoint)this.getJoint()).getLowerLimit(),
                        upperAngleAfterCollision*Box2dSprite.DEGTORAD
                        );
            }
        }
    }

}
