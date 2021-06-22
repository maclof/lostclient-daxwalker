package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.wrappers.item.Item;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ItemHelper {

    public static boolean click(String itemNameRegex, String itemAction){
        return click(new Filter<Item>() {
            @Override
            public boolean accept(Item item) {
                return getItemName(item).matches(itemNameRegex) && Arrays.stream(getItemActions(item)).anyMatch(s -> s.equals(itemAction));
            }
        }, itemAction);
    }

    public static boolean clickMatch(Item item, String regex){
        return item.click(new Filter<RSMenuNode>() {
            @Override
            public boolean accept(RSMenuNode rsMenuNode) {
                String action = rsMenuNode.getAction();
                return action != null && action.matches(regex);
            }
        });
    }

    public static boolean click(int itemID){
        return click(itemID, null);
    }

    public static boolean click(int itemID, String action){
        return click(Filters.Items.idEquals(itemID), action, true);
    }

    public static boolean click(Filter<Item> filter, String action){
        return click(filter, action, true);
    }

    /**
     *
     * @param filter filter for items
     * @param action action to click
     * @param one click only one item.
     * @return
     */
    public static boolean click(Filter<Item> filter, String action, boolean one){
        if (action == null){
            action = "";
        }
        List<Item> list = Arrays.stream(Inventory.find(filter)).collect(Collectors.toList());
        if (one) {
            Item closest = getClosestToMouse(list);
            return closest != null && closest.click(action);
        }
        boolean value = false;
        while (!list.isEmpty()){
            Item item = getClosestToMouse(list);
            if (item != null) {
                list.remove(item);
                if (item.click(action)){
                    value = true;
                }
            }
        }
        return value;
    }

    public static boolean click(Item item, String action){
        if (Banking.isBankScreenOpen()){
            Banking.close();
        }
        return action != null ? item.click(action) : item.click();
    }

    public static boolean use(int itemID){
        String name = Game.getSelectedItemName();
        ItemDefinition rsItemDefinition = ItemDefinition.get(itemID);
        String itemName;
        if (Game.getItemSelectionState() == 1 && name != null && rsItemDefinition != null && (itemName = rsItemDefinition.getName()) != null && name.equals(itemName)){
            return true;
        } else if (Game.getItemSelectionState() == 1){
            Mouse.click(3);
            ChooseOption.select("Cancel");
        }
        return ItemHelper.click(itemID, "Use");
    }

    public static Item getClosestToMouse(List<Item> rsItems){
        Point mouse = Mouse.getPos();
        rsItems.sort(Comparator.comparingInt(o -> (int) getCenter(o.getArea()).distance(mouse)));
        return rsItems.size() > 0 ? rsItems.get(0) : null;
    }

    private static Point getCenter(Rectangle rectangle){
        return new Point(rectangle.x + rectangle.width/2, rectangle.y + rectangle.height/2);
    }


    public static Item getItem(Filter<Item> filter){
        return getClosestToMouse(Arrays.stream(Inventory.find(filter)).collect(Collectors.toList()));
    }

    public static boolean isNoted(Item item) {
        return item != null && isNoted(item.getID());
    }

    public static boolean isNoted(int id) {
        ItemDefinition definition = ItemDefinition.get(id);
        return definition != null && definition.isNoted();
    }


    public static String[] getItemActions(RSGroundItem rsGroundItem){
        return getItemActions(rsGroundItem.getDefinition());
    }

    public static String[] getItemActions(Item rsItem){
        return getItemActions(rsItem.getDefinition());
    }


    public static String getItemName(int id){
        return getItemName(ItemDefinition.get(id));
    }

    public static String getItemName(RSGroundItem rsGroundItem){
        return getItemName(rsGroundItem.getDefinition());
    }

    public static String getItemName(Item rsItem){
        return getItemName(rsItem.getDefinition());
    }


    public static boolean isStackable(int id) {
        ItemDefinition definition = ItemDefinition.get(id);
        return definition != null && definition.isStackable();
    }


    public static boolean isStackable(Item rsItem) {
        ItemDefinition definition = rsItem.getDefinition();
        return definition != null && definition.isStackable();
    }

    private static String[] getItemActions(ItemDefinition rsItemDefinition){
        if (rsItemDefinition == null){
            return new String[0];
        }
        String[] actions = rsItemDefinition.getActions();
        return actions != null ? actions : new String[0];
    }

    private static String getItemName(ItemDefinition definition){
        String name = definition.getName();
        return name != null ? name : "null";
    }

    public static int distanceMouse(Item item){
        Rectangle rectangle = item.getArea();
        if (rectangle == null){
            return Integer.MAX_VALUE;
        }
        return (int) Mouse.getPos().distance(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }


}
