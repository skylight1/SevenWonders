package skylight1.sevenwonders.levels;

public class GameObjectDescriptor {
	/**
	 * Transformation matrix to apply to the object before adding to it to the OpenGLGeometry. To add it without any
	 * transformation, return null. To add it translated by x, y, z:
	 * 
	 * <code>
	 * final float[] m = new float[16];
	 * android.opengl.Matrix.setIdentity(m, 0);
	 * android.opengl.Matrix.translateM(m, 0, x, y, z);
	 * </code>
	 */
	public final float[] coordinateTransformationMatrix;

	public final float[] textureTransformationMatrix;

	/**
	 * The resource for the OBJ file, e.g., R.raw.spell for "spell.obj".
	 */
	public final int objectFileResourceId;

	/**
	 * The resource for the texture file, e.g., R.raw.dunes for "dunes.png".
	 */
	public final int textureResource;

	public GameObjectDescriptor(final float[] aCoordinateTransformationMatrix, float[] aTextureTransformationMatrix,
			final int anObjectFileResourceId, final int aTextureFileResourceId) {
		coordinateTransformationMatrix = aCoordinateTransformationMatrix;
		textureTransformationMatrix = aTextureTransformationMatrix;
		objectFileResourceId = anObjectFileResourceId;
		textureResource = aTextureFileResourceId;
	}
}
