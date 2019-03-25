import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class PicProcess {
	int threadhold = 128;
	int row = 1;
	int col = 1;

	/*
	 * 加载图片
	 */
	public BufferedImage readImage(String fileName) throws IOException {
		BufferedImage bi = ImageIO.read(new File(fileName));
		return bi;
	}

	/*
	 * 图像二值化
	 */
	public BufferedImage binaryImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		int black = new Color(0, 0, 0).getRGB();
		int white = new Color(255, 255, 255).getRGB();
		float[] rgb = new float[3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
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

	/*
	 * 生成图片
	 */
	public void createImage(BufferedImage image, String imagePath) throws IOException {
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

	/*
	 * 添加椒盐噪声 saltNoise
	 */
	public BufferedImage addSaltNoise(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = image;// new
									// BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		int white = new Color(255, 255, 255).getRGB();
		int row = 4;
		int col = 4;
		double r = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				r = Math.random();
				if (r > 0.999) {
					for (int h = 0; h < col; h++) {
						for (int g = 0; g < row; g++) {
							if (i + h < height && j + g < width)
								bi.setRGB(i + h, j + g, white);
						}
					}
				}
			}
		}
		return bi;
	}

	/*
	 * 添加高斯噪声,存在问题==>>已解决
	 */
	public BufferedImage addGaussianNoise(BufferedImage image) {
		BufferedImage bi = image;
		double noise = Math.random() * 8;
		int width = image.getWidth();
		int height = image.getHeight();
		int[] rgb = new int[3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				noise = Math.random() * 32;
				if (Math.random() > 0.5)
					noise *= -1;
				int pixel = bi.getRGB(i, j);// i is width, j is height
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				rgb[0] = clamp(rgb[0] + (int) noise);
				rgb[1] = clamp(rgb[1] + (int) noise);
				rgb[2] = clamp(rgb[2] + (int) noise);

				bi.setRGB(i, j, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
			}
		}
		return bi;
	}

	public int clamp(int p) {
		return p > 255 ? 255 : (p < 0 ? 0 : p);
	}

	public BufferedImage createGrayImage(BufferedImage image) {
		BufferedImage bi = image;
		int width = image.getWidth();
		int height = image.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = bi.getRGB(i, j);
				int[] rgb = new int[3];
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = pixel & 0xff;
				int gray = (rgb[0] + rgb[1] + rgb[1]) / 3;
				rgb[0] = gray;
				rgb[1] = gray;
				rgb[2] = gray;
				bi.setRGB(i, j, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
			}
		}

		return bi;
	}

	/*
	 * 均值,有问题
	 */
	public BufferedImage avgImage(BufferedImage image) {
		BufferedImage bi = image;
		int width = image.getWidth();
		int height = image.getHeight();

		int[] rgb = new int[3];
		for (int i = col; i < width - col; i++) {
			for (int j = row; j < height - row; j++) {
				rgb = getAvgColor1(bi, i, j);
				bi.setRGB(i, j, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
			}
		}
		return bi;
	}

	public int[] getAvgColor1(BufferedImage bi, int i, int j) {
		int[] rgb = new int[3];
		float totalWeight = 0;
		float weight = 0;
		for (int h = i - col; h <= i + col; h++) {
			for (int g = j - row; g <= j + row; g++) {
				if (h == i && g == j)
					continue;
				weight = (float) Math.sqrt(1.0 / ((h - i) * (h - i) + (g - j) * (g - j)));
				int pixel = bi.getRGB(h, g);
				rgb[0] += ((pixel & 0xff0000) >> 16) * weight;
				rgb[1] += ((pixel & 0xff00) >> 8) * weight;
				rgb[2] += ((pixel & 0xff)) * weight;
				totalWeight += weight;
			}
		}
		int pixel = bi.getRGB(i, j);
		weight = (float) 0.5;
		totalWeight += weight;
		rgb[0] += ((pixel & 0xff0000) >> 16) * weight;
		rgb[1] += ((pixel & 0xff00) >> 8) * weight;
		rgb[2] += ((pixel & 0xff)) * weight;
		rgb[0] = (int) (rgb[0] / totalWeight);
		rgb[1] = (int) (rgb[1] / totalWeight);
		rgb[2] = (int) (rgb[2] / totalWeight);

		return rgb;
	}

	public int[] getAvgColor(BufferedImage bi, int i, int j) {
		int[] rgb = new int[3];

		int pixel = bi.getRGB(i - 1, j - 1);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.1;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.1;
		rgb[2] += ((pixel & 0xff)) * 0.1;

		pixel = bi.getRGB(i - 1, j);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.15;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.15;
		rgb[2] += ((pixel & 0xff)) * 0.15;

		pixel = bi.getRGB(i - 1, j + 1);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.1;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.1;
		rgb[2] += ((pixel & 0xff)) * 0.1;

		pixel = bi.getRGB(i, j - 1);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.15;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.15;
		rgb[2] += ((pixel & 0xff)) * 0.15;

		pixel = bi.getRGB(i, j + 1);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.15;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.15;
		rgb[2] += ((pixel & 0xff)) * 0.15;

		pixel = bi.getRGB(i + 1, j - 1);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.1;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.1;
		rgb[2] += ((pixel & 0xff)) * 0.1;

		pixel = bi.getRGB(i + 1, j);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.15;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.15;
		rgb[2] += ((pixel & 0xff)) * 0.15;

		pixel = bi.getRGB(i + 1, j + 1);
		rgb[0] += ((pixel & 0xff0000) >> 16) * 0.1;
		rgb[1] += ((pixel & 0xff00) >> 8) * 0.1;
		rgb[2] += ((pixel & 0xff)) * 0.1;

		return rgb;
	}

	public BufferedImage sharpenImage(BufferedImage image) {
		BufferedImage bi = image;
		int width = image.getWidth();
		int height = image.getHeight();
		System.out.println(width + " " + height);
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pixel = bi.getRGB(i, j);
				int[] rgb = new int[3];
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				int[] avgRgb = avgRgb(bi, i, j);
				rgb[0] = clamp(rgb[0] * 2 - avgRgb[0]);
				rgb[1] = clamp(rgb[1] * 2 - avgRgb[1]);
				rgb[2] = clamp(rgb[2] * 2 - avgRgb[2]);
				bi.setRGB(i, j, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
			}
		}
		return bi;
	}

	public int[] avgRgb(BufferedImage image, int i, int j) {
		int[] rgb = new int[3];
		int count = 0;
		int width = image.getWidth();
		int height = image.getHeight();

		for (int h = i - 1; h <= i + 1; h++) {
			for (int g = j - 1; g <= j + 1; g++) {
				if (h < 0 || g < 0 || g >= height || h >= width) {
					continue;
				}
				int pixel = image.getRGB(h, g);
				rgb[0] += (pixel & 0xff0000) >> 16;
				rgb[1] += (pixel & 0xff00) >> 8;
				rgb[2] += (pixel & 0xff);
				count++;
			}
		}
		rgb[0] /= count;
		rgb[1] /= count;
		rgb[2] /= count;
		return rgb;
	}

	/*
	 * 边缘提取
	 */
	public BufferedImage edgeDetect(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		return bi;
	}

	public double gaussian(float sigma, float dis) {
		double ret = 0;
		ret = 1.0 / Math.sqrt(2 * Math.PI);
		ret = ret * Math.exp(-(dis * dis) / (2 * sigma * sigma));
		return ret;
	}

	public BufferedImage gaussianSmooth(BufferedImage image) {
		BufferedImage bi = image;
		int width = image.getWidth();
		int height = image.getHeight();
		float sigma = 1.5f;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int[] rgb = calGaussianRgb(bi, i, j, sigma);
				bi.setRGB(i, j, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
			}
		}
		return bi;
	}

	public int[] calGaussianRgb(BufferedImage image, int i, int j, float sigma) {
		int[] rgb = new int[3];
		double totalWeight = 0;
		int width = image.getWidth();
		int height = image.getHeight();
		for (int h = i - col; h <= i + col; h++) {
			for (int g = j - row; g <= j + row; g++) {
				if (h < 0 || g < 0 || h >= width || g >= height)
					continue;
				float dis = (float) Math.pow((h - i), 2) + (float) Math.pow((g - j), 2);
				double weight = gaussian(sigma, dis);
				totalWeight += weight;
				int pixel = image.getRGB(h, g);
				rgb[0] += (pixel & 0xff0000) >> 16;
				rgb[1] += (pixel & 0xff00) >> 8;
				rgb[2] += (pixel & 0xff);
			}
		}
		rgb[0] /= totalWeight;
		rgb[1] /= totalWeight;
		rgb[2] /= totalWeight;
		return rgb;
	}

	public BufferedImage gradX(BufferedImage image, int[][] sobelX) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int gray = calSobel(image, i, j, sobelX);
				gray = clamp(Math.abs(gray));
				bi.setRGB(i, j, new Color(gray, gray, gray).getRGB());
			}
		}
		return bi;
	}

	public BufferedImage gradY(BufferedImage image, int[][] sobelY) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int gray = calSobel(image, i, j, sobelY);
				gray = clamp(Math.abs(gray));
				bi.setRGB(i, j, new Color(gray, gray, gray).getRGB());
			}
		}
		return bi;
	}

	public BufferedImage gradXandY(BufferedImage image, int[][] sobelX, int[][] sobelY) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int grayY = calSobel(image, i, j, sobelY);
				int grayX = calSobel(image, i, j, sobelX);
				int gray = (int) Math.sqrt(Math.pow(grayX, 2) + Math.pow(grayY, 2));
				gray = clamp(gray);
				bi.setRGB(i, j, new Color(gray, gray, gray).getRGB());
			}
		}
		return bi;
	}

	public int calSobel(BufferedImage image, int i, int j, int[][] sobel) {
		int width = image.getWidth();
		int height = image.getHeight();
		int ret = 0;
		for (int h = i - 1; h <= i + 1; h++) {
			for (int g = j - 1; g <= j + 1; g++) {
				if (h < 0 || g < 0 || h >= width || g >= height)
					continue;
				int pixel = image.getRGB(h, g);
				/*
				 * int[] rgb=new int[3]; rgb[0]=(pixel & 0xff0000)>>16;
				 * rgb[1]=(pixel & 0xff00)>>8; rgb[2]=(pixel & 0xff);
				 */
				int gray = (pixel & 0xff0000) >> 16;
				ret += gray * sobel[h - i + 1][g - j + 1];
			}
		}
		return ret;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PicProcess myObj = new PicProcess();
		BufferedImage image = myObj.readImage("./image/grayLena.png");
		int[][] sobelX = { { -1, 0, +1 }, { -2, 0, +2 }, { -1, 0, +1 } };
		int[][] sobelY = { { -1, -2, -1 }, { 0, 0, 0 }, { +1, +2, +1 } };
		int[][] sobelX1 = { { -3, 0, +3 }, { -10, 0, +10 }, { -3, 0, +3 } };
		int[][] sobelY1 = { { -3, -10, -3 }, { 0, 0, 0 }, { +3, +10, +3 } };
		BufferedImage bi = myObj.gradXandY(image, sobelX, sobelY);
		myObj.createImage(bi, "./image/gradLenaXandY2.png");
		bi = myObj.gradX(image, sobelX);
		myObj.createImage(bi, "./image/gradLenaX2.png");
		bi = myObj.gradY(image, sobelY);
		myObj.createImage(bi, "./image/gradLenaY2.png");
		System.out.println("the end of program");
	}

}
