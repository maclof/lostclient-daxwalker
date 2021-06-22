package org.lostclient.api.wrappers.walking.dax_api.walker_engine.real_time_collision;

import org.lostclient.api.Client;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.wrappers.input.mouse.Menu;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;

public class CollisionDataCollector {

    public static void generateRealTimeCollision(){
        RealTimeCollisionTile.clearMemory();

        RSTile playerPosition = RSTile.fromTile(Players.localPlayer().getTile());
//            int[][] collisionData = PathFinding.getCollisionData();
        int[][] collisionData = Client.getClient().getCollisionMaps()[0].getFlags();

        if (collisionData == null) {
            return;
        }

        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                RSTile localTile = new RSTile(i, j, playerPosition.getZ(), RSTile.TYPES.LOCAL);
                RSTile worldTile = localTile.toWorldTile();
                RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getZ(), collisionData[i][j]);
            }
        }
    }

    public static void updateRealTimeCollision(){
        RSTile playerPosition = RSTile.fromTile(Players.localPlayer().getTile());
//            int[][] collisionData = PathFinding.getCollisionData();
        int[][] collisionData = Client.getClient().getCollisionMaps()[0].getFlags();
        if(collisionData == null)
            return;
        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                RSTile localTile = new RSTile(i, j, playerPosition.getZ(), RSTile.TYPES.LOCAL);
                RSTile worldTile = localTile.toWorldTile();
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
