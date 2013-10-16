package world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import world.entity.item.Item;
import world.entity.item.consumable.ConsumableFactory;
import world.entity.item.equippable.EquippableFactory;
import world.entity.item.miscellaneous.Key;
import world.entity.item.miscellaneous.KeyFactory;
import world.entity.item.miscellaneous.TreasureFactory;
import world.entity.item.torch.TorchFactory;
import world.entity.staticentity.AbstractStaticLockedActivator;
import world.entity.staticentity.ContainerFactory;
import world.entity.staticentity.DoorFactory;
import world.entity.staticentity.OutpostGate;
import world.entity.staticentity.WorldObject;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * A class to load up the map and return a list of tuples that contain a entity and a location
 * to be put in the scene
 * the options are:
 * spawn, (x,y,z)   //this is the player spawn location
 * win, (x1,y1,z1), (x2,y2,z2)   //this is where the game win zone it
 * asset, filepath, (x,y,z), (scalex, scaley, scale), rotationy, cullFrontOnly
 * file, filepath, (x,y,z), rotationy
 * class, fully.justified.class.and.package.name, (x,y,z)
 * torch, torchType, (x,y,z)
 * equip, equipType, (x,y,z)
 * consumable, consumableType, (x,y,z)
 * treasure, treasureType, (x,y,z)
 * gizmo, fully.justified.class.and.package.name, (x,y,z)    //same as class, just for extensibilty purposes
 * key, keyType, (x,y,z), I.D1, I.D2, I.D3..... //where ID1, ID2.... are the doors/containers it opens. optionally, the last id can be "stop"
 * door, doorType, , (x,y,z), (scalex, scaley, scalez), rotationy, I.D
 * container, containerType, (x,y,z), (scalex, scaley, scalez), rotationy, I.D, item1, item2.... //where item1, item2 are the items that are contained within
 *          //where an item is: gizmo|consumable|weapon|equip|torch|treasure and are loaded like above:
 *          //e.g. torch, torchType, (x,y,z)
 *
 * @author Scott
 */
public class MapLoader{

    private HashMap<Key, ArrayList<Integer>> keys = new HashMap<Key, ArrayList<Integer>>();
    private HashMap<Integer, AbstractStaticLockedActivator> lockables = new HashMap<Integer, AbstractStaticLockedActivator>();
    private boolean first = true;

    private Vector3f spawnPoint = null;
    private BoundingBox winZone = new BoundingBox(Vector3f.UNIT_X, Vector3f.UNIT_XYZ);
    private float playerSpeed = 2;
    private boolean mapWantsFancyVisualEffects = false;

