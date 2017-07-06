package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
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
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
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
    private Deer deer;
    private Deer deer2;


    private Ruoho ruoho;
    private Ruoho rekka;


    private List<Ruoho> rekanOsat=new ArrayList<Ruoho>();

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
        deer = new Deer(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("deer").addScript(deer);

        deer2 = new Deer(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("deer2").addScript(deer2);


        ruoho = new Ruoho(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("ruoho").addScript(ruoho);


        rekka = new Ruoho(this);
        rootItem = new ItemWrapper(sl.getRoot());
        rootItem.getChild("rekka").addScript(rekka);

//        Rekka rekka = new Rekka(this);
//        rootItem = new ItemWrapper(sl.getRoot());
//        rootItem.getChild("rekka").addScript(rekka);
        /*
        Entity rekkaentity =
                rootItem.getChild("rekka").getEntity();

        NodeComponent n = ComponentRetriever.get(rekkaentity, NodeComponent.class);

        for (Entity e : n.children) {
            Ruoho r = new Ruoho(this);

            ItemWrapper ii = new ItemWrapper();
            IScript iskripti=ii.addScript(r);
            rekanOsat.add((Ruoho)iskripti);
        }

*/
    }

    public void update(float dt) {
        //handle user input first
        handleInput(dt);
//        handleSpawningItems();

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);
        deer.update(dt);
        deer2.update(dt);

        ruoho.update(dt);
        rekka.update(dt);

//        if (rekanOsat!=null) {
//            for (Ruoho r:rekanOsat) {
//                r.update(dt);
//            }
//        }


//        player.update(dt);
//        for(Enemy enemy : creator.getEnemies()) {
//            enemy.update(dt);
//            if(enemy.getX() < player.getX() + 224 / MyGdxGame.PPM) {
//                enemy.b2body.setActive(true);
//            }
//        }

//        for(Item item : items)
//            item.update(dt);

//        hud.update(dt);

//        //attach our gamecam to our players.x coordinate
//        if(player.currentState != Mario.State.DEAD) {
//            gamecam.position.x = player.b2body.getPosition().x;
//        }

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
//        bottleSprite.draw(game.batch);

//        deer.physicsBodyComponent.body.getAngle()

//        deer.draw(game.batch);
//        float degree=MathUtils.radDeg(deer.physicsBodyComponent.body.getAngle());


        ruoho.draw(game.batch);
//        ruoho.draw(game.batch,delta);

//        rekka.draw(game.batch,delta);
        rekka.draw(game.batch);


//        for (Ruoho r:rekanOsat) {
//            r.draw(game.batch);
//        }
//        deer.draw(game.batch, delta);
//        deer2.draw(game.batch, delta);

        deer.draw(game.batch);
        deer2.draw(game.batch);



//        rekka.draw(game.batch,delta);

//        player.draw(game.batch);
//        for (Enemy enemy : creator.getEnemies())
//            enemy.draw(game.batch);
//        for (Item item : items)
//            item.draw(game.batch);


        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
//        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
//        hud.stage.draw();

//        if(gameOver()){
//            game.setScreen(new GameOverScreen(game));
//            dispose();
//        }


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
        //control our player using immediate impulses
//            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
//                player.jump();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && deer.physicsBodyComponent.body.getLinearVelocity().x <= 2000)
            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(100f, 0), deer.physicsBodyComponent.body.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && deer.physicsBodyComponent.body.getLinearVelocity().x >= -2000)
            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(-100f, 0), deer.physicsBodyComponent.body.getWorldCenter(), true);


        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(0, 100f), deer.physicsBodyComponent.body.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && deer.physicsBodyComponent.body.getLinearVelocity().x >= -2000)
            deer.physicsBodyComponent.body.applyLinearImpulse(new Vector2(0, -100f), deer.physicsBodyComponent.body.getWorldCenter(), true);


    }


}
