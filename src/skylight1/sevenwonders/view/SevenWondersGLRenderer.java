package skylight1.sevenwonders.view;

import static javax.microedition.khronos.opengles.GL10.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.CollisionDetector.CollisionObserver;
import skylight1.opengl.FastGeometryBuilder;
import skylight1.opengl.FastGeometryBuilderFactory;
import skylight1.opengl.GeometryBuilder;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.OpenGLGeometryBuilderFactory;
import skylight1.opengl.Texture;
import skylight1.opengl.TransformingGeometryBuilder;
import skylight1.opengl.files.ObjFileLoader;
import skylight1.sevenwonders.GameState;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.R;
import skylight1.sevenwonders.Settings;
import skylight1.sevenwonders.SevenWondersApplication;
import skylight1.sevenwonders.levels.CollisionAction;
import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.levels.GameObjectDescriptor;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.FloatMath;
import android.util.Log;

public class SevenWondersGLRenderer implements Renderer {
	
	public static class CollisionActionToCollisionObserverAdapter implements CollisionObserver {
		private final List<OpenGLGeometry> listOfOpenGLGeometries;
		private final CollisionAction collisionAction;
		private SevenWondersGLRenderer sevenWondersGLRenderer;
		private Handler uiHandler;

		public CollisionActionToCollisionObserverAdapter(final List<OpenGLGeometry> aListOfOpenGLGeometries, final CollisionAction aCollisionAction, final SevenWondersGLRenderer aSevenWondersGLRenderer, final Handler aUiHandler) {
			listOfOpenGLGeometries = aListOfOpenGLGeometries;
			collisionAction = aCollisionAction;
			sevenWondersGLRenderer = aSevenWondersGLRenderer;
			uiHandler = aUiHandler;
		}
		
		@Override
		public boolean collisionOccurred(float[] aBoundingSphere) {
			// TODO do this early and cache it to be reused by all collectables with the same size
			
			// create a fast geometry that is out of sight
			final FastGeometryBuilder<?, ?> somewhereFarFarAway = FastGeometryBuilderFactory.createTexturableNormalizable(listOfOpenGLGeometries.get(0));
			// TODO there has to be a better way to make a correctly sized geometry, than to know it has 3 times as many
			// vertices as triangles
			for (int silly = 0; silly < listOfOpenGLGeometries.get(0).getNumberOfVerticies() / 3; silly++) {
				somewhereFarFarAway.add3DTriangle(0, 0, -100, 0, 0, -100, 0, 0, -100);
			}

			// iterate through all animation geometries, moving the objects off screen
			for (int spellAnimationIndex = 0; spellAnimationIndex < listOfOpenGLGeometries.size(); spellAnimationIndex++) {
				listOfOpenGLGeometries.get(spellAnimationIndex).updateModel(somewhereFarFarAway);
			}

			collisionAction.collision(uiHandler, sevenWondersGLRenderer);
			
			// suppress future collisions
			return true;
		}
	}

	static final float MAXIMUM_VELOCITY = 300f * 1000f / 60f / 60f / 1000f;

	// Used to reduce turning velocity, which was too high as the phone angle times the time delta alone.
	private static final int TURNING_VELOCITY_DIVISOR = 100;

	private static final int END_OF_WORLD_MARGIN = 100;

	private static final String TAG = SevenWondersGLRenderer.class.getName();

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final float MINIMUM_VELOCITY = -MAXIMUM_VELOCITY / 10f;

	private static final int NUMBER_OF_SPINNING_ANIMATION_FRAMES = 16;

	private static final int PERIOD_FOR_SPINNING_ANIMATION_CYCLE = 1000;

	private final Context context;

	private FPSLogger fPSLogger = new FPSLogger(TAG, FRAMES_BETWEEN_LOGGING_FPS);

	private OpenGLGeometry[] allSpellsGeometry;
	
	private OpenGLGeometry[] allHazardsGeometry;

	private OpenGLGeometry skyboxGeometry;

	private Position playerWorldPosition = new Position(0, 0, 0);

	private float playerFacing;
	
	private float turningVelocity;

	private float velocity;

	private long timeAtLastOnRenderCall;

	private final CollisionDetector collisionDetector = new CollisionDetector();

