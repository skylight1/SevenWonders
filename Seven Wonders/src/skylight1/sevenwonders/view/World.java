package skylight1.sevenwonders.view;

import static android.view.KeyEvent.*;
import static javax.microedition.khronos.opengles.GL10.*;
import static skylight1.sevenwonders.view.GameTexture.*;
import static skylight1.sevenwonders.view.SevenWondersGLRenderer.*;

import java.io.IOException;

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
import android.content.Context;
import android.util.FloatMath;
import android.util.Log;

public class World {

	private static final float HEIGHT_OF_CARPET_FROM_GROUND = 10f;

	private static final int TERRAIN_MAP_RESOURCE = R.raw.terrain_dunes;

	private static final int TERRAIN_DENSITY = 25;
	
	private static final int[] CARPET_OBJ_IDS = new int[] {R.raw.carpet_wave_0, R.raw.carpet_wave_1, R.raw.carpet_wave_2, R.raw.carpet_wave_3, R.raw.carpet_wave_4, R.raw.carpet_wave_5, R.raw.carpet_wave_6, R.raw.carpet_wave_7, R.raw.carpet_wave_8, R.raw.carpet_wave_9};

	private static final int CARPET_DISPLAY_TIME_MS = 100;
	
	public static final float INITIAL_VELOCITY = 100f * 1000f / 60f / 60f / 1000f;
	
	private static final float MINIMUM_VELOCITY = -INITIAL_VELOCITY;

	private static final float MAXIMUM_VELOCITY = INITIAL_VELOCITY * 2f;

	private static final float PI = (float) Math.PI;
	
	private final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> 
		openGLGeometryBuilder = OpenGLGeometryBuilderFactory.createTexturableNormalizable();

	private final OpenGLGeometry worldGeometry;

	private final OpenGLGeometry sphinxGeometry;

	private OpenGLGeometry[] carpetGeometry;

	private OpenGLGeometry spellGeometry;

	private Texture atlasTexture;
	
	private Texture sphinxTexture;
	
	//Start a little back so that we aren't inside the pyramid.
	private Position playerWorldPosition = new Position(0, 0, 400);
	
	private float playerFacing;
	
	private float velocity = INITIAL_VELOCITY;
	
	private int remainingCarpetDisplayTimeMS;
	
	private int carpetIndex;
	
	public World(final Context aContext) {
		//Add ground and pyramid to a single drawable geometry for the world.
		openGLGeometryBuilder.startGeometry();
		addGroundToGeometry(aContext, openGLGeometryBuilder);
		loadRequiredObj(aContext, R.raw.pyramid, openGLGeometryBuilder);		
		worldGeometry = openGLGeometryBuilder.endGeometry();
		
		sphinxGeometry = loadRequiredObj(aContext, R.raw.sphinx_scaled, openGLGeometryBuilder);
		
		addSpellsToGeometry(openGLGeometryBuilder);
		
		//Add carpet to a separate drawable geometry. 
		//Allows the world to be translated separately from the carpet later.
		addCarpetToGeometry(aContext, openGLGeometryBuilder);
	}
	
