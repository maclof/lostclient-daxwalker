package org.lostclient.api.wrappers.walking.dax_api.api_lib.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;

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
//            public RSTile getAnimablePosition() {
//                return new RSTile(x, y, z);
//            }
//
//            @Override
//            public boolean adjustCameraTo() {
//                return false;
//            }
//
//            @Override
//            public RSTile getPosition() {
//                return new RSTile(x, y, z);
//            }
//        };
    }

    public static Point3D fromPositionable(Locatable positionable) {
        RSTile tile = RSTile.fromTile(positionable.getTile());
        return new Point3D(tile.getX(), tile.getY(), tile.getZ());
    }

}
