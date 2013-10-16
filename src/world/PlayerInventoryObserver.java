package world;

/**
 * @author Alex Campbell 300252131
 */
public interface PlayerInventoryObserver extends InventoryObserver {
	public boolean hideChest();
	public boolean displayChest(Inventory inventory2);
}
