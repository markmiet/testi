package com.tarashgames.handlers;

import java.util.HashSet;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.tarashgames.Main;

public class InputManager  implements InputProcessor  {

	Main mainClass;

	public InputManager(Main main){
		this.mainClass = main;
	}
	
	public enum Key {
		Up, Down, Right, Left
	}

	public HashSet<Key> pressedKeys = new HashSet<Key>();

	public void update(){
		mainClass.car.update(pressedKeys);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.UP) {
			if (!pressedKeys.contains(Key.Up)) {
				pressedKeys.add(Key.Up);
			}
		} else if (keycode == Input.Keys.DOWN) {
			if (!pressedKeys.contains(Key.Down)) {
				pressedKeys.add(Key.Down);
			}
		} else if (keycode == Input.Keys.LEFT) {
			if (!pressedKeys.contains(Key.Left)) {
				pressedKeys.add(Key.Left);
			}
		} else if (keycode == Input.Keys.RIGHT) {
			if (!pressedKeys.contains(Key.Right)) {
				pressedKeys.add(Key.Right);
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.UP) {
			if (pressedKeys.contains(Key.Up)) {
				pressedKeys.remove(Key.Up);
			}
		} else if (keycode == Input.Keys.DOWN) {
			if (pressedKeys.contains(Key.Down)) {
				pressedKeys.remove(Key.Down);
			}
		} else if (keycode == Input.Keys.LEFT) {
			if (pressedKeys.contains(Key.Left)) {
				pressedKeys.remove(Key.Left);
			}
		} else if (keycode == Input.Keys.RIGHT) {
			if (pressedKeys.contains(Key.Right)) {
				pressedKeys.remove(Key.Right);
			}
		}

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
