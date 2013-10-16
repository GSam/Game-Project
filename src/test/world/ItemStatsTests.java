package test.world;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import savefile.SaveUtils;
import static org.junit.Assert.*;
import world.StatModification;
import world.entity.item.Stat;

/**
 * @author Alex Campbell 300252131
 */
public class ItemStatsTests {
	@Test
	public void testGetStats() {
		StatModification s = new StatModification(new Stat[] {Stat.HEALTH, Stat.DAMAGE}, new float[] {1, 3});
		Map<Stat, Float> expected = new HashMap<Stat, Float>();
		expected.put(Stat.HEALTH, 1f);
		expected.put(Stat.DAMAGE, 3f);
		assertEquals(expected, s.getStats());
	}
	
	@Test
	public void testConstructDiffLengths() {
		try {
			new StatModification(new Stat[] {}, new float[] {1});
			fail("constructor with different array lengths should throw IAE");
		} catch(IllegalArgumentException e) {
		}
		
		try {
			new StatModification(new Stat[] {Stat.HEALTH}, new float[] {});
			fail("constructor with different array lengths should throw IAE");
		} catch(IllegalArgumentException e) {
		}
	}
	
	@Test
	public void testConstructNullArgument() {
		try {
			new StatModification(new Stat[] {}, null);
			fail("constructor with null argument should throw NPE");
		} catch(NullPointerException e) {
		}
		
		try {
			new StatModification(null, new float[] {1});
			fail("constructor with null argument should throw NPE");
		} catch(NullPointerException e) {
		}
	}
	
	@Test
	public void testConstructNullElement() {
		try {
			new StatModification(new Stat[] {null}, new float[] {1});
			fail("constructor with null array element should throw NPE");
		} catch(NullPointerException e) {
		}
		
		try {
			new StatModification(new Stat[] {Stat.DAMAGE, null}, new float[] {1, 2});
			fail("constructor with null array element should throw NPE");
		} catch(NullPointerException e) {
		}
		
		try {
			new StatModification(new Stat[] {null, Stat.DAMAGE}, new float[] {1, 2});
			fail("constructor with null array element should throw NPE");
		} catch(NullPointerException e) {
		}
		
		try {
			new StatModification(new Stat[] {Stat.DAMAGE, null, Stat.DAMAGE}, new float[] {1, 2, 3});
			fail("constructor with null array element should throw NPE");
		} catch(NullPointerException e) {
		}
	}
	
	@Test
	public void testConstructValid() {
		new StatModification(new Stat[0], new float[0]);
		new StatModification(new Stat[] {Stat.HEALTH}, new float[] {1});
		new StatModification(new Stat[] {Stat.DAMAGE, Stat.HEALTH, Stat.MAXENERGY}, new float[] {1, 2, 3});
	}
	
	@Test
	public void testNullaryConstructor() {
		assertEquals(new HashMap<Stat, Float>(), new StatModification().getStats());
		assertEquals(new HashMap<String, String>(), new StatModification().statMap());
		assertFalse(new StatModification().iterator().hasNext());
	}
	
	@Test
	public void testSaveAndLoad() {
		StatModification s = new StatModification(new Stat[] {Stat.HEALTH, Stat.DAMAGE}, new float[] {1, 3});
		Map<Stat, Float> expected = new HashMap<Stat, Float>();
		expected.put(Stat.HEALTH, 1f);
		expected.put(Stat.DAMAGE, 3f);
		
		s = SaveUtils.fromBytes(SaveUtils.toBytes(s), StatModification.class, null);
		
		assertEquals(expected, s.getStats());
	}
	
	@Test
	public void testStatMap() {
		StatModification s = new StatModification(new Stat[] {Stat.HEALTH, Stat.DAMAGE}, new float[] {1, 3});
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("HEALTH", new Float(1).toString());
		expected.put("DAMAGE", new Float(3).toString());
		assertEquals(expected, s.statMap());
	}
}
