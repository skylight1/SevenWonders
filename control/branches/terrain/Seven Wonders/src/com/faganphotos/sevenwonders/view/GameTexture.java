package com.faganphotos.sevenwonders.view;

/**
 * A piece of the atlas texture 
 * that is meaningful in the game world
 * specified using atlas coordinate bounds.
 *
 */
public class GameTexture {
	
	public static final GameTexture SAND = new GameTexture(
		320f / 1024f,
		(320f + 256f) / 1024f,
		0,
		256f / 1024f
	);

	final float s1;
	
	final float s2;
	
	final float t1;
	
	final float t2;

	/**
	 * Create a GameTexture.
	 * 
	 * @param s1Param float where the texture starts in the x axis of the atlas texture
	 * @param s2Param float where the texture ends in the x axis of the atlas texture
	 * @param t1Param float where the texture starts in the y axis of the atlas texture
	 * @param t2Param float where the texture ends in the y axis of the atlas texture
	 */
	private GameTexture(
		final float s1Param, final float s2Param, 
		final float t1Param, final float t2Param) {
		
		s1 = s1Param;
		s2 = s2Param;
		t1 = t1Param;
		t2 = t2Param;
	}
	
}
