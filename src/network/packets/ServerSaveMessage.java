package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message for informing the server to save.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class ServerSaveMessage extends GameUpdateMessage{

	public ServerSaveMessage() {
		super(true);
	}
	
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
