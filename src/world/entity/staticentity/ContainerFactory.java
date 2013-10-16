package world.entity.staticentity;

import com.jme3.math.Vector3f;

/**
 * ContainerFactory is the entry point for creating containers. It defines the different types of
 * container in the game and provides access to them through an enum and a static
 * method. 
 * 
 * @author Tony 300242775
 */
public class ContainerFactory {
	/**
	 * The different types of Container that can be created.
	 * @author Tony 300242775
	 */
	public enum Instance {
		CHEST
	};

	/**
	 * @param item the type of Container to get an instance of
	 * @return a Container of the passed type
	 */
	public static SimpleContainer getContainerInstance(Instance instance, Vector3f scale, float angle) {
		if (instance == Instance.CHEST) {
			return new SimpleContainer("Models/chest/chest.scene", scale, angle);
		} else {
			return null;
		}
	}
}
