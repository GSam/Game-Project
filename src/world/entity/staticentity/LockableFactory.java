package world.entity.staticentity;

import com.jme3.math.Vector3f;

/**
 * LockableFactory is the entry point for creating lockables. It defines the different types of
 * lockable in the game and provides access to them through an enum and a static
 * method.
 *
 * LockableFactory is redundant with ContainerFactory and DoorFactory, but exists to make
 * map loading from a file possible.
 * 
 * @author Tony 300242775
 */
public class LockableFactory {
	/**
	 * The different types of Lockable that can be created.
	 * @author Tony 300242775
	 */
	public enum Instance {
		DOOR, CHEST
	};

	/**
	 * @param item the type of Lockable to get an instance of
	 * @return a Lockable of the passed type
	 */
	public static AbstractStaticLockedActivator getLockableInstance(Instance instance, Vector3f scale, float angle) {
		if (instance == Instance.DOOR) {
			return new SimpleDoor("door/door.mesh.xml", scale, angle);
		} if (instance == Instance.CHEST) {
			return new SimpleContainer("door/door.mesh.xml", scale, angle);
		} else {
			return null;
		}
	}
}
