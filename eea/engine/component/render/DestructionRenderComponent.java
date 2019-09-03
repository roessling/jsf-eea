package eea.engine.component.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.BufferedImageUtil;

import eea.engine.component.RenderComponent;
import eea.engine.interfaces.IDestructionPattern;

public class DestructionRenderComponent extends RenderComponent {

	protected int resultWidth;
	protected int resultHeight;

	protected Image result;

	protected ByteBuffer buffer;

	protected Texture texture;

	protected IDestructionPattern pattern;

	protected boolean debug;

	/**
	 * @param pattern
	 *            The destruction pattern that is used when
	 *            {@link #applyDestruction(int, int)} called.
	 * @see {@link #applyDestruction(int, int)}
	 */
	public void setDestructionPattern(IDestructionPattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return The destruction pattern that is used when
	 *         {@link #applyDestruction(int, int)} called.
	 * @see {@link #applyDestruction(int, int)}
	 */
	public IDestructionPattern getDestructionPattern() {
		return pattern;
	}

	public DestructionRenderComponent(URL original, boolean debug) {
		this(original, null, debug);
	}

	public DestructionRenderComponent(URL original,
			IDestructionPattern pattern, boolean debug) {
		super("DestructionRenderComponent");

		this.pattern = pattern;
		this.debug = debug;

		try {
			BufferedImage image = ImageIO.read(original);
			buffer = bufferedImageToByteBuffer(image);

			if (!this.debug) {
				texture = BufferedImageUtil.getTexture(null, image);
				result = new Image(texture);
			}

			resultWidth = image.getWidth();
			resultHeight = image.getHeight();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DestructionRenderComponent(BufferedImage original,
			IDestructionPattern pattern, boolean debug) {
		super("DestructionRenderComponent");

		this.pattern = pattern;
		this.debug = debug;

		try {
			BufferedImage image = original;
			buffer = bufferedImageToByteBuffer(image);

			if (!this.debug) {
				texture = BufferedImageUtil.getTexture(null, image);
				result = new Image(texture);
			}

			resultWidth = image.getWidth();
			resultHeight = image.getHeight();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the pixel data of a BufferedImage to a ByteBuffer
	 * 
	 * @param image
	 *            the BufferedImage
	 * @return a new ByteBuffer, which contains the pixel data of the image
	 */
	private ByteBuffer bufferedImageToByteBuffer(BufferedImage image) {

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0,
				image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()
				* image.getHeight() * 4);

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				// Red component
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				// Green component
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				// Blue component
				buffer.put((byte) (pixel & 0xFF));
				// Alpha component. Only for RGBA
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		return buffer;
	}

	/**
	 * Returns an integer pixel in the default RGB color model (TYPE_INT_ARGB)
	 * and default sRGB colorspace.
	 * 
	 * @param x
	 *            The x-coordinate of the pixel from which to get the pixel.
	 * @param y
	 *            The y-coordinate of the pixel from which to get the pixel.
	 * @return An integer pixel in the default RGB color model and default sRGB
	 *         colorspace.
	 * @see {@link BufferedImage#getRGB(int, int)}
	 */
	public int getRGB(int x, int y) {
		if (buffer != null) {
			return buffer.getInt((x + (y * resultWidth)) * 4);
		} else {
			return 0;
		}
	}

	/**
	 * Sets an integer pixel
	 * 
	 * @param x
	 *            The x-coordinate of the pixel from which to get the pixel.
	 * @param y
	 *            The y-coordinate of the pixel from which to get the pixel.
	 * @param color
	 *            The color of the integer pixel.
	 */
	private void setRGB(int x, int y, int color) {

		buffer.putInt((x + (y * resultWidth)) * 4, color);
	}

	@Override
	public Vector2f getSize() {
		if (buffer != null) {
			return new Vector2f(resultWidth, resultHeight);
		}
		return new Vector2f();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sb, int delta) {
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sb,
			Graphics graphicsContext) {

		if (result == null) {
			return;
		}

		Vector2f position = this.getOwnerEntity().getPosition();
		if (position == null) {
			position = new Vector2f();
		}

		float halfWidth = this.getSize().x / 2;
		float halfHeight = this.getSize().y / 2;
		graphicsContext.drawImage(result, position.x - halfWidth, position.y
				- halfHeight);
	}

	/**
	 * TODO: Comment For this method must be checked that <code>buffer</code> is
	 * not null.
	 * 
	 * @param centerX
	 * @param centerY
	 */
	public void applyDestruction(int centerX, int centerY) {

		// correct possible slightly negative values, which stem from the
		// bounding box test (e. g. if the banana hits the city image from the
		// top)
		centerX = centerX < 0 ? 0 : centerX;
		centerY = centerY < 0 ? 0 : centerY;

		int startX = centerX - Math.round(pattern.getCenter().x);
		int startY = centerY - Math.round(pattern.getCenter().y);

		// if startX or startY are negative they have to be set to 0, but negX
		// and negY have to be set as offset for the application of the image
		// destruction pattern
		int negX = 0;

		int negY = 0;

		// check and correct startX
		if (startX < 0) {
			negX = Math.abs(startX);
			startX = 0;
		} else if (startX >= resultWidth) {
			return; // pattern out of frame
		}

		// check and correct startY
		if (startY < 0) {
			negY = Math.abs(startY);
			startY = 0;
		} else if (startY >= resultHeight) {
			return; // pattern out of frame
		}

		int endX = centerX + pattern.getWidth()
				- Math.round(pattern.getCenter().x);
		int endY = centerY + pattern.getHeight()
				- Math.round(pattern.getCenter().y);

		// check and correct endX
		if (endX < 0) {
			return; // pattern out of frame
		} else if (endX >= resultWidth) {
			endX = resultWidth - 1;
		}

		// check and correct endY
		if (endY < 0) {
			return; // pattern out of frame
		} else if (endY >= resultHeight) {
			endY = resultHeight - 1;
		}

		// modify colors in the pattern area
		for (int x = startX; x <= endX; x++) {

			for (int y = startY; y <= endY; y++) {

				// determine the old color
				int oldColor = getRGB(x, y);

				// calculate and set the new color
				int newColor = pattern.getModifiedColor(oldColor, x - startX
						+ negX, y - startY + negY);

				setRGB(x, y, newColor);
			}
		}
		updateResult();
	}

	/**
	 * updates the image with the pixels in the buffer
	 */
	private void updateResult() {

		if (!debug) {
			buffer.position(buffer.capacity());
			buffer.flip();
			texture.bind();
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0,
					result.getWidth(), result.getHeight(), GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, buffer);
		}
	}
}
