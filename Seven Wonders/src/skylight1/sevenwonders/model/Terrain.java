package skylight1.sevenwonders.model;

import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.sevenwonders.view.GameTexture;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.FloatMath;

/**
 * Terrain for a level.
 * 
 * Stored as image in the res/raw folder. 
 * Pixel brightness indicates Y position in game. 
 * White is the highest, black the lowest. 
 * X,Y axes of image correspond to X,Z axes of the game.
 * 
 */
public class Terrain {

	//Max value of an RGBA component in android.graphics.Color.
	private static final int COMPONENT_MAX = 255;

	private int mapResource;

	private Cube bounds;

	/**
	 * Create terrain.
	 * 
	 * @param mapResourceParam
	 *			int raw image resource with the elevations to use
	 * @param boundsParam
	 *			CubeBounds area terrain should be generated in
	 */
	public Terrain(int mapResourceParam, Cube boundsParam) {
		mapResource = mapResourceParam;
		bounds = boundsParam;
	}

	/**
	 * Add terrain to geometry.
	 * 
	 * @param context
	 *			Context to load assets from
	 * @param texture
	 *			GameTexture to use for each two triangles added
	 * @param density
	 *			int number of vertexes to break X and Z axes up into
	 * @param geometry
	 *			OpenGLGeometryBuilder to add to
	 */
	public void addToGeometry(
			final Context context,
			final GameTexture texture,
			final int density,
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> geometry) {

		// Load terrain map as bitmap to access pixel data.
		// Stored in res/raw to prevent automatic dithering.
		Bitmap image = BitmapFactory.decodeResource(context.getResources(), mapResource);
		if (null == image) {
			throw new RuntimeException("Error decoding terrain map resource: " + mapResource);
		}
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();
		
		//Calculate vertexes based on map.
		float[] vertexX = new float[density];
		float[] vertexZ = new float[density];
		float[] vertexY = new float[density * density];
		for (int zCount = 0; zCount < density; zCount++) {

			vertexZ[zCount] = toRange(zCount, density, bounds.z1, bounds.zSize);
			final int imageY = zCount * imageHeight / density;

			for (int xCount = 0; xCount < density; xCount++) {

				vertexX[xCount] = toRange(xCount, density, bounds.x1, bounds.xSize);
				final int imageX = xCount * imageWidth / density;
				final float y0 = calculateHeight(image, imageX, imageY, bounds.y1, bounds.ySize);

				final int vertextIndex = zCount * density + xCount;
				vertexY[vertextIndex] = y0;
			}
		}
		image.recycle();		
		image = null;
		
		//Calculate triangle normals.
		final int trianglesPerRow = 2 * density - 2;
		float[] normals = new float[density * trianglesPerRow * 3];
		for (int zCount = 1; zCount < density; zCount++) {

			final float z0 = vertexZ[zCount - 1];
			final float z1 = vertexZ[zCount];
			
			for (int xCount = 1; xCount < density; xCount++) {

				final float x0 = vertexX[xCount - 1];
				final float x1 = vertexX[xCount];
				
				final int topVertexYIndex = (zCount - 1) * density + (xCount - 1);			
				final int bottomVertexYIndex = zCount * density + (xCount - 1);
				final float y0 = vertexY[topVertexYIndex];
				final float y1 = vertexY[bottomVertexYIndex];
				final float y2 = vertexY[topVertexYIndex + 1];
				final float y3 = vertexY[bottomVertexYIndex + 1];

				int normalIndex = ( ( trianglesPerRow * (zCount - 1) ) + ( 2 * ( xCount - 1) ) ) * 3;
				
				//Calculate normal for top left triangle.
				calculateNormal(x0, y0, z0, x0, y1, z1, x1, y2, z0, normalIndex, normals);
				
				//Calculate normal for bottom right triangle.
				calculateNormal(x1, y2, z0, x0, y1, z1, x1, y3, z1, normalIndex + 3, normals);
			}
		}
		
		//Calculate vertex normals and pass data on to library.
		final float[] normal0 = new float[3];
		final float[] normal1 = new float[3];
		final float[] normal2 = new float[3];
		final float[] normal3 = new float[3];
		for (int zCount = 1; zCount < density; zCount++) {

			final float z0 = vertexZ[zCount - 1];
			final float z1 = vertexZ[zCount];
			
			for (int xCount = 1; xCount < density; xCount++) {

				final float x0 = vertexX[xCount - 1];
				final float x1 = vertexX[xCount];
				
				final int topVertexYIndex = (zCount - 1) * density + (xCount - 1);			
				final int bottomVertexYIndex = zCount * density + (xCount - 1);
				final float y0 = vertexY[topVertexYIndex];
				final float y1 = vertexY[bottomVertexYIndex];
				final float y2 = vertexY[topVertexYIndex + 1];
				final float y3 = vertexY[bottomVertexYIndex + 1];

				calculateNormal(xCount - 1, zCount - 1, density, normals, normal0);
				calculateNormal(xCount - 1, zCount, density, normals, normal1);
				calculateNormal(xCount, zCount - 1, density, normals, normal2);
				calculateNormal(xCount, zCount, density, normals, normal3);

				// Add triangle for top left half of the texture.
				geometry.add3DTriangle(x0, y0, z0, x0, y1, z1, x1, y2, z0)
				.setTextureCoordinates(texture.s1, texture.t1, texture.s1, texture.t2, texture.s2, texture.t1)
				.setNormal(normal0[0],normal0[1], normal0[2], normal1[0], normal1[1], normal1[2], normal2[0], normal2[1], normal2[2]);

				// Add triangle for bottom right half of the texture.
				geometry.add3DTriangle(x1, y2, z0, x0, y1, z1, x1, y3, z1)
				.setTextureCoordinates(texture.s2, texture.t1, texture.s1, texture.t2, texture.s2, texture.t2)
				.setNormal(normal2[0], normal2[1], normal2[2], normal1[0], normal1[1], normal1[2], normal3[0], normal3[1], normal3[2]);
			}

		}


	}
	
