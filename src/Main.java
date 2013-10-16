import java.io.File;

import com.jme3.math.Vector3f;

import network.ClientMain;
import network.ServerMain;
import GUI.WorldTestMain;
import world.World;
import world.WorldType;

/**
 * Console startup main for the three modes: Single player, server, client
 *
 * @author Garming Sam 300198721
 *
 */
public class Main {
	public static void main(String[] args) {
		String ip = "localhost";
		String port = "6143";
		boolean fast = false;
		WorldType w = WorldType.SP;
		String name = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				String arg = args[i];
				if(arg.equals("-help")) {
					usage();
					System.exit(0);
				} else if(arg.equals("-server")) {
					w = WorldType.SERVER;
				} else if(arg.equals("-client")) {
					w = WorldType.CLIENT;
					if (i + 1 < args.length && !args[i+1].startsWith("-"))
						ip = args[++i];
				} else if (arg.equals("-fast")) {
					fast = true;
				} else if (arg.equals("-port")) {
					if (i + 1 < args.length)
						port = args[++i];
				} else if (arg.equals("-mobs")) {
					try {
						if (i + 1 < args.length)
							World.MAX_MOBS = Integer.parseInt(args[++i]);
					} catch(NumberFormatException e){
						// leave it as it is
					}
				} else if (arg.equals("-name")){
					if (i + 1 < args.length)
						name = args[++i];
				} else if (arg.equals("-clean")) {
					File f = new File("server.sav");
					if(f.exists()) {
						f.setWritable(true);
						f.delete();
					}
				} else if (arg.equals("-test")) {
					World.MAPFILE = "maps/alexMap.txt";
					fast = true;
				}
			}
		}

		switch (w) {
		case SP:
			System.out.println("Single player");
			World.USE_SUPERFAST_RENDERING = fast;
			WorldTestMain.main(null);
			break;
		case SERVER:
			World.USE_SUPERFAST_RENDERING = true;
			System.out.println("SERVER - Port: " + port);
			ServerMain.main(new String[]{port});
			break;
		case CLIENT:
			System.out.println("Client - Address: " + ip + "  Port: " + port);
			World.USE_SUPERFAST_RENDERING = fast;
			ClientMain.main(new String[]{ip, port, name});
			break;
		}

	}

	public static void usage(){
		System.out.println("Usage: -client [<address>] -server -fast -port <port num> -mobs <mob count> -name <name>");
		System.out.println("Client allows an optional arguments for which address and port.");
		System.out.println("Mobs flag allows you to set the max mobs. ");
		System.out.println("Fast flag sets the the rendering type to fast.");
		System.out.println("Name flag allows setting of your name on clients.");
	}
}
