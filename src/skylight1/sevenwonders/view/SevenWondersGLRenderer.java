package skylight1.sevenwonders.view;

import static javax.microedition.khronos.opengles.GL10.GL_CCW;
import static javax.microedition.khronos.opengles.GL10.GL_CW;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.GeometryBuilder;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.OpenGLGeometryBuilderFactory;
import skylight1.opengl.Texture;
import skylight1.opengl.TransformingGeometryBuilder;
import skylight1.opengl.CollisionDetector.CollisionObserver;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.opengl.files.ObjFileLoader;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.R;
import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.levels.GameObjectDescriptor;
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class SevenWondersGLRenderer implements Renderer {

	static final int ANIMATION_INDEX_FOR_COLLISION_DETECTION = 0;

	public static final float MAXIMUM_VELOCITY = 300f * 1000f / 60f / 60f / 1000f;

	private static final int WORLD_SWORDS_MARGIN = 200;

	private static final int END_OF_WORLD_MARGIN = 100;

	private static final String TAG = SevenWondersGLRenderer.class.getName();

	private static final float HEIGHT_OF_CARPET_FROM_GROUND = 12f;

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final float MINIMUM_VELOCITY = -MAXIMUM_VELOCITY / 10f;

	static final int NUMBER_OF_SPINNING_ANIMATION_FRAMES = 16;

	private static final int PERIOD_FOR_SPINNING_ANIMATION_CYCLE = 1000;

	private final Context context;

	private Texture atlasTexture;

	private Texture sphinxTexture;

	private FPSLogger fPSLogger = new FPSLogger(SevenWondersGLRenderer.class.getName(), FRAMES_BETWEEN_LOGGING_FPS);

	private OpenGLGeometry worldGeometry;

	private OpenGLGeometry[] allSpellsGeometry;

	private OpenGLGeometry[] swordGeometries = new OpenGLGeometry[NUMBER_OF_SPINNING_ANIMATION_FRAMES];

	private OpenGLGeometry sphinxGeometry;

	private OpenGLGeometry pyramidGeometry;

	// Start a little back so that we aren't inside the pyramid.
	private Position playerWorldPosition = new Position(0, 0, 200);

	private float playerFacing;

	private float velocity;

	private long timeAtLastOnRenderCall;

	private final CollisionDetector collisionDetector = new CollisionDetector();

	private int score;

	private Carpet carpet;

	private OpenGLGeometry[] pyramidGeometries = new OpenGLGeometry[3];

	private final Handler updateUiHandler;

	private final GameLevel level;

	public SevenWondersGLRenderer(final Context aContext, final Handler aUpdateUiHandler, final GameLevel aLevel) {
		Log.i(TAG, "SevenWondersGLRenderer()");
		context = aContext;
		carpet = new Carpet(this);
		level = aLevel;
		updateUiHandler = aUpdateUiHandler;
	}

	public void onSurfaceCreated(final GL10 aGl, final EGLConfig aConfig) {
		Log.i(TAG, "- onSurfaceCreated - ");

		final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder = OpenGLGeometryBuilderFactory.createTexturableNormalizable();

		// Add ground and pyramid to a single drawable geometry for the world.
		openGLGeometryBuilder.startGeometry();
		loadRequiredObj(R.raw.ground, openGLGeometryBuilder);
		worldGeometry = openGLGeometryBuilder.endGeometry();

		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, 90, 0, 1, 0);
		float[] textureTransform = new float[16];
		Matrix.setIdentityM(textureTransform, 0);

		openGLGeometryBuilder.startGeometry();
		TransformingGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> transformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(openGLGeometryBuilder, coordinateTransform, textureTransform);
		loadRequiredObj(R.raw.sphinx_scaled, transformingGeometryBuilder);
		sphinxGeometry = openGLGeometryBuilder.endGeometry();

		openGLGeometryBuilder.startGeometry();
		pyramidGeometries[0] = addPyramid(openGLGeometryBuilder, 90, 0, 5);
		pyramidGeometries[1] = addPyramid(openGLGeometryBuilder, 255, 0, -2);
		pyramidGeometries[2] = addPyramid(openGLGeometryBuilder, -320, -7, 100);
		pyramidGeometry = openGLGeometryBuilder.endGeometry();

		final SpellCollisionHandler spellsCollisionHandler = new SpellCollisionHandler(collisionDetector, level, updateUiHandler, this);
		allSpellsGeometry = addSpellsToGeometry(openGLGeometryBuilder, spellsCollisionHandler, level.getSpells());
		addSwordToGeometry(openGLGeometryBuilder);
		carpet.createGeometry(openGLGeometryBuilder);
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

	private OpenGLGeometry addPyramid(
			final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder,
			final float x, final float y, final float z) {
		float[] pyramidCoordinateTransform = new float[16];
		Matrix.setIdentityM(pyramidCoordinateTransform, 0);
		Matrix.translateM(pyramidCoordinateTransform, 0, x, y, z);
		float[] pyramidTextureTransform = new float[16];
		Matrix.setIdentityM(pyramidTextureTransform, 0);

		openGLGeometryBuilder.startGeometry();
		TransformingGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> pyramidTransformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(openGLGeometryBuilder, pyramidCoordinateTransform, pyramidTextureTransform);
		loadRequiredObj(R.raw.pyramid, pyramidTransformingGeometryBuilder);
		return openGLGeometryBuilder.endGeometry();
	}

	private static final float[] fillRandom(final float min, final float max, final float[] dest) {
		if (null == dest) {
			return null;
		}

		float range = max - min + 1;
		for (int i = 0; i < dest.length; i++) {
			dest[i] = (float) (min + Math.random() * range);
		}
		return dest;
	}

	private static final float getRandom(final float min, final float max) {
		float range = max - min + 1;
		return (float) (min + Math.random() * range);
	}

	private void addSwordToGeometry(
			OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> openGLGeometryBuilder) {

		final ObjFileLoader fileLoader;
		try {
			fileLoader = new ObjFileLoader(context, R.raw.textured_sword);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// create random location for the sword
		final float minX1 = (CubeBounds.TERRAIN.x1 + WORLD_SWORDS_MARGIN);
		final float minX2 = (CubeBounds.TERRAIN.x2 - WORLD_SWORDS_MARGIN);
		final float minZ1 = (CubeBounds.TERRAIN.z1 + WORLD_SWORDS_MARGIN);
		final float minZ2 = (CubeBounds.TERRAIN.z2 - WORLD_SWORDS_MARGIN);
		final float randomX = getRandom(minX1, minX2);
		final float randomZ = getRandom(minZ1, minZ2);

		// create a number of spells, in a number of orientations
		for (int animationIndex = 0; animationIndex < NUMBER_OF_SPINNING_ANIMATION_FRAMES; animationIndex++) {
			openGLGeometryBuilder.startGeometry();
			float[] coordinateTransform = new float[16];

			Matrix.setIdentityM(coordinateTransform, 0);
			// Sword placed randomly - using CubeBounds for the width of the world

			Matrix.translateM(coordinateTransform, 0, randomX, HEIGHT_OF_CARPET_FROM_GROUND - 1, randomZ);
			Matrix.rotateM(coordinateTransform, 0, 360f * (float) animationIndex
					/ (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES, 0, 1, 0);
			Matrix.rotateM(coordinateTransform, 0, -90f, 1, 0, 0);
			TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> transformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(openGLGeometryBuilder, coordinateTransform, null);
			fileLoader.createGeometry(transformingGeometryBuilder);

			swordGeometries[animationIndex] = openGLGeometryBuilder.endGeometry();
		}

		// add to collision detector
		collisionDetector.addGeometry(swordGeometries[0], new CollisionObserver() {
			@Override
			public void collisionOccurred(OpenGLGeometry anOpenGLGeometry) {
				Log.i(SevenWondersGLRenderer.class.getName(), String.format("Player hit a sword!"));
				updateUiHandler.sendEmptyMessage(PlayActivity.END_GAME_MESSAGE);
			}
		});
	}

	private OpenGLGeometry[] addSpellsToGeometry(
			OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> openGLGeometryBuilder, final SpellCollisionHandler aCollisionObserver,
			final Collection<GameObjectDescriptor> objectDescriptorCollection) {

		final OpenGLGeometry[] objectGeometries = new OpenGLGeometry[NUMBER_OF_SPINNING_ANIMATION_FRAMES];

		final Map<Integer, ObjFileLoader> resourceIdToObjFileLoaderMap = new HashMap<Integer, ObjFileLoader>();

		// create a number of objects, in a number of orientations
		final int numberOfObjects = objectDescriptorCollection.size();
		float[] coordinateTransform = new float[16];
		for (int animationIndex = 0; animationIndex < NUMBER_OF_SPINNING_ANIMATION_FRAMES; animationIndex++) {
			openGLGeometryBuilder.startGeometry();

			int objectIndex = 0;
			for (final GameObjectDescriptor objectDescriptor : objectDescriptorCollection) {
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
				openGLGeometryBuilder.startGeometry();
				
				System.arraycopy(objectDescriptor.coordinateTransformationMatrix, 0, coordinateTransform, 0, coordinateTransform.length);

				Matrix.rotateM(coordinateTransform, 0, 360f * (float) animationIndex
						/ (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES, 0, 1, 0);
				// this final rotate is to "stand up" the ankh
				Matrix.rotateM(coordinateTransform, 0, -90f, 1, 0, 0);

				// load the object into the geometry
				TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> transformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(openGLGeometryBuilder, coordinateTransform, objectDescriptor.textureTransformationMatrix);
				objFileLoader.createGeometry(transformingGeometryBuilder);
				final OpenGLGeometry objectGeometry = openGLGeometryBuilder.endGeometry();

				// add it to the collision observer (it may care!)
				aCollisionObserver.addGeometry(objectGeometry, animationIndex, objectIndex++);
				
				// if this is frame 0, add it to the collision detector
				if (animationIndex == ANIMATION_INDEX_FOR_COLLISION_DETECTION) {
					collisionDetector.addGeometry(objectGeometry, aCollisionObserver);
				}
			}
			objectGeometries[animationIndex] = openGLGeometryBuilder.endGeometry();
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
		GLU.gluPerspective(aGl, 45, (float) aW / (float) aH, 0.1f, 1000f);

		aGl.glMatrixMode(GL10.GL_MODELVIEW);

		// if the surface changed from a prior surface, such as a change of orientation, then free the prior texture
		if (sphinxTexture != null) {
			sphinxTexture.freeTexture();
			sphinxTexture = null;
		}
		sphinxTexture = new Texture(aGl, context, R.raw.sphinx, true);

		if (atlasTexture != null) {
			atlasTexture.freeTexture();
			atlasTexture = null;
		}
		atlasTexture = new Texture(aGl, context, R.raw.textures);

		atlasTexture.activateTexture();

		updateUiHandler.sendEmptyMessage(PlayActivity.START_RENDERING_MESSAGE);
	}

	public void onDrawFrame(final GL10 aGl) {
		aGl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Carpet drawn with no transformations, always right in front of the screen.
		aGl.glLoadIdentity();
		// Drawn first for performance, might occlude other geometry, which OpenGL can then skip.
		// XXX Hack to fix the carpet being drawn face down. Should probably change geometry or disable culling for the
		// carpet instead.
		aGl.glFrontFace(GL_CW);

		carpet.draw(aGl);

		aGl.glFrontFace(GL_CCW);

		applyMovement(aGl);

		detectCollisions();

		worldGeometry.draw(aGl);
		drawSphinx(aGl);
		drawPyramid(aGl);
		drawSpell(aGl);
		drawSword(aGl);

		Message msg = updateUiHandler.obtainMessage(PlayActivity.FPS_MESSAGE, fPSLogger.frameRendered(), 0);
		updateUiHandler.sendMessage(msg);
	}

	private void detectCollisions() {
		float[] carpetBoundingBox = new float[16];
		// TODO should we use Matrix.orthoM()
		Matrix.frustumM(carpetBoundingBox, 0, -0.5f, 0.5f, HEIGHT_OF_CARPET_FROM_GROUND, HEIGHT_OF_CARPET_FROM_GROUND + 2f, 0.1f, 1f);

		// Rotate first, otherwise map rotates around center point we translated away from.
		Matrix.rotateM(carpetBoundingBox, 0, playerFacing, 0, 1, 0);
		Matrix.translateM(carpetBoundingBox, 0, -playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);

		collisionDetector.detectCollisions(carpetBoundingBox);
	}

	private void applyMovement(final GL10 aGl) {
		final long timeDeltaMS = calculateTimeSinceLastRenderMillis();

		final float facingX = (float) Math.sin(playerFacing / 180f * Math.PI);
		final float facingZ = -(float) Math.cos(playerFacing / 180f * Math.PI);

		final float newPositionX = playerWorldPosition.x + facingX * velocity * timeDeltaMS;
		final float newPositionZ = playerWorldPosition.z + facingZ * velocity * timeDeltaMS;

		// TODO: CHECK FOR END OF WORLD
		if (newPositionX > (CubeBounds.TERRAIN.x1 + END_OF_WORLD_MARGIN)
				&& newPositionX < (CubeBounds.TERRAIN.x2 - END_OF_WORLD_MARGIN)) {
			// playerWorldPosition.x += facingX * velocity * timeDeltaMS;
			playerWorldPosition.x = newPositionX;
		}

		if (newPositionZ > (CubeBounds.TERRAIN.z1 + END_OF_WORLD_MARGIN)
				&& newPositionZ < (CubeBounds.TERRAIN.z2 - END_OF_WORLD_MARGIN)) {
			// playerWorldPosition.z += facingZ * velocity * timeDeltaMS;
			playerWorldPosition.z = newPositionZ;
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

	private void drawSphinx(final GL10 aGl) {
		sphinxTexture.activateTexture();
		// Translate a bit so sphinx isn't inside the pyramid.
		// XXX Since this is permanent, we could actually alter the geometry instead.
		aGl.glPushMatrix();
		aGl.glTranslatef(-100, -25, 0);
		sphinxGeometry.draw(aGl);
		aGl.glPopMatrix();
		atlasTexture.activateTexture();
	}

	private void drawPyramid(final GL10 aGl) {
		pyramidGeometry.draw(aGl);
	}

	private void drawSpell(final GL10 aGl) {
		final int spellAnimationIndex = (int) ((float) (SystemClock.uptimeMillis() % PERIOD_FOR_SPINNING_ANIMATION_CYCLE)
				/ (float) PERIOD_FOR_SPINNING_ANIMATION_CYCLE * (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES);
		allSpellsGeometry[spellAnimationIndex].draw(aGl);
	}

	private void drawSword(final GL10 aGl) {
		final int swordAnimationIndex = (int) ((float) (SystemClock.uptimeMillis() % PERIOD_FOR_SPINNING_ANIMATION_CYCLE)
				/ (float) PERIOD_FOR_SPINNING_ANIMATION_CYCLE * (float) NUMBER_OF_SPINNING_ANIMATION_FRAMES);
		swordGeometries[swordAnimationIndex].draw(aGl);
	}

	public void setPlayerVelocity(int aNewVelocity) {
		velocity = aNewVelocity;
	}

	public void turn(float anAngleOfTurn) {
		playerFacing += anAngleOfTurn;
		carpet.recordTurnAngle(anAngleOfTurn);
	}

	public void setPlayerFacing(float anAngleAbosulte) {
		playerFacing = anAngleAbosulte;
	}

	public void changeVelocity(float aVelocityIncrement) {
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, velocity + aVelocityIncrement));
	}

	public void setVelocity(float aVelocity) {
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, aVelocity));
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
