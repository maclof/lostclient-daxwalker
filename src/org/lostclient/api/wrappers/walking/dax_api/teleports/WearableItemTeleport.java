package org.lostclient.api.wrappers.walking.dax_api.teleports;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.containers.equipment.Equipment;
import org.lostclient.api.containers.inventory.Inventory;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.item.Item;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.ItemHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.NPCInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class WearableItemTeleport {
	
	private static final Predicate<Item> NOT_NOTED = i -> !i.isNoted();

	public static final Predicate<Item> RING_OF_WEALTH_FILTER = ItemHelper.nameContainsPredicate("Ring of wealth (").and(i -> i.getName().matches(".*[1-5]\\)")).and(NOT_NOTED);
	public static final Predicate<Item> RING_OF_DUELING_FILTER = ItemHelper.nameContainsPredicate("Ring of dueling").and(NOT_NOTED);
	public static final Predicate<Item> NECKLACE_OF_PASSAGE_FILTER = ItemHelper.nameContainsPredicate("Necklace of passage").and(NOT_NOTED);
	public static final Predicate<Item> COMBAT_BRACE_FILTER = ItemHelper.nameContainsPredicate("Combat bracelet(").and(NOT_NOTED);
	public static final Predicate<Item> GAMES_NECKLACE_FILTER = ItemHelper.nameContainsPredicate("Games necklace").and(NOT_NOTED);
	public static final Predicate<Item> GLORY_FILTER = ItemHelper.nameContainsPredicate("glory").and(ItemHelper.nameContainsPredicate("eternal","(")).and(NOT_NOTED);
	public static final Predicate<Item> SKILLS_FILTER = ItemHelper.nameContainsPredicate("Skills necklace(").and(NOT_NOTED);
	public static final Predicate<Item> BURNING_AMULET_FILTER = ItemHelper.nameContainsPredicate("Burning amulet(").and(NOT_NOTED);
	public static final Predicate<Item> DIGSITE_PENDANT_FILTER = ItemHelper.nameContainsPredicate("Digsite pendant");
	public static final Predicate<Item> TELEPORT_CRYSTAL_FILTER = ItemHelper.nameContainsPredicate("Teleport crystal");
	public static final Predicate<Item> XERICS_TALISMAN_FILTER = ItemHelper.nameEqualsPredicate("Xeric's talisman");
	public static final Predicate<Item> RADAS_BLESSING_FILTER = ItemHelper.nameContainsPredicate("Rada's blessing");
	public static final Predicate<Item> CRAFTING_CAPE_FILTER = ItemHelper.nameContainsPredicate("Crafting cape");
	public static final Predicate<Item> EXPLORERS_RING_FILTER = ItemHelper.nameContainsPredicate("Explorer's ring");
	public static final Predicate<Item> QUEST_CAPE_FILTER = ItemHelper.nameContainsPredicate("Quest point cape");
	public static final Predicate<Item> ARDOUGNE_CLOAK_FILTER = ItemHelper.nameContainsPredicate("Ardougne cloak");
	public static final Predicate<Item> CONSTRUCTION_CAPE_FILTER = ItemHelper.nameContainsPredicate("Construct. cape");
	public static final Predicate<Item> SLAYER_RING = ItemHelper.nameContainsPredicate("Slayer ring");


	private WearableItemTeleport() {

	}

	public static boolean has(Predicate<Item> predicate) {
		return Inventory.contains(predicate) || Equipment.contains(predicate);
	}

	public static boolean teleport(Predicate<Item> filter, String action) {
		return teleportWithItem(filter,action);
	}


	private static boolean teleportWithItem(Predicate<Item> itemFilter, String regex) {
		ArrayList<Item> items = new ArrayList<>();
		items.addAll(Inventory.all(itemFilter));
		items.addAll(Equipment.all(itemFilter));

		if (items.size() == 0) {
			return false;
		}

		Item teleportItem = items.get(0);
		final Tile startingPosition = Players.localPlayer().getTile();

		return ItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")") && WaitFor.condition(
				Calculations.random(3800, 4600), () -> {
					NPCInteraction.handleConversationRegex(regex);
					if (startingPosition.distance(Players.localPlayer().getTile()) > 5) {
						return WaitFor.Return.SUCCESS;
					}
					return WaitFor.Return.IGNORE;
				}) == WaitFor.Return.SUCCESS;
	}

}