	/**
	 * Get index of triangle normal in the array of triangle normals.
	 * 
	 * @param xCount int x position in the terrain grid
	 * @param zCount int z position in the terrain grid
	 * @param density int number of vertexes to break X and Z axes up into
	 * @return index of the normal
	 */
	private static final int normalIndex(final int xCount, final int zCount, final int density) {
		final int trianglesPerRow = 2 * density - 2;
		return ( ( trianglesPerRow * zCount ) + ( 2 * xCount ) ) * 3;
	}

	/**
	 * Add a normal to an array.
	 * 
	 * @param index int index of the normal
	 * @param normals float[] normals
	 * @param result float[] to add to
	 */
	private static final void addNormal(final int index, final float[] normals, final float[] result) {
		result[0] += normals[index];
		result[1] += normals[index + 1];
		result[2] += normals[index + 2];
	}
	
	/**
	 * Calculate unit normal for a point in the terrain grid.
	 * 
	 * @param xCount int x position in the terrain grid
	 * @param zCount int z position in the terrain grid
	 * @param density int number of vertexes to break X and Z axes up into
	 * @param normals float[] triangle normals
	 * @param result array to store result in
	 */
	private static void calculateNormal(final int xCount, final int zCount, final int density, final float[] normals, final float[] result) {
		
		//XXX Currently doesn't sum anything for missing triangles at edges. Maybe should sum a flat triangle?
		
		final int lastXZ = density - 1;
		
		result[0] = result[1] = result[2] = 0;
		
		//Add top left adjacent triangle.
		if ( xCount > 0 &&	zCount > 0 ) {
			addNormal(normalIndex(xCount - 1, zCount - 1, density) + 3, normals, result);
		}
		
		//Add middle left adjacent triangle.
		//Add bottom left adjacent triangle.
		if ( xCount > 0 && zCount < lastXZ ) {
			addNormal(normalIndex(xCount - 1, zCount, density), normals, result);
			addNormal(normalIndex(xCount - 1, zCount, density) + 3, normals, result);			
		}
				
		//Add top right adjacent triangle.
		//Add middle right adjacent triangle.
		if ( xCount < lastXZ && zCount > 0 ) {
			addNormal(normalIndex(xCount, zCount - 1, density), normals, result);
			addNormal(normalIndex(xCount, zCount - 1, density) + 3, normals, result);			
		}
		
		//Add bottom right adjacent triangle.
		if ( xCount < lastXZ && zCount < lastXZ ) {
			addNormal(normalIndex(xCount, zCount, density), normals, result);			
		}
		
		//Normalize
		normalize(0, result);
	}

