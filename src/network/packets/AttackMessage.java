package network.packets;

import network.interfaces.PacketHandler;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * Message sent when a player attacks.
 * 
 * @author Garming Sam 300198721
 *
 */
@Serializable
public class AttackMessage extends GameUpdateMessage{
	public int eid;
	public float x;
	public float y;
	public float z;
	public int damage;
	public float dirX;
	public float dirY;
	public float dirZ;
	
	public AttackMessage() {
	}
	
	public AttackMessage(int eid, Vector3f loc, Vector3f dir) {
		super(true);
		this.eid = eid;
		this.x = loc.x;
		this.y = loc.y;
		this.z = loc.z;
		this.dirX = dir.x;
		this.dirY = dir.y;
		this.dirZ = dir.z;
	}
	
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}
}
