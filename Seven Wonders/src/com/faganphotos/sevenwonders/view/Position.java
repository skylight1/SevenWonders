package com.faganphotos.sevenwonders.view;

public class Position {
	public float x, y, z;
	
	@Override
	public String toString() {
		return String.format("%s: %f, %f, %f", this.getClass().getName(), x, y, z);
	}
}
