package network.packets;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

@Serializable
public class PlayerSpeedMessage extends GameUpdateMessage {
	public float speed;
	public float spx, spy, spz;

	public PlayerSpeedMessage() {
	}

	public PlayerSpeedMessage(float speed, Vector3f spawnPt){
		super(true);
		this.speed = speed;
		this.spx = spawnPt.x;
		this.spy = spawnPt.y;
		this.spz = spawnPt.z;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);

	}

}
