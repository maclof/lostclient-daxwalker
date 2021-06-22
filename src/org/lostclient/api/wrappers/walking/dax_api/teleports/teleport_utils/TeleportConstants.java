package org.lostclient.api.wrappers.walking.dax_api.teleports.teleport_utils;

import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.wrappers.combat.Combat;

public class TeleportConstants {

    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 30;

    public static final int
            GE_TELEPORT_VARBIT = 4585, SPELLBOOK_INTERFACE_MASTER = 218, SCROLL_INTERFACE_MASTER = 187;

    private static int getWildernessLevel() {
        return Combat.getWildernessLevel();
    }

    public static boolean isVarrockTeleportAtGE(){
        return PlayerSettings.getBitValue(GE_TELEPORT_VARBIT) > 0;
    }

}