	/**
	 * Calculate unit normal from a normal.
	 * 
	 * @param normalOffset int offset into normal parameter
	 * @param normal float[] where normal is stored
	 * @return passed in normal float[]
	 */
	private static float[] normalize(final int normalOffset, final float[] normal) {
		final int yOffset = normalOffset + 1;
		final int zOffset = yOffset + 1;
		
		final float xNormal = normal[normalOffset];
		final float yNormal = normal[yOffset];
		final float zNormal = normal[zOffset];
		
		final float length = FloatMath.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);
		
		normal[normalOffset] = xNormal / length;
		normal[yOffset] = yNormal / length;
		normal[zOffset] = zNormal / length;
		
		return normal;
	}
	
	/**
	 * Calculate unit normal for triangle.
	 * 
	 * @param p1x
	 *			float x coordinate of the first point in the triangle
	 * @param p1y
	 *			float y coordinate of the first point in the triangle
	 * @param p1z
	 *			float z coordinate of the first point in the triangle
	 * @param p2x
	 *			float x coordinate of the second point in the triangle
	 * @param p2y
	 *			float y coordinate of the second point in the triangle
	 * @param p2z
	 *			float z coordinate of the second point in the triangle
	 * @param p3x
	 *			float x coordinate of the third point in the triangle
	 * @param p3y
	 *			float y coordinate of the third point in the triangle
	 * @param p3z
	 *			float z coordinate of the third point in the triangle
	 * @param normalOffset int offset into normal parameter			 
	 * @param normal
	 *			float[3] to reuse for storing the normal in
	 * @return float array with normal
	 */
	private static float[] calculateNormal(final float p1x, final float p1y, final float p1z, final float p2x,
			final float p2y, final float p2z, final float p3x, final float p3y, final float p3z, 
			int normalOffset, float[] normal) {

		// Calculate vectors from points.
		final float v1x = p2x - p1x;
		final float v1y = p2y - p1y;
		final float v1z = p2z - p1z;
		final float v2x = p3x - p1x;
		final float v2y = p3y - p1y;
		final float v2z = p3z - p1z;
	
		// Calculate normal from vectors.
		normal[normalOffset] = v1y * v2z - v1z * v2y;
		normal[normalOffset + 1] = v1z * v2x - v1x * v2z;
		normal[normalOffset + 2] = v1x * v2y - v1y * v2x;
	
		// Make normal unit length and return.
		return normalize(normalOffset, normal);
	}	

	/**
	 * Calculate a range value from a fraction indicating the amount of the range traversed.
	 * 
	 * @param numerator
	 *			int numerator of fraction of range traversed
	 * @param denominator
	 *			int denominator of fraction of range traversed
	 * @param start
	 *			float lowest number in the range
	 * @param run
	 *			float length of the range
	 * @return value within range
	 */
	private static float toRange(final int numerator, final int denominator, final float start, final float run) {
		return start + (numerator * run / denominator);
	}

	/**
	 * Calculate height using lightness of a pixel from an image.
	 * 
	 * @param imageX
	 *			int x location of the pixel
	 * @param imageY
	 *			int y location of the pixel
	 * @param heightStart
	 *			float minimum height
	 * @param heightRun
	 *			float height range
	 * @return height
	 */
	private static float calculateHeight(final Bitmap image, final int imageX, final int imageY,
			final float heightStart, final float heightRun) {

		final int pixel = image.getPixel(imageX, imageY);

		//Terrain is black and white, so only need to check one component.
		return heightStart + ( Color.red(pixel) * heightRun / COMPONENT_MAX );
	}
}