package test.world;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import savefile.SaveUtils;

import com.jme3.math.Vector3f;

import world.PlayerEquipment;
import world.WorldType;
import world.entity.item.Item;
import world.entity.item.equippable.EquipType;
import world.entity.item.equippable.Equippable;
import world.entity.item.equippable.EquippableFactory;
import world.entity.item.equippable.SimpleGun;
import world.entity.item.equippable.EquippableFactory.Instance;
import static org.junit.Assert.*;

/**
 * @author Alex Campbell 300252131
 */
public class PlayerEquipmentTests extends GameTest {
	
	public PlayerEquipmentTests() {
		super(WorldType.SP);
	}
	
	@After public void after() {stop();}
	@Before public void before() {start();}
	
	PlayerEquipment e;
	Item head1, head2, legs, weapon;
	
	@Override
	protected void onstart() {
		e = new PlayerEquipment();
		e.linkToWorld(world);
		head1 = EquippableFactory.getEquippableInstance(Instance.HEAD_LOW);
		head2 = EquippableFactory.getEquippableInstance(Instance.HEAD_HIGH);
		legs = EquippableFactory.getEquippableInstance(Instance.LEGS_LOW);
		weapon = new SimpleGun();
		world.addEntity(head1, Vector3f.ZERO);
		world.addEntity(head2, Vector3f.ZERO);
		world.addEntity(legs, Vector3f.ZERO);
		world.addEntity(weapon, Vector3f.ZERO);
	}
	
	@Test
	public void testEquipNull() {
		try {
			e.equip(null, EquipType.HEAD);
			fail("equip(null ent) should throw NPE");
		} catch(NullPointerException e) {
		}
		
		try {
			e.equip(head1, null);
			fail("equip(null type) should throw NPE");
		} catch(NullPointerException e) {
		}
	}
	
	@Test
	public void testUnequipNull() {
		try {
			e.unequip(null);
			fail("unequip(null ent) should throw NPE");
		} catch(NullPointerException e) {
		}
	}
	
	@Test
	public void testEquipWrongSlot() {
		try {
			e.equip(head1, EquipType.LEGS);
			fail("equip(helmet, legs slot) should throw IAE");
		} catch(IllegalArgumentException e) {
			assertNull(this.e.getEquipped(EquipType.LEGS));
			assertNull(this.e.getEquipped(EquipType.HEAD));
			assertFalse(this.e.iterator().hasNext());
		}
	}
	
	@Test
	public void testEquipping() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertNull(e.getEquipped(EquipType.HEAD));
				assertNull(e.getEquipped(EquipType.LEGS));
				
				assertNull(e.equip(head1, EquipType.HEAD));
				assertSame(head1, e.equip(head2, EquipType.HEAD));
				assertNull(e.equip(legs, EquipType.LEGS));
				
				assertSame(head2, e.getEquipped(EquipType.HEAD));
				assertSame(legs, e.getEquipped(EquipType.LEGS));
				
				Set<Item> expected = new HashSet<Item>();
				Set<Equippable> actual = new HashSet<Equippable>();
				for(Equippable a : e)
					actual.add(a);
				expected.add(head2);
				expected.add(legs);
				assertEquals(expected, actual);
				
				assertFalse(e.unequip(head1));
				assertTrue(e.unequip(head2));
				assertFalse(e.unequip(head2));
				
				assertNull(e.getEquipped(EquipType.HEAD));
				assertSame(legs, e.getEquipped(EquipType.LEGS));
				
				assertTrue(e.unequip(legs));
				assertFalse(e.unequip(legs));
				
				assertNull(e.getEquipped(EquipType.HEAD));
				assertNull(e.getEquipped(EquipType.LEGS));
				
				return null;
			}
		});
	}
	
	@Test
	public void testGetWeapon() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertNull(e.getWeapon());
				e.equip(head1, EquipType.HEAD);
				assertNull(e.getWeapon());
				e.equip(legs, EquipType.LEGS);
				assertNull(e.getWeapon());
				e.equip(weapon, EquipType.WEAPON);
				assertSame(weapon, e.getWeapon());
				e.unequip(head1);
				assertSame(weapon, e.getWeapon());
				e.unequip(weapon);
				assertNull(e.getWeapon());
				e.unequip(legs);
				assertNull(e.getWeapon());
				
				return null;
			}
		});
	}
	
	@Test
	public void testSaveAndLoad() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertNull(e.getEquipped(EquipType.HEAD));
				assertNull(e.getEquipped(EquipType.LEGS));
				
				assertNull(e.equip(head1, EquipType.HEAD));
				assertSame(head1, e.equip(head2, EquipType.HEAD));
				assertNull(e.equip(legs, EquipType.LEGS));
				
				assertSame(head2, e.getEquipped(EquipType.HEAD));
				assertSame(legs, e.getEquipped(EquipType.LEGS));
				
				e = SaveUtils.fromBytes(SaveUtils.toBytes(e), PlayerEquipment.class, world.getAssetManager());
				e.linkToWorld(world);
				
				assertFalse(e.unequip(head1));
				assertTrue(e.unequip(head2));
				assertFalse(e.unequip(head2));
				
				assertNull(e.getEquipped(EquipType.HEAD));
				assertSame(legs, e.getEquipped(EquipType.LEGS));
				
				assertTrue(e.unequip(legs));
				assertFalse(e.unequip(legs));
				
				assertNull(e.getEquipped(EquipType.HEAD));
				assertNull(e.getEquipped(EquipType.LEGS));
				
				return null;
			}
		});
	}
	
	@Test
	public void testSaveAndLoadWithRemovedEntity() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				assertNull(e.getEquipped(EquipType.HEAD));
				assertNull(e.getEquipped(EquipType.LEGS));
				
				assertNull(e.equip(head1, EquipType.HEAD));
				assertSame(head1, e.equip(head2, EquipType.HEAD));
				assertNull(e.equip(legs, EquipType.LEGS));
				
				assertSame(head2, e.getEquipped(EquipType.HEAD));
				assertSame(legs, e.getEquipped(EquipType.LEGS));
				
				byte[] bytes = SaveUtils.toBytes(e);
				
				world.removeEntity(legs);
				
				PlayerEquipment loaded = SaveUtils.fromBytes(bytes, PlayerEquipment.class, world.getAssetManager());
				try {
					loaded.linkToWorld(world);
					fail("Linking when item not in world should fail");
				} catch(RuntimeException e) {
				}
				return null;
			}
		});
	}
}
