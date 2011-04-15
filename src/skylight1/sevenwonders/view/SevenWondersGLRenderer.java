package skylight1.sevenwonders.view;

import static javax.microedition.khronos.opengles.GL10.GL_CCW;
import static javax.microedition.khronos.opengles.GL10.GL_CW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.GeometryBuilder;
import skylight1.opengl.CollisionDetector.CollisionObserver;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.OpenGLGeometryBuilderFactory;
import skylight1.opengl.Texture;
import skylight1.opengl.TransformingGeometryBuilder;
import skylight1.opengl.files.ObjFileLoader;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.R;
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
import android.util.Log;

public class SevenWondersGLRenderer implements Renderer {

	static final int ANIMATION_INDEX_FOR_COLLISION_DETECTION = 0;

	public static final float MAXIMUM_VELOCITY = 300f * 1000f / 60f / 60f / 1000f;

	private static final int END_OF_WORLD_MARGIN = 100;

	private static final String TAG = SevenWondersGLRenderer.class.getName();

	private static final float HEIGHT_OF_CARPET_FROM_GROUND = 12f;

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final float MINIMUM_VELOCITY = -MAXIMUM_VELOCITY / 10f;

	static final int NUMBER_OF_SPINNING_ANIMATION_FRAMES = 16;

	private static final int PERIOD_FOR_SPINNING_ANIMATION_CYCLE = 1000;

	private final Context context;

	private FPSLogger fPSLogger = new FPSLogger(TAG, FRAMES_BETWEEN_LOGGING_FPS);

	private OpenGLGeometry[] allSpellsGeometry;
	
	private OpenGLGeometry[] allHazardsGeometry;

	private OpenGLGeometry skyboxGeometry;

	// Start a little back so that we aren't inside the pyramid.
	private Position playerWorldPosition = new Position(0, 0, 0);

	private float playerFacing;

	private float velocity;

	private long timeAtLastOnRenderCall;

	private final CollisionDetector collisionDetector = new CollisionDetector();

	private int score;

	private Carpet carpet;

	private List<OpenGLGeometry> decorationGeometries = new ArrayList<OpenGLGeometry>();
	
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

