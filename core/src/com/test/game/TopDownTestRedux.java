package com.test.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.test.game.audio.AudioManager;
import com.test.game.ecs.ECSEngine;
import com.test.game.map.MapManager;
import com.test.game.screen.AbstractScreen;
import com.test.game.screen.ScreenType;
import com.test.game.input.InputManager;
import com.test.game.view.GameRenderer;

import java.util.EnumMap;

public class TopDownTestRedux extends Game {
	private static final String TAG = TopDownTestRedux.class.getSimpleName();
	private SpriteBatch spriteBatch;
	private EnumMap<ScreenType, AbstractScreen> screenCache;
	private OrthographicCamera gameCamera;
	private FitViewport screenViewport;
	public static final BodyDef BODY_DEF = new BodyDef();
	public static final FixtureDef FIXTURE_DEF = new FixtureDef();
	public static final float UNIT_SCALE = 1/32f;
	public static final short BIT_PLAYER = 1 << 0;
	public static final short BIT_GROUND = 1 << 1;
	private World world;
	private WorldContactListener worldContactListener;
	private Box2DDebugRenderer box2DDebugRenderer;
	private static final float FIXED_TIME_STEP = 1 / 60f;
	private float accumulator;
	private AssetManager assetManager;
	private AudioManager audioManager;
	private Stage stage;
	private Skin skin;
	private InputManager inputManager;
	private MapManager mapManager;
	private ECSEngine ecsEngine;
	private GameRenderer gameRenderer;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		spriteBatch = new SpriteBatch();

		accumulator = 0;

		//initialize the box 2d framework
		Box2D.init();
		//create the world, give it the gravity of earth and set non-moving objects as sleeping
		world = new World(new Vector2(0,0), true);
		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);
		box2DDebugRenderer = new Box2DDebugRenderer();

		//init asset manager
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));


		initializeSkin();
		stage = new Stage(new FitViewport(1920,1080),spriteBatch);

		//audio
		audioManager = new AudioManager(this);

		//input manager
		inputManager = new InputManager();
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager,stage));

		gameCamera = new OrthographicCamera();
		screenViewport = new FitViewport(16,9, gameCamera);

		//map manager
		mapManager = new MapManager(this);

		//ecs engine
		ecsEngine = new ECSEngine(this);

		//game renderer
		gameRenderer = new GameRenderer(this);

		screenCache = new EnumMap<ScreenType, AbstractScreen>(ScreenType.class);
		setScreen(ScreenType.LOADING);


	}

	public void resetBodyAndFixtureDefinition() {
		BODY_DEF.position.set(0,0);
		BODY_DEF.gravityScale = 1;
		BODY_DEF.type = BodyDef.BodyType.StaticBody;
		BODY_DEF.fixedRotation = false;

		FIXTURE_DEF.density = 0;
		FIXTURE_DEF.isSensor = false;
		FIXTURE_DEF.restitution = 0;
		FIXTURE_DEF.friction = 0.2f;
		FIXTURE_DEF.filter.categoryBits = 0x0001;
		FIXTURE_DEF.filter.maskBits = -1;
		FIXTURE_DEF.shape = null;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public ECSEngine getEcsEngine() {
		return ecsEngine;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public InputManager getInputManager() {
		return inputManager;
	}

	public Stage getStage() {
		return stage;
	}

	public Skin getSkin() {
		return skin;
	}

	private void initializeSkin () {
		//setup markup colors
		Colors.put("Red", Color.RED);
		Colors.put("Blue", Color.BLUE);
		Colors.put("Green", Color.GREEN);



		//generate ttf bitmaps
		final ObjectMap<String,Object> resources = new ObjectMap<String,Object>();
		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.minFilter = Texture.TextureFilter.Linear;
		fontParameter.magFilter = Texture.TextureFilter.Linear;
		final int[] sizesToCreate = {16,20,26,32};
		for(int size:sizesToCreate) {
			fontParameter.size = size;
			final BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
			bitmapFont.getData().markupEnabled = true;
			resources.put("font_" + size, bitmapFont);
		}
		fontGenerator.dispose();


		//load the skin
		final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("hud.atlas",resources);
		assetManager.load("hud.json",Skin.class,skinParameter);
		assetManager.finishLoading();
		skin = assetManager.get("hud.json",Skin.class);

	}

	public FitViewport getScreenViewport() {
		return screenViewport;
	}

	public World getWorld() {
		return world;
	}

	public Box2DDebugRenderer getBox2DDebugRenderer() {
		return box2DDebugRenderer;
	}

	public WorldContactListener getWorldContactListener() {
		return worldContactListener;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public OrthographicCamera getGameCamera() {
		return gameCamera;
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public void setScreen(final ScreenType screenType) {
		final Screen screen = screenCache.get(screenType);
		if (screen == null) {
			try {
				Gdx.app.debug(TAG, "Creating New Screen: " + screenType);
				final Object newScreen = ClassReflection.getConstructor(screenType.getScreenClass(), TopDownTestRedux.class).newInstance(this);
				screenCache.put(screenType, (AbstractScreen) newScreen);
				setScreen((AbstractScreen) newScreen);
			} catch (ReflectionException e) {
				throw new GdxRuntimeException("Screen" + screenType + "could not be created", e);
			}
		} else {
			Gdx.app.debug(TAG, "Switching to screen: " + screenType);
			setScreen(screen);
		}
	}

	@Override
	public void render() {
		super.render();

		final float deltaTime = Math.min(0.25f,Gdx.graphics.getDeltaTime());
		ecsEngine.update(deltaTime);

		accumulator += deltaTime;
		while(accumulator >= FIXED_TIME_STEP) {
			world.step(FIXED_TIME_STEP,6,2);
			accumulator -= FIXED_TIME_STEP;
		}

		gameRenderer.render(accumulator / FIXED_TIME_STEP);
		stage.getViewport().apply();
		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
		assetManager.dispose();
		spriteBatch.dispose();
		gameRenderer.dispose();
	}

}
