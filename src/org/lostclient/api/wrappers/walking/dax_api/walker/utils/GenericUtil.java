package org.lostclient.api.wrappers.walking.dax_api.walker.utils;

import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.interactives.Player;
import org.lostclient.api.wrappers.item.GroundItem;
import org.lostclient.api.wrappers.item.Item;

import java.util.Arrays;
import java.util.List;

public class GenericUtil {

    public static String getName(Object o) {
        if (o == null) return null;

        if (o instanceof GameObject) {
            return ((GameObject) o).getName();
        }

        if (o instanceof Item) {
            return ((Item) o).getName();
        }

        if (o instanceof GroundItem) {
            return ((GroundItem) o).getName();
        }

        if (o instanceof Player) {
            return ((Player) o).getName();
        }

        if (o instanceof NPC) {
            return ((NPC) o).getName();
        }

        throw new IllegalStateException("Unknown object. Must be qualifying TriBot Object.");
    }

    public static List<String> getActions(Object o) {
        if (o == null) return null;

        if (o instanceof GameObject) {
            return Arrays.asList(((GameObject) o).getActions());
        }

        if (o instanceof Item) {
            return Arrays.asList(((Item) o).getActions());
        }

        if (o instanceof GroundItem) {
            return Arrays.asList(((GroundItem) o).getActions());
        }

        throw new IllegalStateException("Unknown object. Must be qualifying TriBot Object.");
    }

}
