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
	/*加载图片
	 * */
	public BufferedImage readImage(String fileName) throws IOException {
		BufferedImage bi = ImageIO.read(new File(fileName));
		return bi;
	}
    /*图像二值化
     * */
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
	/*生成图片
	 * */
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
	/*添加椒盐噪声 saltNoise
	 * */
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
	/*添加高斯噪声,存在问题
	 * */
	public BufferedImage addGussisNoise(BufferedImage image){
		BufferedImage bi=image;
		double noise=Math.random()*8;
		int width=image.getWidth();
		int height=image.getHeight();
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				noise=Math.random()*4;
				if(Math.random()>0.5)
					noise*=-1;
				bi.setRGB(i, j, bi.getRGB(i, j)+(int)noise);
			}
		}
		return bi;
	}
	/*均值,有问题
	 * */
	public BufferedImage avgImage(BufferedImage image){
		BufferedImage bi=image;
		int width=image.getWidth();
		int height=image.getHeight();
		int row=1;
		int col=1;
		for(int i=row;i<height-row;i++){
			for(int j=col;j<width-col;j++){
				int ret=0;
				
				ret=(int) (bi.getRGB(i-1, j-1)*0.1+bi.getRGB(i-1, j)*0.15+bi.getRGB(i-1, j+1)*0.1);
				ret+=bi.getRGB(i, j-1)*0.1+bi.getRGB(i, j)*0.15+bi.getRGB(i, j+1)*0.1;
				ret+=bi.getRGB(i+1, j-1)*0.1+bi.getRGB(i+1, j)*0.15+bi.getRGB(i+1, j+1)*0.1;
				
				//ret=ret/((2*row+1)*(2*col+1));
				bi.setRGB(i, j, ret);
			}
		}
		return bi;
	}
	
	/*边缘提取
	 * */
	public BufferedImage edgeDetect(BufferedImage image){
		int width=image.getWidth();
		int height=image.getHeight();
		BufferedImage bi=new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		return bi;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		PicProcess myObj = new PicProcess();
		BufferedImage image=myObj.readImage("./image/flower1.jpg");
		BufferedImage bi=myObj.avgImage(image);
		myObj.createImage(bi, "./image/avgFlower1.png");
		System.out.println("the end of program");
	}

}
