package world;

/**
 * An Activatable is anything that can be activated (via right-click) in the game world.
 * For example, doors, chests, and torches are activatable.
 * 
 * This is not to be confused with RightClickable, which indicates an Item can be right-
 * clicked when in an inventory.
 * 
 * @author Tony 300242775
 */
public interface Activatable {
	public void activate (Player player);
}
