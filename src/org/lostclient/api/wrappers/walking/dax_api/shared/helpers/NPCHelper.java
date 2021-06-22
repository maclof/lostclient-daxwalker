package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.map.Area;

import java.util.Arrays;
import java.util.function.Predicate;

public class NPCHelper {

    public static Predicate<NPC> nameEqualsPredicate(String... names) {
        return (n) -> StringHelper.nameEquals(n.getName(), names);
    }

    public static Predicate<NPC> nameContainsPredicate(String... names) {
        return (n) -> StringHelper.nameContains(n.getName(), names);
    }

    public static Predicate<NPC> actionsEqualsPredicate(String... actions) {
        return (n) -> Arrays.stream(n.getActions()).anyMatch((a) -> StringHelper.nameEquals(a, actions));
    }

    public static Predicate<NPC> actionsContainsPredicate(String... actions) {
        return (n) -> Arrays.stream(n.getActions()).anyMatch((a) -> StringHelper.nameContains(a, actions));
    }

}
