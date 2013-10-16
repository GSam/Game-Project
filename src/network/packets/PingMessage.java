package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message for testing ping and calculating time offset between the client and
 * the server. A ping packet maintains a count of how many more times it needs
 * to be sent.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class PingMessage extends GameUpdateMessage {
	public long sentTime;
	public int count = 4;
	public int id;
	
	public PingMessage() {
	}
	
	public PingMessage(int id) {
		super(true);
		this.id = id;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}
}
