package network.packets;

import network.interfaces.PacketHandler;

import com.jme3.network.serializing.Serializable;

/**
 * Basic chat message with some text, a source and a destination.
 *
 * @author Garming Sam 300198721
 *
 */
@Serializable
public class ChatMessage extends GameUpdateMessage {
	public String text;
	public String source;
	public int id;

	public ChatMessage() {
	}

	public ChatMessage(String source, String text, int id) {
		super(true);
		this.text = text;
		this.source = source;
		this.id = id;
	}
	@Override
	public void accept(PacketHandler visitor) {
		visitor.handleMessage(this);

	}
}
