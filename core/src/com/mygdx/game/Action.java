package com.mygdx.game;

/**
 * Created by mietmark on 12.7.2017.
 */
public class Action {
    private boolean collisionAction;
    private boolean canRecur;
    private boolean joint;
    private ACTION action;

    public Action(boolean collisionAction, boolean canRecur, boolean joint, ACTION action) {
        this.collisionAction = collisionAction;
        this.canRecur = canRecur;
        this.joint = joint;
        this.action = action;
    }

    public static Action getAction(String str) {
        if (str.equals("DJ")) {
            return new Action(true, false, true, ACTION.DESTROY);
        }
        return null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        Action action1 = (Action) o;
        if (isCollisionAction() != action1.isCollisionAction()) return false;
        if (isCanRecur() != action1.isCanRecur()) return false;
        if (isJoint() != action1.isJoint()) return false;
        return getAction() == action1.getAction();
    }

    @Override
    public int hashCode() {
        int result = (isCollisionAction() ? 1 : 0);
        result = 31 * result + (isCanRecur() ? 1 : 0);
        result = 31 * result + (isJoint() ? 1 : 0);
        result = 31 * result + (getAction() != null ? getAction().hashCode() : 0);
        return result;
    }

    public enum ACTION {
        DESTROY
    }
}
