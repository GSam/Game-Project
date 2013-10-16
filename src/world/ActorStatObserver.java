package world;

import world.entity.item.Stat;

/**
 * ActorStatObserver is used as a hook for the GUI, to be notified
 * when aspects of an ActorStat change.
 * 
 * @author Tony 300242775
 */
public interface ActorStatObserver {
	
	/**
	 * Update the observer that the passed Stat has changed by the
	 * passed amount.
	 * @param stat the Stat that has changed
	 * @param amount the amount it has changed by
	 */
	public void update(Stat stat, float amount);
}
