package world.entity.trigger;

import com.jme3.export.Savable;

import world.Player;

/**
 * @author Alex Campbell 300252131
 */
public interface TriggerAction extends Savable {
	public void trigger(Player triggerer);
}
