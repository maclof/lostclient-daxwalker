package org.lostclient.api.wrappers.walking.dax_api.shared.helpers.magic;

import org.lostclient.api.containers.equipment.Equipment;
import org.lostclient.api.containers.inventory.Inventory;
import org.lostclient.api.wrappers.item.Item;
import org.lostclient.api.wrappers.worlds.Worlds;

public enum RuneElement {

    AIR("Air", "Smoke", "Mist", "Dust"),
    EARTH("Earth", "Lava", "Mud", "Dust"),
    FIRE("Fire", "Lava", "Smoke", "Steam"),
    WATER("Water", "Mud", "Steam", "Mist"),
    LAW("Law"),
    NATURE("Nature"),
    SOUL("Soul");

    private String[] alternativeNames;

    RuneElement(String... alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String[] getAlternativeNames() {
        return alternativeNames;
    }

    public int getCount() {
        if (haveStaff()) {
            return Integer.MAX_VALUE;
        }
        return Inventory.all((item) -> {
            if (item.isMembers() && !Worlds.getMyWorld().isMember())
                return false;
            String name = item.getName().toLowerCase();

            if (!name.contains("rune")) {
                return false;
            }

            for (String alternativeName : alternativeNames) {
                if (name.startsWith(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }).stream().mapToInt(Item::getAmount).sum() + RunePouch.getQuantity(this);
    }

    private boolean haveStaff() {
        return Equipment.contains((item) -> {
            if(item.isMembers() && !Worlds.getMyWorld().isMember())
                return false;
            String name = item.getName().toLowerCase();
            if (!name.contains("staff")) {
                return false;
            }
            for (String alternativeName : alternativeNames) {
                if (name.contains(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

}
