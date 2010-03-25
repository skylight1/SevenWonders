package skylight1.sevenwonders.view;

import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_CCW;
import static javax.microedition.khronos.opengles.GL10.GL_CW;
import static skylight1.sevenwonders.view.GameTexture.SPELL;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.GeometryBuilder;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.OpenGLGeometryBuilderFactory;
import skylight1.opengl.Texture;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.opengl.files.ObjFileLoader;
import skylight1.sevenwonders.R;
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

public class SevenWondersGLRenderer implements Renderer {

	private RendererListener rendererListener;

	private static final String TAG = SevenWondersGLRenderer.class.getName();

	public static final float INITIAL_VELOCITY = 35f * 1000f / 60f / 60f / 1000f;

	private static final boolean LOG = false;

	private static final float HEIGHT_OF_CARPET_FROM_GROUND = 10f;

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final int TERRAIN_MAP_RESOURCE = R.raw.terrain_dunes;

	private static final int TERRAIN_DENSITY = 25;

	private static final int[] CARPET_OBJ_IDS = new int[] {
		R.raw.carpet_wave_0, R.raw.carpet_wave_1, R.raw.carpet_wave_2, R.raw.carpet_wave_3, R.raw.carpet_wave_4,
		R.raw.carpet_wave_5, R.raw.carpet_wave_6, R.raw.carpet_wave_7, R.raw.carpet_wave_8, R.raw.carpet_wave_9
	};

	private static final float MINIMUM_VELOCITY = -INITIAL_VELOCITY / 10f;

	private static final float MAXIMUM_VELOCITY = INITIAL_VELOCITY * 3f;

	private final Context context;

	private Texture atlasTexture;

	private Texture sphinxTexture;

	private FPSLogger fPSLogger = new FPSLogger(SevenWondersGLRenderer.class.getName(), FRAMES_BETWEEN_LOGGING_FPS);

	private OpenGLGeometry worldGeometry;

	private OpenGLGeometry[] carpetGeometry;

	private OpenGLGeometry spellGeometry;

	private OpenGLGeometry sphinxGeometry;

	//Start a little back so that we aren't inside the pyramid.
	private Position playerWorldPosition = new Position(0, 0, 200);

	private float playerFacing;

	private float velocity = INITIAL_VELOCITY;

	private long timeAtLastOnRenderCall;

	public SevenWondersGLRenderer(Context aContext) {
		context = aContext;
	}

	public void onSurfaceCreated(final GL10 aGl, final EGLConfig aConfig) {

		Log.i(TAG,"- onSurfaceCreated - ");

		final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder = OpenGLGeometryBuilderFactory
				.createTexturableNormalizable();

		//Add ground and pyramid to a single drawable geometry for the world.
		openGLGeometryBuilder.startGeometry();
		addGroundToGeometry(openGLGeometryBuilder);
		loadRequiredObj(R.raw.pyramid, openGLGeometryBuilder);
		worldGeometry = openGLGeometryBuilder.endGeometry();

		sphinxGeometry = loadRequiredObj(R.raw.sphinx_scaled, openGLGeometryBuilder);

		addSpellsToGeometry(openGLGeometryBuilder);

		//Add carpet to a separate drawable geometry.
		//Allows the world to be translated separately from the carpet later.
		addCarpetToGeometry(openGLGeometryBuilder);

		openGLGeometryBuilder.enable(aGl);

		aGl.glColor4f(1, 1, 1, 1);
		aGl.glClearColor(0.5f, 0.5f, 1, 1.0f);

		//Don't draw inside facing surfaces on things like the pyramid and sphinx.
		//This fixes a z-fighting issue: At long distances OpenGL doesn't have enough precision in the depth buffer
		//to tell if the front is closer or if the inside of a nearby back surface is closer and gets it wrong some times.
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
		openGLGeometryBuilder.startGeometry();
		float spellX = -25;
		float spellY = HEIGHT_OF_CARPET_FROM_GROUND;
		float spellZ = 25;
		float spellEdgeLength = 4;
		final float xLeft = spellX - spellEdgeLength / 2f;
		final float xRight = spellX + spellEdgeLength / 2f;
		openGLGeometryBuilder.add3DTriangle(xLeft, spellY, spellZ, xRight, spellY, spellZ, xRight, spellY + spellEdgeLength, spellZ).setTextureCoordinates(SPELL.s1, SPELL.t2, SPELL.s2, SPELL.t2, SPELL.s2, SPELL.t1);
		openGLGeometryBuilder.add3DTriangle(xLeft, spellY, spellZ, xRight, spellY + spellEdgeLength, spellZ, xLeft, spellY + spellEdgeLength, spellZ).setTextureCoordinates(SPELL.s1, SPELL.t2, SPELL.s2, SPELL.t1, SPELL.s1, SPELL.t1);;
		spellGeometry = openGLGeometryBuilder.endGeometry();
	}

