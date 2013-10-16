package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message to be sent when an entity is removed. The server is in charge of
 * removing entities, so it is the only side which makes these.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class RemoveEntityMessage extends GameUpdateMessage {
	public int eid;
	
	public RemoveEntityMessage() {
	}
	
	public RemoveEntityMessage(int eid){
		super(true);
		this.eid = eid;
		
	}
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