	private final CollisionDetector obstacleCollisionDetector = new CollisionDetector();
	
	private Carpet carpet;

	private List<OpenGLGeometry> decorationGeometries = new ArrayList<OpenGLGeometry>();

	private List<OpenGLGeometry> glowGeometries = new ArrayList<OpenGLGeometry>();
	
	private final Map<Integer, Texture> textureResourceIdToTextureMap = new HashMap<Integer, Texture>();

	private final Handler updateUiHandler;

	private final GameLevel level;

	private final float[] temporaryMatrix = new float[16];

	private float playerFacingThisFrame;

	private boolean paused;

	private Integer spellAnimationIndex;

	private Integer swordAnimationIndex;

	private final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder;

	private float startOfFramePlayerWorldPositionX;

	private float startOfFramePlayerWorldPositionZ;

	private Settings settings;

	private final GameState gameState;
	
	public SevenWondersGLRenderer(final Context aContext, final Handler aUpdateUiHandler, final GameLevel aLevel, final GameState aGameState) {
		Log.i(TAG, "SevenWondersGLRenderer()");
		context = aContext;
		gameState = aGameState;
		carpet = new Carpet(this);
		level = aLevel;
		updateUiHandler = aUpdateUiHandler;

		settings = new Settings(aContext);
		
		openGLGeometryBuilder = OpenGLGeometryBuilderFactory.createTexturableNormalizable(60453);

		// load all of the decorations (land, water, sphinx, pyramids, etc.)
		addGeometriesFromGameObjects(decorationGeometries, level.getDecorations(), false);

		// load glows that brighten areas
		addGeometriesFromGameObjects(glowGeometries, level.getGlows(), true);
		
		openGLGeometryBuilder.startGeometry(getTexture(R.raw.skybox_texture, false));		
		loadRequiredObj(R.raw.skybox_model, openGLGeometryBuilder);
		skyboxGeometry = openGLGeometryBuilder.endGeometry();

		// add the collectables to the world geometry
		allSpellsGeometry = addObjectsToGeometry(openGLGeometryBuilder, level.getCollectables(), R.raw.textures);
		
		// add the hazards to the world geometry
		allHazardsGeometry = addObjectsToGeometry(openGLGeometryBuilder, level.getHazards(), R.raw.textures);
		
		// add the carpet
		carpet.createGeometry(openGLGeometryBuilder);

		// collision with an obstacle moves the player back to where
		// they were at the start of the frame, prior to any movement
		final CollisionObserver obstacleCollisionObserver = new CollisionObserver() {
			@Override
			public boolean collisionOccurred(final float[] aBoundingSphere) {
				// Return early without doing anything if the player has gained the ability to fly through obstacles.
				if ( gameState.isPlayerAbleToFlyThroughObstacles() ) {
					return false;
				}
				
			    SoundTracks.getInstance().play(SoundTracks.BUMP);
		
			    // Find the distance traveled toward the pyramid.
				float initialOffsetToPyramidX = aBoundingSphere[0] - startOfFramePlayerWorldPositionX;
				float initialOffsetToPyramidZ = aBoundingSphere[2] - startOfFramePlayerWorldPositionZ;
				float collidingOffsetToPyramidX = aBoundingSphere[0] - playerWorldPosition.x;
				float collidingOffsetToPyramidZ = aBoundingSphere[2] - playerWorldPosition.z;
				float initialDistanceFromPyramid = Matrix.length(Math.abs(initialOffsetToPyramidX), 0, Math.abs(initialOffsetToPyramidZ));
				float collidingDistanceFromPyramid = Matrix.length(Math.abs(collidingOffsetToPyramidX), 0, Math.abs(collidingOffsetToPyramidZ));
				float distanceTraveledTowardPyramid = initialDistanceFromPyramid - collidingDistanceFromPyramid;

				// Find X offset to travel that distance away using same slope as if headed toward pyramid.
				float xToZ = initialOffsetToPyramidX / initialOffsetToPyramidZ;
				float slideAmountZ = FloatMath.sqrt(distanceTraveledTowardPyramid * distanceTraveledTowardPyramid / (xToZ * xToZ + 1));
				float slideOffsetZ = slideAmountZ * initialOffsetToPyramidZ / Math.abs(initialOffsetToPyramidZ);

				// Find Z offset to travel that distance away using same slope as if headed toward pyramid.
				float zToX = initialOffsetToPyramidZ / initialOffsetToPyramidX;
				float slideAmountX = FloatMath.sqrt(distanceTraveledTowardPyramid * distanceTraveledTowardPyramid / (zToX * zToX + 1));
				float slideOffsetX = slideAmountX * initialOffsetToPyramidX / Math.abs(initialOffsetToPyramidX);
				
				// Take where the player would have gone had he not collided
				// and subtract the distance traveled toward the pyramid. 
				// This should leave only the motion to the side of the pyramid.
				float playerSlidingPositionX = playerWorldPosition.x - slideOffsetX;
				float playerSlidingPositionZ = playerWorldPosition.z - slideOffsetZ;
				
				playerWorldPosition.x = playerSlidingPositionX;
				playerWorldPosition.z = playerSlidingPositionZ;
			    
				float slidingOffsetToPyramidX = aBoundingSphere[0] - playerWorldPosition.x;
				float slidingOffsetToPyramidZ = aBoundingSphere[2] - playerWorldPosition.z;
				float slidingDistanceFromPyramid = Matrix.length(Math.abs(slidingOffsetToPyramidX), 0, Math.abs(slidingOffsetToPyramidZ));
				Log.i(TAG, String.format("just colided with (%s) at distance %f, retreating to distance %f", 
						Arrays.toString(aBoundingSphere), collidingOffsetToPyramidZ, slidingDistanceFromPyramid));
				
				// do not suppress future collisions
				return false;
			}
		};
		for (final float[] boundingSphere : level.getObstacles()) {
			obstacleCollisionDetector.addBoundingSphere(boundingSphere, obstacleCollisionObserver);
		}
	}

