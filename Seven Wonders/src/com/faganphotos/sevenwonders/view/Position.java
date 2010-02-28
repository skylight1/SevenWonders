package com.faganphotos.sevenwonders.view;

/**
 * A position in three dimensions.
 *
 */
public class Position {
	
	public float x, y, z;
	
	public Position(float anX, float aY, float aZ) {
		x = anX;
		y = aY;
		z = aZ;
	}
	
	public Position() {
	}

	@Override
	public String toString() {
		return String.format("%s: %f, %f, %f", this.getClass().getName(), x, y, z);
	}
}
