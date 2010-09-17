package skylight1.sevenwonders.view;

import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_CCW;
import static javax.microedition.khronos.opengles.GL10.GL_CW;
import static skylight1.sevenwonders.view.GameTexture.SPELL;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.FastGeometryBuilder;
import skylight1.opengl.FastGeometryBuilderFactory;
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
import skylight1.sevenwonders.R;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

public class SevenWondersGLRenderer implements Renderer {

	private static final int PERIOD_FOR_CARPET_ANIMATION_CYCLE = 800;

	public static interface ScoreObserver {
		void observerNewScore(int aNewScore);
	}

	private RendererListener rendererListener;

	private static final String TAG = SevenWondersGLRenderer.class.getName();

	public static final float INITIAL_VELOCITY = 35f * 1000f / 60f / 60f / 1000f;

	private static final float HEIGHT_OF_CARPET_FROM_GROUND = 12f;

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final int TERRAIN_MAP_RESOURCE = R.raw.terrain_dunes;

	private static final int TERRAIN_DENSITY = 25;

	private static final int[] CARPET_OBJ_IDS = new int[] { R.raw.carpet_wave_0, R.raw.carpet_wave_1,
			R.raw.carpet_wave_2, R.raw.carpet_wave_3, R.raw.carpet_wave_4, R.raw.carpet_wave_5, R.raw.carpet_wave_6,
			R.raw.carpet_wave_7, R.raw.carpet_wave_8, R.raw.carpet_wave_9 };

	private static final float MINIMUM_VELOCITY = -INITIAL_VELOCITY / 10f;

	private static final float MAXIMUM_VELOCITY = INITIAL_VELOCITY * 3f;

	private static final int NUMBER_OF_SPELLS = 10;

	private static final int NUMBER_OF_SPELL_ANIMATION_FRAMES = 16;

	private static final int PERIOD_FOR_SPELL_ANIMATION_CYCLE = 1000;

	private FastGeometryBuilder<?, ?> somewhereFarFarAway;

	private final Context context;

	private Texture atlasTexture;

	private Texture sphinxTexture;

	private FPSLogger fPSLogger = new FPSLogger(SevenWondersGLRenderer.class.getName(), FRAMES_BETWEEN_LOGGING_FPS);

	private OpenGLGeometry worldGeometry;

	private OpenGLGeometry[] carpetGeometry;

	private OpenGLGeometry[] allSpellsGeometry = new OpenGLGeometry[NUMBER_OF_SPELL_ANIMATION_FRAMES];

	private OpenGLGeometry[][] spellGeometries = new OpenGLGeometry[NUMBER_OF_SPELL_ANIMATION_FRAMES][];

	private OpenGLGeometry sphinxGeometry;

	private OpenGLGeometry pyramidGeometry;

	// Start a little back so that we aren't inside the pyramid.
	private Position playerWorldPosition = new Position(0, 0, 200);

	private float playerFacing;

	/*
	 * private float angYaw; private float angPitch; private float angRoll;
	 */

	private float velocity = INITIAL_VELOCITY;

	/*
	 * private float velocityX = INITIAL_VELOCITY; private float velocityY = 0; private float velocityZ = 0;
	 */
	private long timeAtLastOnRenderCall;

	private CollisionDetector collisionDetector;

	private int score;

	private ScoreObserver scoreObserver;

	public SevenWondersGLRenderer(Context aContext, ScoreObserver aScoreObserver) {
		context = aContext;
		scoreObserver = aScoreObserver;
	}

