package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tarashgames.handlers.InputManager;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mietmark on 4.7.2017.
 */

public class PlayScreen implements Screen {
    private World world;
    //basic playscreen variables
    private OrthographicCamera gamecam;
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
//    private List<TextureRegionSprite> rekanOsat = new ArrayList<TextureRegionSprite>();


    private ArrayList<Sprite> sprites = new ArrayList<Sprite>();


    public PlayScreen(MyGdxGame game) {
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gamecam);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        world.setContactListener(new WorldContactListener(this));
        sl = new SceneLoader(); // default scene loader loads all resources from default RM as usual.
        sl.loadScene("MainScene", gamePort); // loading scene as usual
        deer = new AnimationSprite(this, "deer");
        deer2 = new AnimationSprite(this, "deer2");
        ruoho = new TextureRegionSprite(this, "ruoho");
        rekka = new TextureRegionSprite(this, "rekka");
        truck = new TextureRegionSprite(this, "truck");
        Auto auto = new Auto(this, "auto");
        autot.add(auto);
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
        deer.update(dt);
        deer2.update(dt);
        for (Auto auto : autot) {
            auto.update(dt);
            for (Rengas r : auto.getTires()) {
                r.update(dt);
            }
        }
        ruoho.update(dt);
        rekka.update(dt);
        truck.update(dt);
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
        ruoho.draw(game.batch);
        rekka.draw(game.batch);
        truck.draw(game.batch);
        for (Auto auto : autot) {
            auto.draw(game.batch);
            for (Rengas r : auto.getTires()) {
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

    public void handleInput(float dt) {
        HashSet<InputManager.Key> pressedKeys = new HashSet<InputManager.Key>();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && deer.physicsBodyComponent.body.getLinearVelocity().x <= 2000) {
            pressedKeys.add(InputManager.Key.Right);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && deer.physicsBodyComponent.body.getLinearVelocity().x >= -2000) {
            pressedKeys.add(InputManager.Key.Left);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pressedKeys.add(InputManager.Key.Up);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && deer.physicsBodyComponent.body.getLinearVelocity().x >= -2000) {
            pressedKeys.add(InputManager.Key.Down);
        }
        for (Auto auto : autot) {
            auto.update(pressedKeys);
        }
    }
}

