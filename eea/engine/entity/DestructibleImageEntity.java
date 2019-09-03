package eea.engine.entity;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.newdawn.slick.geom.Vector2f;

import eea.engine.component.Component;
import eea.engine.component.render.DestructionRenderComponent;
import eea.engine.component.render.ImageDestructionPattern;
import eea.engine.entity.Entity;
import eea.engine.interfaces.ICollidable;
import eea.engine.interfaces.IDestructible;

public class DestructibleImageEntity extends Entity implements ICollidable,
		IDestructible {

	/**
	 * This entity consists of an image that can be destroyed by destruction
	 * patterns.
	 * 
	 * @param entityID
	 *            the ID of this entity
	 * @param image
	 *            the image that will be destroyed
	 * @param destructionPath
	 *            path to the image which defines how this entity is destructed
	 * @param debug
	 *            the debug switch. <code>true</code> disables rendering.
	 */
	public DestructibleImageEntity(String entityID, BufferedImage image,
			String destructionPath, boolean debug) {
		super(entityID);

		URL destructionURL = DestructibleImageEntity.class.getResource("/"
				+ destructionPath);
		ImageDestructionPattern pattern = new ImageDestructionPattern(
				destructionURL);
		this.addComponent(new DestructionRenderComponent(image, pattern, debug));
	}

	public DestructibleImageEntity(String entityID,
			DestructionRenderComponent renderComponent) {
		super(entityID);

		this.addComponent(renderComponent);
	}

	@Override
	public boolean collides(float x, float y) {

		DestructionRenderComponent renderer = null;
		Component component = getEvent("DestructionRenderComponent");
		if (component instanceof DestructionRenderComponent) {
			renderer = (DestructionRenderComponent) component;
		} else {
			return false;
		}

		// calculate the relative coordinates (relative to the upper left corner
		// of the buffer used in the renderer)
		float halfWidth = this.getSize().x / 2;
		float halfHeight = this.getSize().y / 2;
		int relX = Math.round(x - this.getPosition().x + halfWidth);
		int relY = Math.round(y - this.getPosition().y + halfHeight);

		if (relX < 0 || relY < 0 || relX >= this.getSize().x
				|| relY >= this.getSize().y) {
			return false; // not in buffer area
		}

		// return true, if a pixel is hit that is not fully transparent
		return (renderer.getRGB(relX, relY) & 0xFF000000) != 0;
	}

	@Override
	public void impactAt(Vector2f position) {

		if (position == null) {
			return;
		}

		DestructionRenderComponent renderer = null;
		Component component = getEvent("DestructionRenderComponent");
		if (component instanceof DestructionRenderComponent) {
			renderer = (DestructionRenderComponent) component;
		} else {
			return;
		}

		float halfWidth = this.getSize().x / 2;
		float halfHeight = this.getSize().y / 2;
		int relX = Math.round(position.x - this.getPosition().x + halfWidth);
		int relY = Math.round(position.y - this.getPosition().y + halfHeight);

		renderer.applyDestruction(relX, relY);
	}
}
