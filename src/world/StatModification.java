package world;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import world.entity.item.Stat;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

/**
 * StatModification encodes a change to an ActorStats object. At present it
 * is very similar to a HashMap<Stat, Float>, but this is not guaranteed to
 * always be the case. 
 * 
 * @author Tony 300242775
 */
public class StatModification implements Iterable<Map.Entry<Stat, Float>>, Savable {
	private Map<Stat, Float> statEffects = new HashMap<Stat, Float> ();
	
	public StatModification() {}
	
	public StatModification (Stat[] keys, float[] values) {
		if (values.length != keys.length) throw new IllegalArgumentException ("input arrays must be of the same size.");
		
		for (int i=0; i < values.length; i++) {
			if(keys[i] == null) throw new NullPointerException("null key");
			statEffects.put (keys[i], values[i]);
		}
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		for(Stat s : Stat.values()) {
			float val = c.readFloat(s.name(), Float.NaN);
			if(!Float.isNaN(val))
				statEffects.put(s, val);
		}
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		for(Map.Entry<Stat, Float> e : statEffects.entrySet()) {
			c.write(e.getValue(), e.getKey().name(), 0);
		}
	}
	
	/**
	 * Returns the map of stats to values.
	 */
	public Map<Stat, Float> getStats () {
		return statEffects;
	}
	
	/**
	 * Returns a map of human-readable stat names to human-readable values.
	 */
	public HashMap<String,String> statMap(){
		HashMap<String,String> strings = new HashMap<String,String>();
		
		for (Map.Entry<Stat, Float> stat : statEffects.entrySet()) {
			strings.put(stat.getKey().toString(), stat.getValue().toString());
		}
		
		return strings;		
	}

	/**
	 * Equivalent to getStats().entrySet().iterator()
	 */
	@Override
	public Iterator<Entry<Stat, Float>> iterator() {
		return statEffects.entrySet().iterator();
	}
}