    /**
     * loads a file and returns all the entities that can be taken from that file
     * applying all rotations and adds all keys mentioned to the doors and containers
     * that they specify
     * @param filename
     *  The filename to load up
     * @return
     *  All the entities found
     */
    @SuppressWarnings("resource")
	public Collection<Tuple> loadMap(String filename){
        Scanner scan = null;
        boolean firstLocal = first;
        first = false;

        ArrayList<Tuple> entities = new ArrayList<Tuple>();

        try{
            scan = new Scanner(new File(filename)).useDelimiter("\\Z");
            String[] lines = scan.next().split("\n");

            for(String line : lines){
                if(line.startsWith("<") || line.startsWith("//")){
                    continue;
                }
                Collection<Tuple> temp = readLine(line);

                if(!(temp == null || temp.isEmpty())){
                    entities.addAll(temp);
                }


            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } finally{
            if(scan != null){
                scan.close();
            }
        }


        if(firstLocal){ // the topmost call of this method
            for(Map.Entry<Key, ArrayList<Integer>> e : keys.entrySet()){

                for(Integer i : e.getValue()){
                    if(lockables.get(i) != null){
                        lockables.get(i).addKey(e.getKey()); // pun unintentional
                    }
                }
            }
        }

        return entities;
    }

    /**
     * Reads a single line of the text file and passes it on to helper methods that return the appropriate entities.
     * @param line
     *      The line to read
     * @return
     *      All the entities found that that line is refering to
     */
    private Collection<Tuple> readLine(String line){
        Scanner s = new Scanner(line).useDelimiter(",");
        String type = s.next();
        ArrayList<Tuple> entities = new ArrayList<Tuple>();

        if(type.equals("spawn")){
        	spawnPoint = readVector(s);
        } else if(type.equals("win")) {
        	winZone = new BoundingBox(readVector(s), readVector(s));
        } else if(type.equals("plspeed")) {
        	playerSpeed = Float.parseFloat(s.next());
        } else if(type.equals("asset")){
            entities.add(loadAsset(s));
        } else if(type.equals("file")){ // recursively parses another file
            entities.addAll(readFile(s));
        } else if(type.equals("class")){
            entities.add(loadClass(s));
        } else if(type.equals("key")){
            entities.add(loadKey(s));
        } else if(type.equals("door")){ // need a special type for keys and doors due to them being relient on each other
            entities.add(loadDoor(s));
        } else if(type.equals("container")){
            entities.add(loadContainer(s));
        } else if(type.equals("consumable")){
            entities.add(loadConsumable(s));
        } else if(type.equals("torch")){
            entities.add(loadTorch(s));
        } else if(type.equals("equip")){
            entities.add(loadEquip(s));
        } else if(type.equals("gizmo")){
            entities.add(loadGizmo(s));
        } else if(type.equals("treasure")){
            entities.add(loadTreasure(s));
        }else if(type.equals("endGate")){
            Vector3f pos = readVector(s);
            Vector3f scale = readVector(s);
            float rotation = FastMath.DEG_TO_RAD*s.nextFloat();
            entities.add(new Tuple(new OutpostGate(scale, rotation), pos));
        }

        s.close();
        return entities;
    }

    

    /**
     * Loads a single asset as a WorldObject and returns it
     * @param s
     *      The scanner to get the details about the WorldObject from
     * @return
     *      a Tuple containing the entity and it's location found
     */
    private static Tuple loadAsset(Scanner s){
        String filename = s.next().trim();
        Vector3f pos = readVector(s);
        Vector3f scale = readVector(s);
        float rotation = FastMath.DEG_TO_RAD * s.nextFloat();
        boolean cull = Boolean.parseBoolean(s.next().trim());

        WorldObject o = new WorldObject(filename, scale, 0, rotation, cull);
        return new Tuple(o, pos);
    }

    /**
     * Reads a nest file within this text file and returns all the entities that were
     * found in that text file whilst still applying all rotations and scalings to it
     * @param s
     *  The scanner to read the details about the file from
     * @return
     *      A Collection of Tuples that were found in the file
     */
    private Collection<Tuple> readFile(Scanner s){
        String filename = s.next().trim();

        Vector3f posOffset = readVector(s);
        float rotation = FastMath.DEG_TO_RAD * s.nextFloat();

        Collection<Tuple> fileContents = loadMap(filename);
        for(Tuple t : fileContents){ // adjust each of them
            t.location.addLocal(posOffset);

            if(t.entity instanceof WorldObject){
                ((WorldObject) t.entity).setAngle(((WorldObject) t.entity).getAngle() + rotation);
            }/*else if(t.entity instanceof AbstractDoor){
                ((AbstractDoor)t.entity).setAngle(((AbstractDoor) t.entity).getAngle()+rotation);
            }*/
        }

        return fileContents;
    }

    /**
     * Loads a key from the given scanner, along with all the ID's of
     * the doors it shoould be attached to
     * @param s
     *      THe scanner to read the information from
     * @return
     *      A Tuple containing the entity and the location of the object
     */
    private Tuple loadKey(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);
        Entity e = null;


        String name = null;
        if(s.hasNext("name=")) {
        	s.next();
        	Pattern oldDelim = s.delimiter();
        	s.useDelimiter(">");
        	name = s.next();
        	name = name.substring(name.indexOf('<')+1);
        	s.useDelimiter(oldDelim);
        	s.next(); // reads the >
        }

        for(KeyFactory.Instance i : KeyFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = KeyFactory.getKeyInstance(i, name);
            }
        }

