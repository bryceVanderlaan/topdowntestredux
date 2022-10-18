package com.test.game.screen.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.test.game.TopDownTestRedux.UNIT_SCALE;

public class GameMap {
    //tag for debug log
    public static final String TAG = GameMap.class.getSimpleName();
    //the tiled map that will be loaded into the game
    private final TiledMap tiledMap;
    //array of collision objects
    private final Array<CollisionArea>collisionAreas;
    //starting position of the player
    private final Vector2 startLocation;

    /**
     * Parameterized constructor that initializes the map and collision objects array,
     * populates the collision objects, and sets the starting position of the player.
     * @param tiledMap .tmx tiled map (usually made in the Tiled Map editor)
     */
    public GameMap(final TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        collisionAreas = new Array<CollisionArea>();

        parseCollisionLayer();
        startLocation = new Vector2();
        parsePlayerStartLocation();

    }

    private void parsePlayerStartLocation() {
        //create new map layer var to hold the layer containing the starting position of the player
        final MapLayer startLocLayer = tiledMap.getLayers().get("playerStartLoc");
        //if the layer comes back as null, it does not exist
        //print this info to the debug log and return
        if (startLocLayer == null) {
            Gdx.app.debug(TAG, "There is no collision layer");
            return;
        }

        //list of map objects taken from the current layer
        final MapObjects mapObjects = startLocLayer.getObjects();

        //for each map object in the list of map objects
        for (final MapObject mapObject : mapObjects) {
            //if that object is a rectangle
            if (mapObject instanceof RectangleMapObject) {
                //type cast the current map object to a rectangle map object
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
                //retrieve the rectangle from the rectangle map object
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                //set the start location of the player as the x and y coords of the rectangle both multiplied by the unit scale of the game
                startLocation.set(rectangle.x * UNIT_SCALE,rectangle.y * UNIT_SCALE);
            } else {
                Gdx.app.debug(TAG,"Map Object of type " + mapObject + " is not supported for player start location layer");
            }
        }
    }

    private void parseCollisionLayer() {
        //create a map layer var to hold the layer of the map titled "collision"
        final MapLayer collisionLayer = tiledMap.getLayers().get("collision");
        //if we didn't find anything, there is no collision layer
        //print this info to the debug log and return
        if (collisionLayer == null) {
            Gdx.app.debug(TAG,"There is no collision layer");
            return;
        }

        //list of map objects take from the current layer
        final MapObjects mapObjects = collisionLayer.getObjects();

        //for each map object in the list of map objects
        for(final MapObject mapObject : mapObjects) {
            //if the current object is a rectangle object
            if (mapObject instanceof RectangleMapObject) {
                //retrieve the rectangle and set the vertices
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                final float[] rectVertices = new float[10];

                //left bottom
                rectVertices[0] = 0;
                rectVertices[1] = 0;
                //left top
                rectVertices[2] = 0;
                rectVertices[3] = rectangle.height;
                //right top
                rectVertices[4] = rectangle.width;
                rectVertices[5] = rectangle.height;
                //right bottom
                rectVertices[6] = rectangle.width;
                rectVertices[7] = 0;
                //left bottom
                rectVertices[8] = 0;
                rectVertices[9] = 0;

                //add this to the array of collision areas using the x and y coords of the rect
                //as the starting point and the vertices to actually build the rectangle.
                collisionAreas.add(new CollisionArea(rectangle.x,rectangle.y,rectVertices));

            //otherwise, if the map object is a polyline (a polygon, essentially)
            } else if (mapObject instanceof PolylineMapObject) {
                //retrieve the polyline
                final PolylineMapObject polylineMapObject = (PolylineMapObject) mapObject;
                final Polyline polyline = polylineMapObject.getPolyline();
                //add this to the array of collision areas using the x and y coords of the polyline
                //as the starting point and the vertices to actually build the polyline/polygon
                collisionAreas.add(new CollisionArea(polyline.getX(),polyline.getY(),polyline.getVertices()));
            } else {
                //otherwise, the object type is not supported, print this info to the debug log
                Gdx.app.debug(TAG,"Map Object of type " + mapObject + " is not supported for collision layer");
            }
        }
    }

    public Array<CollisionArea> getCollisionAreas() {
        return collisionAreas;
    }

    public Vector2 getStartLocation() {
        return startLocation;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
