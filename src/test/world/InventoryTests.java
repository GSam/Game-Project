package test.world;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import savefile.SaveUtils;
import static org.junit.Assert.*;
import GUI.Managers.InventoryManager;

import com.jme3.export.xml.XMLExporter;
import com.jme3.math.Vector3f;

import world.Inventory;
import world.Player;
import world.WorldType;
import world.entity.item.Item;
import world.entity.item.miscellaneous.GarmingsNetwork;

/**
 * @author Alex Campbell 300252131
 */
public class InventoryTests extends GameTest {

	private static class TestInvMgr extends InventoryManager {
		public TestInvMgr() {
			super(null, null, null);
		}

		private Item lastItem = null;
		private String lastPos = null;

		@Override
		public boolean addItem(Item item) {
			lastItem = item;
			lastPos = null;
			return true;
		}

		@Override
		public boolean addItemInPos(Item item, String position) {
			lastItem = item;
			lastPos = position;
			return true;
		}
	}

	public InventoryTests() {
		super(WorldType.SP);
	}

	@Before public void before() {start();}
	@After public void after() {stop();}

	Player player;
	Inventory inv;
	TestInvMgr mgr;

	@Override
	protected void onstart() {
		player = new Player();
		world.addEntity(player, Vector3f.ZERO);

		inv = new Inventory(player, 4);
		inv.linkToWorld(world);

		mgr = new TestInvMgr();
		inv.addObserver(mgr);
	}

