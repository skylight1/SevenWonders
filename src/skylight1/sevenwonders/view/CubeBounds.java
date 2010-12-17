package skylight1.sevenwonders.view;

/**
 * Cube used to specify bounds in 3D space.
 *
 */
public class CubeBounds {
	
	public static final CubeBounds TERRAIN = new CubeBounds(
		-1000, 1000,
		-50f, 0f,
		-1000, 1000
	);

	final float x1;
	
	final float x2;
	
	final float y1;
	
	final float y2;
	
	final float z1;
	
	final float z2;
	
	final float xSize;
	
	final float ySize;
	
	final float zSize;
	
	/**
	 * Create a cube bounds.
	 * 
	 * @param x1Param starting x coordinate
	 * @param x2Param ending x coordinate
	 * @param y1Param starting y coordinate
	 * @param y2Param ending y coordinate
	 * @param z1Param starting z coordinate
	 * @param z2Param ending z coordinate
	 */
	private CubeBounds(float x1Param, float x2Param, float y1Param, float y2Param, float z1Param, float z2Param) {
		if ( x1Param > x2Param || y1Param > y2Param || z1Param > z2Param ) {
			throw new IllegalArgumentException("Start before end.");
		}
		
		x1 = x1Param;
		x2 = x2Param;
		y1 = y1Param;
		y2 = y2Param;
		z1 = z1Param;
		z2 = z2Param;
		xSize = x2 - x1;
		ySize = y2 - y1;
		zSize = z2 - z1;
	}

}
