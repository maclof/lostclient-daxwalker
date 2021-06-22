package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.accessor.GameObjects;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.item.Item;
import org.lostclient.api.wrappers.map.Area;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


public class GameObjectHelper {

    public static Predicate<GameObject> nameEqualsPredicate(String... names) {
        return (o) -> StringHelper.nameEquals(o.getName(), names);
    }

    public static Predicate<GameObject> nameContainsPredicate(String... names) {
        return (o) -> StringHelper.nameContains(o.getName(), names);
    }

    public static Predicate<GameObject> idEqualsPredicate(int... ids) {
        return (o) -> Arrays.stream(ids).anyMatch((id) -> id == o.getID());
    }

    public static Predicate<GameObject> inAreaPredicate(Area area) {
        return (o) -> area.contains(o);
    }

    public static Predicate<GameObject> actionsEqualsPredicate(String... actions) {
        return (o) -> Arrays.stream(o.getActions()).anyMatch((a) -> StringHelper.nameEquals(a, actions));
    }

    public static Predicate<GameObject> actionsContainsPredicate(String... actions) {
        return (o) -> Arrays.stream(o.getActions()).anyMatch((a) -> StringHelper.nameContains(a, actions));
    }

    public static GameObject[] find(int distance, String name) {
        return GameObjects.all((o) -> o.distance() <= distance && o.getName().equals(name)).toArray(GameObject[]::new);
    }

    public static GameObject[] find(int distance, Predicate<GameObject> predicate) {
        return GameObjects.all((o) -> o.distance() <= distance && predicate.test(o)).toArray(GameObject[]::new);
    }

    public static GameObject[] findClosest(int distance, String name) {
        List<GameObject> gameObjects = GameObjects.all((o) -> o.distance() <= distance && o.getName().equals(name));
        gameObjects.sort((o1, o2) -> (int)o1.distance() - (int)o2.distance());
        return gameObjects.toArray(GameObject[]::new);
    }

    public static GameObject[] findClosest(int distance, Predicate<GameObject> predicate) {
        List<GameObject> gameObjects = GameObjects.all((o) -> o.distance() <= distance && predicate.test(o));
        gameObjects.sort((o1, o2) -> (int)o1.distance() - (int)o2.distance());
        return gameObjects.toArray(GameObject[]::new);
    }



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
