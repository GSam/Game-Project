package GUI.ScreenControllers;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import network.interfaces.GUIObserver;
import world.Inventory;
import world.entity.item.Item;
import world.entity.item.ItemDisplayTuple;
import world.entity.item.RightClickable;
import world.entity.item.Stat;
import world.entity.item.consumable.AbstractConsumable;
import world.entity.item.container.AbstractContainerItem;
import world.entity.item.equippable.EquipType;
import world.entity.item.equippable.Equippable;
import world.entity.item.gizmo.AbstractGizmo;
import GUI.Managers.ScreenManager;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Draggable;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.controls.DroppableDropFilter;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryReleaseEvent;
import de.lessvoid.nifty.elements.events.NiftyMouseSecondaryReleaseEvent;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * The chief GUI controller. Controls and interprets player actions and updates the Model accordingly
 *
 * @author Craig Forret
 */
public class HudScreenController extends AbstractAppState implements ScreenController, DroppableDropFilter{

	private Nifty nifty;
	private Screen screen;
	private SimpleApplication app;
	protected HashSet<String> positionSet;
	private Item openContainer;
	private ScreenManager screenManager;
	private String currentElementId = "";
	private List<GUIObserver> observers = new ArrayList<GUIObserver>();
	private String onStartMessage = "";
	private boolean startCompleted;

	public HudScreenController(String data, Nifty nifty, ScreenManager manager) {
		this.screenManager = manager;
		this.nifty = nifty;
		this.positionSet = new HashSet<String>();
		//registering custom effects with Nifty
		nifty.registerEffect("fadeErase", "GUI.CustomEffects.FadeEraseText");
		nifty.registerEffect("orbChanger", "GUI.CustomEffects.OrbChanger");
		nifty.registerEffect("orbFadeChange", "GUI.CustomEffects.OrbFadeChange");
		nifty.registerEffect("mobDisplay", "GUI.CustomEffects.MobDisplay");
	}

	//Event subscribing. The controller acts as a listener on the Event bus for
	//specific element ids captured either explicitly or through regex.

	/**Triggered by the player pressing the inventory icon and
	 * displays or hides the Inventory panel
	 */
	@NiftyEventSubscriber(id="InventoryBut")
	public void hideShowInventory(String id, NiftyMousePrimaryReleaseEvent event) {
		Element el = nifty.getScreen("hud").findElementByName("InventoryPanel");
		el.setVisible((el.isVisible())?false:true);
		hideInnerPopups(screenManager.getInventoryManager().getPlayer().getContainerInventory());
		if(openContainer != null) closeContainer();
	}

	/**Triggered by player pressing the character equip button and displays
	 * or hides the equip screen
	 */
	@NiftyEventSubscriber(id="CharBut")
	public void hideShowEquipScreen(String id, NiftyMousePrimaryReleaseEvent event) {
		Element el = nifty.getScreen("hud").findElementByName("CharEquip");
		el.setVisible((el.isVisible())?false:true);
		hideInnerPopups(screenManager.getInventoryManager().getPlayer().getContainerInventory());
	}

	/**Triggered when an Item is right clicked to activate
	 * Processes the item according to its type, and updates
	 * the model accordingly
	 */
	@NiftyEventSubscriber(pattern="ItemVal.*")
	public void consumeItem(String id, NiftyMouseSecondaryReleaseEvent event){
		Item item = findItemById(id.substring(7),screenManager.getInventoryManager().getPlayer().getContainerInventory());

		//container items
		if (item instanceof AbstractContainerItem) {
			Element containerPanel = nifty.getScreen("hud").findElementByName("ContainerPanel");
			if (!containerPanel.isVisible()){
				containerPanel.setVisible(true);
				openContainer = item;
				screenManager.getInventoryManager().loadContainer(((AbstractContainerItem)item).getContainerInventory());
			}else{
				closeContainer();
			}
			hideInnerPopups(screenManager.getInventoryManager().getPlayer().getContainerInventory());
			Element containerText = nifty.getScreen("hud").findElementByName("ContainerText");
			String name = null;
			for (ItemDisplayTuple p : item) {
				if (p.stat == Stat.NAME){
					name = p.string;
				}
			}
			if (name == null){
				containerText.getRenderer(TextRenderer.class).setText("Container");
			}else{
				containerText.getRenderer(TextRenderer.class).setText(name);
			}
			return;
		}

		//Rightclickable items
		if (item instanceof RightClickable) {
			RightClickable toClick = (RightClickable) item;
			toClick.rightClick();

			if (!(toClick instanceof AbstractGizmo)) screenManager.getInventoryManager().removeItem(item);

			for(GUIObserver g : observers){
				g.onRightClicked(item);
			}
			return;
		}

		//Consumable items
		if (item instanceof AbstractConsumable){
			AbstractConsumable toConsume = (AbstractConsumable)item;
			toConsume.rightClick();
			screenManager.getInventoryManager().removeItem(item);
		}
	}