	private void addGeometriesFromGameObjects(final List<OpenGLGeometry> aDestinationGeometries,
			final Collection<GameObjectDescriptor> aGameObjects, final boolean aUseseparateGeometries) {
		int currentTextureResource = 0;
		for (GameObjectDescriptor objectDescriptor : aGameObjects) {
			// if the texture has changed (including the first time through), then ...
			if (aUseseparateGeometries || objectDescriptor.textureResource != currentTextureResource) {
				// wrap up the existing geometry (if any, since first time through there won't be an existing geometry)
				if (openGLGeometryBuilder.isBuildingGeometry()) {
					final OpenGLGeometry previousGeometry = openGLGeometryBuilder.endGeometry();
					aDestinationGeometries.add(previousGeometry);
				}
				
				currentTextureResource = objectDescriptor.textureResource;
				
				// create a new texture object, store it in the texture id to texture map,
				// and start a new geometry using the new texture
				final Texture texture = getTexture(objectDescriptor.textureResource, true);
				openGLGeometryBuilder.startGeometry(texture);
			}

			// load the object
			TransformingGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> transformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(openGLGeometryBuilder, objectDescriptor.coordinateTransformationMatrix, objectDescriptor.textureTransformationMatrix);
			loadRequiredObj(objectDescriptor.objectFileResourceId, transformingGeometryBuilder);
		}
		// wrap up the existing geometry (if any, since first time through there won't be an existing geometry)
		if (openGLGeometryBuilder.isBuildingGeometry()) {
			final OpenGLGeometry lastGeometry = openGLGeometryBuilder.endGeometry();
			aDestinationGeometries.add(lastGeometry);
		}
	}

