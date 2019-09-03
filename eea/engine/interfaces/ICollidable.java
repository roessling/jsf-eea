package eea.engine.interfaces;

/**
 * Defines an interface for pixel-based collision detection.
 * 
 * @author Peter Kloeckner, Sebastian Fach
 * @version 1.0
 */
public interface ICollidable {

	/**
	 * Determines whether a collision with the implementer occurs at the
	 * specified coordinates.
	 * 
	 * @param x
	 *            The x-coordinate in the frame's coordinate system.
	 * @param y
	 *            The y-coordinate in the frame's coordinate system.
	 * @return <code>true</code> if a collision has been detected,
	 *         <code>false</code> otherwise.
	 */
	public boolean collides(float x, float y);
}
