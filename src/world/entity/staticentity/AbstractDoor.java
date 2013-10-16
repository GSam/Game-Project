package world.entity.staticentity;

import com.jme3.math.Vector3f;

import world.Player;

/**
 * 
 * @author Tony
 *
 */
public abstract class AbstractDoor extends AbstractStaticLockedActivator {
	public AbstractDoor () {}
	
    public AbstractDoor (String meshPath, Vector3f scale, float angle) {
    	super(meshPath, scale, angle);
    }
	
	@Override
	protected abstract void onOpen(Player player);

	@Override
	protected abstract void onClose(Player player);
}
