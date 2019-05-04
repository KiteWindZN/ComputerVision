package Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class BaseImage {
	/*
	 * 加载图片
	 */
	public static BufferedImage readImage(String fileName) throws IOException {
		BufferedImage bi = ImageIO.read(new File(fileName));
		return bi;
	}
	/*
	 * 生成图片
	 */
	public static void createImage(BufferedImage image, String imagePath) throws IOException {
		Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
		ImageWriter writer = it.next();
		File f = new File(imagePath);
		ImageOutputStream ios = ImageIO.createImageOutputStream(f);
		writer.setOutput(ios);
		writer.write(image);
		image.flush();
		ios.flush();
		ios.close();
	}
}
