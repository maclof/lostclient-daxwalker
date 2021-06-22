package org.lostclient.api.wrappers.walking.dax_api.teleports.teleport_utils;

import org.lostclient.api.accessor.Dialogues;
import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.containers.inventory.Inventory;
import org.lostclient.api.interfaces.Interactable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.input.mouse.Menu;
import org.lostclient.api.wrappers.item.Item;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.InterfaceHelper;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.StringHelper;
import org.lostclient.api.wrappers.widgets.WidgetChild;
import org.lostclient.api.wrappers.widgets.Widgets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class MasterScrollBook {

	public static final int
			INTERFACE_MASTER = 597, DEFAULT_VARBIT = 5685,
			SELECT_OPTION_MASTER = 219, SELECT_OPTION_CHILD = 1,
			GAMETABS_INTERFACE_MASTER = 161;
	private static Map<String, Integer> cache = new HashMap<String, Integer>();
	
	public enum Teleports {
		NARDAH(5672,"Nardah", TeleportScrolls.NARDAH.getLocation()),
		DIGSITE(5673,"Digsite", TeleportScrolls.DIGSITE.getLocation()),
		FELDIP_HILLS(5674,"Feldip Hills", TeleportScrolls.FELDIP_HILLS.getLocation()),
		LUNAR_ISLE(5675,"Lunar Isle", TeleportScrolls.LUNAR_ISLE.getLocation()),
		MORTTON(5676,"Mort'ton", TeleportScrolls.MORTTON.getLocation()),
		PEST_CONTROL(5677,"Pest Control", TeleportScrolls.PEST_CONTROL.getLocation()),
		PISCATORIS(5678,"Piscatoris", TeleportScrolls.PISCATORIS.getLocation()),
		TAI_BWO_WANNAI(5679,"Tai Bwo Wannai", TeleportScrolls.TAI_BWO_WANNAI.getLocation()),
		ELF_CAMP(5680,"Elf Camp", TeleportScrolls.ELF_CAMP.getLocation()),
		MOS_LE_HARMLESS(5681,"Mos Le'Harmless", TeleportScrolls.MOS_LE_HARMLESS.getLocation()),
		LUMBERYARD(5682,"Lumberyard", TeleportScrolls.LUMBERYARD.getLocation()),
		ZULLANDRA(5683,"Zul-Andra", TeleportScrolls.ZULLANDRA.getLocation()),
		KEY_MASTER(5684,"Key Master", TeleportScrolls.KEY_MASTER.getLocation()),
		REVENANT_CAVES(6056,"Revenant cave", TeleportScrolls.REVENANT_CAVES.getLocation()),
		WATSON(8253, "Watson", TeleportScrolls.WATSON.getLocation());
		
		private int varbit;
		private String name;
		private RSTile destination;
		Teleports(int varbit, String name, RSTile destination){
			this.varbit = varbit;
			this.name = name;
			this.destination = destination;
		}
		
		//Returns the number of scrolls stored in the book.
		public int getCount(){
			return PlayerSettings.getBitValue(varbit);
		}
		
		//Returns the name of the teleport.
		public String getName(){
			return name;
		}
		
		//Returns the destination that the teleport will take you to.
		public RSTile getDestination(){
			return destination;
		}
		
		//Sets the teleport as the default left-click option of the book.
		public boolean setAsDefault(){
			if(Dialogues.getOptions().length > 0){
				String text = getDefaultTeleportText();
				if(text.contains(this.getName())){
					return Dialogues.clickOption("Yes");
				}
			}
			if(!isOpen()){
				openBook();
			}
			WidgetChild target = getInterface(this);
			if(target == null)
				return false;
			return target.interact("Set as default") && waitForOptions() && Dialogues.clickOption("Yes");
			
		}
		
		//Uses the teleport and waits until you arrive at the destination.
		public boolean use(){
			if(this == getDefault()){
				Item[] book = getBook();
				return book.length > 0 && click(book[0],"Teleport") && waitTillAtDestination(this);
			}
			if(this == REVENANT_CAVES) // bug where you can't activate it from the interface for whatever reason.
				return setAsDefault() && use();
			if(!isOpen() && !openBook())
				return false;
			WidgetChild target = getInterface(this);
			return target != null && target.interact("Activate") && waitTillAtDestination(this);
		}
		
	}
	
	public static boolean teleport(Teleports teleport){
		return teleport != null && teleport.getCount() > 0 && teleport.use();
	}
	
	public static int getCount(Teleports teleport){
		return teleport != null ? teleport.getCount() : 0;
	}
	
	public static boolean isDefault(Teleports teleport){
		return getDefault() == teleport;
	}
	
	public static boolean setAsDefault(Teleports teleport){
		return teleport != null && teleport.setAsDefault();
	}
	
	public static Teleports getDefault(){
		int value = PlayerSettings.getBitValue(DEFAULT_VARBIT);
		if (value == 0) {
			return null;
		}
		return Teleports.values()[value-1];
	}
	
	//Removes the default left click teleport option.
	public static boolean removeDefault(){
		Item[] book = getBook();
		String toolTip = Menu.getToolTip();
		if(toolTip != null && toolTip.contains("->")){
			resetUptext();
		}
		return book.length > 0 && click(book[0],"Remove default") && waitForOptions() && Dialogues.clickOption("Yes");
	}
	
	//Caches the index and returns the WidgetChild associated with the selected teleport.
	private static WidgetChild getInterface(Teleports teleport){
		if(cache.containsKey(teleport.getName())){
			return Widgets.getWidgetChild(INTERFACE_MASTER,cache.get(teleport.getName()));
		}
		WidgetChild master = Widgets.getWidgetChild(INTERFACE_MASTER);
		if(master == null)
			return null;
		for(WidgetChild child:master.getChildren()){
			String name = child.getName();
			if(name == null){
				continue;
			} else if(name.startsWith("<") && StringHelper.stripFormatting(name).contains(teleport.getName())){
				cache.put(teleport.getName(), child.getIndex());
				return child;
			}
		}
		return null;
	}
	
	//Returns true if the Master scroll book interface is open.
	public static boolean isOpen(){
		return InterfaceHelper.isInterfaceSubstantiated(INTERFACE_MASTER);
	}
	
	//Opens the master scroll book interface.
	public static boolean openBook(){
		Item[] book = getBook();
		String toolTip = Menu.getToolTip();
		if(toolTip != null && toolTip.contains("->")){
			resetUptext();
		}
		return book.length > 0 && click(book[0],"Open") && waitForBookToOpen();
	}


	public static boolean hasBook(){
		return getBook().length > 0;
	}

	public static boolean has(){
		return getBook().length > 0;
	}

	private static Item[] getBook(){
		return Inventory.all((i) -> i.getName().contains("Master scroll book")).toArray(Item[]::new);
	}
	
	private static boolean waitForBookToOpen(){
		return MethodProvider.sleepUntil(() -> {
			MethodProvider.sleep(50,200);
			return isOpen();
		}, 5000);
	}
	
	private static boolean waitForOptions(){
		return MethodProvider.sleepUntil(() -> {
			MethodProvider.sleep(50,200);
			return Dialogues.getOptions().length > 0;
		}, 5000);
	}
	
	//Checks which scroll we are setting to default currently.
	private static String getDefaultTeleportText(){
		WidgetChild master = Widgets.getWidgetChild(SELECT_OPTION_MASTER,SELECT_OPTION_CHILD);
		if(master == null)
			return null;
		List<WidgetChild> ifaces = master.getChildren();
		if(ifaces == null)
			return null;
		for(WidgetChild iface:ifaces){
			String txt = iface.getText();
			if(txt == null || !txt.startsWith("Set"))
				continue;
			return txt;
		}
		return null;
	}
	
	//Resets uptext.
	private static void resetUptext(){
		WidgetChild master = Widgets.getWidgetChild(GAMETABS_INTERFACE_MASTER);
		List<WidgetChild> children = master.getChildren();
		if(children == null)
			return;
		WidgetChild inventory = null;
		for(WidgetChild child:children){
			String[] actions = child.getActions();
			if(actions == null || actions.length == 0)
				continue;
			if(Arrays.asList(actions).contains("Inventory")){
				inventory = child;
				break;
			}
		}
		if(inventory != null)
			inventory.interact();
	}
	
	private static boolean waitTillAtDestination(Teleports location){
		return MethodProvider.sleepUntil(() -> {
			MethodProvider.sleep(50,200);
			return location.getDestination().distance(Players.localPlayer().getTile()) < 10;
		}, 8000);
	}
	
	private static boolean click(Item item, String action){
		String toolTip = Menu.getToolTip();
		if(toolTip != null && toolTip.contains("->") && !action.contains("->")){
			resetUptext();
		}
		return item.interact(action);
	}
	
	
}
