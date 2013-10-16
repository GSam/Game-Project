package test.network;

import static org.junit.Assert.*;

import network.PlayerData;

import org.junit.Test;

import savefile.SaveUtils;

/**
 * Test some player data usage.
 * 
 * @author Garming Sam 300198721
 * 
 */
public class PlayerDataTest {
	@Test
	public void testAdd1() {
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		assertTrue(data.containsName("Bob"));
		assertEquals(data.getID("Bob"), 3);
	}
	
	@Test
	public void testAdd2() {
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		assertTrue(data.containsName("Bob"));
		assertEquals(data.getID("Bob"), 3);
		data.addData("Bill", 4);
		assertTrue(data.containsName("Bob"));
		assertEquals(data.getID("Bob"), 3);
		assertTrue(data.containsName("Bill"));
		assertEquals(data.getID("Bill"), 4);
	}
	
	@Test
	public void testAdd3() {
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		assertEquals(data.getName(3), "Bob");
	}
	
	@Test
	public void testAdd4() {
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		assertEquals(data.getName(3), "Bob");
		data.addData("Bill", 4);
		assertEquals(data.getName(3), "Bob");
		assertEquals(data.getName(4), "Bill");
	}
	
	@Test
	public void testGetMissing() {
		PlayerData data = new PlayerData();
		assertEquals(data.getID("dsfsdfs"), -1);
		assertNull(data.getName(4));
		data.addData("Bob", 3);
		assertEquals(data.getID("dsfsdfs"), -1);
		assertNull(data.getName(4));
	}
	
	@Test
	public void testAddDuplicateID(){
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		data.addData("Bill", 3);
		assertTrue(data.containsName("Bill"));
		assertFalse(data.containsName("Bob"));
	}
	
	@Test
	public void testAddDuplicateName(){
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		data.addData("Bob", 4);
		assertTrue(data.containsName("Bob"));
		assertEquals(data.getID("Bob"), 4); // should be most recent
	}

	@Test
	public void saveAndLoad1(){
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		byte[] bytes = SaveUtils.toBytes(data);
		PlayerData newData = SaveUtils.fromBytes(bytes, PlayerData.class, null);
		assertTrue(newData.containsName("Bob"));
		assertEquals(newData.getID("Bob"), 3);
	}
	
	@Test
	public void saveAndLoad2(){
		PlayerData data = new PlayerData();
		data.addData("Bob", 3);
		data.addData("Bill", 4);
		byte[] bytes = SaveUtils.toBytes(data);
		PlayerData newData = SaveUtils.fromBytes(bytes, PlayerData.class, null);
		assertTrue(newData.containsName("Bob"));
		assertEquals(newData.getID("Bob"), 3);
		assertTrue(newData.containsName("Bill"));
		assertEquals(newData.getID("Bill"), 4);
	}
}
