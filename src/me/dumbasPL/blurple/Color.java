package me.dumbasPL.blurple;

public class Color {

	public int red;
	public int green;
	public int blue;

	public Color(int r, int g, int b) {
		red = r;
		green = g;
		blue = b;
	}

	public int getColor() {
		return (255 << 24) | (red << 16) | (green << 8) | blue;
	}

}
