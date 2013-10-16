package world.entity.item.equippable;

import world.entity.item.Item;
import world.entity.item.Stat;

/**
 * EquippableFactory is the entry point for creating equippables. It defines the different
 * types of equippables in the game and provides access to them through an enum and a static
 * method.
 *
 * Note that factory does not supply weapons, as they should be instantiated manually.
 *
 * @author Tony 300242775
 */
public class EquippableFactory {

	/**
	 * The different types of Equippable that can be created.
	 * @author Tony 300242775
	 */
	public enum Instance {
		HEAD_LOW, HEAD_HIGH, BODY_LOW, BODY_HIGH, LEGS_LOW, LEGS_HIGH
	};

	/**
	 * @param item the type of Equippable to get an instance of
	 * @return an Equippable of the passed type
	 */
	public static Item getEquippableInstance(Instance item) {
		if (item == Instance.HEAD_LOW) {
			return new SimpleEquippable(null, EquipType.HEAD, new Stat[] { Stat.NAME }, new String[] { "Rusty Iron Helmet" }, new Stat[] { Stat.ARMOUR }, new float[] { 1 });
		} else if (item == Instance.HEAD_HIGH) {
			return new SimpleEquippable(null, EquipType.HEAD, new Stat[] { Stat.NAME }, new String[] { "Nano Helmet" }, new Stat[] { Stat.ARMOUR }, new float[] { 3 });
		} else if (item == Instance.BODY_LOW) {
			return new SimpleEquippable(null, EquipType.CHEST, new Stat[] { Stat.NAME}, new String[] { "Rusty Cuirass" }, new Stat[] { Stat.ARMOUR }, new float[] { 3 });
		} else if (item == Instance.BODY_HIGH) {
			return new SimpleEquippable(null, EquipType.CHEST, new Stat[] { Stat.NAME }, new String[] { "Composite Armour" }, new Stat[] { Stat.ARMOUR }, new float[] { 5 });
		} else if (item == Instance.LEGS_LOW) {
			return new SimpleEquippable(null, EquipType.LEGS, new Stat[] { Stat.NAME}, new String[] { "Combat Boots" }, new Stat[] { Stat.SPEED }, new float[] { 1 });
		} else if (item == Instance.LEGS_HIGH) {
			return new SimpleEquippable(null, EquipType.LEGS, new Stat[] { Stat.NAME }, new String[] { "Anti-Gravity Boots" }, new Stat[] { Stat.SPEED }, new float[] { 2 });
		} else {
			return null;
		}
	}
}
