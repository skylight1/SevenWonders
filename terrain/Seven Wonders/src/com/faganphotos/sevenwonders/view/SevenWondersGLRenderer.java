package com.faganphotos.sevenwonders.view;

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
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

import com.faganphotos.sevenwonders.R;

public class SevenWondersGLRenderer implements Renderer {

	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final int TERRAIN_MAP_RESOURCE = R.raw.terrain_vertical_waves;

	private static final int TERRAIN_DENSITY = 25;

	private final Context context;

	private Texture texture;

	private FPSLogger fPSLogger = new FPSLogger(SevenWondersGLRenderer.class.getName(), FRAMES_BETWEEN_LOGGING_FPS);

	private OpenGLGeometry worldGeometry;

	private OpenGLGeometry carpetGeometry;

	public SevenWondersGLRenderer(Context aContext) {
		context = aContext;
	}

	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
		final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> openGLGeometryBuilder = OpenGLGeometryBuilderFactory
				.createTexturableNormalizable();

		// final ObjFileLoader objFileLoader;
		// try {
		// objFileLoader = new ObjFileLoader(context, R.raw.airplane_red_mesh_obj);
		// } catch (IOException e) {
		// throw new RuntimeException(e);
		// }
		//
		// planeGeometry = objFileLoader.createGeometry(openGLGeometryBuilder);

		addGroundToGeometry(openGLGeometryBuilder);
		addCarpetToGeometry(openGLGeometryBuilder);

		openGLGeometryBuilder.enable(gl);

		gl.glColor4f(1, 1, 1, 1);
		gl.glClearColor(0.5f, 0.5f, 1, 1.0f);

		// gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);

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

	private void addGroundToGeometry(
			final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		final Terrain terrain = new Terrain(TERRAIN_MAP_RESOURCE, CubeBounds.TERRAIN);

		anOpenGLGeometryBuilder.startGeometry();
		terrain.addToGeometry(context, GameTexture.SAND, TERRAIN_DENSITY, anOpenGLGeometryBuilder);
		worldGeometry = anOpenGLGeometryBuilder.endGeometry();
	}

	private void addCarpetToGeometry(
			OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		anOpenGLGeometryBuilder.startGeometry();
		final float x1 = -0.5f;
		final float x2 = 0.5f;
		final float z1 = -1.25f;
		final float z2 = 1.25f;
		final float y = -0.25f;

		final float s1 = 0;
		final float t2 = 480f / 1024f;
		final float s2 = 320f / 1024f;
		final float t1 = 0;

		anOpenGLGeometryBuilder.add3DTriangle(x1, y, z1, x2, y, z1, x1, y, z2).setTextureCoordinates(s1, t1, s2, t1,
				s1, t2).setNormal(0, 1, 0, 0, 1, 0, 0, 1, 0);
		anOpenGLGeometryBuilder.add3DTriangle(x2, y, z1, x2, y, z2, x1, y, z2).setTextureCoordinates(s2, t1, s2, t2,
				s1, t2).setNormal(0, 1, 0, 0, 1, 0, 0, 1, 0);
		carpetGeometry = anOpenGLGeometryBuilder.endGeometry();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glViewport(0, 0, w, h);
		GLU.gluPerspective(gl, 45, (float) w / (float) h, 0.1f, 1000f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// if the surface changed from a prior surface, such as a change of orientation, then free the prior plane
		// texture
		if (texture != null) {
			texture.freeTexture();
			texture = null;
		}

		texture = new Texture(gl, context, R.raw.textures);
		texture.activateTexture();
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// gl.glLoadIdentity();
		float distance = -500f + (float) (System.currentTimeMillis() % 15000) / 15000f * 1000f;
		gl.glTranslatef(0, -10f, distance);

		// float distance = -250f + (float) (System.currentTimeMillis() % 15000) / 15000f * 300f;

		// gl.glLoadIdentity();
		// GLU.gluLookAt(gl, 0, 0, 0, 0, 2, distance, 0, 1, 0);

		worldGeometry.draw(gl);

		gl.glLoadIdentity();
		carpetGeometry.draw(gl);
		//		
		// drawPlane(gl, 0, 2, distance);
		// for (int p = 0; p < 4; p++) {
		// final int rank = p / 2 + 1;
		// final int side = (int) Math.signum(0.5f - (float) (p % 2));
		// drawPlane(gl, side * 8 * rank, 2 + 2 * rank, distance);
		// }

		fPSLogger.frameRendered();
	}
}
