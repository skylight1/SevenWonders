package com.faganphotos.sevenwonders.view;

import static com.faganphotos.sevenwonders.view.GameTexture.*;
import static javax.microedition.khronos.opengles.GL10.*;
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
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

import com.faganphotos.sevenwonders.R;

public class SevenWondersGLRenderer implements Renderer {
	
	private static final boolean LOG = false;

	private static final float HEIGHT_OF_CARPET_FROM_GROUND = 10f;

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final int TERRAIN_MAP_RESOURCE = R.raw.terrain_dunes;

	private static final int TERRAIN_DENSITY = 25;

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
	
	private float velocity = 35f * 1000f / 60f / 60f / 1000f;

	private long timeAtLastOnRenderCall;
	
	public SevenWondersGLRenderer(Context aContext) {
		context = aContext;
	}

	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
		
		final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder = OpenGLGeometryBuilderFactory
				.createTexturableNormalizable();

		//Add ground and pyramid to a single drawable geometry for the world.
		openGLGeometryBuilder.startGeometry();
		addGroundToGeometry(openGLGeometryBuilder);
		final ObjFileLoader pyramidLoader;
		try {
			pyramidLoader = new ObjFileLoader(context, R.raw.pyramid);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		pyramidLoader.createGeometry(openGLGeometryBuilder);		
		worldGeometry = openGLGeometryBuilder.endGeometry();

		//Load sphinx geometry.
		final ObjFileLoader sphinxLoader;
		try {
			sphinxLoader = new ObjFileLoader(context, R.raw.sphinx_scaled);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		sphinxGeometry = sphinxLoader.createGeometry(openGLGeometryBuilder);
		
		addSpellsToGeometry(openGLGeometryBuilder);
		
		//Add carpet to a separate drawable geometry. 
		//Allows the world to be translated separately from the carpet later.
		addCarpetToGeometry(openGLGeometryBuilder);

		openGLGeometryBuilder.enable(gl);

		gl.glColor4f(1, 1, 1, 1);
		gl.glClearColor(0.5f, 0.5f, 1, 1.0f);

		//Don't draw inside facing surfaces on things like the pyramid and sphinx.
		//This fixes a z-fighting issue: At long distances OpenGL doesn't have enough precision in the depth buffer
		//to tell if the front is closer or if the inside of a nearby back surface is closer and gets it wrong some times.
		gl.glEnable(GL10.GL_CULL_FACE);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glDisable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glShadeModel(GL10.GL_SMOOTH);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, new float[] { 0.75f, 0.75f, 0.75f, 1f }, 0);

		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] { -1f, 0f, 1f, 0.0f }, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[] { 0.5f, 0.5f, 0.5f, 1f }, 0);

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, new float[] { 0.1f, 0.1f, 0.1f, 1.0f }, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, new float[] { 50.0f }, 0);

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

	private void addCarpetToGeometry(
			OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {
		
		carpetGeometry = new OpenGLGeometry[10];
		ObjFileLoader objFileLoader;

		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[0] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[1] = anOpenGLGeometryBuilder.endGeometry();
		
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag2);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[2] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag3);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[3] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag4);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[4] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag5);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[5] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag6);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[6] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag7);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[7] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[8] = anOpenGLGeometryBuilder.endGeometry();
		anOpenGLGeometryBuilder.startGeometry();
		try {
			objFileLoader = new ObjFileLoader(context, R.raw.triag9);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		objFileLoader.createGeometry(anOpenGLGeometryBuilder);		
		carpetGeometry[9] = anOpenGLGeometryBuilder.endGeometry();

	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glViewport(0, 0, w, h);
		GLU.gluPerspective(gl, 45, (float) w / (float) h, 0.1f, 1000f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// if the surface changed from a prior surface, such as a change of orientation, then free the prior texture
		if (sphinxTexture != null) {
			sphinxTexture.freeTexture();
			sphinxTexture = null;
		}
		sphinxTexture = new Texture(gl, context, R.raw.sphinx, true);
		
		if (atlasTexture != null) {
			atlasTexture.freeTexture();
			atlasTexture = null;
		}
		atlasTexture = new Texture(gl, context, R.raw.textures);

		atlasTexture.activateTexture();
	}

	public void onDrawFrame(final GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		//Carpet drawn with no transformations, always right in front of the screen.
		gl.glLoadIdentity();
		//Drawn first for performance, might occlude other geometry, which OpenGL can then skip.
		//XXX Hack to fix the carpet being drawn face down. Should probably change geometry or disable culling for the carpet instead.
		gl.glFrontFace(GL_CW);
		int myIndex=(int)((System.currentTimeMillis() % 800+1) /100f);
		carpetGeometry[myIndex].draw(gl);
		gl.glFrontFace(GL_CCW);
		
		//Spin player to test movement calculations. 
		//TODO Replace this line with using input to change facing.
		playerFacing += 360f / 1000f;
       
		applyMovement(gl);

		worldGeometry.draw(gl);
		drawSphinx(gl);
		drawSpell(gl);

		fPSLogger.frameRendered();
	}
	
	private void applyMovement(final GL10 gl) {
        final long timeDeltaMS = calculateTimeSinceLastRenderMillis();
		playerWorldPosition.x += Math.sin( playerFacing / 180f * Math.PI ) * velocity * timeDeltaMS;
        playerWorldPosition.z += Math.cos( playerFacing / 180f * Math.PI ) * velocity * timeDeltaMS;
        if ( LOG ) Log.i(SevenWondersGLRenderer.class.getName(), playerWorldPosition + ", " + playerFacing);
		
        gl.glTranslatef(-playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);
		gl.glRotatef(-playerFacing, 0, 1, 0);
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
	
	private void drawSphinx(final GL10 gl) {
		sphinxTexture.activateTexture();
		//Translate a bit so sphinx isn't inside the pyramid. 
		//XXX Since this is permanent, we could actually alter the geometry instead.
		gl.glPushMatrix();
		gl.glTranslatef(-100, 0, 0);
		
		sphinxGeometry.draw(gl);
		
		gl.glPopMatrix();
		atlasTexture.activateTexture();		
	}
	
	private void drawSpell(final GL10 gl) {
		
		//The spell only has one surface at the moment, that we want to be visible from both sides.
		gl.glDisable(GL10.GL_CULL_FACE);
		//Disable depth writing so that transparent pixels don't block things behind them.
		gl.glDepthMask(false);
		gl.glEnable(GL_BLEND);
		
		spellGeometry.draw(gl);
		
		gl.glDisable(GL_BLEND);
		gl.glDepthMask(true);
		gl.glEnable(GL10.GL_CULL_FACE);
	}
	
}
