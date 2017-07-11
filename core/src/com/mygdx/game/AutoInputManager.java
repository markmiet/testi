package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.tarashgames.Main;
import com.tarashgames.handlers.InputManager;

import java.util.HashSet;

/**
 * Created by mietmark on 6.7.2017.
 */

public class AutoInputManager implements InputProcessor {

    public HashSet<InputManager.Key> pressedKeys = new HashSet<InputManager.Key>();
    Main mainClass;

    public AutoInputManager(Main main) {
        this.mainClass = main;
    }

    public void update() {
        mainClass.car.update(pressedKeys);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            if (!pressedKeys.contains(InputManager.Key.Up)) {
                pressedKeys.add(InputManager.Key.Up);
            }
        } else if (keycode == Input.Keys.DOWN) {
            if (!pressedKeys.contains(InputManager.Key.Down)) {
                pressedKeys.add(InputManager.Key.Down);
            }
        } else if (keycode == Input.Keys.LEFT) {
            if (!pressedKeys.contains(InputManager.Key.Left)) {
                pressedKeys.add(InputManager.Key.Left);
            }
        } else if (keycode == Input.Keys.RIGHT) {
            if (!pressedKeys.contains(InputManager.Key.Right)) {
                pressedKeys.add(InputManager.Key.Right);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.UP) {
            if (pressedKeys.contains(InputManager.Key.Up)) {
                pressedKeys.remove(InputManager.Key.Up);
            }
        } else if (keycode == Input.Keys.DOWN) {
            if (pressedKeys.contains(InputManager.Key.Down)) {
                pressedKeys.remove(InputManager.Key.Down);
            }
        } else if (keycode == Input.Keys.LEFT) {
            if (pressedKeys.contains(InputManager.Key.Left)) {
                pressedKeys.remove(InputManager.Key.Left);
            }
        } else if (keycode == Input.Keys.RIGHT) {
            if (pressedKeys.contains(InputManager.Key.Right)) {
                pressedKeys.remove(InputManager.Key.Right);
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

    public enum Key {
        Up, Down, Right, Left
    }
}
