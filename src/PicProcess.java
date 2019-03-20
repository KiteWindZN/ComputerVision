import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class PicProcess {

	int threadhold=128;
	public void readImage(String fileName) throws IOException {
		BufferedImage bi = ImageIO.read(new File(fileName));
		int width = bi.getWidth();
		int height = bi.getHeight();
		int black = new Color(0, 0, 0).getRGB();
		int white = new Color(255, 255, 255).getRGB();
		float[] rgb = new float[3];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int pixel = bi.getRGB(i, j);
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				float avg = (rgb[0] + rgb[1] + rgb[2]) / 3;
				if (avg < this.threadhold) {
					bi.setRGB(i, j, black);
				} else {
					bi.setRGB(i, j, white);
				}
			}
		}

		Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
		ImageWriter writer = it.next();
		File f = new File("newPic.png");
		ImageOutputStream ios = ImageIO.createImageOutputStream(f);
		writer.setOutput(ios);
		writer.write(bi);
		bi.flush();
		ios.flush();
		ios.close();
	}

	public void edgeDetect(String imagePath){
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		PicProcess myObj = new PicProcess();
		myObj.readImage("flower.jpg");
		System.out.println("the end of program");
	}

}
