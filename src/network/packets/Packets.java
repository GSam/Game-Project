package network.packets;


import com.jme3.network.serializing.Serializer;

/**
 * Packet utility class for registering the serializer and keeping track of all
 * the possible messages.
 *
 * @author Garming Sam 300198721
 *
 */
public class Packets {
	/**
	 * List of all the packet classes
	 */
	public static Class<?>[] classes = { AttackMessage.class,
			ChatMessage.class, GameUpdateMessage.class, EffectMessage.class,
			MoveMessage.class, PlayerJoinedMessage.class, PingMessage.class,
			ItemTransferMessage.class, AddEntityMessage.class,
			DayNightMessage.class, RemoveEntityMessage.class,
			ChestAccessMessage.class, InventoryTransferMessage.class,
			ServerSaveMessage.class, EquipItemMessage.class,
			AddEntityFinishMessage.class, RightClickMessage.class,
			OnActivateMessage.class, PlayerSetupMessage.class,
			GameWonMessage.class, PlayerSpeedMessage.class };

	/**
	 * Registers all the message with the inbuilt serializer.
	 */
	public static void register() {
		Serializer.registerClasses(classes);
	}
}
