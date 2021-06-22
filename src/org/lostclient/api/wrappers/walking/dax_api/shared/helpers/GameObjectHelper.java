package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.accessor.GameObjects;
import org.lostclient.api.wrappers.interactives.GameObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class GameObjectHelper {

    public static GameObject get(Predicate<GameObject> predicate){
        return GameObjects.closest((o) -> {
            return o.distance() <= 10 && predicate.test(o);
        });
    }

    public static boolean exists(Predicate<GameObject> predicate){
        return GameObjectHelper.get(predicate) != null;
    }

    public static List<String> getActionsList(GameObject object){
        return Arrays.asList(getActions(object));
    }
    
    public static String[] getActions(GameObject object) {
        return object.getActions();
    }

    public static String getName(GameObject object){
        return object.getName();
    }

}
