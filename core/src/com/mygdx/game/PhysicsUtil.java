package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

public abstract class PhysicsUtil {

    /**
     * Calculates a {@link BoundingBox} for the given {@link Fixture}. It will
     * be in physics/world coordinates.
     */
    public static BoundingBox calculateBoundingBox(Fixture fixture) {
        BoundingBox boundingBox = new BoundingBox();
        switch (fixture.getShape().getType()) {
            case Polygon: {
                PolygonShape shape = (PolygonShape) fixture.getShape();
                Vector2 tmp = new Vector2();
                shape.getVertex(0, tmp);
                tmp = fixture.getBody().getWorldPoint(tmp);
                boundingBox = new BoundingBox(new Vector3(tmp, 0), new Vector3(tmp, 0));
                for (int i = 1; i < shape.getVertexCount(); i++) {
                    shape.getVertex(i, tmp);
                    boundingBox.ext(new Vector3(fixture.getBody().getWorldPoint(tmp), 0));
                }
                break;
            }
            case Circle: {
                // TODO implement
                break;
            }
            case Chain: {
                // TODO implement
                break;
            }
            case Edge:
                // TODO how to handle this? not at all?
                break;
            default:
                throw new RuntimeException("Type unkown.");
        }
        return boundingBox;
    }

    public static List<Body> getBodiesAtPoint(World world, Vector2 point) {
        float delta = 0.01f;
        SimpleQueryCallback callback = new SimpleQueryCallback(true);
        world.QueryAABB(callback, point.x - delta, point.y - delta, point.x + delta, point.y + delta);
        return callback.bodies;
    }


    public static BoundingBox calculateBoundingBox(Body body) {
        BoundingBox boundingBox = null;
        for (Fixture fixture : body.getFixtureList()) {
            if (boundingBox == null) {
                boundingBox = calculateBoundingBox(fixture);
            } else {
                boundingBox.ext(calculateBoundingBox(fixture));
            }
        }
        return boundingBox;
    }
}