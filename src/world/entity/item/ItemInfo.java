package world.entity.item;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

/**
 * ItemInfo stores informational data about an Item that can be displayed to the user and
 * is easy for the GUI to parse.
 * 
 * @author Tony 300242775
 */
public class ItemInfo implements Iterable <Entry<Stat, String>>, Savable {
	private Map<Stat, String> info = new HashMap<Stat, String> ();
	
	public ItemInfo() {}
	
	public ItemInfo (Stat[] keys, String[] values) {
		if (values.length != keys.length) throw new IllegalArgumentException ("input arrays must be of the same size.");
		
		for (int i=0; i < values.length; i++) {
			info.put (keys[i], values[i]);
		}
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		for(Stat s : Stat.values()) {
			String val = c.readString(s.name(), null);
			if(val != null)
				info.put(s, val);
		}
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		for(Map.Entry<Stat, String> e : info.entrySet()) {
			c.write(e.getValue(), e.getKey().name(), null);
		}
	}
	
	/**
	 * @return the information encoded in the ItemInfo object
	 */
	public Map<Stat, String> getStats () {
		return info;
	}
	
	/**
	 * @return a well formatted HashMap corresponding to the contents of
	 * this ItemInfo
	 */
	public HashMap<String,String> statMap(){
		HashMap<String,String> strings = new HashMap<String,String>();
		
		for (Map.Entry<Stat, String> stat : info.entrySet()) {
			strings.put(stat.getKey().toString(), stat.getValue());
		}
		
		return strings;		
	}

	@Override
	public Iterator<Entry<Stat, String>> iterator() {
		return info.entrySet().iterator();
	}
}