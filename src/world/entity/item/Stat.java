package world.entity.item;

/**
 * Stat defines the characteristics of an Item or Actor. For an Actor this
 * encodes its state with respect to its health, energy, speed, etc. For an
 * Item, this encodes the effects that item can have, for example, when it
 * is equipped.
 * 
 * Stat also encodes information about an Item (eg. it's name). This is an
 * imperfect solution as these are different concepts, but the quirks of
 * NiftyGUI leave us with few options in this area.
 * 
 * @author Tony 300242775
 */
public enum Stat {
	HEALTH,
	MAXHEALTH,

	ENERGY,
	MAXENERGY,

	DAMAGE,
	DAMAGEMOD,

	ARMOUR,
	SPEED,

	NAME,
	TYPE,
	DESCRIPTION
}