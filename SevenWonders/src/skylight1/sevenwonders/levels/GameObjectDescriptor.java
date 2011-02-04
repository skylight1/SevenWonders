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
	public final float[] transformationMatrix;

	public final int objectFileResourceId; // the resource id of the OBJ file, e.g., spell.obj

	public GameObjectDescriptor(final float[] aTransformationMatrix, final int anObjectFileResourceId) {
		transformationMatrix = aTransformationMatrix;
		objectFileResourceId = anObjectFileResourceId;
	}
}
