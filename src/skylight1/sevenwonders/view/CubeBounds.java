package skylight1.sevenwonders.view;

/**
 * Cube used to specify bounds in 3D space.
 *
 */
public class CubeBounds {
	
	public static final float WORLD_EDGE_LENGTH = 1000;

	public static final CubeBounds TERRAIN = new CubeBounds(
		-WORLD_EDGE_LENGTH, WORLD_EDGE_LENGTH,
		-50f, 0f,
		-WORLD_EDGE_LENGTH, WORLD_EDGE_LENGTH
	);

	public final float x1;
	
	public final float x2;
	
	public final float y1;
	
	public final float y2;
	
	public final float z1;
	
	public final float z2;
	
	public final float xSize;
	
	public final float ySize;
	
	public final float zSize;
	
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
