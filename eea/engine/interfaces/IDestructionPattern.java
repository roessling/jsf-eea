package eea.engine.interfaces;

import org.newdawn.slick.geom.Vector2f;

public interface IDestructionPattern {

	/**
	 * @return The width of the bounding box around the destruction.
	 */
	public int getWidth();
	
	/**
	 * @return The height of the bounding box around the destruction.
	 */
	public int getHeight();

	/**
	 * @return The center of the destruction. Note: The center of the
	 *         destruction is not necessarily the same as the center of the
	 *         bounding box around the pattern.
	 */
	public Vector2f getCenter();

	/**
	 * Calculates the color of a pixel after the destruction.
	 * 
	 * @param color
	 *            The color before the destruction. Color format must be ARGB.
	 * @param x
	 *            The x-coordinate of the pixel relative to the upper left
	 *            corner of the bounding box around the pattern.
	 * @param y
	 *            The y-coordinate of the pixel relative to the upper left
	 *            corner of the bounding box around the pattern.
	 * @return The color after the destruction. Color format: ARGB.
	 */
	public int getModifiedColor(int color, int x, int y);

}
