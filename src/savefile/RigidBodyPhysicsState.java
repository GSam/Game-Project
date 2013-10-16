package savefile;

import java.io.IOException;
import java.lang.reflect.Field;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Holds the state of a RigidBodyControl at a given point in time.
 * Because RigidBodyControls cannot be serialized correctly, an instance of this is serialized instead.
 * 
 * @author Alex Campbell 300252131
 */
public class RigidBodyPhysicsState implements Savable {
	private float angularDamping, angularFactor, angularSleepingThreshold;
	private Vector3f angularVelocity;
	private boolean applyPhysicsLocal;
	private float ccdMotionThreshold, ccdSweptSphereRadius;
	private int collideWithGroups, collisionGroup;
	private boolean isKinematic, isKinematicSpatial;
	private float linearDamping, linearSleepingThreshold;
	private Vector3f linearVelocity;
	private float mass;
	private Vector3f physicsLocation;
	private Quaternion physicsRotation;
	private float restitution;
	
	public RigidBodyPhysicsState() {}
	
	/**
	 * Creates a RigidBodyPhysicsState, copying the state of a RigidBodyControl.
	 * @param c The RigidBodyControl to get the state from.
	 */
	public RigidBodyPhysicsState(RigidBodyControl c) {
		angularDamping = c.getAngularDamping();
		angularFactor = c.getAngularFactor();
		angularSleepingThreshold = c.getAngularSleepingThreshold();
		angularVelocity = c.getAngularVelocity();
		applyPhysicsLocal = c.isApplyPhysicsLocal();
		ccdMotionThreshold = c.getCcdMotionThreshold();
		ccdSweptSphereRadius = c.getCcdSweptSphereRadius();
		collideWithGroups = c.getCollideWithGroups();
		collisionGroup = c.getCollisionGroup();
		isKinematic = c.isKinematic();
		isKinematicSpatial = c.isKinematicSpatial();
		linearDamping = c.getLinearDamping();
		linearSleepingThreshold = c.getLinearSleepingThreshold();
		linearVelocity = c.getLinearVelocity();
		mass = c.getMass();
		physicsLocation = c.getPhysicsLocation();
		physicsRotation = c.getPhysicsRotation();
		restitution = c.getRestitution();
	}
	
	/**
	 * Sets the state of a RigidBodyControl to the state stored in this object.
	 * @param c The RigidBodyControl to copy state to.
	 */
	public void copyTo(RigidBodyControl c) {
		c.setAngularFactor(angularFactor);
		c.setSleepingThresholds(linearSleepingThreshold, angularSleepingThreshold);
		c.setAngularVelocity(angularVelocity);
		c.setApplyPhysicsLocal(applyPhysicsLocal);
		c.setCcdMotionThreshold(ccdMotionThreshold);
		c.setCcdSweptSphereRadius(ccdSweptSphereRadius);
		c.setCollideWithGroups(collideWithGroups);
		c.setCollisionGroup(collisionGroup);
		c.setKinematic(isKinematic);
		c.setKinematicSpatial(isKinematicSpatial);
		c.setDamping(linearDamping, angularDamping);
		c.setLinearVelocity(linearVelocity);
		c.setMass(mass);
		c.setPhysicsLocation(physicsLocation);
		c.setPhysicsRotation(physicsRotation);
		c.setRestitution(restitution);
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule c = im.getCapsule(this);
		try {
			for(Field f : RigidBodyPhysicsState.class.getDeclaredFields()) {
				if(f.getType() == boolean.class)
					f.setBoolean(this, c.readBoolean(f.getName(), false));
				else if(f.getType() == int.class)
					f.setInt(this, c.readInt(f.getName(), 0));
				else if(f.getType() == float.class)
					f.setFloat(this, c.readFloat(f.getName(), 0));
				else
					f.set(this, c.readSavable(f.getName(), null));
			}
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule c = ex.getCapsule(this);
		try {
			for(Field f : RigidBodyPhysicsState.class.getDeclaredFields()) {
				if(f.getType() == boolean.class)
					c.write(f.getBoolean(this), f.getName(), false);
				else if(f.getType() == int.class)
					c.write(f.getInt(this), f.getName(), 0);
				else if(f.getType() == float.class)
					c.write(f.getFloat(this), f.getName(), 0);
				else
					c.write((Savable)f.get(this), f.getName(), null);
			}
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
