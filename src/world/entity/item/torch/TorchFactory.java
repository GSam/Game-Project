package world.entity.item.torch;

import world.entity.item.Stat;

import com.jme3.math.ColorRGBA;

/**
 * TorchFactory is the entry point for creating torches. It defines the different types of
 * torch in the game and provides access to them through an enum and a static
 * method.
 *
 * @author Tony 300242775
 */
public class TorchFactory {
	private static final ColorRGBA FLAME = new ColorRGBA(1, 1, 0.7f, 1);
	private static final ColorRGBA GREEN = new ColorRGBA(0.7f, 1, 0.7f, 1);
	private static final ColorRGBA BLUE = new ColorRGBA(0.3f, 0.6f, 1, 1);

	/**
	 * The different types of Torch that can be created.
	 * @author Tony 300242775
	 */
	public enum Instance {
		WEAK, INDUSTRIAL, FUTURE, FLICKERY
	};

	/**
	 * @param item the type of Torch to get an instance of
	 * @return a Torch of the passed type
	 */
	public static AbstractTorch getTorchInstance(Instance item) {
		if (item == Instance.WEAK) {
			return new SimpleTorch (FLAME.mult(8f), 50f, 0.4f, 50f, new Stat[] {Stat.NAME, Stat.DESCRIPTION}, new String[] {"Old Torch", "An old gas-light torch"}, null);
		} else if (item == Instance.INDUSTRIAL) {
			return new SimpleTorch (GREEN.mult(8f), 100f, 0.4f, 75f, new Stat[] {Stat.NAME, Stat.DESCRIPTION}, new String[] {"Industrial Lamp", "A weak but long-ranged lamp"}, null);
		} else if (item == Instance.FUTURE) {
			return new SimpleTorch (BLUE.mult(8f), 80f, 0.2f, 80f, new Stat[] {Stat.NAME, Stat.DESCRIPTION}, new String[] {"Plasma Beacon", "A strong but short-range light"}, null);
		} else if(item == Instance.FLICKERY) {
			return new FlickerTorch (new ColorRGBA(1, 0.7f, 0.9f, 1).mult(8f), 100f, 0.01f, 100f, new Stat[] {Stat.NAME, Stat.DESCRIPTION}, new String[] {"Torchocraptic", "Like a torchomatic, but of inferior quality."}, null);
		} else {
			return null;
		}
	}
}