	@Test
	public void testNothing() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {



				return null;
			}
		});
	}

	@Test
	public void testAddDirectValid() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				assertNotNull(item.getPhysics().getPhysicsSpace());
				assertNotNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				assertNull(mgr.lastItem);

				assertFalse(inv.iterator().hasNext());
				inv.addDirect(item);

				assertNull(mgr.lastItem);

				assertSame(item.getInventory(), inv);
				assertNull(item.getInventoryPosition()); // !!!

				Iterator<Item> it = inv.iterator();
				assertTrue(it.hasNext());
				assertSame(it.next(), item);
				assertFalse(it.hasNext());

				assertNull(item.getPhysics().getPhysicsSpace());
				assertNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				return null;
			}
		});
	}

	@Test
	public void testAddToPositionValid() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				assertNotNull(item.getPhysics().getPhysicsSpace());
				assertNotNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				assertNull(mgr.lastItem);

				assertFalse(inv.iterator().hasNext());
				inv.addToPosition(item, "asdf");

				assertSame(mgr.lastItem, item);
				assertEquals(mgr.lastPos, "asdf");

				assertSame(item.getInventory(), inv);
				assertNull(item.getInventoryPosition()); // !!!

				Iterator<Item> it = inv.iterator();
				assertTrue(it.hasNext());
				assertSame(it.next(), item);
				assertFalse(it.hasNext());

				assertNull(item.getPhysics().getPhysicsSpace());
				assertNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				return null;
			}
		});
	}

	@Test
	public void testAddValid() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				assertNotNull(item.getPhysics().getPhysicsSpace());
				assertNotNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				assertNull(mgr.lastItem);

				assertFalse(inv.iterator().hasNext());
				inv.add(item);

				assertSame(mgr.lastItem, item);
				assertNull(mgr.lastPos);

				assertSame(item.getInventory(), inv);
				assertNull(item.getInventoryPosition()); // !!!

				Iterator<Item> it = inv.iterator();
				assertTrue(it.hasNext());
				assertSame(it.next(), item);
				assertFalse(it.hasNext());

				assertNull(item.getPhysics().getPhysicsSpace());
				assertNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				return null;
			}
		});
	}

	@Test
	public void testAddNull() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				try {
					inv.add(null);
					fail("inv.add(null) should throw NullPointerException");
				} catch(NullPointerException e) {
					assertFalse(inv.iterator().hasNext());
				}

				try {
					inv.addDirect(null);
					fail("inv.addDirect(null) should throw NullPointerException");
				} catch(NullPointerException e) {
					assertFalse(inv.iterator().hasNext());
				}

				try {
					inv.addToPosition(null, "asdf");
					fail("inv.addToPosition(null item) should throw NullPointerException");
				} catch(NullPointerException e) {
					assertFalse(inv.iterator().hasNext());
				}

				try {
					inv.addToPosition(new GarmingsNetwork(), null);
					fail("inv.addToPosition(null position) should throw NullPointerException");
				} catch(NullPointerException e) {
					assertFalse(inv.iterator().hasNext());
				}

				return null;
			}
		});
	}

	@Test
	public void testAddFull() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				for(int k = 0; k < 4; k++) {
					Item item = new GarmingsNetwork();
					world.addEntity(item, Vector3f.ZERO);
					assertTrue(inv.add(item));
				}

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);
				assertFalse(inv.add(item));

				return null;
			}
		});
	}

	@Test
	public void testAddDuplicate() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				inv.add(item);

				Iterator<Item> it = inv.iterator();
				assertTrue(it.hasNext());
				assertSame(it.next(), item);
				assertFalse(it.hasNext());

				//try {
				//	inv.add(item);
				//	fail("duplicate inv.add should throw IllegalArgumentException");
				//} catch(IllegalArgumentException e) {
				//}

				assertFalse(inv.add(item));

				try {
					inv.addDirect(item);
					fail("duplicate inv.addDirect should throw IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				try {
					inv.addToPosition(item, "asdf");
					fail("duplicate inv.addToPosition should throw IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				it = inv.iterator();
				assertTrue(it.hasNext());
				assertSame(it.next(), item);
				assertFalse(it.hasNext());

				return null;
			}
		});
	}

	@Test
	public void testDropNotHeld() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				try {
					inv.dropItem(item);
					fail("inv.dropItem with random item should throw IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				return null;
			}
		});
	}

	@Test
	public void testDropNull() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				try {
					inv.dropItem(null);
					fail("inv.dropItem with null item should throw NullPointerException");
				} catch(NullPointerException e) {
				}

				return null;
			}
		});
	}

	@Test
	public void testRemoveNotHeld() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				try {
					inv.removeItem(item);
					fail("inv.remove with random item should throw IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				return null;
			}
		});
	}

	@Test
	public void testRemoveNull() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				try {
					inv.removeItem(null);
					fail("inv.remove with null item should throw NullPointerException");
				} catch(NullPointerException e) {
				}

				return null;
			}
		});
	}

	@Test
	public void testNonWorldLinked() {
		Inventory nwl = new Inventory(new Player(), 4);

		try {
			nwl.dropItem(new GarmingsNetwork());
			fail("drop before linkToWorld should throw ISE");
		} catch(IllegalStateException e) {
		}

		try {
			nwl.iterator();
			fail("iterator before linkToWorld should throw ISE");
		} catch(IllegalStateException e) {
		}

		try {
			nwl.write(XMLExporter.getInstance());
			fail("write before linkToWorld should throw ISE");
		} catch(IllegalStateException e) {
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		try {
			nwl.removeItem(new GarmingsNetwork());
			fail("remove before linkToWorld should throw ISE");
		} catch(IllegalStateException e) {
		}

		try {
			nwl.addDirect(new GarmingsNetwork());
			fail("addDirect before linkToWorld hould throw ISE");
		} catch(IllegalStateException e) {
		}

		try {
			nwl.addToPosition(new GarmingsNetwork(), "asdf");
			fail("addToPosition before linkToWorld hould throw ISE");
		} catch(IllegalStateException e) {
		}
	}

	@Test
	public void coverEmptyMethods() {
		// these methods do nothing by themselves
		// but increases coverage so 100% is possible
		inv.prePhysicsTick(null, 0);
		inv.physicsTick(null, 0);
	}

	@Test
	public void testDropSameItemTwice() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				inv.add(item);
				inv.dropItem(item);

				try {
					inv.dropItem(item);
					fail("inv.dropItem with duplicate item should throw IllegalArgumentException");
				} catch(IllegalArgumentException e) {
				}

				return null;
			}
		});
	}

	@Test
	public void testDropOne() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				assertNotNull(item.getPhysics().getPhysicsSpace());
				assertNotNull(item.getMesh().getParent());
				inv.add(item);

				assertNull(item.getPhysics().getPhysicsSpace());
				assertNull(item.getMesh().getParent());

				inv.dropItem(item);
				inv.prePhysicsTick(world.getPhysicsSpace(), 0.01f);

				assertNotNull(item.getPhysics().getPhysicsSpace());
				assertNotNull(item.getMesh().getParent());

				return null;
			}
		});
	}

	@Test
	public void testDropTwo() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				inv.getOwner().setLocation(new Vector3f(100, 100, 100));

				Item i1 = new GarmingsNetwork();
				Item i2 = new GarmingsNetwork();
				Item i3 = new GarmingsNetwork();
				world.addEntity(i1, Vector3f.ZERO);
				world.addEntity(i2, Vector3f.ZERO);
				world.addEntity(i3, Vector3f.ZERO);

				inv.add(i1);
				inv.add(i2);
				inv.add(i3);

				assertNull(i1.getPhysics().getPhysicsSpace());
				assertNull(i1.getMesh().getParent());
				assertNull(i2.getPhysics().getPhysicsSpace());
				assertNull(i2.getMesh().getParent());
				assertNull(i3.getPhysics().getPhysicsSpace());
				assertNull(i3.getMesh().getParent());

				inv.dropItem(i1);
				inv.dropItem(i2);
				inv.prePhysicsTick(world.getPhysicsSpace(), 0.01f);

				assertNotNull(i1.getPhysics().getPhysicsSpace());
				assertNotNull(i1.getMesh().getParent());
				assertNotNull(i2.getPhysics().getPhysicsSpace());
				assertNotNull(i2.getMesh().getParent());
				assertNull(i3.getPhysics().getPhysicsSpace());
				assertNull(i3.getMesh().getParent());

				assertFalse(i1.getLocation().equals(Vector3f.ZERO));
				assertFalse(i2.getLocation().equals(Vector3f.ZERO));

				return null;
			}
		});
	}

	@Test
	public void testRemoveValid() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item item = new GarmingsNetwork();
				world.addEntity(item, Vector3f.ZERO);

				assertNotNull(item.getPhysics().getPhysicsSpace());
				assertNotNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				assertFalse(inv.iterator().hasNext());
				inv.add(item);

				assertSame(item.getInventory(), inv);
				assertNull(item.getInventoryPosition()); // !!!

				Iterator<Item> it = inv.iterator();
				assertTrue(it.hasNext());
				assertSame(it.next(), item);
				assertFalse(it.hasNext());

				assertNull(item.getPhysics().getPhysicsSpace());
				assertNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				inv.removeItem(item);

				assertNull(item.getInventory());
				assertNull(item.getInventoryPosition());

				// inv.remove does NOT put the item back in the world
				assertNull(item.getPhysics().getPhysicsSpace());
				assertNull(item.getMesh().getParent());
				assertTrue(world.getEntities().contains(item));

				assertFalse(inv.iterator().hasNext());


				return null;
			}
		});
	}

	@Test
	public void testSaveAndLoad() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Item i1 = new GarmingsNetwork();
				Item i2 = new GarmingsNetwork();
				world.addEntity(i1, Vector3f.ZERO);
				world.addEntity(i2, Vector3f.ZERO);

				inv.add(i1);
				inv.add(i2);

				byte[] bytes = SaveUtils.toBytes(inv);

				inv.removeItem(i1);
				inv.removeItem(i2);
				i1.attachToNode();
				i2.attachToNode();
				i1.addToPhysicsSpace();
				i2.addToPhysicsSpace();

				assertNull(i1.getInventory());
				assertNull(i2.getInventory());

				Inventory inv2 = SaveUtils.fromBytes(bytes, Inventory.class, world.getAssetManager());

				inv2.linkToWorld(world);

				assertSame(inv2, i1.getInventory());
				assertSame(inv2, i2.getInventory());
				assertSame(inv.getOwner(), inv2.getOwner());

				return null;
			}
		});
	}

}
