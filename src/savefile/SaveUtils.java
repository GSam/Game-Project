package savefile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xml.sax.SAXParseException;

import com.jme3.asset.AssetManager;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.export.xml.XMLExporter;
import com.jme3.export.xml.XMLImporter;

/**
 * @author Alex Campbell 300252131
 */
public class SaveUtils {

	/**
	 * Saves a savable object to an XML file.
	 * @param object The object to save.
	 * @param file The file to save to.
	 * @throws RuntimeException if anything goes wrong - no proper error handling (TODO)
	 */
	public static void save(Savable object, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			try {
				XMLExporter.getInstance().save(object, out);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Loads a savable object from an XML file.
	 * @param assetManager The asset manager - used for loading any referenced assets.
	 * @param file The file to load from.
	 * @param expectedClass The class of the object that is expected to be saved in the file.
	 * @return The loaded object.
	 * @throws RuntimeException if anything goes wrong - no proper error handling (TODO)
	 */
	public static <T> T load(AssetManager assetManager, File file, Class<T> expectedClass) {
		try {
			XMLImporter im = XMLImporter.getInstance();
			im.setAssetManager(assetManager);
			return expectedClass.cast(im.load(file));
		} catch (IOException e) {
			if(e.getCause() instanceof SAXParseException) {
				SAXParseException pe = ((SAXParseException)e.getCause());
				System.err.println("XML error on "+pe.getLineNumber()+":"+pe.getColumnNumber());
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Serializes a savable object to a byte array.
	 * @param object The object to serialize.
	 * @return The byte array.
	 * @throws RuntimeException if anything goes wrong - no proper error handling (TODO)
	 */
	public static byte[] toBytes(Savable object) {
		try {
			BinaryExporter ex = BinaryExporter.getInstance();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ex.save(object, baos);
			return baos.toByteArray();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Deserializes a savable object from a byte array.
	 * @param bytes The byte array to deserialize.
	 * @param expectedClass The class of the object that is expected to be saved in the byte array.
	 * @param world The asset manager, used for loading any referenced assets.
	 * @return The deserialized object.
	 * @throws RuntimeException if anything goes wrong - no proper error handling (TODO)
	 */
	public static <T extends Savable> T fromBytes(byte[] bytes, Class<T> expectedClass, AssetManager am) {
		try {
			BinaryImporter im = BinaryImporter.getInstance();
			if(am != null)
				im.setAssetManager(am);
			return expectedClass.cast(im.load(bytes));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
