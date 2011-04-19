package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.R;
import android.opengl.Matrix;

public class LevelConstructionToolkit {
	private static final float HEIGHT_OF_HAZARDS_FROM_GROUND = 9f;

	private static final float HEIGHT_OF_SPELLS_FROM_GROUND = 11f;

	private static final float[] SPELLS_TEXTURE_TRANSFORM = new float[16];

	static {
		// the texture is within the main texture, so it needs a little transformation to map onto the spell
		Matrix.setIdentityM(SPELLS_TEXTURE_TRANSFORM, 0);
		Matrix.translateM(SPELLS_TEXTURE_TRANSFORM, 0, 576f / 1024f, 0, 0);
		Matrix.scaleM(SPELLS_TEXTURE_TRANSFORM, 0, 0.25f, 0.25f, 1f);
	}

	static void addSpell(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_SPELLS_FROM_GROUND, aZ);
		aGameLevel.spells.add(new GameObjectDescriptor(transformationMatrix, SPELLS_TEXTURE_TRANSFORM, R.raw.ankh, R.raw.textures));
	}

	static void addHazard(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_HAZARDS_FROM_GROUND, aZ);
		aGameLevel.hazards.add(new GameObjectDescriptor(transformationMatrix, null, R.raw.textured_sword, R.raw.textures));
	}

	static float[] createNewIdentityMatrix() {
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		return matrix;
	}

	static float[] createBoundingSphere(float anX, float aY, float aZ, float aRadius) {
		return new float[] { anX, aY, aZ, aRadius };
	}

	static void addPyramid(final GameLevel aGameLevel, float aRotation, final float anX, final float aY, final float aZ) {
		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, aRotation, 0, 1, 0);
		Matrix.translateM(coordinateTransform, 0, anX, aY, aZ);
		aGameLevel.decorations.add(new GameObjectDescriptor(coordinateTransform, null, R.raw.pyramid, R.raw.textures));
	}

	static void addSphynx(final GameLevel aGameLevel, final float aRotation, final float anX, final float aY,
			final float aZ) {
		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, aRotation, 0, 1, 0);
		Matrix.translateM(coordinateTransform, 0, anX, aY, aZ);
		float[] textureTransform = new float[16];
		Matrix.setIdentityM(textureTransform, 0);
		aGameLevel.decorations.add(new GameObjectDescriptor(coordinateTransform, null, R.raw.sphinx_scaled, R.raw.sphinx));
	}
}