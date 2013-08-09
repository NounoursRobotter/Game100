package com.smfandroid.game100;

public class Difficulty {

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getNbVoids() {
		return nbVoids;
	}
	
	protected int width;
	protected int height;
	protected int nbVoids;
	
	public Difficulty(int width, int height, int nbVoids) {
		this.width = width;
		this.height = height;
		this.nbVoids = nbVoids;
	}
	
	public String toString() { return width + "x" + height + ", voids : " + nbVoids; }
	
}