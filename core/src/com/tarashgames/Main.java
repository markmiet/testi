package com.tarashgames;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.tarashgames.car.Car;
import com.tarashgames.car.Constants;
import com.tarashgames.car.GroundAreaType;
import com.tarashgames.handlers.CarContactListener;
import com.tarashgames.handlers.InputManager;

public class Main extends ApplicationAdapter {

	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	
	OrthographicCamera camera;
	Box2DDebugRenderer renderer;
	public World world;
	InputManager inputManager;
	CarContactListener cl;
	public Car car;
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		camera.zoom = 0.3f;
		camera.position.x = 0;
		camera.position.y = 0;
		
		world = new World(new Vector2(0, 0f), true);

		cl = new CarContactListener();
		world.setContactListener(cl);
		
		renderer = new Box2DDebugRenderer();
		renderer.setDrawJoints(false);
		
		inputManager = new InputManager(this);
		Gdx.input.setInputProcessor(inputManager);
		
	    this.car = new Car(world);

		createGrounds();
	}

	@Override
	public void render () {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		inputManager.update();

		world.step(1 / 60f, 6, 2);

		renderer.render(world, camera.combined);

//		camera.position.set(new Vector3(car.body.getPosition().x, car.body
//				.getPosition().y, camera.position.z));
//
//		camera.update();
	}
	
	private void createGrounds(){
		 
		BodyDef bodyDef = new BodyDef();
		Body ground = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Constants.GROUND;
		fixtureDef.filter.maskBits = Constants.TIRE;
		
		shape.setAsBox(9, 7, new Vector2(-10,15), 20*Constants.DEGTORAD);
		Fixture groundAreaFixture = ground.createFixture(fixtureDef);
		groundAreaFixture.setUserData(new GroundAreaType(2, false));
		
		shape.setAsBox(9,  5, new Vector2(5, 20), -40 * Constants.DEGTORAD);
		groundAreaFixture = ground.createFixture(fixtureDef);
		groundAreaFixture.setUserData(new GroundAreaType(0.02f, false));
		
	}
}