	private void addSpellsToGeometry(
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> openGLGeometryBuilder) {
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

	private void addGroundToGeometry(final Context aContext, 
			final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		final Terrain terrain = new Terrain(TERRAIN_MAP_RESOURCE, CubeBounds.TERRAIN);
		terrain.addToGeometry(aContext, GameTexture.SAND, TERRAIN_DENSITY, anOpenGLGeometryBuilder);
	}

	private OpenGLGeometry loadRequiredObj(final Context aContext, final int aObjId, final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> aBuilder) {
		final ObjFileLoader loader;
		try {
			loader = new ObjFileLoader(aContext, aObjId);
		} catch (IOException e) {
			throw new RuntimeException("Error loading required geometry from OBJ file:" + aObjId, e);
		}		
		return loader.createGeometry(aBuilder);		
	}
	
	private void addCarpetToGeometry(final Context aContext, 
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {
		
		carpetGeometry = new OpenGLGeometry[CARPET_OBJ_IDS.length];
		for(int i = 0; i < CARPET_OBJ_IDS.length; i++ ) {			
			carpetGeometry[i] = loadRequiredObj(aContext, CARPET_OBJ_IDS[i], anOpenGLGeometryBuilder);
		}
	}
	
	public void enable(final GL10 aGl, final Context aContext) {
		
		openGLGeometryBuilder.enable(aGl);
		
		// if the surface changed from a prior surface, such as a change of orientation, then free the prior texture
		if (sphinxTexture != null) {
			sphinxTexture.freeTexture();
			sphinxTexture = null;
		}
		sphinxTexture = new Texture(aGl, aContext, R.raw.sphinx, true);
		
		if (atlasTexture != null) {
			atlasTexture.freeTexture();
			atlasTexture = null;
		}
		atlasTexture = new Texture(aGl, aContext, R.raw.textures);

		atlasTexture.activateTexture();
	}
	
	public void update(final int timeDeltaMS) {
		playerWorldPosition.x += FloatMath.sin( playerFacing / 180f * PI ) * velocity * timeDeltaMS;
        playerWorldPosition.z -= FloatMath.cos( playerFacing / 180f * PI ) * velocity * timeDeltaMS;
        if ( LOG ) if ( LOG ) Log.i(SevenWondersGLRenderer.class.getName(), playerWorldPosition + ", " + playerFacing);	
        
        remainingCarpetDisplayTimeMS -= timeDeltaMS;
        while ( remainingCarpetDisplayTimeMS < 0 ) {
        	remainingCarpetDisplayTimeMS += CARPET_DISPLAY_TIME_MS;
        	carpetIndex = ( carpetIndex + 1 ) % carpetGeometry.length;
        }
	}
	
	public void draw(final GL10 aGl) {

		//Carpet drawn with no transformations, always right in front of the screen.
		aGl.glLoadIdentity();
		drawCarpet(aGl);
		
		applyMovement(aGl);
		worldGeometry.draw(aGl);
		drawSphinx(aGl);
		drawSpell(aGl);
	}
	
	private void drawCarpet(final GL10 aGl) {
		//Drawn first for performance, might occlude other geometry, which OpenGL can then skip.
		//XXX Hack to fix the carpet being drawn face down. Should probably change geometry or disable culling for the carpet instead.
		aGl.glFrontFace(GL_CW);
		carpetGeometry[carpetIndex].draw(aGl);
		aGl.glFrontFace(GL_CCW);		
	}
	
	private void applyMovement(final GL10 aGl) {
		//Rotate first, otherwise map rotates around center point we translated away from.
		aGl.glRotatef(playerFacing, 0, 1, 0);
		
		aGl.glTranslatef(-playerWorldPosition.x, -playerWorldPosition.y, -playerWorldPosition.z);
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
		aGl.glDisable(GL_CULL_FACE);
		//Disable depth writing so that transparent pixels don't block things behind them.
		aGl.glDepthMask(false);
		aGl.glEnable(GL_BLEND);
		
		spellGeometry.draw(aGl);
		
		aGl.glDisable(GL_BLEND);
		aGl.glDepthMask(true);
		aGl.glEnable(GL_CULL_FACE);
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

	public boolean handleInput(final int keyCode) {
		switch( keyCode ) {
			case KEYCODE_DPAD_LEFT:
			case KEYCODE_Q:
				if ( LOG ) Log.i(SevenWondersGLSurfaceView.class.getName(), "Turning left.");
				turn(-5f);
				return true;
				
			case KEYCODE_DPAD_RIGHT:
			case KEYCODE_W:
				if ( LOG ) Log.i(SevenWondersGLSurfaceView.class.getName(), "Turning right.");
				turn(+5f);
				return true;
				
			case KEYCODE_DPAD_CENTER:
			case KEYCODE_SPACE:
				if ( LOG ) Log.i(SevenWondersGLSurfaceView.class.getName(), "Stopping.");
				setPlayerVelocity(0);
				return true;				
				
			case KEYCODE_DPAD_UP:
				if ( LOG ) Log.i(SevenWondersGLSurfaceView.class.getName(), "Speeding up.");
				changeVelocity(INITIAL_VELOCITY / 10f);
				return true;
				
			case KEYCODE_DPAD_DOWN: 
				if ( LOG ) Log.i(SevenWondersGLSurfaceView.class.getName(), "Slowing down.");
				changeVelocity(-INITIAL_VELOCITY / 10f);
				return true;
		}
		
		return false;
	}
	
}
