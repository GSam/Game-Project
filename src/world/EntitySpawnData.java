package world;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

/**
 * Stores an entity, its location and its ID.
 * An array of these is saved, as entities are not responsible for saving their own location or ID.
 * 
 * @author Alex Campbell 300252131
 */
public class EntitySpawnData implements Savable {
	Entity e;
	Vector3f location;
	int id;

	public EntitySpawnData() {}

	public EntitySpawnData(Entity e) {
		this.e = e;
		this.location = e.getLocation();
		this.id = e.getEntityID();
	}

	public EntitySpawnData(Entity e, Vector3f loc, int eid) {
		this.e = e;
		this.location = loc;
		this.id = eid;
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		e = (Entity)ic.readSavable("e", null);
		location = (Vector3f)ic.readSavable("loc", null);
		id = ic.readInt("id", 0);
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(e, "e", null);
		oc.write(location, "loc", null);
		oc.write(id, "id", 0);
	}

	@Override
	public String toString() {
		return e+"@"+location;
	}
}