	/**Sets the current item id for use when processing drops **/
	@NiftyEventSubscriber(pattern="ItemVal.*")
	public void onClick8(String id, NiftyMousePrimaryClickedEvent event) {
		currentElementId = id;
	}

	/**Called when an item is equipped. Updates the inventory manager**/
	@NiftyEventSubscriber(pattern="EquipPos.*")
	public void onEquip(String id, DroppableDroppedEvent event) {

		Item it = findItemById(currentElementId.substring(7),screenManager.getInventoryManager().getPlayer().getContainerInventory());
		if(it!=null){
			screenManager.getInventoryManager().equip(it);
		}
	}

	/**Called when an item is droped from the inventory into the world**/
	@NiftyEventSubscriber(id = "DropArea")
	public void removeDropped(String id, DroppableDroppedEvent event){
		Element toRemove = nifty.getScreen("hud").findElementByName("DropArea");
		for (Element el : toRemove.getElements()){
			nifty.removeElement(nifty.getScreen("hud"),el);
		}
	}

	/**Called when the chatbutton on the basebar is pressed. Hides or shows the chat panel*/
	@NiftyEventSubscriber(pattern="chatButton")
	public void hideShowChat(String id, NiftyMousePrimaryReleaseEvent event) {
		if(nifty.getScreen("hud").findElementByName("chatPanel").isVisible())
			hideChat();
		else{
			showChat();
		}
	}

	/**Processes text submitted in the chat box and notifies network observers**/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@NiftyEventSubscriber(id = "chatText")
	public void submitChatText(String id, NiftyInputEvent event){
		TextField tf = nifty.getScreen("hud").findNiftyControl("chatText",TextField.class);
		ListBox lb = nifty.getScreen("hud").findNiftyControl("listBoxChat",ListBox.class);
		if (NiftyInputEvent.SubmitText.equals(event)) {
			String sendText = tf.getDisplayedText();
			if (sendText.length() == 0){
				nifty.getScreen("hud").getFocusHandler().resetFocusElements();
				return;
			}
			tf.setText("");
			if(observers.size()== 0){
				lb.addItem(sendText);
			}
			for (GUIObserver g : observers) {
				g.onChatMessage(sendText);
			}
			nifty.getScreen("hud").getFocusHandler().resetFocusElements();
			lb.showItem(lb.getItems().get(lb.getItems().size()-1));
		}
	}

	/**Adds a line of text sent by the network to the the player's chat box.
	 * Breaks up longer messages into multiple lines to avoid breaching the bounds
	 * of the chat box
	 *
	 * @param name - the name of the player sending the message
	 * @param line - the message sent
	 */
	@SuppressWarnings("unchecked")
	public void addChatLine(String name, String line){
		@SuppressWarnings("rawtypes")
		ListBox lb = nifty.getScreen("hud").findNiftyControl("listBoxChat",ListBox.class);
		if (line.length() + name.length() > 55){
			while (line.length() > 35){
				String splitLine = line.substring(0,35);
				lb.addItem("<"+name+">"+": "+splitLine);
				line = line.substring(35);
			}
		}
		lb.addItem("<"+name+">"+": "+line);
		lb.showItem(lb.getItems().get(lb.getItems().size()-1));
	}

	/**Displays the chat panel
	 *
	 */
	public void showChat(){
		Element chatPanel = nifty.getScreen("hud").findElementByName("chatPanel");
		Element chatText = nifty.getScreen("hud").findElementByName("chatText");
		if(!chatPanel.isVisible()){
			chatPanel.startEffect(EffectEventId.onCustom);
			chatPanel.setVisible(true);
		}
		chatText.setFocus();
	}

