package world.entity.item.consumable;

import world.entity.item.Item;
import world.entity.item.Stat;

/**
 * ConsumableFactory is the entry point for creating consumables. It defines the different
 * types of consumables in the game and provides access to them through an enum and a static
 * method.
 *
 * @author Tony 300242775
 */
public class ConsumableFactory {
	/**
	 * The different types of consumables
	 * @author Tony 300242775
	 */
	public enum Instance {
		HEALTH_LOW, HEALTH_HIGH, ENERGY_LOW, ENERGY_HIGH, MAX_HEALTH_BOOST, MAX_ENERGY_BOOST
	};

	/**
	 * @param item the type of Consumable to get an instance of
	 * @return a consumable of the passed type
	 */
	public static Item getConsumableInstance(Instance item) {
		if (item == Instance.HEALTH_LOW) {
			return new SimpleConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Health Vial", "Restores some health" }, new Stat[] { Stat.HEALTH }, new float[] { 50 });
		} else if (item == Instance.HEALTH_HIGH) {
			return new SimpleConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Med Pack", "Restores a lot of health" }, new Stat[] { Stat.HEALTH }, new float[] { 90 });
		} else if (item == Instance.ENERGY_LOW) {
			return new SimpleConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Batteries", "Restores some energy" }, new Stat[] { Stat.ENERGY }, new float[] { 50 });
		} else if (item == Instance.ENERGY_HIGH) {
			return new SimpleConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Recharge Pack", "Restores a lot of energy" }, new Stat[] { Stat.ENERGY }, new float[] { 100 });
		} else if (item == Instance.MAX_HEALTH_BOOST) {
			return new TemporaryConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Stimulants", "Temporarily raise your maximum health" }, new Stat[] { Stat.MAXHEALTH, Stat.HEALTH }, new float[] { 250, 250 }, 20);
		} else if (item == Instance.MAX_ENERGY_BOOST) {
			return new TemporaryConsumable("healthvial/healthvial.scene", new Stat[] { Stat.NAME, Stat.DESCRIPTION }, new String[] { "Overcharger", "Temporarily raise your maximum energy" }, new Stat[] { Stat.MAXENERGY, Stat.ENERGY }, new float[] { 250, 250 }, 20);
		} else {
			return null;
		}
	}
}
