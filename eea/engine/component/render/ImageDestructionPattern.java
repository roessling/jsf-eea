package eea.engine.component.render;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.newdawn.slick.geom.Vector2f;

import eea.engine.interfaces.IDestructionPattern;

/**
 * This class provides a destruction pattern defined by the alphamap of an
 * image, maintaining the original color.
 * 
 * @author Peter Kloeckner, Sebastian Fach
 */
public class ImageDestructionPattern implements IDestructionPattern {

	/**
	 * The image which this pattern is based on.
	 */
	protected BufferedImage pattern;

	/**
	 * The x-coordinate of the center of the pattern.
	 */
	protected int centerX;

	/**
	 * The y-coordinate of the center of the pattern.
	 */
	protected int centerY;

	/**
	 * Constructor
	 * 
	 * @param pattern
	 *            The URL of the image which defines the pattern by it's
	 *            alphamap.
	 */
	public ImageDestructionPattern(URL pattern) {

		try {
			this.pattern = ImageIO.read(pattern);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.pattern != null)
			centerX = this.pattern.getWidth() / 2;
		centerY = this.pattern.getHeight() / 2;
	}

	@Override
	public int getWidth() {
		if (pattern != null) {
			return pattern.getWidth();
		}
		return 0;
	}

	@Override
	public int getHeight() {
		if (pattern != null) {
			return pattern.getHeight();
		}
		return 0;
	}

	@Override
	public Vector2f getCenter() {
		return new Vector2f(centerX, centerY);
	}

	@Override
	public int getModifiedColor(int color, int x, int y) {

		try {

			int patternAlpha = pattern.getRGB(x, y) >>> 24;
			int oldAlpha = (color & 0xFF000000) >>> 24; // extract alpha byte
			int rgb = color & 0x00FFFFFF; // extract color
			int newAlpha = (oldAlpha * patternAlpha) / 255;
			return (newAlpha << 24) | rgb; // assemble new color

		} catch (Exception e) {
			return color; // in case of an error, modify nothing
		}

	}

}