	/**Returns whether the chat text area currently has focus
	 *
	 * @return true if the chat text area has focus, false otherwise
	 */
	public boolean chatFocused(){
		TextField tf = nifty.getScreen("hud").findNiftyControl("chatText",TextField.class);
		return tf.hasFocus();
	}

	/**Hides the chat panel
	 *
	 */
	public void hideChat(){
		Element chat = nifty.getScreen("hud").findElementByName("chatPanel");
		chat.setVisible(false);
	}

	/**Displays a message on the HUD screen with a fade effect
	 *
	 * @param message - the message to display
	 */
	public void displayMessage(String message){

		Element mes = nifty.getScreen("hud").findElementByName("MessagePanel");
		mes.getRenderer(TextRenderer.class).setText(message);
		mes.startEffect(EffectEventId.onCustom);
	}

	/**Notifies observers of a world chest being shown
	 */
	public void showWorldChest(){
		// if there are no observers, assume a single player game
		if(observers.isEmpty()) {
			actualShowWorldChest();
			return;
		}
		// notify the server on open
		for(GUIObserver g : observers){
			g.onChestAccess(screenManager.getInventoryManager().getOpenWorldChest(), true);
		}
	}

	/**Shows a world chest panel and suppresses internal elements
	 *
	 */
	public void actualShowWorldChest(){
		Element el = nifty.getScreen("hud").findElementByName("WorldChest");
		el.show();
		for(Item it : screenManager.getInventoryManager().getOpenWorldChest()){
			if(it.getInventoryPosition() != null){
				Element toHide = nifty.getScreen("hud").findElementByName("hoverstats-"+it.getId());
				if(toHide!=null){
					toHide.setVisible(false);
				}
			}
		}
	}
	/**Displays a message intended for networked players arriving in the world
	 * on startup
	 * @param message - the message to display
	 */
	public void displayStartMessage(String message){
		if (startCompleted){
			displayMessage(message);
		}
		else{
			System.out.println(onStartMessage);
			this.onStartMessage = message;
		}
	}

	/**Closes a container panel*/
	public void closeContainer(){
		Element containerPanel = nifty.getScreen("hud").findElementByName("ContainerPanel");
		containerPanel.setVisible(false);
		if (openContainer == null) throw new AssertionError("Trying to close a container that is not open");
		screenManager.getInventoryManager().hideContainer(((AbstractContainerItem)openContainer).getContainerInventory());
		openContainer = null;
	}

	/**Helper method for suppressing item stat information when opening a panel**/
	private void hideInnerPopups(Inventory inventory){
		for(Item it : inventory){
			if (it instanceof AbstractContainerItem)hideInnerPopups(((AbstractContainerItem)it).getContainerInventory());
			if(it.getInventoryPosition() != null){
				Element toHide = nifty.getScreen("hud").findElementByName("hoverstats-"+it.getId());
				if(toHide!=null){
					toHide.hide();
				}
			}
		}
	}

	/**Hides an open world chest**/
	public void hideWorldChest(){
		Element el = nifty.getScreen("hud").findElementByName("WorldChest");
		el.hide();
		// notify the server on close
		for(GUIObserver g : observers){
			g.onChestAccess(screenManager.getInventoryManager().getOpenWorldChest(), false);
		}
	}

	/**Commences the custom effect on the health orb to ensure that the appropriate
	 * health level is displayed. Creates a fade effect.
	 * @param up - whether the health is increasing or decreasing
	 */
	public void updateHealth(boolean up){
		if (up){
			Element el = nifty.getScreen("hud").findElementByName("HealthOrb");
			el.startEffect(EffectEventId.onCustom);
			Element elfade = nifty.getScreen("hud").findElementByName("HealthOrbFade");
			elfade.startEffect(EffectEventId.onCustom,null,"OrbChange");
		}
		else{
			Element elfade = nifty.getScreen("hud").findElementByName("HealthOrbFade");
			elfade.startEffect(EffectEventId.onCustom,null,"OrbFade");
			Element el = nifty.getScreen("hud").findElementByName("HealthOrb");
			el.startEffect(EffectEventId.onCustom);
		}
	}