	public void onSurfaceCreated(final GL10 aGl, final EGLConfig aConfig) {
		Log.i(TAG, "- onSurfaceCreated - ");

		final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder = OpenGLGeometryBuilderFactory.createTexturableNormalizable();

		// Add ground and pyramid to a single drawable geometry for the world.
		openGLGeometryBuilder.startGeometry();

		addGroundToGeometry(openGLGeometryBuilder);

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
		// loadRequiredObj(R.raw.pyramid, transformingGeometryBuilder);
		loadRequiredObj(R.raw.pyramid, openGLGeometryBuilder);
		pyramidGeometry = openGLGeometryBuilder.endGeometry();

		addSpellsToGeometry(openGLGeometryBuilder);

		// Add carpet to a separate drawable geometry.
		// Allows the world to be translated separately from the carpet later.
		addCarpetToGeometry(openGLGeometryBuilder);

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

	private void addSpellsToGeometry(
			OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> openGLGeometryBuilder) {
		collisionDetector = new CollisionDetector();

		// create a number of spells

		final ObjFileLoader fileLoader;
		try {
			fileLoader = new ObjFileLoader(context, R.raw.spellobject);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (int spellAnimationIndex = 0; spellAnimationIndex < NUMBER_OF_SPELL_ANIMATION_FRAMES; spellAnimationIndex++) {
			openGLGeometryBuilder.startGeometry();
			spellGeometries[spellAnimationIndex] = new OpenGLGeometry[NUMBER_OF_SPELLS];
			float[] coordinateTransform = new float[16];
			float[] textureTransform = new float[16];
			Matrix.setIdentityM(textureTransform, 0);
			for (int spellIndex = 0; spellIndex < NUMBER_OF_SPELLS; spellIndex++) {
				openGLGeometryBuilder.startGeometry();
				Matrix.setIdentityM(coordinateTransform, 0);
				Matrix.translateM(coordinateTransform, 0, -25, HEIGHT_OF_CARPET_FROM_GROUND, 25 - spellIndex * 25);
				Matrix.rotateM(coordinateTransform, 0, 180f * (float) spellAnimationIndex
						/ (float) NUMBER_OF_SPELL_ANIMATION_FRAMES, 0, 1, 0);
				TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> transformingGeometryBuilder = new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(openGLGeometryBuilder, coordinateTransform, textureTransform);
				fileLoader.createGeometry(transformingGeometryBuilder);

				final OpenGLGeometry spellGeometry = openGLGeometryBuilder.endGeometry();
				spellGeometries[spellAnimationIndex][spellIndex] = spellGeometry;
			}
			allSpellsGeometry[spellAnimationIndex] = openGLGeometryBuilder.endGeometry();
		}

		// add to collision detector
		for (int spellIndex = 0; spellIndex < NUMBER_OF_SPELLS; spellIndex++) {
			final int finalSpellIndex = spellIndex;
			
			collisionDetector.addGeometry(spellGeometries[0][spellIndex], new CollisionObserver() {
				@Override
				public void collisionOccurred(OpenGLGeometry anOpenGLGeometry) {
					Log.i(SevenWondersGLRenderer.class.getName(), String.format("collided with " + anOpenGLGeometry));

					collisionDetector.removeGeometry(anOpenGLGeometry);

					for (int spellAnimationIndex = 0; spellAnimationIndex < NUMBER_OF_SPELL_ANIMATION_FRAMES; spellAnimationIndex++) {
						spellGeometries[spellAnimationIndex][finalSpellIndex].updateModel(somewhereFarFarAway);
					}

					// add one to the score for colliding with a spell
					score++;

					// notify the observer
					scoreObserver.observerNewScore(score);

					SoundTracks.getInstance().play(SoundTracks.SPELL);
				}
			});
		}

		// create a fast geometry that is out of sight
		somewhereFarFarAway = FastGeometryBuilderFactory.createTexturableNormalizable(spellGeometries[0][0]);
		somewhereFarFarAway.add3DTriangle(0, 0, -100, 0, 0, -100, 0, 0, -100);
		somewhereFarFarAway.add3DTriangle(0, 0, -100, 0, 0, -100, 0, 0, -100);
	}

	private void addGroundToGeometry(
			final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		final Terrain terrain = new Terrain(TERRAIN_MAP_RESOURCE, CubeBounds.TERRAIN);
		terrain.addToGeometry(context, GameTexture.SAND, TERRAIN_DENSITY, anOpenGLGeometryBuilder);
	}

	private void loadRequiredObj(
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

	private void addCarpetToGeometry(
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		carpetGeometry = new OpenGLGeometry[CARPET_OBJ_IDS.length];
		for (int i = 0; i < CARPET_OBJ_IDS.length; i++) {
			anOpenGLGeometryBuilder.startGeometry();
			loadRequiredObj(CARPET_OBJ_IDS[i], anOpenGLGeometryBuilder);
			carpetGeometry[i] = anOpenGLGeometryBuilder.endGeometry();
		}
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

		if (rendererListener != null) {
			rendererListener.startedRendering();
		}
	}

	public void onDrawFrame(final GL10 aGl) {
		aGl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Carpet drawn with no transformations, always right in front of the screen.
		aGl.glLoadIdentity();
		// Drawn first for performance, might occlude other geometry, which OpenGL can then skip.
		// XXX Hack to fix the carpet being drawn face down. Should probably change geometry or disable culling for the
		// carpet instead.
		aGl.glFrontFace(GL_CW);
		final int carpetIndex = (int) ((SystemClock.uptimeMillis() % PERIOD_FOR_CARPET_ANIMATION_CYCLE) / (PERIOD_FOR_CARPET_ANIMATION_CYCLE / carpetGeometry.length));
		carpetGeometry[carpetIndex].draw(aGl);
		aGl.glFrontFace(GL_CCW);

		applyMovement(aGl);

		detectCollisions();

		worldGeometry.draw(aGl);
		drawSphinx(aGl);
		drawPyramid(aGl, 90, 0, 5);
		drawPyramid(aGl, 255, 0, -2);
		drawPyramid(aGl, -320, -7, 100);

		drawSpell(aGl);

		rendererListener.drawFPS(fPSLogger.frameRendered());
	}

	private void detectCollisions() {
		float[] carpetBoundingBox = new float[16];
		// TODO should we use Matrix.orthoM()
		Matrix.frustumM(carpetBoundingBox, 0, -0.5f, 0.5f, HEIGHT_OF_CARPET_FROM_GROUND, HEIGHT_OF_CARPET_FROM_GROUND + 2f, 0.1f, 1f);
		// Log.i(SevenWondersGLRenderer.class.getName(), "carpet frustum is " + Arrays.toString(carpetFrustum));

		// Rotate first, otherwise map rotates around center point we translated away from.
		Matrix.rotateM(carpetBoundingBox, 0, playerFacing, 0, 1, 0);
		Matrix.translateM(carpetBoundingBox, 0, -playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);

		collisionDetector.detectCollisions(carpetBoundingBox);
	}

	private void applyMovement(final GL10 aGl) {
		final long timeDeltaMS = calculateTimeSinceLastRenderMillis();

		final float facingX = (float) Math.sin(playerFacing / 180f * Math.PI);
		final float facingZ = -(float) Math.cos(playerFacing / 180f * Math.PI);
		playerWorldPosition.x += facingX * velocity * timeDeltaMS;
		playerWorldPosition.z += facingZ * velocity * timeDeltaMS;

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

	private void drawPyramid(final GL10 aGl, final int x, final int y, final int z) {

		aGl.glPushMatrix();
		aGl.glTranslatef(x, y, z);

		pyramidGeometry.draw(aGl);

		aGl.glPopMatrix();
	}

	private void drawSpell(final GL10 aGl) {

		// The spell only has one surface at the moment, that we want to be visible from both sides.
		aGl.glDisable(GL10.GL_CULL_FACE);
		// Disable depth writing so that transparent pixels don't block things behind them.
		aGl.glDepthMask(false);
		aGl.glEnable(GL_BLEND);

		final int spellAnimationIndex = (int) ((float) (SystemClock.uptimeMillis() % PERIOD_FOR_SPELL_ANIMATION_CYCLE) / (float) PERIOD_FOR_SPELL_ANIMATION_CYCLE * (float) NUMBER_OF_SPELL_ANIMATION_FRAMES);
		allSpellsGeometry[spellAnimationIndex].draw(aGl);

		aGl.glDisable(GL_BLEND);
		aGl.glDepthMask(true);
		aGl.glEnable(GL10.GL_CULL_FACE);
	}

	/*
	 * public void setPlayerVelocity(int aNewVelocity) { velocityX = aNewVelocity; velocityY = 0; velocityZ = 0; }
	 */
	public void setPlayerVelocity(int aNewVelocity) {
		velocity = aNewVelocity;
	}

	/*
	 * public void turn(float yaw, float pitch, float roll) { angYaw += yaw; angPitch += pitch; angRoll += roll; //
	 * Log.i("angle now ", "" + yaw); // Log.i("angle now ", "" + pitch); // Log.i("angle now ", "" + roll); }
	 */
	public void turn(float anAngleOfTurn) {
		playerFacing += anAngleOfTurn;
		// Log.i("angle now ", "" + playerFacing);
	}

	/*
	 * public void setPlayerFacing(float yaw, float pitch, float roll){ angYaw = yaw; angPitch = pitch; angRoll = roll;
	 * }
	 */
	public void setPlayerFacing(float anAngleAbosulte) {
		playerFacing = anAngleAbosulte;

	}

	public void changeVelocity(float aVelocityIncrement) {
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, velocity + aVelocityIncrement));
		// Log.i("velocity now ", "" + velocity);
	}

	public void setVelocity(float aVelocity) {
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, aVelocity));
		// Log.i("velocity now ", "" + velocity);
	}

	public void setRendererListener(RendererListener rendererListener2) {
		rendererListener = rendererListener2;
	}

}
