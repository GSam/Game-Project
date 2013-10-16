package network.packets;

import savefile.SaveUtils;
import world.Entity;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to be broadcast to add an entity.
 * 
 * @author Garming Sam 300198721
 *
 */
@Serializable
public class AddEntityMessage extends GameUpdateMessage{
	public int eid;
	public float x;
	public float y;
	public float z;
	public byte[] data;

	public AddEntityMessage() {
	}

	public AddEntityMessage(int eid, Entity e, Vector3f v) {
		super(true);
		this.eid = eid;
		if (v == null) {
			throw new NullPointerException();
		} else {
			this.x = v.x;
			this.y = v.y;
			this.z = v.z;
		}
		data = SaveUtils.toBytes(e);
	}

	public AddEntityMessage(Entity e) {
		this(e.getEntityID(), e, e.getLocation());
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);

	}
}
