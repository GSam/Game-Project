package network.packets;

import network.interfaces.PacketHandler;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

/**
 * Message for the general updating of each moving entity. Uses an unreliable
 * message and are the most frequent messages sent due to constant updates from
 * both the client and the server.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class MoveMessage extends GameUpdateMessage {
	public int eid;
	public float x;
	public float y;
	public float z;
	public int state;
	public float dirA;
	public float dirB;
	public float dirC;

	public MoveMessage() {
	}

	public MoveMessage(int eid, Vector3f pos, int state, Vector3f dir) {
		super(false);
		this.eid = eid;
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
		this.state = state;
		this.dirA = dir.x;
		this.dirB = dir.y;
		this.dirC = dir.z;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);

	}
}
