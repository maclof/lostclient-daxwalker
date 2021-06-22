package org.lostclient.api.wrappers.walking.dax_api.shared;

import org.lostclient.api.Client;
import org.lostclient.api.wrappers.map.Tile;

public class RSTile extends Tile {
    private final TYPES type;

    public RSTile(int x, int y) {
        super(x, y);
        this.type = TYPES.WORLD;
    }

    public RSTile(int x, int y, int z) {
        super(x, y, z);
        this.type = TYPES.WORLD;
    }

    public RSTile(int x, int y, int z, TYPES type) {
        super(x, y, z);
        this.type = type;
    }

    public static RSTile fromTile(Tile tile) {
        return new RSTile(tile.getX(), tile.getY(), tile.getZ());
    }

    public RSTile translate(int x, int y) {
        return new RSTile(getX() + x, getY() + y, getZ(), type);
    }

    public TYPES getType() {
        return type;
    }

    public RSTile toLocalTile() {
        switch (type) {
            case LOCAL:
                return this;
            case ANIMABLE:
                return this.toWorldTile().toLocalTile();
            case WORLD:
                int x = this.getX() - Client.getClient().getBaseX();
                int y = this.getY() - Client.getClient().getBaseY();
                return new RSTile(x, y, getZ(), RSTile.TYPES.LOCAL);
            default:
                return null;
        }
    }

    public RSTile toWorldTile() {
        int x, y;
        switch (type) {
            case LOCAL:
                x = Client.getClient().getBaseX() + this.getX();
                y = Client.getClient().getBaseY() + getY();
                return new RSTile(x, y, getZ(), RSTile.TYPES.WORLD);
            case ANIMABLE:
                x = Client.getClient().getBaseX() + (getX() >> 7);
                y = Client.getClient().getBaseY() + (getY() >> 7);
                return new RSTile(x, y, getZ(), RSTile.TYPES.WORLD);
            case WORLD:
                return this;
            default:
                return null;
        }
    }


    public RSTile toAnimableTile() {
        int x, y;
        switch (type) {
            case LOCAL:
                x = (int)(((double)this.getX() + 0.5D) * 128.0D);
                y = (int)(((double)this.getY() + 0.5D) * 128.0D);
                return new RSTile(x, y, getZ(), RSTile.TYPES.ANIMABLE);
            case ANIMABLE:
                return this;
            case WORLD:
                x = (int)(((double)(this.getX() - Client.getClient().getBaseX()) + 0.5D) * 128.0D);
                y = (int)(((double)(this.getY() - Client.getClient().getBaseY()) + 0.5D) * 128.0D);
                return new RSTile(x, y, getZ(), RSTile.TYPES.ANIMABLE);
            default:
                return null;
        }
    }

    public enum TYPES {
        WORLD,
        LOCAL,
        ANIMABLE;
    }
}
