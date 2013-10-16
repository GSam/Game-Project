package world;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import world.entity.item.Item;
import world.entity.item.Stat;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

/**
 * ActorStats represents all (static and dynamic) statistics of an Actor, and provides methods to
 * easily modify them. The stats stored by ActorStats are defined in the Stats enum.
 *
 * @author Tony 300242775
 */
public class ActorStats implements Savable {
	/**
	 * The core data storage of an ActorStats. This represents the current
	 * state of this Actor's stats.
	 */
	protected Map<Stat, Float> stats = new HashMap<Stat, Float> ();

	private Map<Stat, Float> baseline;
	private Set<ActorStatObserver> statObservers = new HashSet<ActorStatObserver>();

	public ActorStats() {baseline = new HashMap<Stat, Float>();}

	/**
	 * Throws a NullPointerException is baseline is null.
	 * 
	 * @param baseline the initial stats that this ActorStats object should hold
	 */
	public ActorStats (Map<Stat, Float> baseline) {
		if(baseline == null) throw new NullPointerException("baseline is null");
		this.baseline = baseline;
		initialise();
	}

	/**
	 * Throws an IllegalArgumentException is keys and values are not the same
	 * size.
	 * 
	 * @param keys the Stats to hold
	 * @param values the values of the respectively indexed Stats in keys
	 */
	public ActorStats(Stat[] keys, float[] values) {
		if (keys.length != values.length) throw new IllegalArgumentException ("input arrays must be of the same size");

		baseline = new HashMap<Stat, Float> ();

		for (int i=0; i < keys.length; i++) {
			baseline.put(keys[i], values[i]);
		}
		initialise();
	}

	/**
	 * Increases the value of the given stat by the given amount.
	 *
	 * @param stat the Stat to change
	 * @param amount the amount to increase by
	 */
	public void modStat (Stat stat, float amount) {
		float toChange = stats.get(stat) + amount;
		setStat(stat, toChange);
	}

	/**
	 * Multiple the value of the given stat by the given amount.
	 *
	 * @param stat the Stat to increase
	 * @param multiplier the amount to multiply by
	 */
	public void multStat (Stat stat, float multiplier) {
		setStat(stat, getStat(stat) * multiplier);
	}
	
	/**
	 * Resets the passed Stat to the baseline value given at
	 * contstruction.
	 * @param stat the Stat to reset
	 */
	public void resetStat (Stat stat) {
		Float value = baseline.get(stat);
		if(value == null) value = 0f;
		stats.put(stat, value);
	}

	/**
	 * Sets the value of the passed Stat to the passed amount.
	 *
	 * @param stat the Stat to set
	 * @param amount the value to set to
	 */
	public void setStat (Stat stat, float amount) {
		stats.put(stat, amount);
		for (ActorStatObserver obs : statObservers){
			obs.update(stat, amount);
		}
	}

	/**
	 * Returns the value of the passed Stat.
	 *
	 * @param stat the Stat to get the value of
	 * @return the value of the stat
	 */
	public float getStat (Stat stat) {
		return stats.get(stat);
	}


	/**
	 * Initialises player stats from the baseline values passed
	 * at construction.
	 */
	public void initialise() {
		for (Stat stat : Stat.values()) {
			Float i = baseline.get(stat);
			if (i == null){
				setStat(stat,0);
			} else {
				setStat(stat,i);
			}
		}
	}

	/**
	 * Equips the passed item and recalculates stats based on the
	 * properties of that item.
	 * @param item the Item to equip
	 */
	public void equip(Item item){
		for (Entry<Stat, Float> entry : item.getItemStats()){
			modStat(entry.getKey(),entry.getValue());
		}
	}
	
	/**
	 * Unequips the passed item and recalculates stats based on the
	 * properties of that item.
	 * @param item the Item to equip
	 */
	public void unequip(Item item){
		for (Entry<Stat, Float> entry : item.getItemStats()){
			modStat(entry.getKey(),-entry.getValue());
		}
		
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		for(Stat s : Stat.values()) {
			baseline.put(s, c.readFloat("base"+s.name(), 0));
			stats.put(s, c.readFloat("cur"+s.name(), 0));
		}
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		for(Map.Entry<Stat, Float> e : baseline.entrySet())
			c.write(e.getValue(), "base"+e.getKey().name(), 0);
		for(Map.Entry<Stat, Float> e : stats.entrySet())
			c.write(e.getValue(), "cur"+e.getKey().name(), 0);
	}
	

	/**
	 * Add an observer to this ActorStats to be notified of changes.
	 * Used to update the UI of a Player based on changes to their
	 * stats.
	 * @param psm the observer add
	 */
	public void addObserver(ActorStatObserver psm){
		statObservers.add(psm);
		
	}

	/**
	 * Removes an observer from this ActorStats object.
	 * @param psm the observer to remove
	 */
	public void removeObserver(ActorStatObserver psm) {
		statObservers.remove(psm);
	}
}