        if(e == null){
            throw new IllegalStateException("You cannot create a consumable of that type");
        }

        ArrayList<Integer> doorIds = new ArrayList<Integer>();

        while(s.hasNext()){
        	String ids = s.next();
        	if(ids.equals("stop"))
        		break;
        	else
        		doorIds.add(Integer.parseInt(ids));
        }

        keys.put((Key) e, doorIds);

        return new Tuple(e, position);
    }

    /**
     * Loads a door from the scanner and stores it in the, indexed by it's ID in a
     * map for the keys to figure out what to attach themselves to
     * @param s
     *      The scanner to read the information from
     * @return
     *      A tuple containin the door as an entitiy and the location of it
     */
    private Tuple loadDoor(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);
        Vector3f scale = readVector(s);
        float rotation = FastMath.DEG_TO_RAD*s.nextFloat();
        int id = s.nextInt();

        AbstractStaticLockedActivator e = null;

        for(DoorFactory.Instance i : DoorFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = DoorFactory.getDoorInstance(i, scale, rotation);
            }
        }

        if(e == null){
            throw new IllegalStateException("You cannot create a door of that type");
        }


        lockables.put(id, (AbstractStaticLockedActivator)e);

        return new Tuple(e, position);
    }

    /**
     * Loads a container from the scanner and puts it in a map
     * for the keys to attach themselves to
     * @param s
     *  The scanner to load themselves from
     * @return
     *  A Tuple containing the entity loaded and it's location
     */
    private Tuple loadContainer(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);
        Vector3f scale = readVector(s);
        float rotation = FastMath.DEG_TO_RAD*s.nextFloat();
        int id = s.nextInt();

        Container e = null;

        for(ContainerFactory.Instance i : ContainerFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = ContainerFactory.getContainerInstance(i, scale, rotation);
            }
        }



        if(e == null){
            throw new IllegalStateException("You cannot create a container of that type");
        }

        while(s.hasNext()){
            String itemType = s.next();
            Entity item = null;
            if(itemType.equalsIgnoreCase("consumable")){
                item = loadConsumable(s).entity;
            }else if(itemType.equals("torch")){
                item = loadTorch(s).entity;
            }else if(itemType.equals("gizmo")){
                item = loadClass(s).entity;
            }else if(itemType.equals("equip")){
                item = loadEquip(s).entity;
            }else if(itemType.equals("weapon")){
                item = loadClass(s).entity;
            }else if(itemType.equals("key")){
                item = loadKey(s).entity;
            }else if(itemType.equals("treasure")){
                item = loadTreasure(s).entity;
            }else if(itemType.equals("class")){
                item = loadClass(s).entity;
            }else{
                throw new IllegalStateException("You cannot add an item of that type to the chest");
            }

            if(item instanceof Item){
                e.getContainerInventory().add((Item)item);
            }else{
                throw new IllegalStateException("That thing you are trying to add to the container is not an item");
            }
        }


        lockables.put(id, (AbstractStaticLockedActivator)e);

        return new Tuple((Entity)e, position);   //there are no instances of container that aren't also an Entity
    }

    /**
     * Loads a class from a string that is it's fully qualified package and Class name
     *
     * @param s
     *  the scanner to load the data from
     * @return
     *  a tuple that contains the entity and it's location
     */
    private static Tuple loadClass(Scanner s){
        String className = s.next().trim();
        Vector3f position = readVector(s);

        Object ent = null;

        try{
            ent = Class.forName(className).newInstance();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            throw new IllegalStateException("Shit, you tried to create a item that was private ");
        } catch (ClassNotFoundException e){
            throw new IllegalStateException("Shit, you tried to create a item that was not an exsistent " + className);
        }

        if(ent == null || !(ent instanceof Entity)){
            throw new IllegalStateException("Shit, you tried to create a item that was not an entity " + s.next() + " " + ent.getClass().getName());
        }

        return new Tuple((Entity) ent, position);
    }

    /**
     * Loads a consumable
     *
     * @param s
     *  the scanner to load the data from
     * @return
     *  a tuple that contains the entity and it's location
     */
    private static Tuple loadConsumable(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);
        Entity e = null;

        for(ConsumableFactory.Instance i : ConsumableFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = ConsumableFactory.getConsumableInstance(i);
            }
        }

        if(e == null){
            throw new IllegalStateException("You cannot create a consumable of that type");
        }

        return new Tuple(e, position);
    }

    /**
     * Loads a torch
     *
     * @param s
     *  the scanner to load the data from
     * @return
     *  a tuple that contains the entity and it's location
     */
    private static Tuple loadTorch(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);

        Entity e = null;

        for(TorchFactory.Instance i : TorchFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = TorchFactory.getTorchInstance(i);
            }
        }

        if(e == null){
            throw new IllegalStateException("You cannot create a Torch of that type");
        }
        return new Tuple(e, position);
    }

    /**
     * Loads a piece of equipment
     *
     * @param s
     *  the scanner to load the data from
     * @return
     *  a tuple that contains the entity and it's location
     */
    private static Tuple loadEquip(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);

        Entity e = null;

        for(EquippableFactory.Instance i : EquippableFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = EquippableFactory.getEquippableInstance(i);
            }
        }

        if(e == null){
            throw new IllegalStateException("You cannot create a equippable of that type");
        }
        return new Tuple(e, position);
    }

    /**
     * Loads a piece of treasure
     *
     * @param s
     *  the scanner to load the data from
     * @return
     *  a tuple that contains the entity and it's location
     */
    private static Tuple loadTreasure(Scanner s){
        String type = s.next().trim();
        Vector3f position = readVector(s);

        Entity e = null;

        for(TreasureFactory.Instance i : TreasureFactory.Instance.values()){
            if(type.equalsIgnoreCase(i.toString())){
                e = TreasureFactory.getTreasureInstance(i);
            }
        }

        if(e == null){
            throw new IllegalStateException("You cannot create a equippable of that type");
        }
        return new Tuple(e, position);
    }

    /**
     * @deprecated
     * Loads a gizmo, but currently just diverts to the load class method as there
     * is no special case for a gizmo right now
     *
     * @param s
     *  the scanner to load the data from
     * @return
     *  a tuple that contains the entity and it's location
     */
    @Deprecated
	private static Tuple loadGizmo(Scanner s){

        return loadClass(s);
    }

    /**
     * parses a vector from the given scanner
     *
     * @param s
     *            the scanner to parse the vector from
     * @return the vector, if found, otherwise it'll throw something, at you
     */
    private static Vector3f readVector(Scanner s){
        s.useDelimiter("[\\s,]+|(?=[(),])|(?<=[(),])");

        gobble("\\(", s);
        float x = s.nextFloat();
        float y = s.nextFloat();
        float z = s.nextFloat();
        gobble("\\)", s);

        return new Vector3f(x, y, z);
    }

    /**
     * Gobbles the given string pattern from the scanner, or throws something at you if it can't find it
     *
     * @param pattern
     *            the pattern to gobble
     * @param scan
     *            the scanner to gobble from
     */
    private static void gobble(String pattern, Scanner scan){
        if(!scan.hasNext(pattern)){
            for(int i = 0; i < 5; i++){
            }
            throw new IllegalArgumentException("Cannot parse the file at " + pattern);
        }

        scan.next(pattern);
    }

    /**
     * A Class to represent a tuple of an entity and a location
     * what the map loader returns in large amounts
     * @author scott
     *
     */
    public static class Tuple{
        public Entity entity;
        public Vector3f location;

        public Tuple(Entity entity, Vector3f location){
            this.entity = entity;
            this.location = location;
        }
    }

	public Vector3f getSpawnPoint() {
		if(spawnPoint == null)
			throw new RuntimeException("spawn point not set in map file");
		return spawnPoint;
	}

	public BoundingBox getWinZone() {
		return winZone;
	}

	public float getPlayerSpeed() {
		return playerSpeed;
	}


}
