package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.wrappers.interactives.NPC;

public class NPCHelper {

    public static String getName(NPC rsnpc){
        return rsnpc.getName();
    }

    public static String[] getActions(NPC rsnpc){
        return rsnpc.getActions();
    }

}
