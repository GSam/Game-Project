package world.entity.item;

/**
 * A simple carrier class that stores a Stat and the related data from one entry in an Item's
 * ItemStats or ItemInformation objects. 
 * @author Tony
 */
public class ItemDisplayTuple {
	public final Stat stat;
	public final String string;

	public ItemDisplayTuple(Stat stat, String string) {
		this.stat = stat;
		this.string = string;
	}
}