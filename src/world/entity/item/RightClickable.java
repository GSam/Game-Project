package world.entity.item;

/**
 * A RightClickable is something that can be right clicked in an inventory. It defines a single
 * method, rightClick, that implements the behaviour for when a user right clicks the object.
 *  
 * @author Tony 300242775
 */
public interface RightClickable {
	/**
	 * Defines the behaviour that should occur when this object is right
	 * clicked in an inventory. 
	 */
	public void rightClick ();
}
