package network.packets;


import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to keep the clients updated on the current day/night time.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class DayNightMessage extends GameUpdateMessage {
	public float time;

	public DayNightMessage() {

	}

	public DayNightMessage(float time) {
		super(true);
		this.time = time;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);

	}
}
