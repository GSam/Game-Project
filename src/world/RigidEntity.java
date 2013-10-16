package world;

import java.io.IOException;

import savefile.RigidBodyPhysicsState;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * A rigid entity is an entity that conforms to rigid-body physics. At present this is equivalent
 * to everything except for Actors.
 *
 * @author Tony 300242775
 */
public abstract class RigidEntity extends AbstractEntity {
	
	/**
	 * The physics control for this RigidEntity. This is guaranteed
	 * to be linked to this object's Geometry when it is non-null,
	 * but is not guaranteed to be non-null.
	 */
	protected RigidBodyControl physics;
	private RigidBodyPhysicsState loadedPhysicsState;

	// PHYSICS AND GEOMETRY

	@Override
	protected void makePhysics (PhysicsSpace physicsSpace) {
		if(physics != null)
			throw new IllegalStateException("physics already created");

        CollisionShape shape = CollisionShapeFactory.createDynamicMeshShape(geometry);
        physics = new RigidBodyControl(shape, getWeight());
        geometry.addControl(physics);
        physicsSpace.add(physics);
        physics.setGravity(new Vector3f(0, -100, 0));
	}

	@Override
	protected void destroyPhysics (PhysicsSpace physicsSpace) {
		removeFromPhysicsSpace();
		geometry.removeControl(physics);
		physics = null;
	}

	/**
	 * Returns the weight of this item. Must be non-negative, and 0 indicates
	 * 'infinite weight' (as per JMonkey standard).
	 * @return the weight of this item as a float
	 */
	protected abstract float getWeight ();

	@Override
	protected abstract void makeMesh (AssetManager assetManager);

	@Override
	public RigidBodyControl getPhysics () {
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		return physics;
	}

	// WORLD LINK

	@Override
	public void linkToWorld(World world, Vector3f location, int id) {
		super.linkToWorld(world, location, id);

		if(loadedPhysicsState != null) {
			if(physics != null) {
				loadedPhysicsState.copyTo(physics);
			} else {
				new Exception("Physics loaded, but rigid entity has no physics").printStackTrace();
			}
			loadedPhysicsState = null;
		}
	}

	@Override
	public Node getNodeToAttach () {
		return world.getRigidNode();
	}

	// LOCATION AND DIRECTION

	@Override
	public Vector3f getDirection() {
		// not useful for an inanimate object
		return new Vector3f(0, 0, 0);
	}

	@Override
	public Vector3f getLocation() {
		return physics.getPhysicsLocation();
	}

	@Override
	public void setLocation(Vector3f location) {
		physics.setPhysicsLocation(location);
	}

	@Override
	public void changeLocation(Vector3f location) {
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		Vector3f change = physics.getPhysicsLocation().add(location);
		physics.setPhysicsLocation(change);
	}

	@Override
	public void changeLocation(float x, float y, float z) {
		if (physics == null) throw new IllegalStateException ("physics accessed before being initialised");
		changeLocation (new Vector3f(x,y,z));
	}

	// BEHAVIOUR

	@Override
	public void update (float tpf) {} // we won't want any updating for most of the RigidEntities.

	// SAVING AND LOADING

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		loadedPhysicsState = (RigidBodyPhysicsState)arg0.getCapsule(this).readSavable("physics", null);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		if(physics != null)
			arg0.getCapsule(this).write(new RigidBodyPhysicsState(physics), "physics", null);
	}
}
