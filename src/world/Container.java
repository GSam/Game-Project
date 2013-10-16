package world;

/**
 * A Container represents any non-Actor in the game world with an inventory.
 * 
 * @author Tony 300242775
 */
public interface Container {
	
	/**
	 * @return the Inventory of this Container
	 */
	public Inventory getContainerInventory ();
	
	/**
	 * @return true if this Container can be opened, false otherwise
	 */
	public boolean canAccess();
	
	/**
	 * Sets whether this container can be opened or not.
	 * 
	 * @param access whether this container can be opened
	 */
	public void setCanAccess(boolean access);
}
