package eea.engine.interfaces;

import org.newdawn.slick.geom.Vector2f;

/**
 * Defines an interface for toggling destruction on destructible objects.
 * 
 * @author Peter Kloeckner, Sebastian Fach
 * @version 1.0
 */
public interface IDestructible {

	/**
	 * Notifies the implementer about an impact at the given position.
	 * 
	 * @param position
	 *            The position where the impact occurred.
	 */
	public void impactAt(Vector2f position);
}
