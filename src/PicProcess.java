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
	public BufferedImage readImage(String fileName) throws IOException {
		BufferedImage bi = ImageIO.read(new File(fileName));
		return bi;
	}

	public BufferedImage binaryImage(BufferedImage image){
		int width=image.getWidth();
		int height=image.getHeight();
		BufferedImage bi=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
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
		return bi;
	}
	
	public void createImage(BufferedImage image,String imagePath) throws IOException{
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
	
	public BufferedImage addSaltNoise(BufferedImage image){
		int width=image.getWidth();
		int height=image.getHeight();
		BufferedImage bi=image;//new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		int white=new Color(255,255,255).getRGB();
		int row=4;
		int col=4;
		double r=0;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				r=Math.random();
				if(r>0.999){
					for(int h=0;h<col;h++){
						for(int g=0;g<row;g++){
							if(i+h<height&&j+g<width)
							   bi.setRGB(i+h, j+g, white);
						}
					}
				}
			}
		}
		return bi;
	}
	
	public BufferedImage addGussisNoise(BufferedImage image){
		BufferedImage bi=image;
		return bi;
	}
	
	public BufferedImage edgeDetect(BufferedImage image){
		int width=image.getWidth();
		int height=image.getHeight();
		BufferedImage bi=new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		return bi;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		PicProcess myObj = new PicProcess();
		BufferedImage image=myObj.readImage("./image/flower.jpg");
		BufferedImage bi=myObj.addSaltNoise(image);
		myObj.createImage(bi, "./image/saltFlower.png");
		System.out.println("the end of program");
	}

}
