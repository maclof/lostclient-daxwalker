package org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring;

import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.containers.equipment.Equipment;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.GameObjectHelper;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.InterfaceHelper;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.ItemHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.InteractionHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters.FirstLetter;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters.SecondLetter;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters.ThirdLetter;
import org.lostclient.api.wrappers.widgets.WidgetChild;
import org.lostclient.api.wrappers.widgets.Widgets;

import static org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters.FirstLetter.*;
import static org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters.SecondLetter.*;
import static org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters.ThirdLetter.*;

public class FairyRing {

	public static final int
		INTERFACE_MASTER = 398,
		TELEPORT_CHILD = 26,
		ELITE_DIARY_VARBIT = 4538;
	private static final int[]
			DRAMEN_STAFFS = {772,9084};

	private static GameObject[] ring;


	private static WidgetChild getTeleportButton() {
		return Widgets.getWidgetChild(INTERFACE_MASTER, TELEPORT_CHILD);
	}

	public static boolean takeFairyRing(Locations location){

		if(location == null)
			return false;
		if (PlayerSettings.getBitValue(ELITE_DIARY_VARBIT) == 0 && Equipment.count(DRAMEN_STAFFS) == 0){
			if (!InteractionHelper.click(InteractionHelper.getItem(ItemHelper.idEqualsPredicate(DRAMEN_STAFFS)), "Wield")){
				return false;
			}
		}
		if(!hasInterface()){
			if(hasCachedLocation(location)){
				return takeLastDestination(location);
			} else if(!openFairyRing()){
				return false;
			}
		}
		final RSTile myPos = RSTile.fromTile(Players.localPlayer().getTile());
		return location.turnTo() && pressTeleport() && MethodProvider.sleepUntil(() -> myPos.distance(Players.localPlayer().getTile()) > 20,8000);
	}

	private static boolean hasInterface(){
		return InterfaceHelper.isInterfaceSubstantiated(INTERFACE_MASTER);
	}

	private static boolean hasCachedLocation(Locations location){
		ring = GameObjectHelper.findClosest(25,"Fairy ring");
		return ring.length > 0 && GameObjectHelper.actionsContainsPredicate(location.toString()).test(ring[0]);
	}

	private static boolean takeLastDestination(Locations location){
		final RSTile myPos = RSTile.fromTile(Players.localPlayer().getTile());
		return InteractionHelper.click(ring[0],"Last-destination (" + location + ")") &&
				MethodProvider.sleepUntil(() -> myPos.distance(Players.localPlayer().getTile()) > 20,8000);
	}

	private static boolean pressTeleport(){
		WidgetChild iface = getTeleportButton();
		return iface != null && iface.interact();
	}

	private static boolean openFairyRing(){
		if(ring.length == 0)
			return false;
		return InteractionHelper.click(ring[0],"Configure") &&
				MethodProvider.sleepUntil(() -> InterfaceHelper.isInterfaceSubstantiated(INTERFACE_MASTER),10000);
	}

	public enum Locations {
		ABYSSAL_AREA(A, L, R),
		ABYSSAL_NEXUS(D, I, P),
		APE_ATOLL(C, L, R),
		ARCEUUS_LIBRARY(C, I, S),
		ARDOUGNE_ZOO(B, I, S),
		CANIFIS(C, K, S),
		CHASM_OF_FIRE(D, J, R),
		COSMIC_ENTITYS_PLANE(C, K, P),
		DORGESH_KAAN_SOUTHERN_CAVE(A, J, Q),
		DRAYNOR_VILLAGE_ISLAND(C, L, P),
		EDGEVILLE(D, K, R),
		ENCHANTED_VALLEY(B, K, Q),
		FELDIP_HILLS_HUNTER_AREA(A, K, S),
		FISHER_KINGS_REALM(B, J, R),
		GORAKS_PLANE(D, I, R),
		HAUNTED_WOODS(A, L, Q),
		HAZELMERE(C, L, S),
		ISLAND_SOUTHEAST_ARDOUGNE(A, I, R),
		KALPHITE_HIVE(B, I, Q),
		KARAMJA_KARAMBWAN_SPOT(D, K, P),
		LEGENDS_GUILD(B, L, R),
		LIGHTHOUSE(A, L, P),
		MCGRUBOR_WOODS(A, L, S),
		MISCELLANIA(C, I, P),
		MISCELLANIA_PENGUINS(A, J, S),
		MORT_MYRE_ISLAND(B, I, P),
		MORT_MYRE_SWAMP(B, K, R),
		MOUNT_KARUULM(C, I, R),
		MUDSKIPPER_POINT(A, I, Q),
		MYREQUE_HIDEOUT(D, L, S),
		NORTH_OF_NARDAH(D, L, Q),
		PISCATORIS_HUNTER_AREA(A, K, Q),
		POH(D, I, Q),
		POISON_WASTE(D, L, R),
		POLAR_HUNTER_AREA(D, K, S),
		RELLEKKA_SLAYER_CAVE(A, J, R),
		SHILO_VILLAGE(C, K, R),
		SINCLAIR_MANSION(C, J, R),
		SOUTH_CASTLE_WARS(B, K, P),
		TOWER_OF_LIFE(D, J, P),
		TZHAAR(B, L, P),
		WIZARDS_TOWER(D, I, S),
		YANILLE(C, I, Q),
		ZANARIS(B, K, S),
		ZUL_ANDRA(B, J, S);

		FirstLetter first;
		SecondLetter second;
		ThirdLetter third;

		Locations(FirstLetter first, SecondLetter second, ThirdLetter third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public boolean turnTo() {
			return first.turnTo() && WaitFor.milliseconds(200, 800) != null &&
					second.turnTo() && WaitFor.milliseconds(200, 800) != null &&
					third.turnTo() && WaitFor.milliseconds(200, 800) != null;
		}

		@Override
		public String toString() {
			return "" + first + second + third;
		}
	}
}
