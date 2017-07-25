package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by mietmark on 4.7.2017.
 */
public class PlayScreen implements Screen {
    BitmapFont font = new BitmapFont(); //or use alex answer to use custom font
    private World world;
    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Box2DDebugRenderer b2dr;
    private MyGdxGame game;
    private SceneLoader sl;
    private ItemWrapper rootItem;
    //    private AnimationSprite deer;
//    private AnimationSprite deer2;
//    private TextureRegionSprite ruoho;
//    private TextureRegionSprite rekka;
//    private TextureRegionSprite truck;
//    private ArrayList<Auto> autot = new ArrayList<Auto>();
//    private List<TextureRegionSprite> rekanOsat = new ArrayList<TextureRegionSprite>();
    private ArrayList<Box2dSprite> sprites = new ArrayList<Box2dSprite>();
    private ArrayList<Box2dSprite> rvjs = new ArrayList<Box2dSprite>();
    private Entity root;
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private Car car = null;

    public PlayScreen(MyGdxGame game) {
        font.getData().scale(-0.5f);
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gamecam);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        world.setContactListener(new WorldContactListener(this));
        sl = new SceneLoader(); // default scene loader loads all resources from default RM as usual.
        sl.loadScene("MainScene", gamePort); // loading scene as usual
        root = sl.getRoot();
        NodeComponent nc = ComponentRetriever.get(root, NodeComponent.class);
        ArrayList<String> bids = new ArrayList<String>();
        for (Entity c : nc.children) {
            entities.add(c);
        }
        for (Entity c : nc.children) {
            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
            CustomVariables customVariables = new CustomVariables();
            customVariables.loadFromString(m.customVars);
            String BID = customVariables.getStringVariable("BID");
            String parentname = customVariables.getStringVariable("parentname");
            if (m.itemIdentifier != null && m.itemIdentifier.length() > 0 && parentname == null) {
                String luokka = customVariables.getStringVariable("class");
                try {
                    if (BID != null) {
                        continue;
                    } else if (luokka == null) {
                        Box2dSprite children = new Box2dSprite(this, m.itemIdentifier, null);
                        sprites.add(children);
                    } else {
                        Class<?> clazz = Class.forName("com.mygdx.game." + luokka);
                        Constructor<?> constructor = clazz.getConstructor(PlayScreen.class, String.class, Box2dSprite.class);
                        Object instance = constructor.newInstance(this, m.itemIdentifier, null);
                        sprites.add((Box2dSprite) instance);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        for (Entity c : nc.children) {
            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
            CustomVariables customVariables = new CustomVariables();
            customVariables.loadFromString(m.customVars);
            String BID = customVariables.getStringVariable("BID");
            String parentname = customVariables.getStringVariable("parentname");
            if (m.itemIdentifier != null && m.itemIdentifier.length() > 0 && parentname == null) {
                String luokka = customVariables.getStringVariable("class");
                try {
                    if (BID != null) {
                        Box2dSprite children = new Box2dSprite(this, m.itemIdentifier, null, true);
                        rvjs.add(children);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
//        for (String bid:bids) {
//            makeAIDBID(bid);
//        }
    }

    public Car getCar() {
        if (car != null) {
            return car;
        }
        for (Box2dSprite b : sprites) {
            if (b instanceof Car) {
                car = (Car) b;
                break;
            }
        }
        return car;
    }

    public float getLeftFromtMaxLateralImpulse() {
        return getCar().getLeftFromtMaxLateralImpulse();
    }

    public void setLeftFromtMaxLateralImpulse(float f) {
        getCar().setLeftFromtMaxLateralImpulse(f);
        getCar().setRightFromtMaxLateralImpulse(f);
    }

    public void addLeftFromtMaxLateralImpulse() {
        float old = getCar().getLeftFromtMaxLateralImpulse();
        float newValue = old + 0.01f;
        setLeftFromtMaxLateralImpulse(newValue);
    }

    public void subLeftFromtMaxLateralImpulse() {
        float old = getCar().getLeftFromtMaxLateralImpulse();
        float newValue = old - 0.01f;
        setLeftFromtMaxLateralImpulse(newValue);
    }

    public boolean isInRvjs(String itemIdentifier) {
        for (Entity c : entities) {
            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
            CustomVariables customVariables = new CustomVariables();
            customVariables.loadFromString(m.customVars);
//            String BID = customVariables.getStringVariable("BID");
            String BID = customVariables.getStringVariable("BID");
            if (itemIdentifier.equals(BID)) {
                return true;
            }
        }
        return false;
    }

    public Box2dSprite getBox2dSprite(String itemIdentifier) {
        for (Box2dSprite sprite : sprites) {
            if (sprite.getOverlap2dIdentifier().equals(itemIdentifier)) {
                return sprite;
            }
            for (Box2dSprite children : sprite.getChildren()) {
                if (children.getOverlap2dIdentifier().equals(itemIdentifier)) {
                    return children;
                }
            }
        }
        return null;
    }
//    private void makeAIDBID(String oid) {
//        Entity root=this.getSl().getRoot();
//        NodeComponent nc = ComponentRetriever.get(root, NodeComponent.class);
//        for (Entity c : nc.children) {
//            MainItemComponent m = ComponentRetriever.get(c, MainItemComponent.class);
//            CustomVariables customVariables = new CustomVariables();
//            customVariables.loadFromString(m.customVars);
//            String BID=customVariables.getStringVariable("BID");
//            if (BID!=null) {
//                String AID=customVariables.getStringVariable("AID");
//
//                PhysicsBodyLoader instanssi =
//                        PhysicsBodyLoader.getInstance();
//                physicsBodyComponent.body =
//                        instanssi.createBody(playscreen.getWorld(), this.entity, physicsBodyComponent, polygonComponent.vertices,
//                                transformComponent);
//                Box2dSprite a=this.get(AID);
//                Box2dSprite b=this.get(BID);
//                RevoluteJointDef jointDef = new RevoluteJointDef();
//                jointDef.bodyA = a.getPhysicsBodyComponent().body;
//                jointDef.bodyB = b.getPhysicsBodyComponent().body;
//                jointDef.enableLimit = true;
//                jointDef.lowerAngle = 0;
//                jointDef.upperAngle = 0;
////                BoundingBox boundingBoxa = PhysicsUtil.calculateBoundingBox(jointDef.bodyA);
////                float wa = (boundingBoxa.max.x - boundingBoxa.min.x);
////                float ha = (boundingBoxa.max.y - boundingBoxa.min.y);
////
////
////                BoundingBox boundingBoxb = PhysicsUtil.calculateBoundingBox(jointDef.bodyB);
////                float wb = (boundingBoxb.max.x - boundingBoxb.min.x);
////                float hb = (boundingBoxb.max.y - boundingBoxb.min.y);
//                //pitää siis laskea 2 asiaa
//
//                //1. parentin ja jointtipisteen välinen etäisyys
//
//
//
////                float x =
////                        jointDef.bodyB.getPosition().x - jointDef.bodyA.getPosition().x;
////                float y =
////                        jointDef.bodyB.getPosition().y - jointDef.bodyA.getPosition().y;
//
////                jointDef.localAnchorA.set(x, y);
////                String localAnchorA = customVariables.getStringVariable("localAnchorA");
////                if (localAnchorA != null) {
////                    String xy[] = localAnchorA.split(",");
////                    jointDef.localAnchorA.set(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]));
////                }
////                String localAnchorB = customVariables.getStringVariable("localAnchorB");
////                if (localAnchorB != null) {
////                    String xy[] = localAnchorB.split(",");
////                    jointDef.localAnchorB.set(Float.parseFloat(xy[0]), Float.parseFloat(xy[1]));
////                }
//                String enableLimit = customVariables.getStringVariable("enableLimit");
//                if (enableLimit != null) {
//                    jointDef.enableLimit = Boolean.parseBoolean(enableLimit);
//                }
//                String collideConnected = customVariables.getStringVariable("collideConnected");
//                if (collideConnected != null) {
//                    jointDef.collideConnected = Boolean.parseBoolean(collideConnected);
//                }
//                String lowerAngle = customVariables.getStringVariable("lowerAngle");
//                if (lowerAngle != null) {
//                    jointDef.lowerAngle = Float.parseFloat(lowerAngle) * DEGTORAD;
//                }
//                String upperAngle = customVariables.getStringVariable("upperAngle");
//                if (upperAngle != null) {
//                    jointDef.upperAngle = Float.parseFloat(upperAngle) * DEGTORAD;
//                }
//                String referenceAngle = customVariables.getStringVariable("referenceAngle");
//                if (referenceAngle != null) {
//                    jointDef.referenceAngle = Float.parseFloat(referenceAngle) * DEGTORAD;
//                }
//                b.setJoint(this.getWorld().createJoint(jointDef));
//            }
//        }
//    }
//    public Box2dSprite get(String itemIdentifier) {
//        for (Box2dSprite c : sprites) {
//            if (c.mainItemComponent.itemIdentifier.equals(itemIdentifier)) {
//                return c;
//            }
//        }
//        return null;
//    }

    public ArrayList<Box2dSprite> getSprites() {
        return sprites;
    }

    public void setSprites(ArrayList<Box2dSprite> sprites) {
        this.sprites = sprites;
    }

    //Box2d variables
    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public SceneLoader getSl() {
        return sl;
    }

    public void setSl(SceneLoader sl) {
        this.sl = sl;
    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);
//        deer.update(dt);
//        deer2.update(dt);
//        for (Auto auto : autot) {
//            auto.update(dt);
//            for (Rengas r : auto.getTires()) {
//                r.update(dt);
//            }
//        }
//        ruoho.update(dt);
//        rekka.update(dt);
//        truck.update(dt);
        for (Box2dSprite b : sprites) {
            b.update(dt);
        }
        gamecam.update();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        update(delta);
        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (b2dr != null)
            b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
//        ruoho.draw(game.batch);
//        rekka.draw(game.batch);
//        truck.draw(game.batch);
//        for (Auto auto : autot) {
//            auto.draw(game.batch);
//            for (Rengas r : auto.getTires()) {
//                r.draw(game.batch);
//            }
//        }
//        deer.draw(game.batch);
//        deer2.draw(game.batch);
        int x = 10;
        int y = 120;
        for (Box2dSprite b : sprites) {
            if (b.isDraw()) {
                if (b instanceof Car) {
                    Car c = (Car) b;
                    font.draw(game.batch, "LeftFromtMaxLateralImpulse=" + getLeftFromtMaxLateralImpulse(), x, y);
                    y -= 5;
                    font.draw(game.batch, "center.x=" + c.getPhysicsBodyComponent().body.getMassData().center.x, x, y);
                    y -= 5;
                    font.draw(game.batch, "center.y=" + c.getPhysicsBodyComponent().body.
                            getMassData().center.y, x, y);
                    y -= 5;
                    font.draw(game.batch, "LinearVelocity().x="
                            + c.getPhysicsBodyComponent().body.getLinearVelocity().x, x, y);
                    y -= 5;
                    font.draw(game.batch, "Acceleration()="
                            + c.getAcceleration(), x, y);
                    y -= 5;
                    font.draw(game.batch, "CurrentSpeed()="
                            + c.getCurrentSpeed(), x, y);

//                    y -= 5;
//                    font.draw(game.batch, "lateralspeed()="
//                            + c.getLateralSpeed(), x, y);
                }
//                b.draw(game.batch);
            }
        }
//font.d
//        font.draw(game.batch,""+getLeftFromtMaxLateralImpulse(), 10, 10);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //updated our game viewport
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    public void handleInput(float dt) {
        HashSet<InputManager.Key> pressedKeys = new HashSet<InputManager.Key>();
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            addLeftFromtMaxLateralImpulse();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.V)) {
            subLeftFromtMaxLateralImpulse();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            pressedKeys.add(InputManager.Key.Right);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            pressedKeys.add(InputManager.Key.Left);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            pressedKeys.add(InputManager.Key.Up);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            pressedKeys.add(InputManager.Key.Down);
        }
        for (Box2dSprite b : sprites) {
            if (b instanceof Car) {
                ((Car) b).update(pressedKeys);
            }
        }
    }

    public void removeSprite(Box2dSprite b) {
        for (Box2dSprite k : sprites) {
            k.getChildren().remove(b);
        }
        sprites.remove(b);
    }
}

