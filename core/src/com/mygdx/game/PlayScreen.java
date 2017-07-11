package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mietmark on 4.7.2017.
 */

public class PlayScreen implements Screen {
    //Box2d variables
    public World world;
    //basic playscreen variables
    private OrthographicCamera gamecam;
//    private Hud hud;

    //Tiled map variables
//    private TmxMapLoader maploader;
//    private TiledMap map;
//    private OrthogonalTiledMapRenderer renderer;
    private Viewport gamePort;
    private Box2DDebugRenderer b2dr;
    private MyGdxGame game;
    private SceneLoader sl;
    private ItemWrapper rootItem;
    private AnimationSprite deer;
    private AnimationSprite deer2;
    private TextureRegionSprite ruoho;
    private TextureRegionSprite rekka;
    private TextureRegionSprite truck;
    private ArrayList<Auto> autot = new ArrayList<Auto>();
    private List<TextureRegionSprite> rekanOsat = new ArrayList<TextureRegionSprite>();

    //    private B2WorldCreator creator;
    public PlayScreen(MyGdxGame game) {
//    atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        //create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();
        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gamecam);
        //create our game HUD for scores/timers/level info
//    hud = new Hud(game.batch);
        //Load our map and setup our map renderer
//    maploader = new TmxMapLoader();
//    map = maploader.load("level1.tmx");
//    renderer = new OrthogonalTiledMapRenderer(map, 1  / MyGdxGame.PPM);
        //initially set our gamcam to be centered correctly at the start of of map
//    gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);
//        world.setContactListener(new WorldContactListener(this));
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();
/*
//        b2dr = null;

    creator = new B2WorldCreator(this);

    //create mario in our game world
    player = new Mario(this);

    world.setContactListener(new WorldContactListener());

    music = MyGdxGame.manager.get("audio/music/mario_music.ogg", Music.class);
    music.setLooping(true);
    music.setVolume(0.3f);
    //music.play();

    items = new Array<Item>();
    itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
*/
//        createBottle();
//        createSprites();
        sl = new SceneLoader(); // default scene loader loads all resources from default RM as usual.
        sl.loadScene("MainScene", gamePort); // loading scene as usual
//        sl.loadScene("MainScene", true); // loading scene as usual
        deer = new AnimationSprite(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("deer").addScript(deer);
        deer2 = new AnimationSprite(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("deer2").addScript(deer2);
        ruoho = new TextureRegionSprite(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("ruoho").addScript(ruoho);
        rekka = new TextureRegionSprite(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("rekka").addScript(rekka);
        truck = new TextureRegionSprite(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("truck").addScript(truck);

        Auto auto = new Auto(this, "auto");
        autot.add(auto);

        Auto auto2 = new Auto(this, "auto");
        autot.add(auto2);

//
//
//
//        Auto auto = new Auto(this);
//        auto.overriddenx =1050f;
//////        rootItem = new ItemWrapper(sl.getRoot());
//        rootItem1.getChild("auto").addScript(auto);
//        autot.add(auto);
//
////        sl1.getEngine().removeAllEntities();
//
//        SceneLoader sl2 = new SceneLoader();
//
//
//        sl2.loadScene("MainScene");
//
////        sl1.getEngine().removeEntity(rootItem1.getChild("auto").getEntity());
//        ItemWrapper rootItem2 = new ItemWrapper(sl2.getRoot());
//
//
//        Auto auto2 = new Auto(this);
//        rootItem2.getChild("auto").addScript(auto2);
//        autot.add(auto2);
//sl.getEngine().
//        Entity atu=rootItem.getChild("auto").getEntity();
//        Entity atu2=new Entity();
//
//        for (Component c:atu.getComponents())
//        atu2.add(c);
//        rootItem.addChild(atu2);
//
//        Auto auto2 = new Auto(this);
////        rootItem = new ItemWrapper(sl.getRoot());
//        rootItem.getChild("auto").addScript(auto2);
//        autot.add(auto2);
//        Auto auto2=(Auto)deepClone(auto);
//        autot.add(auto2);
    }
//    public static Object deepClone(Object object) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(object);
//            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//            ObjectInputStream ois = new ObjectInputStream(bais);
//            return ois.readObject();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public SceneLoader getSl() {
        return sl;
    }

    public void setSl(SceneLoader sl) {
        this.sl = sl;
    }

    public void update(float dt) {
        //handle user input first
        handleInput(dt);
//        handleSpawningItems();
        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);
        deer.update(dt);
        deer2.update(dt);
        for (Auto auto : autot) {
            auto.update(dt);
            for (Rengas r : auto.tires) {
                r.update(dt);
            }
        }
        ruoho.update(dt);
        rekka.update(dt);
        truck.update(dt);
        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world.
//        renderer.setView(gamecam);
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
        //render our game map
//        renderer.render();
        //renderer our Box2DDebugLines
        if (b2dr != null)
            b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        ruoho.draw(game.batch);
        rekka.draw(game.batch);
        truck.draw(game.batch);
        for (Auto auto : autot) {
            auto.draw(game.batch);
//            System.out.println("auto.getX()="+auto.getX());

            for (Rengas r : auto.tires) {
                r.draw(game.batch);
            }
        }
        deer.draw(game.batch);
        deer2.draw(game.batch);
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

    //    boolean lippu=true;
    public void handleInput(float dt) {
        HashSet<InputManager.Key> pressedKeys = new HashSet<InputManager.Key>();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && deer.physicsBodyComponent.body.getLinearVelocity().x <= 2000) {
//            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(100f, 0), deer.physicsBodyComponent.body.getWorldCenter(), true);
            pressedKeys.add(InputManager.Key.Right);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && deer.physicsBodyComponent.body.getLinearVelocity().x >= -2000) {
//            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(-100f, 0), deer.physicsBodyComponent.body.getWorldCenter(), true);
            pressedKeys.add(InputManager.Key.Left);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(0, 100f), deer.physicsBodyComponent.body.getWorldCenter(), true);
            pressedKeys.add(InputManager.Key.Up);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && deer.physicsBodyComponent.body.getLinearVelocity().x >= -2000) {
//            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(0, -100f), deer.physicsBodyComponent.body.getWorldCenter(), true);
            pressedKeys.add(InputManager.Key.Down);
        }
//        boolean eka=true;
        for (Auto auto : autot) {
//            if (eka && lippu)
//            if (auto.autonimi.equals("ferrari"))
            auto.update(pressedKeys);
//            eka=true;
        }
//        lippu=!lippu;
    }
}

