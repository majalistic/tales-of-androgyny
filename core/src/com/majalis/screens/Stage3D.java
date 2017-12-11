package com.majalis.screens;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Stage3D extends Stage {
    private final Vector3 intersection;
	private final Plane plane;
    
	public Stage3D(FitViewport viewport, PolygonSpriteBatch batch) {
		super(viewport, batch);
		intersection = new Vector3(0, 0, 1);
		plane = new Plane(new Vector3(0, 0, 1), Vector3.Zero);
	}

	@Override
    public Vector2 screenToStageCoordinates (Vector2 screenCoords) {
        Ray pickRay = getViewport().getPickRay(screenCoords.x, screenCoords.y);
        if (Intersector.intersectRayPlane(pickRay, plane, intersection)) {
                screenCoords.x = intersection.x;
                screenCoords.y = intersection.y;
        } else {
                screenCoords.x = Float.MAX_VALUE;
                screenCoords.y = Float.MAX_VALUE;
        }
        return screenCoords;
    }

}