	/**Commences the custom effect on the energy orb to ensure that the appropriate
	 * health level is displayed. Creates a fade effect.
	 * @param up - whether the energy is increasing or decreasing
	 */
	public void updateEnergy(boolean up){
		if (up){
			Element el = nifty.getScreen("hud").findElementByName("EnergyOrb");
			el.startEffect(EffectEventId.onCustom);
			Element elfade = nifty.getScreen("hud").findElementByName("EnergyOrbFade");
			elfade.startEffect(EffectEventId.onCustom,null,"OrbChange");
		}
		else{
			Element elfade = nifty.getScreen("hud").findElementByName("EnergyOrbFade");
			elfade.startEffect(EffectEventId.onCustom,null,"OrbFade");
			Element el = nifty.getScreen("hud").findElementByName("EnergyOrb");
			el.startEffect(EffectEventId.onCustom);
		}
	}

	/** Nifty GUI ScreenControl methods */
	public void bind(Nifty nifty, Screen screen) {
		this.nifty = nifty;
		this.screen = screen;
	}


	@Override
	/**ScreenController method. Updates observers that the player has entered the game
	 * and displays any game commencement message sent over the network
	 */
	public void onStartScreen() {
		for(GUIObserver o : observers){
			o.onStartGame();
		}
		if (!startCompleted && onStartMessage.length()>2){
			displayMessage(onStartMessage);
		}
		startCompleted = true;
	}

	public void onEndScreen() { }

