package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import world.EntitySpawnData;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

/**
 * Stores player data with their name and ID. Implements savable to allow it to
 * be stored offline and then reloaded. 
 * 
 * @author Garming Sam 300198721
 * 
 */
public class PlayerData implements Savable {
	private Map<String, Integer> nameToID = new ConcurrentHashMap<String,Integer>();
	private Map<Integer, String> names = new ConcurrentHashMap<Integer,String>();
	
	/**
	 * Returns if the data contains the name given.
	 * 
	 * @param name
	 *            to check
	 * @return if it contains the name
	 */
	public boolean containsName(String name){
		return nameToID.containsKey(name);
	}
	
	/**
	 * Adds a new data pair to the stored data. Any existing id can replace
	 * their name, but not necessarily the other way around.
	 * 
	 * @param name
	 * @param id
	 */
	public void addData(String name, int id){
		String old = names.put(id, name);
		// remove old name if possible
		if(old != null) nameToID.remove(old);
		nameToID.put(name, id);
	}
	
	/**
	 * Get the id associated with the given name. Returns -1 if no such name exists.
	 * 
	 * @param name
	 *            name
	 * @return the id
	 */
	public int getID(String name){
		if (nameToID.containsKey(name))
			return nameToID.get(name);
		else 
			return -1;
	}
	
	/**
	 * Get the name associated with the given id. Null if it doesn't exist.
	 * 
	 * @param id
	 *            id
	 * @return the name
	 */
	public String getName(int id){
		return names.get(id);
	}
	
	@Override
	public void read(JmeImporter arg0) throws IOException {
		InputCapsule ic = arg0.getCapsule(this);
		names.clear();
		nameToID.clear();
		Collection<NamePair> namePair = (Collection<NamePair>)ic.readSavableArrayList("names", null);
		for(NamePair p : namePair){
			nameToID.put(p.name, p.id);
			names.put(p.id, p.name);
		}
		
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		OutputCapsule oc = arg0.getCapsule(this);
		ArrayList<NamePair> pairs = new ArrayList<NamePair>();
		for(Map.Entry<String, Integer> entry : nameToID.entrySet()){
			pairs.add(new NamePair(entry.getKey(), entry.getValue()));
		}
		oc.writeSavableArrayList(pairs, "names", null);
		
	}

	/**
	 * Represent a name, id pair for use in offline data storage.
	 * 
	 * @author Garming Sam 300198721
	 * 
	 */
	public static class NamePair implements Savable{
		private String name;
		private Integer id;
		
		/**
		 * Construct a name pair
		 * 
		 * @param name
		 *            name
		 * @param id
		 *            id
		 */
		public NamePair(String name, int id) {
			this.name = name;
			this.id = id;
		}
		
		public NamePair() {
		}
		
		@Override
		public void read(JmeImporter arg0) throws IOException {
			InputCapsule ic = arg0.getCapsule(this);
			name = ic.readString("name", null);
			id = ic.readInt("id", 0);
		}
		
		@Override
		public void write(JmeExporter arg0) throws IOException {
			OutputCapsule ic = arg0.getCapsule(this);
			ic.write(name, "name", null);
			ic.write(id, "id", 0);
		}
	}

}
