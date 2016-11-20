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
    public Stage3D(FitViewport viewport, PolygonSpriteBatch batch) {
		super(viewport, batch);
	}

	@Override
    public Vector2 screenToStageCoordinates (Vector2 screenCoords) {
        Ray pickRay = getViewport().getPickRay(screenCoords.x, screenCoords.y);
        Vector3 intersection = new Vector3(0, 0, 1);
        if (Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), Vector3.Zero), intersection)) {
                screenCoords.x = intersection.x;
                screenCoords.y = intersection.y;
        } else {
                screenCoords.x = Float.MAX_VALUE;
                screenCoords.y = Float.MAX_VALUE;
        }
        return screenCoords;
    }

}