	public void onSurfaceCreated(final GL10 aGl, final EGLConfig aConfig) {
		Log.i(TAG, "- onSurfaceCreated - ");

		openGLGeometryBuilder.enable(aGl);

		aGl.glColor4f(1, 1, 1, 1);
		aGl.glClearColor(0.5f, 0.5f, 1, 1.0f);

		// Don't draw inside facing surfaces on things like the pyramid and sphinx.
		// This fixes a z-fighting issue: At long distances OpenGL doesn't have enough precision in the depth buffer
		// to tell if the front is closer or if the inside of a nearby back surface is closer and gets it wrong some
		// times.
		aGl.glEnable(GL10.GL_CULL_FACE);
		aGl.glEnable(GL10.GL_DEPTH_TEST);
		aGl.glDisable(GL10.GL_BLEND);
		// Function for additive blending. Blending is only currently turned on for glows that add to the light of something.
		aGl.glBlendFunc(GL_ONE, GL_ONE);
		aGl.glShadeModel(GL10.GL_SMOOTH);
		aGl.glEnable(GL10.GL_LIGHTING);
		aGl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, new float[] { 0.75f, 0.75f, 0.75f, 1f }, 0);
		aGl.glEnable(GL10.GL_LIGHT0);
		aGl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] { -1f, 0f, 1f, 0.0f }, 0);
		aGl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[] { 0.5f, 0.5f, 0.5f, 1f }, 0);

		aGl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		aGl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		aGl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, new float[] { 0.1f, 0.1f, 0.1f, 1.0f }, 0);
		aGl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, new float[] { 50.0f }, 0);
	}

	private OpenGLGeometry[] addObjectsToGeometry(
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder, 
			final Map<GameObjectDescriptor, CollisionAction> aMapOfObjectDescriptorsToCollisionActions,
			final int aTextureResource) {

		// return null quickly if there are no objects in the collection
		if (aMapOfObjectDescriptorsToCollisionActions.isEmpty()) {
			return null;
		}

		final Map<GameObjectDescriptor, List<OpenGLGeometry>> mapOfGameObjectDescriptorsToListOfOpenGLGeometries = new HashMap<GameObjectDescriptor, List<OpenGLGeometry>>();
		
		final Texture texture = getTexture(aTextureResource, true);

		final OpenGLGeometry[] objectGeometries = new OpenGLGeometry[NUMBER_OF_SPINNING_ANIMATION_FRAMES];

		final Map<Integer, ObjFileLoader> resourceIdToObjFileLoaderMap = new HashMap<Integer, ObjFileLoader>();

		// create a number of objects, in a number of orientations
		float[] coordinateTransform = new float[16];
		for (int animationIndex = 0; animationIndex < NUMBER_OF_SPINNING_ANIMATION_FRAMES; animationIndex++) {
			anOpenGLGeometryBuilder.startGeometry(texture);

			int objectIndex = 0;
			for (final GameObjectDescriptor objectDescriptor : aMapOfObjectDescriptorsToCollisionActions.keySet()) {
				final int objectFileResourceId = objectDescriptor.objectFileResourceId;
				ObjFileLoader objFileLoader = resourceIdToObjFileLoaderMap.get(objectFileResourceId);
				if (objFileLoader == null) {
					try {
						objFileLoader = new ObjFileLoader(context, objectFileResourceId);
					} catch (IOException e) {
						throw new RuntimeException("could not load file", e);
					}
					resourceIdToObjFileLoaderMap.put(objectFileResourceId, objFileLoader);
				}
				anOpenGLGeometryBuilder.startGeometry(texture);
				
				System.arraycopy(objectDescriptor.coordinateTransformationMatrix, 0, coordinateTransform, 0, coordinateTransform.length);

				Matrix.rotateM(coordinateTransform, 0, 360f * (float) animationIndex
						/ (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES, 0, 1, 0);
				// this final rotate is to "stand up" the ankh
				Matrix.rotateM(coordinateTransform, 0, -90f, 1, 0, 0);

				// load the object into the geometry
				TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> transformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(anOpenGLGeometryBuilder, coordinateTransform, objectDescriptor.textureTransformationMatrix);
				objFileLoader.createGeometry(transformingGeometryBuilder);
				final OpenGLGeometry objectGeometry = anOpenGLGeometryBuilder.endGeometry();

				final List<OpenGLGeometry> listOfOpenGLGeometries;
				if (mapOfGameObjectDescriptorsToListOfOpenGLGeometries.containsKey(objectDescriptor)) {
					listOfOpenGLGeometries = mapOfGameObjectDescriptorsToListOfOpenGLGeometries.get(objectDescriptor);
				} else {
					listOfOpenGLGeometries = new ArrayList<OpenGLGeometry>();
					mapOfGameObjectDescriptorsToListOfOpenGLGeometries.put(objectDescriptor, listOfOpenGLGeometries);
				}
				listOfOpenGLGeometries.add(objectGeometry);
				
				// next object
				objectIndex++;
			}
			objectGeometries[animationIndex] = anOpenGLGeometryBuilder.endGeometry();
		}

		// create collision observers and add them for the objects
		for (final Map.Entry<GameObjectDescriptor, List<OpenGLGeometry>> entry : mapOfGameObjectDescriptorsToListOfOpenGLGeometries.entrySet()) {
			final List<OpenGLGeometry> listOfOpenGLGeometries = entry.getValue();
			final CollisionActionToCollisionObserverAdapter adapter = new CollisionActionToCollisionObserverAdapter(listOfOpenGLGeometries, aMapOfObjectDescriptorsToCollisionActions.get(entry.getKey()), this, updateUiHandler);
			collisionDetector.addBoundingSphere(listOfOpenGLGeometries.get(0).getBoundingSphere(), adapter);
		}
		
		return objectGeometries;
	}

	void loadRequiredObj(
			final int aObjId,
			final GeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> aBuilder) {
		final ObjFileLoader loader;
		try {
			loader = new ObjFileLoader(context, aObjId);
		} catch (IOException e) {
			throw new RuntimeException("Error loading required geometry from OBJ file:" + aObjId, e);
		}
		loader.createGeometry(aBuilder);
	}

	public void onSurfaceChanged(final GL10 aGl, final int aW, final int aH) {
		aGl.glMatrixMode(GL10.GL_PROJECTION);
		aGl.glLoadIdentity();
		aGl.glViewport(0, 0, aW, aH);
		GLU.gluPerspective(aGl, 45, (float) aW / (float) aH, 0.25f, 4000.0f);

		aGl.glMatrixMode(GL10.GL_MODELVIEW);
		
		// load all of the used textures
		for (Texture texture : textureResourceIdToTextureMap.values()) {
			texture.load(aGl);
		}

		updateUiHandler.sendEmptyMessage(PlayActivity.START_RENDERING_MESSAGE);
	}
	
	Texture getTexture(final int aResource, final boolean aUsesMipMaps) {
		final Texture texture;
		if (textureResourceIdToTextureMap.containsKey(aResource)) {
			texture = textureResourceIdToTextureMap.get(aResource);
		} else {
			texture = new Texture(context, aResource, aUsesMipMaps);
			textureResourceIdToTextureMap.put(aResource, texture);
		}
		return texture;
	}
	
	public void onDrawFrame(final GL10 aGl) {
		aGl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
		
		final long timeDeltaMS = calculateTimeSinceLastRenderMillis();
		float turnAmountFromTurningVelocity = turningVelocity * timeDeltaMS / TURNING_VELOCITY_DIVISOR;
		turn(turnAmountFromTurningVelocity);
		
		playerFacingThisFrame = playerFacing;
		
		drawCarpet(aGl);

		//Bank the world if we are turning.
		float worldAngle = carpet.getWorldAngle();
		aGl.glPushMatrix();		
		aGl.glRotatef(worldAngle, 0f, 0f, 1f);
		drawSkybox(aGl, worldAngle);
				
		applyMovement(aGl, timeDeltaMS);
		detectCollisions();

		drawSpells(aGl);		
		drawSwords(aGl);

		for (int geometryIndex = 0; geometryIndex < decorationGeometries.size(); geometryIndex++) {
			final OpenGLGeometry geometry = decorationGeometries.get(geometryIndex);
			geometry.draw(aGl);
		}

		drawGlows(aGl);

		aGl.glPopMatrix();

		if (SevenWondersApplication.isDebug) {
			if (settings.isDebugEnabled()) {
				Message msg = updateUiHandler.obtainMessage(PlayActivity.FPS_MESSAGE, fPSLogger.frameRendered(), 0);
				updateUiHandler.sendMessage(msg);
			}
		}
	}

	private void drawGlows(final GL10 aGl) {
		// Blend function is set to additive blending at init time, which adds light, not obscures
		aGl.glEnable(GL10.GL_BLEND);
		for (int geometryIndex = 0; geometryIndex < glowGeometries.size(); geometryIndex++) {
			final OpenGLGeometry geometry = glowGeometries.get(geometryIndex);
			if ( null != geometry ) {
				geometry.draw(aGl);
			}
		}
		aGl.glDisable(GL10.GL_BLEND);
	}

	private void drawCarpet(final GL10 aGl) {
		// Carpet drawn with no transformations, always right in front of the screen.
		aGl.glLoadIdentity();
		// Drawn first for performance, might occlude other geometry, which OpenGL can then skip.
		// XXX Hack to fix the carpet being drawn face down. Should probably change geometry or disable culling for the
		// carpet instead.
		aGl.glFrontFace(GL_CW);

		carpet.draw(aGl, paused);

		aGl.glFrontFace(GL_CCW);
	}

	private void drawSkybox(GL10 aGl, float worldAngle) {	
		// save the current matrix for later - later? what later?
		aGl.glPushMatrix();

		// rotate the skybox to match the player's facing
		aGl.glRotatef(playerFacingThisFrame, 0f, 1f, 0f);
		aGl.glDisable(GL10.GL_LIGHTING);

		skyboxGeometry.draw(aGl);
		
	   aGl.glEnable(GL10.GL_LIGHTING);
		
		// restore the matrix
		aGl.glPopMatrix();
	}	

	private void detectCollisions() {
		float[] carpetBoundingBox = temporaryMatrix;
		// TODO should we use Matrix.orthoM()
		Matrix.frustumM(carpetBoundingBox, 0, -0.3f, 0.3f, GameState.HEIGHT_OF_CARPET_FROM_GROUND, GameState.HEIGHT_OF_CARPET_FROM_GROUND + 2f, 0.1f, 1f);

		// Rotate first, otherwise map rotates around center point we translated away from.
		Matrix.rotateM(carpetBoundingBox, 0, playerFacingThisFrame, 0, 1, 0);
		Matrix.translateM(carpetBoundingBox, 0, -playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);

		if(!paused) {
			collisionDetector.detectCollisions(carpetBoundingBox);
		}
	}

	private void applyMovement(final GL10 aGl, final long aTimeDeltaMS) {	
		// keep the old position in case a collision with an obstacle requires a "movement rollback"
		startOfFramePlayerWorldPositionX = playerWorldPosition.x;
		startOfFramePlayerWorldPositionZ = playerWorldPosition.z;

		final float facingX = (float) Math.sin(playerFacingThisFrame / 180f * Math.PI);
		final float facingZ = -(float) Math.cos(playerFacingThisFrame / 180f * Math.PI);

		if ( !paused ) {
			final float newPositionX = playerWorldPosition.x + facingX * velocity * aTimeDeltaMS;
			final float newPositionZ = playerWorldPosition.z + facingZ * velocity * aTimeDeltaMS;
	
			// Only update the position if it isn't too far from the end of the world.
			if (newPositionX > (CubeBounds.TERRAIN.x1 + END_OF_WORLD_MARGIN)
					&& newPositionX < (CubeBounds.TERRAIN.x2 - END_OF_WORLD_MARGIN)) {
				playerWorldPosition.x = newPositionX;
			} else {
			    SoundTracks.getInstance().play(SoundTracks.BUMP);
			}
	
			if (newPositionZ > (CubeBounds.TERRAIN.z1 + END_OF_WORLD_MARGIN)
					&& newPositionZ < (CubeBounds.TERRAIN.z2 - END_OF_WORLD_MARGIN)) {
				playerWorldPosition.z = newPositionZ;
			} else {
			    SoundTracks.getInstance().play(SoundTracks.BUMP);
			}
		}

		checkForCollisionsWithObstacles();

		GLU.gluLookAt(aGl, playerWorldPosition.x, GameState.HEIGHT_OF_CARPET_FROM_GROUND, playerWorldPosition.z, playerWorldPosition.x
				+ facingX, GameState.HEIGHT_OF_CARPET_FROM_GROUND, playerWorldPosition.z + facingZ, 0f, 1f, 0f);
	}

	private void checkForCollisionsWithObstacles() {
		float[] centerOfCarpet = temporaryMatrix;
		// a single point only, otherwise turning away from collisions is problematic
		Matrix.frustumM(centerOfCarpet, 0, -0.001f, 0.001f, GameState.HEIGHT_OF_CARPET_FROM_GROUND, GameState.HEIGHT_OF_CARPET_FROM_GROUND+0.001f, 0.001f, 0.002f);

		// Rotate first, otherwise map rotates around center point we translated away from.
		Matrix.rotateM(centerOfCarpet, 0, playerFacingThisFrame, 0, 1, 0);
		Matrix.translateM(centerOfCarpet, 0, -playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);

		obstacleCollisionDetector.detectCollisions(centerOfCarpet);
	}

	private long calculateTimeSinceLastRenderMillis() {
		final long now = SystemClock.uptimeMillis();
		if (timeAtLastOnRenderCall == 0) {
			timeAtLastOnRenderCall = now;
		}

		final long timeDeltaMS = now - timeAtLastOnRenderCall;
		timeAtLastOnRenderCall = now;
		return timeDeltaMS;
	}

	private void drawSpells(final GL10 aGl) {
		if ( !paused || null == spellAnimationIndex ) {
			spellAnimationIndex = (int) ((float) (SystemClock.uptimeMillis() % PERIOD_FOR_SPINNING_ANIMATION_CYCLE)
					/ (float) PERIOD_FOR_SPINNING_ANIMATION_CYCLE * (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES);
		}
		allSpellsGeometry[spellAnimationIndex].draw(aGl);
	}

	private void drawSwords(final GL10 aGl) {
		if ( !paused || null == swordAnimationIndex ) {
			swordAnimationIndex = (int) ((float) (SystemClock.uptimeMillis() % PERIOD_FOR_SPINNING_ANIMATION_CYCLE)
					/ (float) PERIOD_FOR_SPINNING_ANIMATION_CYCLE * (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES);
		}
		if (allHazardsGeometry != null) {
			allHazardsGeometry[swordAnimationIndex].draw(aGl);
		}
	}

	public synchronized void setPlayerVelocity(final int aNewVelocity) {
		if ( paused ) {
			return;
		}
		velocity = aNewVelocity;
	}

	public synchronized void turn(final float anAngleOfTurn) {
		if ( paused ) {
			return;
		}
		playerFacing += anAngleOfTurn;
		carpet.recordTurnAngle(anAngleOfTurn);
	}

	public synchronized void setTurningVelocity(final float aTurningVelocity) {
		if ( paused ) {
			return;
		}
		turningVelocity = aTurningVelocity;
	}

	public synchronized void setPlayerFacing(final float anAngleAbosulte) {
		if ( paused ) {
			return;
		}
		playerFacing = anAngleAbosulte;
	}

	public synchronized void changeVelocity(final float aVelocityIncrement) {
		if ( paused ) {
			return;
		}
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, velocity + aVelocityIncrement));
	}

	public synchronized void setVelocity(final float aVelocity) {
		if ( paused ) {
			return;
		}
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, aVelocity));
	}

	public synchronized void togglePaused() {
		paused = !paused;
	}

	/**
	 * Sets the player's velocity via a percentage of the maximum velocity. This prevents calling code from having to
	 * understand the actual velocity maximum when calculating a new velocity to set due to the player using the
	 * controls.
	 * 
	 * @param aPercent
	 *            float percent from -1 to 1
	 */
	public void setVelocityPercent(final float aPercent) {
		if ( paused ) {
			return;
		}
		final float newVelocity = aPercent < 0 ? MINIMUM_VELOCITY * aPercent : MAXIMUM_VELOCITY * aPercent;
		velocity = constrainVelocity(newVelocity);
	}

	/**
	 * Constrains a candidate velocity to an allowed velocity range.
	 * 
	 * @param aCandidateVelocity
	 *            float candidate velocity in meters per second
	 * @return allowable velocity
	 */
	private float constrainVelocity(final float aCandidateVelocity) {
		return aCandidateVelocity < MINIMUM_VELOCITY ? MINIMUM_VELOCITY
				: aCandidateVelocity > MAXIMUM_VELOCITY ? MAXIMUM_VELOCITY : aCandidateVelocity;
	}
	
	public GameState getGameState() {
		return gameState;
	}

	public void hideGlowAt(final int aGlowIndex) {
		glowGeometries.set(aGlowIndex, null);
	}
}
