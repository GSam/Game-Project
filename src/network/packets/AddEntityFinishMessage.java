package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to be sent and received by the server after the client has added all
 * entities upon joining.
 * 
 * @author Garming Sam 300198721
 * @author Alex Campbell 300252131
 * 
 */
@Serializable
public class AddEntityFinishMessage extends GameUpdateMessage {
	public AddEntityFinishMessage() {
		super(true);
	}
	
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}
}
