package org.lostclient.api.wrappers.walking.dax_api.walker_engine.real_time_collision;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.wrappers.input.mouse.Menu;
import org.lostclient.api.wrappers.map.Tile;

public class CollisionDataCollector {

    public static void generateRealTimeCollision(){
        RealTimeCollisionTile.clearMemory();

        Tile playerPosition = Players.localPlayer().getTile();
        int[][] collisionData = PathFinding.getCollisionData();

        if (collisionData == null) {
            return;
        }

        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                Tile localTile = new Tile(i, j, playerPosition.getZ(), Tile.TYPES.LOCAL);
                Tile worldTile = localTile.toWorldTile();
                RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getZ(), collisionData[i][j]);
            }
        }
    }

    public static void updateRealTimeCollision(){
        Tile playerPosition = Players.localPlayer().getTile();
        int[][] collisionData = PathFinding.getCollisionData();
        if(collisionData == null)
            return;
        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                Tile localTile = new Tile(i, j, playerPosition.getZ(), Tile.TYPES.LOCAL);
                Tile worldTile = localTile.toWorldTile();
                RealTimeCollisionTile realTimeCollisionTile = RealTimeCollisionTile.get(worldTile.getX(), worldTile.getY(), worldTile.getZ());
                if (realTimeCollisionTile != null){
                    realTimeCollisionTile.setCollisionData(collisionData[i][j]);
                } else {
                    RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getZ(), collisionData[i][j]);
                }
            }
        }
    }

}