	/** jME3 AppState methods */

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app=(SimpleApplication)app;
	}

	/**The central processing method for Droppables as defined by the DroppableDropFilter interface
	 * Processes the droppable and determines whether it should be dropped, or should revert to its initial position
	 * @param dropSource - the element from which the drop originated
	 * @param dragged - the draggable being moved
	 * @param dropTarger - the target droppable slot
	 */
	public boolean accept(Droppable dropSource, Draggable dragged, Droppable dropTarget) {
		try{
			//assess the item and route to appropriate helper method
			if (!currentElementId.startsWith("ItemVal")) return false;
			String target = dropTarget.getId();
			Item swapItem = fullSearch(target);

			//Within the inventory
			Item inventItem = findItemById(currentElementId.substring(7),screenManager.getInventoryManager().getPlayer().getContainerInventory());
			if (inventItem != null){
				String source = inventItem.getInventoryPosition();
				if (checkInception(inventItem,target))return false;
				return (processInventDrop(source, target, inventItem, swapItem));
			}

			//Within a chest
			inventItem = findItemById(currentElementId.substring(7),screenManager.getInventoryManager().getOpenWorldChest());
			if (inventItem != null ){
				String source = inventItem.getInventoryPosition();
				if (checkInception(inventItem,target))return false;
				return (processChestDrop("Chest","Invent",screenManager.getInventoryManager().getOpenWorldChest(),
						screenManager.getInventoryManager().getPlayer().getContainerInventory(),source, target, inventItem, swapItem));
			}

			//Within a container
			inventItem = findItemById(currentElementId.substring(7),((AbstractContainerItem)openContainer).getContainerInventory());
			if (inventItem == null)return false;
			if (inventItem.getWeight()>30){
				displayMessage("Item too heavy to be placed in that container");
				return false;
			}
			String source = inventItem.getInventoryPosition();
			if (checkInception(inventItem,target))return false;
			return (processChestDrop("ContPos","Invent",inventItem.getInventory(),
					screenManager.getInventoryManager().getPlayer().getContainerInventory(),source, target, inventItem, swapItem));

		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**Helper method for moving items within the same inventory **/
	private boolean inventToInvent(String source, String target, Item toMove, Item toSwap){
		if (toSwap == null){
			toMove.setInventoryPosition(target);
			return true;
		}
		else if (source.equals(target)){
			return false;
		}
		else{
			Element toSwapDrag = nifty.getScreen("hud").findElementByName("ItemVal"+toSwap.getId());
			cleanRemove(toSwapDrag);
			screenManager.getInventoryManager().addItemInPos(toSwap, toMove.getInventoryPosition());
			toMove.setInventoryPosition(target);
			return true;
		}
	}

	/**Helper method for processing items dropped into the world**/
	private boolean processInventDrop(String source, String target, Item toMove, Item toSwap){

		if (source.startsWith("Invent") && target.startsWith("Invent")){
			return (inventToInvent(source, target, toMove, toSwap));
		}
		if (source.startsWith("Invent") && target.startsWith("EquipPos")){

			if (!(toMove instanceof Equippable)){
				displayMessage("YOU CANNOT EQUIP THAT ITEM");
				return false;
			}else{
				if (!canEquip(toMove,target))return false;
				displayMessage("EQUIPPED");
				if (toSwap!=null){
					Element toSwapDrag = nifty.getScreen("hud").findElementByName("ItemVal"+toSwap.getId());
					cleanRemove(toSwapDrag);
					screenManager.getInventoryManager().addItemInPos(toSwap, toMove.getInventoryPosition());
					screenManager.getInventoryManager().unequip(toSwap);
					toMove.setInventoryPosition(target);
				}
				Element message = nifty.getScreen("hud").findElementByName("MessagePanel");
				message = screen.findElementByName("CharEquipVisuals");
				message.startEffect(EffectEventId.onCustom,null,"shaker");
				toMove.setInventoryPosition(target);
				return true;
			}
		}

		if (source.startsWith("Invent") && target.startsWith("Chest")){
			//toSwap = findChestItemByPos(target);
			if (toSwap == null){
				Inventory from = toMove.getInventory();
				Inventory chest = screenManager.getInventoryManager().getOpenWorldChest();
				from.removeItem(toMove);
				toMove.setInventoryPosition(target);
				chest.addDirect(toMove);

				// send transfer message
				for(GUIObserver g : observers){
					g.onItemTransfer(from, chest, toMove);
				}
				return true;
			}
			return false;
		}

		if (source.startsWith("Invent") && target.startsWith("Con")){
			if (toSwap == null){
				Inventory from = toMove.getInventory();
				Inventory container = ((AbstractContainerItem)openContainer).getContainerInventory();
				if (container == null){
					throw new AssertionError("Container not open");
				}
				from.removeItem(toMove);
				toMove.setInventoryPosition(target);
				container.addDirect(toMove);
				for(GUIObserver g : observers){
					g.onItemTransfer(from, container, toMove);
				}
				return true;
			}
			return false;
		}

		if (source.startsWith("Invent") && target.startsWith("Drop") || target.startsWith("EqDrop")){
			if (target.startsWith("EqDrop")){
				if(nifty.getScreen("hud").findElementByName("CharEquip").isVisible()){
					return false;
				}
			}
			if (currentElementId.startsWith("ItemVal")){
				screenManager.getInventoryManager().dropItem(toMove);

				if (toMove == openContainer){
					closeContainer();
				}
				else{
					toMove.setInventoryPosition(null);
				}

				// network
				for(GUIObserver g : observers){
					g.onDropItem(toMove);
				}

				nifty.removeElement(nifty.getScreen("hud"),nifty.getScreen("hud").findElementByName(currentElementId));
			}
			Droppable dropArea = nifty.getScreen("hud").findNiftyControl("DropArea",Droppable.class);
			for(Element el : dropArea.getElement().getElements()){
				nifty.removeElement(nifty.getScreen("hud"), el);
			}
			Droppable eqDropArea = nifty.getScreen("hud").findNiftyControl("EqDropArea",Droppable.class);
			for(Element el : eqDropArea.getElement().getElements()){
				nifty.removeElement(nifty.getScreen("hud"), el);
			}
			return true;
		}

		if (source.startsWith("Equip") && target.startsWith("Invent")){
			if (inventToInvent(source, target, toMove, toSwap)){
				screenManager.getInventoryManager().unequip(toMove);
				return true;
			};
		}

		return false;
	}

	/**Helper method for moving items dropped into a chest**/
	private boolean processChestDrop(String fromType, String toType, Inventory inventFrom, Inventory inventTo, String source, String target, Item toMove, Item toSwap){
		if (source.startsWith(fromType) && target.startsWith(toType)){
			if (toSwap == null){
				inventFrom.removeItem(toMove);
				inventTo.addDirect(toMove);

				toMove.setInventoryPosition(target);

				// network
				for (GUIObserver g : observers) {
					g.onItemTransfer(
							inventFrom,
							inventTo,
							toMove);
				}

				return true;
			}
			return false;
		}

		if (source.startsWith(fromType) && target.startsWith(fromType)){
			return inventToInvent(source, target, toMove, toSwap);
		}
		return false;
	}

	/**Helper method for determining whether an item can be equipped into
	 * a given Droppable slot**/
	private boolean canEquip(Item item,String target){

		if (! (item instanceof Equippable))return false;
		Equippable toEquip = (Equippable)item;
		switch(toEquip.getEquipType()){
		case HEAD:
			if (target.startsWith("EquipPos"+EquipType.HEAD)) return true;
			break;
		case CHEST:
			if (target.startsWith("EquipPos"+EquipType.CHEST)) return true;
			break;
		case WEAPON:
			if (target.startsWith("EquipPos"+EquipType.WEAPON)) return true;
			break;
		case LEGS:
			if (target.startsWith("EquipPos"+EquipType.LEGS)) return true;
			break;
		case NECK:
			if (target.startsWith("EquipPos"+EquipType.NECK)) return true;
			break;
		case BELT:
			if (target.startsWith("EquipPos"+EquipType.BELT)) return true;
			break;
		case BOOTS:
			if (target.startsWith("EquipPos"+EquipType.BOOTS)) return true;
		}
		return false;
	}

	/**Cleanly removes a Nifty element from its bindings **/
	public void cleanRemove(Element element){
		for(Element el : element.getElements()){
			nifty.removeElement(nifty.getScreen("hud"),el);
		}
		nifty.removeElement(nifty.getScreen("hud"), element);
		nifty.executeEndOfFrameElementActions();
	}

	/**Helper method for determining whether an item is in a given position**/
	private Item findItemByPos(String pos, Inventory invent){
		if (invent == null) return null;
		for (Item item : invent){
			if (item.getInventoryPosition().equals(pos)){
				return item;
			}
		}
		return null;
	}

	/**Helper method for determining whether an item with a given id
	 * exists within a given inventory**/
	private Item findItemById(String curId, Inventory invent){
		if (invent == null) return null;
		for (Item item : invent){
			if (item.getId().equals(curId)){
				return item;
			}
		}
		return null;
	}

	/** As we may be swapping with any item on screen, we need to travese both the chest
	 * and inventory, along with any open container
	 *
	 * @param id - the id of the item to find
	 * @return - the Item if found, or null if not
	 */
	private Item fullSearch (String target){
		try{
			if(target == null) return null;
			Item item = null;
				item = findItemByPos(target,screenManager.getInventoryManager().getPlayer().getContainerInventory());
			if (item != null) return item;
			if (screenManager.getInventoryManager().getOpenWorldChest()!= null)
				item = findItemByPos(target,screenManager.getInventoryManager().getOpenWorldChest());
			if (item != null) return item;
			if ( openContainer !=null)
				item = findItemByPos(target,((AbstractContainerItem)openContainer).getContainerInventory());
			return item;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**Helper method to avoid containers being placed inside themselves.*/
	private boolean checkInception(Item it1, String target){
		if (it1 == openContainer && target.startsWith("ContPos")){
			displayMessage("INCEPTION FAILED. NICE TRY");
			return true;
		}
		return false;
	}

	/**Adds a GUI observer**/
	public void addObserver(GUIObserver g){
		observers.add(g);
	}

	/**Basic field getters**/

	public HashSet<String> getPositions(){
		return positionSet;
	}

	public void initialisePositions(ArrayList<String> itemSlots){
		for(String val : itemSlots){
			positionSet.add(val);
		}
	}
	public String getScreenName() {
		return "hud";
	}
	public ScreenManager getScreenManager(){
		return this.screenManager;
	}
	public Nifty getNifty() {
		return nifty;
	}
	public String getCurrentElementId(){
		return currentElementId;
	}
	public String getPlayerName(){
		return System.getProperty("user.name");
	}
	public boolean isInventoryOpen(){
		return nifty.getScreen("hud").findElementByName("InventoryPanel").isVisible();
	}
	public boolean isEquipOpen(){
		return nifty.getScreen("hud").findElementByName("CharEquip").isVisible();
	}
	public void hideShowCrossHairs(boolean show){
		Element crossHairs = nifty.getScreen("hud").findElementByName("CrossHairs");
		crossHairs.setVisible(show);
	}

}