package com.mygdx.game;

/**
 * Created by mietmark on 12.7.2017.
 */
public class Action {
    private boolean collisionAction;
    private boolean canRecur;
    private boolean joint;
    private ACTION action;
    private float force;

    public Action(boolean collisionAction, boolean canRecur, boolean joint, ACTION action, float force) {
        this.collisionAction = collisionAction;
        this.canRecur = canRecur;
        this.joint = joint;
        this.action = action;
        this.force = force;
    }

    public static Action getAction(String str) {
        if (str.equals("DJ")) {
            return new Action(true, false, true, ACTION.DESTROY, 0);
        }
        return null;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public ACTION getAction() {
        return action;
    }

    public void setAction(ACTION action) {
        this.action = action;
    }

    public boolean isCanRecur() {
        return canRecur;
    }

    public void setCanRecur(boolean canRecur) {
        this.canRecur = canRecur;
    }

    public boolean isCollisionAction() {
        return collisionAction;
    }

    public void setCollisionAction(boolean collisionAction) {
        this.collisionAction = collisionAction;
    }

    public boolean isJoint() {
        return joint;
    }

    public void setJoint(boolean joint) {
        this.joint = joint;
    }

    public enum ACTION {
        DESTROY
    }
}
