package world.entity.item.miscellaneous;

import world.entity.item.Item;
import world.entity.item.Stat;

/**
 * KeyFactory is the entry point for creating keys. It defines the different types of
 * key in the game and provides access to them through an enum and a static
 * method. 
 * 
 * @author Tony 300242775
 */
public class KeyFactory {
	/**
	 * The different types of Key that can be created.
	 * @author Tony 300242775
	 */
	public enum Instance {
		NEW, OLD
	};

	/**
	 * @param item the type of Key to get an instance of
	 * @return a Key of the passed type
	 */
	public static Item getKeyInstance(Instance item, String name) {
		if (item == Instance.OLD) {
			return new Key("Models/key/key.scene", new Stat[] {Stat.NAME, Stat.DESCRIPTION }, new String[] { name!=null?name:"Key", "A rusty old key" });
		} else if (item == Instance.NEW) {
			return new Key(null, new Stat[] {Stat.NAME, Stat.DESCRIPTION }, new String[] { name!=null?name:"Key", "A faded key card" });
		} else {
			return null;
		}
	}
}