	public SevenWondersGLRenderer(final Context aContext, final Handler aUpdateUiHandler, final GameLevel aLevel) {
		Log.i(TAG, "SevenWondersGLRenderer()");
		context = aContext;
		carpet = new Carpet(this);
		level = aLevel;
		updateUiHandler = aUpdateUiHandler;

		openGLGeometryBuilder = OpenGLGeometryBuilderFactory.createTexturableNormalizable(60453);

		// load all of the decorations (land, water, sphinx, pyramids, etc.)
		int currentTextureResource = 0;
		for (GameObjectDescriptor objectDescriptor : level.getDecorations()) {
			// if the texture has changed (including the first time through), then ...
			if (objectDescriptor.textureResource != currentTextureResource) {
				// wrap up the existing geometry (if any, since first time through there won't be an existing geometry)
				if (openGLGeometryBuilder.isBuildingGeometry()) {
					final OpenGLGeometry previousGeometry = openGLGeometryBuilder.endGeometry();
					decorationGeometries.add(previousGeometry);
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
			decorationGeometries.add(lastGeometry);
		}
		
		openGLGeometryBuilder.startGeometry(getTexture(R.raw.skybox_texture, false));		
		loadRequiredObj(R.raw.skybox_model, openGLGeometryBuilder);
		skyboxGeometry = openGLGeometryBuilder.endGeometry();
		
		final GeometryAwareCollisionObserver spellsCollisionHandler = new SpellCollisionHandler(collisionDetector, updateUiHandler, this);
		allSpellsGeometry = addObjectsToGeometry(openGLGeometryBuilder, spellsCollisionHandler, level.getSpells(), R.raw.textures);
		final GeometryAwareCollisionObserver hazardCollisionObserver = new HazardCollisionHandler(updateUiHandler);
		allHazardsGeometry = addObjectsToGeometry(openGLGeometryBuilder, hazardCollisionObserver, level.getHazards(), R.raw.textures);
		carpet.createGeometry(openGLGeometryBuilder);

		// collision with an obstacle moves the player back to where
		// they were at the start of the frame, prior to any movement
		final CollisionObserver obstacleCollisionObserver = new CollisionObserver() {
			@Override
			public void collisionOccurred(final float[] aBoundingSphere) {
			    SoundTracks.getInstance().play(SoundTracks.BUMP);

				playerWorldPosition.x = startOfFramePlayerWorldPositionX; 
				playerWorldPosition.z = startOfFramePlayerWorldPositionZ; 
			}
		};
		for (final float[] boundingSphere : level.getObstacles()) {
			collisionDetector.addBoundingSphere(boundingSphere, obstacleCollisionObserver);
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
		aGl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
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
			OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder, final GeometryAwareCollisionObserver aCollisionObserverPrototype,
			final Collection<GameObjectDescriptor> anObjectDescriptorCollection, int aTextureResource) {

		// return null if there are no objects in the collection
		if (anObjectDescriptorCollection.isEmpty()) {
			return null;
		}

		// make one collision observer for each object by cloning the prototype passed in
		final List<GeometryAwareCollisionObserver> collisionObservers = new ArrayList<GeometryAwareCollisionObserver>(anObjectDescriptorCollection.size());
		try {
			for (final GameObjectDescriptor objectDescriptor : anObjectDescriptorCollection) {
				GeometryAwareCollisionObserver clone;
				clone = aCollisionObserverPrototype.clone();
				collisionObservers.add(clone);
			}
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		
		final Texture texture = getTexture(aTextureResource, true);

		final OpenGLGeometry[] objectGeometries = new OpenGLGeometry[NUMBER_OF_SPINNING_ANIMATION_FRAMES];

		final Map<Integer, ObjFileLoader> resourceIdToObjFileLoaderMap = new HashMap<Integer, ObjFileLoader>();

		// create a number of objects, in a number of orientations
		// final int numberOfObjects = objectDescriptorCollection.size();
		float[] coordinateTransform = new float[16];
		for (int animationIndex = 0; animationIndex < NUMBER_OF_SPINNING_ANIMATION_FRAMES; animationIndex++) {
			anOpenGLGeometryBuilder.startGeometry(texture);

			int objectIndex = 0;
			for (final GameObjectDescriptor objectDescriptor : anObjectDescriptorCollection) {
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

				// find the collision observer for this object
				final GeometryAwareCollisionObserver geometryAwareCollisionObserver = collisionObservers.get(objectIndex);

				// add the geometry to the collision observer (it just may care!)
				geometryAwareCollisionObserver.addGeometry(objectGeometry);

				// if this is frame 0, add the goemetry's bounding sphere to the collision detector
				if (animationIndex == ANIMATION_INDEX_FOR_COLLISION_DETECTION) {
					collisionDetector.addBoundingSphere(objectGeometry.getBoundingSphere(), geometryAwareCollisionObserver);
				}
				
				// next object
				objectIndex++;
			}
			objectGeometries[animationIndex] = anOpenGLGeometryBuilder.endGeometry();
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
		aGl.glViewport(0, 0, aW, aH);
		GLU.gluPerspective(aGl, 45, (float) aW / (float) aH, 0.1f, 5000f);

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
		aGl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		playerFacingThisFrame = playerFacing;
		
		drawCarpet(aGl);

		applyMovement(aGl);

		detectCollisions();
		
		for (int geometryIndex = 0; geometryIndex < decorationGeometries.size(); geometryIndex++) {
			final OpenGLGeometry geometry = decorationGeometries.get(geometryIndex);
			geometry.draw(aGl);
		}

		drawSpells(aGl);
		drawSwords(aGl);
		drawSkybox(aGl);
		
		Message msg = updateUiHandler.obtainMessage(PlayActivity.FPS_MESSAGE, fPSLogger.frameRendered(), 0);
		updateUiHandler.sendMessage(msg);
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

	private void drawSkybox(GL10 aGl) {	
		// save the current matrix for later - later? what later?
		aGl.glPushMatrix();

		// rotate the skybox to match the player's facing
		Matrix.setIdentityM(temporaryMatrix, 0);
		Matrix.rotateM(temporaryMatrix, 0, playerFacingThisFrame, 0, 1, 0);
		aGl.glLoadMatrixf(temporaryMatrix, 0);

		aGl.glDisable(GL10.GL_LIGHTING);
		aGl.glDisable(GL10.GL_LIGHT0);
		
		skyboxGeometry.draw(aGl);
		
		aGl.glEnable(GL10.GL_LIGHTING);
		aGl.glEnable(GL10.GL_LIGHT0);
		
		// restore the matrix
		aGl.glPopMatrix();
	}	

	private void detectCollisions() {
		float[] carpetBoundingBox = temporaryMatrix;
		// TODO should we use Matrix.orthoM()
		Matrix.frustumM(carpetBoundingBox, 0, -0.3f, 0.3f, HEIGHT_OF_CARPET_FROM_GROUND, HEIGHT_OF_CARPET_FROM_GROUND + 2f, 0.1f, 1f);

		// Rotate first, otherwise map rotates around center point we translated away from.
		Matrix.rotateM(carpetBoundingBox, 0, playerFacingThisFrame, 0, 1, 0);
		Matrix.translateM(carpetBoundingBox, 0, -playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);

		if(!paused) {
			collisionDetector.detectCollisions(carpetBoundingBox);
		}
	}

	private void applyMovement(final GL10 aGl) {	
		// keep the old position in case a collision with an obstacle requires a "movement rollback"
		startOfFramePlayerWorldPositionX = playerWorldPosition.x;
		startOfFramePlayerWorldPositionZ = playerWorldPosition.z;

		final long timeDeltaMS = calculateTimeSinceLastRenderMillis();

		final float facingX = (float) Math.sin(playerFacingThisFrame / 180f * Math.PI);
		final float facingZ = -(float) Math.cos(playerFacingThisFrame / 180f * Math.PI);

		if ( !paused ) {
			final float newPositionX = playerWorldPosition.x + facingX * velocity * timeDeltaMS;
			final float newPositionZ = playerWorldPosition.z + facingZ * velocity * timeDeltaMS;
	
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
		GLU.gluLookAt(aGl, playerWorldPosition.x, HEIGHT_OF_CARPET_FROM_GROUND, playerWorldPosition.z, playerWorldPosition.x
				+ facingX, HEIGHT_OF_CARPET_FROM_GROUND, playerWorldPosition.z + facingZ, 0f, 1f, 0f);
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

	public synchronized void setPlayerVelocity(int aNewVelocity) {
		if ( paused ) {
			return;
		}
		velocity = aNewVelocity;
	}

	public synchronized void turn(float anAngleOfTurn) {
		if ( paused ) {
			return;
		}
		playerFacing += anAngleOfTurn;
		carpet.recordTurnAngle(anAngleOfTurn);
	}

	public synchronized void setPlayerFacing(float anAngleAbosulte) {
		if ( paused ) {
			return;
		}
		playerFacing = anAngleAbosulte;
	}

	public synchronized void changeVelocity(float aVelocityIncrement) {
		if ( paused ) {
			return;
		}
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, velocity + aVelocityIncrement));
	}

	public synchronized void setVelocity(float aVelocity) {
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
	
	protected int incrementScore(final int anIncrement) {
		score += anIncrement;
		return score;
	}
}
