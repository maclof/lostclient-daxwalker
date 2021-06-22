package org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.GameObjectHelper;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.InterfaceHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.InteractionHelper;
import org.lostclient.api.wrappers.widgets.WidgetChild;

public class SpiritTree {

    private static final int SPIRIT_TREE_MASTER_INTERFACE = 187;

    public enum Location {
        SPIRIT_TREE_GRAND_EXCHANGE("Grand Exchange", 3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD("Gnome Stronghold", 2461, 3444, 0),
        SPIRIT_TREE_KHAZARD("Battlefield of Khazard", 2555, 3259, 0),
        SPIRIT_TREE_VILLAGE("Tree Gnome Village", 2542, 3170, 0);

        private int x, y, z;
        private String name;
        Location(String name, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public RSTile getTile(){
            return new RSTile(x, y, z);
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
    }

    public static boolean to(Location location){
        if (!InterfaceHelper.isInterfaceSubstantiated(SPIRIT_TREE_MASTER_INTERFACE)
                && !InteractionHelper.click(InteractionHelper.getGameObject(GameObjectHelper.actionsContainsPredicate("Travel")), "Travel", () -> InterfaceHelper.isInterfaceSubstantiated(SPIRIT_TREE_MASTER_INTERFACE) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
            return false;
        }

        WidgetChild option = InterfaceHelper.getAllInterfaces(SPIRIT_TREE_MASTER_INTERFACE).stream().filter(rsInterface -> {
            String text = rsInterface.getText();
            return text != null && text.contains(location.getName());
        }).findAny().orElse(null);

        if (option == null){
            return false;
        }

        if (!option.interact()){
            return false;
        }

        if (WaitFor.condition(Calculations.random(5400, 6500), () -> location.getTile().distance(Players.localPlayer().getTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(250, 500);
            return true;
        }
        return false;
    }

}
