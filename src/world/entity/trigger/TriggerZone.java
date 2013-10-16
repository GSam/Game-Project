package world.entity.trigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import world.AbstractEntity;
import world.Player;
import world.WorldType;

/**
 * An area of space which triggers
 * @author Alex Campbell 300252131
 */
public class TriggerZone extends AbstractEntity {
	public TriggerZone() {}

	private GhostControl physics;

	private CollisionShape colshape;
	private ArrayList<TriggerAction> actions;
	private boolean triggered;

	public TriggerZone(Spatial node) {
		colshape = CollisionShapeFactory.createBoxShape(node);
		actions = new ArrayList<TriggerAction>();
		triggered = false;
	}

	@Override
	public void read(JmeImporter arg0) throws IOException {
		super.read(arg0);
		colshape = (CollisionShape)arg0.getCapsule(this).readSavable("colshape", null);
		actions = (ArrayList<TriggerAction>)arg0.getCapsule(this).readSavableArrayList("actions", null);
		triggered = arg0.getCapsule(this).readBoolean("triggered", false);
	}

	@Override
	public void write(JmeExporter arg0) throws IOException {
		super.write(arg0);
		arg0.getCapsule(this).write(colshape, "colshape", null);
		arg0.getCapsule(this).writeSavableArrayList(actions, "actions", null);
		arg0.getCapsule(this).write(triggered, "triggered", false);
	}

	@Override
	public void getPreloadAssets(Set<String> assets) {
	}

	@Override
	protected void makeMesh(AssetManager assetManager) {
		geometry = new Node();
	}

	@Override
	protected void makePhysics(PhysicsSpace physicsSpace) {
		physics = new GhostControl(colshape);

        physics.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_07);
        physics.removeCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        physics.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_07);

        geometry.addControl(physics);
        physicsSpace.add(physics);
	}

	@Override
	public void changeLocation(float dx, float dy, float dz) {
		changeLocation(new Vector3f(dx, dy, dz));
	}

	@Override
	public void changeLocation(Vector3f change) {
		setLocation(getLocation().add(change));
	}

	@Override
	protected void destroyPhysics(PhysicsSpace physicsSpace) {
		geometry.removeControl(physics);
		physicsSpace.remove(physics);
	}

	@Override
	public PhysicsControl getPhysics() {
		return physics;
	}

	@Override
	public Vector3f getLocation() {
		return physics.getPhysicsLocation();
	}

	@Override
	public Vector3f getDirection() {
		return new Vector3f(0, 0, 0);
	}

	@Override
	public void setLocation(Vector3f location) {
		physics.setPhysicsLocation(location);
	}

	@Override
	protected Node getNodeToAttach() {
		return world.getNode();
	}

	@Override
	public void update(float tpf) {

	}

	public void trigger(Player p) {
		if(!triggered && p.getWorld().getWorldType() != WorldType.CLIENT) {
			triggered = true;
			for(TriggerAction a : actions)
				a.trigger(p);
			System.out.println("Triggered "+this+" by "+p+"!");
		}
	}

	public void addAction(TriggerAction a) {
		actions.add(a);
	}


}
