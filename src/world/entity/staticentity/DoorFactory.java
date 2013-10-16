package world.entity.staticentity;

import com.jme3.math.Vector3f;

/**
 * DoorFactory is the entry point for creating doors. It defines the different types of
 * door in the game and provides access to them through an enum and a static
 * method. 
 * 
 * @author Tony 300242775
 */
public class DoorFactory {
	/**
	 * The different types of Door that can be created.
	 * @author Tony 300242775
	 */
	public enum Instance {
		ROTATING, LIFTING, SWINGLIFTING, GATE
	};

	/**
	 * @param item the type of Door to get an instance of
	 * @return a Door of the passed type
	 */
	public static AbstractDoor getDoorInstance(Instance instance, Vector3f scale, float angle) {
		if (instance == Instance.ROTATING) {
			return new RotatingDoor("door/door.mesh.xml", scale, angle);
		} else if(instance == Instance.LIFTING) {
			return new LiftingDoor("door/door.mesh.xml", scale, angle);
		} else if(instance == Instance.SWINGLIFTING) {
			return new SwingLiftingDoor("door/door.mesh.xml", scale, angle);
		} else if (instance == Instance.GATE) {
			return new SimpleDoor("Gate/Gate.scene", scale, angle);
		} else {
			return null;
		}
	}
}