	private void addGroundToGeometry(
			final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		final Terrain terrain = new Terrain(TERRAIN_MAP_RESOURCE, CubeBounds.TERRAIN);
		terrain.addToGeometry(context, GameTexture.SAND, TERRAIN_DENSITY, anOpenGLGeometryBuilder);
	}

	private OpenGLGeometry loadRequiredObj(final int aObjId, final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> aBuilder) {
		final ObjFileLoader loader;
		try {
			loader = new ObjFileLoader(context, aObjId);
		} catch (IOException e) {
			throw new RuntimeException("Error loading required geometry from OBJ file:" + aObjId, e);
		}
		return loader.createGeometry(aBuilder);
	}

	private void addCarpetToGeometry(
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		carpetGeometry = new OpenGLGeometry[CARPET_OBJ_IDS.length];
		for(int i = 0; i < CARPET_OBJ_IDS.length; i++ ) {
			carpetGeometry[i] = loadRequiredObj(CARPET_OBJ_IDS[i], anOpenGLGeometryBuilder);
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
	}

	public void onDrawFrame(final GL10 aGl) {
		aGl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		//Carpet drawn with no transformations, always right in front of the screen.
		aGl.glLoadIdentity();
		//Drawn first for performance, might occlude other geometry, which OpenGL can then skip.
		//XXX Hack to fix the carpet being drawn face down. Should probably change geometry or disable culling for the carpet instead.
		aGl.glFrontFace(GL_CW);
		final int carpetIndex = (int)((SystemClock.uptimeMillis() % 800+1) /100f);
		carpetGeometry[carpetIndex].draw(aGl);
		aGl.glFrontFace(GL_CCW);

		applyMovement(aGl);

		worldGeometry.draw(aGl);
		drawSphinx(aGl);
		drawSpell(aGl);

		if(!fPSLogger.isStarted()) {
			rendererListener.startedRendering();
		}
		fPSLogger.frameRendered();
	}

	private void applyMovement(final GL10 aGl) {
        final long timeDeltaMS = calculateTimeSinceLastRenderMillis();
		playerWorldPosition.x += Math.sin( playerFacing / 180f * Math.PI ) * velocity * timeDeltaMS;
        playerWorldPosition.z += Math.cos( playerFacing / 180f * Math.PI ) * velocity * timeDeltaMS;
        if ( LOG ) Log.i(TAG, playerWorldPosition + ", " + playerFacing);

        aGl.glTranslatef(-playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);
		aGl.glRotatef(-playerFacing, 0, 1, 0);
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
		//Translate a bit so sphinx isn't inside the pyramid.
		//XXX Since this is permanent, we could actually alter the geometry instead.
		aGl.glPushMatrix();
		aGl.glTranslatef(-100, -25, 0);

		sphinxGeometry.draw(aGl);

		aGl.glPopMatrix();
		atlasTexture.activateTexture();
	}

	private void drawSpell(final GL10 aGl) {

		//The spell only has one surface at the moment, that we want to be visible from both sides.
		aGl.glDisable(GL10.GL_CULL_FACE);
		//Disable depth writing so that transparent pixels don't block things behind them.
		aGl.glDepthMask(false);
		aGl.glEnable(GL_BLEND);

		spellGeometry.draw(aGl);

		aGl.glDisable(GL_BLEND);
		aGl.glDepthMask(true);
		aGl.glEnable(GL10.GL_CULL_FACE);
	}

	public void setPlayerVelocity(int aNewVelocity) {
		velocity = aNewVelocity;
	}

	public void turn(float anAngleOfTurn) {
		playerFacing += anAngleOfTurn;
	}

	public void changeVelocity(float aVelocityIncrement) {
		velocity = Math.min(MAXIMUM_VELOCITY, Math.max(MINIMUM_VELOCITY, velocity + aVelocityIncrement));
	}

	public void setRendererListener(RendererListener rendererListener2) {
		rendererListener = rendererListener2;
	}

}
