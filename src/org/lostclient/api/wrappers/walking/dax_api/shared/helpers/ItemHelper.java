package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.Client;
import org.lostclient.api.containers.bank.Bank;
import org.lostclient.api.containers.inventory.Inventory;
import org.lostclient.api.wrappers.input.Mouse;
import org.lostclient.api.wrappers.item.GroundItem;
import org.lostclient.api.wrappers.item.Item;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ItemHelper {

    public static Predicate<Item> nameEqualsPredicate(String... names) {
        return (i) -> StringHelper.nameEquals(i.getName(), names);
    }

    public static Predicate<Item> nameContainsPredicate(String... names) {
        return (i) -> StringHelper.nameContains(i.getName(), names);
    }

    public static Predicate<Item> idEqualsPredicate(int... ids) {
        return (i) -> Arrays.stream(ids).anyMatch((id) -> id == i.getID());
    }

    public static boolean click(String itemNameRegex, String itemAction){
        return click((i) -> i.getName().matches(itemNameRegex), itemAction);
    }

    public static boolean clickMatch(Item item, String regex){
        String action = Arrays.stream(item.getActions()).filter((a) -> a.matches(regex)).findFirst().orElse(null);
        return action != null && item.interact(action);
    }

    public static boolean click(int itemID){
        return click(itemID, null);
    }

    public static boolean click(int itemID, String action){
        return click((i) -> i.getID() == itemID, action, true);
    }

    public static boolean click(Predicate<Item> predicate, String action){
        return click(predicate, action, true);
    }

    /**
     *
     * @param predicate filter for items
     * @param action action to click
     * @param one click only one item.
     * @return
     */
    public static boolean click(Predicate<Item> predicate, String action, boolean one){
        if (action == null){
            action = "";
        }
        List<Item> list = Inventory.all(predicate);
        if (one) {
            Item closest = getClosestToMouse(list);
            return closest != null && closest.interact(action);
        }
        boolean value = false;
        while (!list.isEmpty()){
            Item item = getClosestToMouse(list);
            if (item != null) {
                list.remove(item);
                if (item.interact(action)){
                    value = true;
                }
            }
        }
        return value;
    }

    public static boolean click(Item item, String action){
        if (Bank.isOpen()){
            Bank.close();
        }
        return action != null ? item.interact(action) : item.interact(item.getActions()[0]);
    }

    public static boolean use(int itemID){
//        String name = Client.getClient().getSelectedItemName();
//        ItemDefinition rsItemDefinition = ItemDefinition.get(itemID);
//        String itemName;
//        if (Game.getItemSelectionState() == 1 && name != null && rsItemDefinition != null && (itemName = rsItemDefinition.getName()) != null && name.equals(itemName)){
//            return true;
//        } else if (Game.getItemSelectionState() == 1){
//            Mouse.click(3);
//            ChooseOption.select("Cancel");
//        }
        return ItemHelper.click(itemID, "Use");
    }

    public static Item getClosestToMouse(List<Item> rsItems){
//        Point mouse = Mouse.getMousePosition();
//        rsItems.sort(Comparator.comparingInt(o -> (int) getCenter(o.getArea()).distance(mouse)));
//        return rsItems.size() > 0 ? rsItems.get(0) : null;
        return null;
    }

    private static Point getCenter(Rectangle rectangle){
        return new Point(rectangle.x + rectangle.width/2, rectangle.y + rectangle.height/2);
    }
}
