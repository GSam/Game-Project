package world.entity.item.miscellaneous;

import java.util.Collection;

import world.entity.item.Item;
import world.entity.item.Stat;

/**
 * TreasureFactory is the entry point for creating treasures. It defines the
 * different types of treasure in the game and provides access to them through
 * an enum and a static method.
 * 
 * As treasures should only ever have one instance, TreasureFactory uses a
 * (modified) flyweight pattern. Because all treasures are guaranteed to be in
 * the game, the modification is that TreasureFactory statically creates all
 * treasures on initialisation.
 * 
 * @author Tony 300242775
 */
public class TreasureFactory {
	/**
	 * The different types of Treasure that can be created.
	 * 
	 * @author Tony 300242775
	 */
	public enum Instance {
		ONE, TWO, THREE
	};

	/**
	 * @param item
	 *            the type of Treasure to get an instance of
	 * @return a Treasure of the passed type
	 */
	public static Item getTreasureInstance(Instance instance) {
		if (instance == Instance.ONE) {
			return new Treasure(null, null, new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Treasure 1/3", "A treasure" });
		} else if (instance == Instance.TWO) { return new Treasure(null, null, new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Treasure 2/3", "A treasure" }); }
		if (instance == Instance.THREE) {
			return new Treasure(null, null, new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Treasure 3/3", "A treasure" });
		} else {
			return null;
		}
	}
}