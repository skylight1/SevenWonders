package com.faganphotos.sevenwonders.view;

import java.io.IOException;
import java.io.InputStream;

import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.FloatMath;

/**
 * Terrain for a level.
 * 
 * Stored as an image in the assets folder. The brightness of each pixel indicates Y position in the game. White is the
 * highest, black the lowest. The X,Y axes of the image correspond to the X,Z axes of the game, respectively.
 */
public class Terrain {

	private String file;

	private float xStart;

	private float xRun;

	private float yStart;

	private float yRun;

	private float zStart;

	private float zRun;

	/**
	 * Create terrain.
	 * 
	 * @param file
	 *            String path to image file with the elevations to use
	 * @param xStartParam
	 *            float x coordinate where terrain should start in game
	 * @param xEnd
	 *            float x coordinate where terrain should end in game
	 * @param yStartParam
	 *            float y coordinate where terrain should start in game
	 * @param yEnd
	 *            float y coordinate where terrain should end in game
	 * @param zStartParam
	 *            float z coordinate where terrain should start in game
	 * @param zEnd
	 *            float z coordinate where terrain should end in game
	 */
	public Terrain(String fileParam, final float xStartParam, final float xEnd, final float yStartParam,
			final float yEnd, final float zStartParam, final float zEnd) {

		file = fileParam;
		xStart = xStartParam;
		xRun = xEnd - xStart;
		yStart = yStartParam;
		yRun = yEnd - yStart;
		zStart = zStartParam;
		zRun = zEnd - zStart;
	}

	/**
	 * Add terrain to geometry.
	 * 
	 * @param context
	 *            Context to load assets from
	 * @param texture
	 *            SubTexture to use for each two triangles added
	 * @param density
	 *            int number of vertexes to break X and Z axes up into
	 * @param anOpenGLGeometryBuilder
	 *            OpenGLGeometryBuilder to add to
	 */
	public void addToGeometry(
			final Context context,
			final SubTexture texture,
			final int density,
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		// Get input stream for terrain map.
		// Stored in assets to prevent automatic dithering.
		final InputStream input;
		try {
			input = context.getResources().getAssets().open(file);
		} catch (IOException e) {
			throw new RuntimeException("Error reading terrain image from path:" + file, e);
		}

		// Load as a bitmap.
		final Bitmap image = BitmapFactory.decodeStream(input);
		if (null == image) {
			throw new RuntimeException("Error decoding terrain image from path: " + file);
		}
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();

		// Generate vertexes from bitmap.
		// XXX Doesn't cache reused vertexes, add later if needed for performance.
		final float[] normal = new float[3];
		for (int zCount = 1; zCount < density; zCount++) {

			final float z0 = toRange(zCount - 1, density, zStart, zRun);
			final float z1 = toRange(zCount, density, zStart, zRun);
			final int imageY0 = (zCount - 1) * imageHeight / density;
			final int imageY1 = zCount * imageHeight / density;

			for (int xCount = 1; xCount < density; xCount++) {

				final float x0 = toRange(xCount - 1, density, xStart, xRun);
				final float x1 = toRange(xCount, density, xStart, xRun);
				final int imageX0 = (xCount - 1) * imageWidth / density;
				final int imageX1 = xCount * imageWidth / density;

				final float y0 = calculateHeight(image, imageX0, imageY0, yStart, yRun);
				final float y1 = calculateHeight(image, imageX0, imageY1, yStart, yRun);
				final float y2 = calculateHeight(image, imageX1, imageY0, yStart, yRun);
				final float y3 = calculateHeight(image, imageX1, imageY1, yStart, yRun);

				// Add triangle for top left half of the texture.
				// XXX Using normal for face. Fix to use all adjacent faces per vertex if lighting looks faceted.
				calculateNormal(x0, y0, z0, x0, y1, z1, x1, y2, z0, normal);
				anOpenGLGeometryBuilder.add3DTriangle(x0, y0, z0, x0, y1, z1, x1, y2, z0).setTextureCoordinates(
						texture.s1, texture.t1, texture.s1, texture.t2, texture.s2, texture.t1).setNormal(normal[0],
						normal[1], normal[2], normal[0], normal[1], normal[2], normal[0], normal[1], normal[2]);

				// Add triangle for bottom right half of the texture.
				calculateNormal(x1, y2, z0, x0, y1, z1, x1, y3, z1, normal);
				anOpenGLGeometryBuilder.add3DTriangle(x1, y2, z0, x0, y1, z1, x1, y3, z1).setTextureCoordinates(
						texture.s2, texture.t1, texture.s1, texture.t2, texture.s2, texture.t2).setNormal(normal[0],
						normal[1], normal[2], normal[0], normal[1], normal[2], normal[0], normal[1], normal[2]);
			}
		}

		image.recycle();

	}

	/**
	 * Calculate unit normal for triangle.
	 * 
	 * @param p1x
	 *            float x coordinate of the first point in the triangle
	 * @param p1y
	 *            float y coordinate of the first point in the triangle
	 * @param p1z
	 *            float z coordinate of the first point in the triangle
	 * @param p2x
	 *            float x coordinate of the second point in the triangle
	 * @param p2y
	 *            float y coordinate of the second point in the triangle
	 * @param p2z
	 *            float z coordinate of the second point in the triangle
	 * @param p3x
	 *            float x coordinate of the third point in the triangle
	 * @param p3y
	 *            float y coordinate of the third point in the triangle
	 * @param p3z
	 *            float z coordinate of the third point in the triangle
	 * @param normal
	 *            float[3] to reuse for storing the normal in
	 * @return float array with normal
	 */
	private static float[] calculateNormal(final float p1x, final float p1y, final float p1z, final float p2x,
			final float p2y, final float p2z, final float p3x, final float p3y, final float p3z, float[] normal) {

		if (null == normal) {
			normal = new float[3];
		}

		// Calculate vectors from points.
		final float v1x = p2x - p1x;
		final float v1y = p2y - p1y;
		final float v1z = p2z - p1z;
		final float v2x = p3x - p1x;
		final float v2y = p3y - p1y;
		final float v2z = p3z - p1z;

		// Calculate normal from vectors.
		normal[0] = v1y * v2z - v1z * v2y;
		normal[1] = v1z * v2x - v1x * v2z;
		normal[2] = v1x * v2y - v1y * v2x;

		// Make normal unit length.
		final float length = FloatMath.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
		normal[0] = normal[0] / length;
		normal[1] = normal[1] / length;
		normal[2] = normal[2] / length;

		return normal;
	}

	/**
	 * Calculate a range value from a fraction indicating the amount of the range traversed.
	 * 
	 * @param numerator
	 *            int numerator of fraction of range traversed
	 * @param denominator
	 *            int denominator of fraction of range traversed
	 * @param start
	 *            float lowest number in the range
	 * @param run
	 *            float length of the range
	 * @return value within range
	 */
	private static float toRange(final int numerator, final int denominator, final float start, final float run) {
		return start + (numerator * run / denominator);
	}

	/**
	 * Calculate height using lightness of a pixel from an image.
	 * 
	 * @param imageX
	 *            int x location of the pixel
	 * @param imageY
	 *            int y location of the pixel
	 * @param heightStart
	 *            float minimum height
	 * @param heightRun
	 *            float height range
	 * @return height
	 */
	private static float calculateHeight(final Bitmap image, final int imageX, final int imageY,
			final float heightStart, final float heightRun) {

		final int componentMax = 255;
		final int pixel = image.getPixel(imageX, imageY);

		final float height = heightStart
				+ ((Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) * heightRun / (componentMax * 3));
		return height;
	}
}
