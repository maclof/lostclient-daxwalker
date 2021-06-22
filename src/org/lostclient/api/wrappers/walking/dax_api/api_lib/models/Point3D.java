package org.lostclient.api.wrappers.walking.dax_api.api_lib.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.map.Tile;

public class Point3D {

    private int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public JsonElement toJson() {
        return new Gson().toJsonTree(this);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }


    public Locatable toPositionable() {
        MethodProvider.logDebug("toLocatable NOT IMPLEMENTED!");
        return null;
//        return new Locatable() {
//            @Override
//            public Tile getAnimablePosition() {
//                return new Tile(x, y, z);
//            }
//
//            @Override
//            public boolean adjustCameraTo() {
//                return false;
//            }
//
//            @Override
//            public Tile getPosition() {
//                return new Tile(x, y, z);
//            }
//        };
    }

    public static Point3D fromPositionable(Locatable positionable) {
        Tile Tile = positionable.getTile();
        return new Point3D(Tile.getX(), Tile.getY(), Tile.getZ());
    }

}
