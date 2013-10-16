package test.world;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import world.Entity;
import world.World;
import world.WorldType;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.asset.FilterKey;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Caps;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shader.Shader;
import com.jme3.shader.ShaderGenerator;
import com.jme3.shader.ShaderKey;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

/**
 * @author Alex Campbell 300252131
 */
class GameTest {

	private static class TestApp extends SimpleApplication {
		private static TestApp instance;

		private World world;

		static {
			instance = new TestApp();
			instance.start();
		}

		public TestApp() {
			setShowSettings(false);

			AppSettings s = new AppSettings(true);
			//s.setResolution(1024, 768);
			s.setResolution(320, 200);
			s.setBitsPerPixel(32);
			setSettings(s);
		}

		@Override
		public void simpleInitApp() {
			flyCam.setDragToRotate(true);
			flyCam.setEnabled(false);
		}

		public void setWorld(World w, boolean physics, boolean rendering, WorldType worldType) {
			if(world != null) {
				for(Entity e : world.getEntitiesOfClass(Entity.class))
					world.removeEntity(e);
				world.detachFromGame(stateManager);
				rootNode.detachChild(world.getNode());
			}

			world = w;

			if(world != null) {
				world.USE_SUPERFAST_PHYSICS = !physics;
				World.USE_SUPERFAST_RENDERING = !rendering;

				world.attachToGame(stateManager, new CachingAssetManager(assetManager), null, cam, worldType, viewPort);

				if(!physics)
					stateManager.detach(stateManager.getState(BulletAppState.class));
				if(rendering) {
					rootNode.attachChild(world.getNode());

					if(worldType == WorldType.SP) {
						CameraNode cn = new CameraNode("camera", cam);
				        cn.setLocalRotation(new Quaternion(new float[] {(float)Math.PI*0.25f, (float)Math.PI, 0}));
				        cn.setLocalTranslation(0, 80, 80);
				        ((Node)world.getPlayer().getMesh()).attachChild(cn);
					}
				}
			}
		}

		@Override
		public void simpleUpdate(float tpf) {
			if(world == null)
				return;
			world.gameTick(tpf);
		}
	}

	private WorldType worldType;

	protected World world;
	protected boolean physics, rendering, usePathNodes;

	GameTest(WorldType type) {
		this.worldType = type;
	}

	public void start() {
		world = new World();
		world.setUsePathNodes(usePathNodes);
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				TestApp.instance.setWorld(world, physics, rendering, worldType);
				onstart();
				return null;
			}
		});
	}

	protected void onstart() {}

	public void stop() {
		run(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				TestApp.instance.setWorld(null, false, false, null);
				return null;
			}
		});
	}

	// utility methods
	public static Vector3f getRandomVector() {
		return new Vector3f((float)(Math.random()-0.5)*100, (float)(Math.random()-0.5)*100, (float)(Math.random()-0.5)*100);
	}

	public static <T> T run(final Callable<T> task) {
		Future<Future<T>> ff = TestApp.instance.enqueue(new Callable<Future<T>>() {
			@Override
			public Future<T> call() throws Exception {
				FutureTask<T> ff = new FutureTask<T>(task);
				ff.run();
				return ff;
			}
		});

		try {
			return ff.get().get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			if(e.getCause() instanceof Error)
				throw (Error)e.getCause();
			if(e.getCause() instanceof RuntimeException)
				throw (RuntimeException)e.getCause();
			throw new RuntimeException(e);
		}
	}
}

class CachingAssetManager implements AssetManager {

	private AssetManager wraps;
	public CachingAssetManager(AssetManager am) {wraps = am;}

	@Override public void addClassLoader(ClassLoader loader) {wraps.addClassLoader(loader);}

	@Override
	public void removeClassLoader(ClassLoader loader) {wraps.removeClassLoader(loader);}
	@Override
	public List<ClassLoader> getClassLoaders() {return wraps.getClassLoaders();}

	@Override
	@Deprecated
	public void registerLoader(String loaderClassName, String... extensions) {wraps.registerLoader(loaderClassName, extensions);}
	@Override
	@Deprecated
	public void registerLocator(String rootPath, String locatorClassName) {wraps.registerLocator(rootPath, locatorClassName);}

	@Override
	public void registerLoader(Class<? extends AssetLoader> loaderClass, String... extensions) {wraps.registerLoader(loaderClass, extensions);}

	@Override
	public void unregisterLoader(Class<? extends AssetLoader> loaderClass) {wraps.unregisterLoader(loaderClass);}

	@Override
	public void registerLocator(String rootPath, Class<? extends AssetLocator> locatorClass) {wraps.registerLocator(rootPath, locatorClass);}

	@Override
	public void unregisterLocator(String rootPath, Class<? extends AssetLocator> locatorClass) {wraps.unregisterLocator(rootPath, locatorClass);}

	@Override
	public void addAssetEventListener(AssetEventListener listener) {wraps.addAssetEventListener(listener);}
	@Override
	public void removeAssetEventListener(AssetEventListener listener) {wraps.removeAssetEventListener(listener);}

	@Override
	public void clearAssetEventListeners() {wraps.clearAssetEventListeners();}

	@Override
	@Deprecated
	public void setAssetEventListener(AssetEventListener listener) {wraps.setAssetEventListener(listener);}

	@Override
	public AssetInfo locateAsset(AssetKey<?> key) {return wraps.locateAsset(key);}

	@Override
	public <T> T loadAsset(AssetKey<T> key) {return wraps.loadAsset(key);}

	@Override
	public Object loadAsset(String name) {return wraps.loadAsset(name);}

	@Override
	public Texture loadTexture(TextureKey key) {return wraps.loadTexture(key);}

	@Override
	public Texture loadTexture(String name) {return wraps.loadTexture(name);}

	@Override
	public AudioData loadAudio(AudioKey key) {return wraps.loadAudio(key);}

	@Override
	public AudioData loadAudio(String name) {return wraps.loadAudio(name);}

	@Override
	public Spatial loadModel(ModelKey key) {return wraps.loadModel(key);}

	private static Map<String, Spatial> models = new HashMap<String, Spatial>();
	@Override
	public Spatial loadModel(String name) {
		if(!models.containsKey(name))
			models.put(name, wraps.loadModel(name));
		return models.get(name).clone();
	}

	@Override
	public Material loadMaterial(String name) {return wraps.loadMaterial(name);}

	@Override
	public Shader loadShader(ShaderKey key) {return wraps.loadShader(key);}

	@Override
	public BitmapFont loadFont(String name) {return wraps.loadFont(name);}

	@Override
	public FilterPostProcessor loadFilter(FilterKey key) {return wraps.loadFilter(key);}

	@Override
	public FilterPostProcessor loadFilter(String name) {return wraps.loadFilter(name);}

	@Override
	public void setShaderGenerator(ShaderGenerator generator) {wraps.setShaderGenerator(generator);}

	@Override
	public ShaderGenerator getShaderGenerator(EnumSet<Caps> caps) {return wraps.getShaderGenerator(caps);}

}
