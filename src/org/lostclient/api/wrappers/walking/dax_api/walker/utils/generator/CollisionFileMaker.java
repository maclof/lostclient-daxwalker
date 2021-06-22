package org.lostclient.api.wrappers.walking.dax_api.walker.utils.generator;

import org.lostclient.api.Client;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.local_pathfinding.AStarNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CollisionFileMaker {

    public static void getCollisionData(){
        try {
//            int[][] collisionData = PathFinding.getCollisionData();
            int[][] collisionData = Client.getClient().getCollisionMaps()[0].getFlags();
            if(collisionData == null)
                return;
            int baseX = Client.getClient().getBaseX();
            int baseY = Client.getClient().getBaseY();
            int baseZ = Players.localPlayer().getTile().getZ();

            File file = new File(System.getProperty("user.home") + File.separator + "LostClient" + File.separator + baseX + "x" + baseY + "x" + baseZ + ".cdata");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (int x = 0; x < collisionData.length; x++) {
                for (int y = 0; y < collisionData[x].length; y++) {
                    int flag = collisionData[x][y];
                    RSTile tile = new RSTile(x, y, baseZ, RSTile.TYPES.LOCAL).toWorldTile();
                    CollisionTile collisionTile = new CollisionTile(
                            tile.getX(), tile.getY(), tile.getZ(),
                            AStarNode.blockedNorth(flag),
                            AStarNode.blockedEast(flag),
                            AStarNode.blockedSouth(flag),
                            AStarNode.blockedWest(flag),
                            !AStarNode.isWalkable(flag),
                            false,
                            !AStarNode.isInitialized(flag));
                    bufferedWriter.write(collisionTile.toString());
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
