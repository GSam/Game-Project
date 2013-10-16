package network.packets;

import com.jme3.network.serializing.Serializable;

import network.interfaces.PacketHandler;

/**
 * Message for transferring items between two inventories.
 * 
 * @author Garming Sam 300198721
 * 
 */
@Serializable
public class InventoryTransferMessage extends GameUpdateMessage {
	public int idFrom;
	public int idTo;
	public int itemID;

	public InventoryTransferMessage() {

	}

	public InventoryTransferMessage(int idFrom, int idTo, int itemID){
		super(true);
		this.idFrom = idFrom;
		this.idTo = idTo;
		this.itemID = itemID;
	}

	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);
	}

}
