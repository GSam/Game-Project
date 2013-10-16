package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message for sending when an item is activated.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class OnActivateMessage extends GameUpdateMessage {
	public int eid;
	public int playerID;
	
	public OnActivateMessage() {
	}
	
	public OnActivateMessage(int eid, int playerID){
		super(true);
		this.eid = eid;
		this.playerID = playerID;
	}
	
	@Override
	public void accept(PacketHandler visitor) {	
		visitor.handleMessage(this);
	}

}
