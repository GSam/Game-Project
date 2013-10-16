package network.packets;

import network.interfaces.PacketHandler;

import com.jme3.network.serializing.Serializable;

/**
 * Message to be sent when a player joins the game. This message is usually
 * accompanied by the sending of all the entity data.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class PlayerJoinedMessage extends GameUpdateMessage {
	public int eid;
	public int connection;
	
	public PlayerJoinedMessage(int eid, int connection) {
		super(true);
		this.eid = eid;
		this.connection = connection;
	}

	public PlayerJoinedMessage() {
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}
}
