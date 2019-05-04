import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.BaseImage;

public class HoughTransform {

	public void printHoughList(List<Hough> list){
		for(int i=0;i<list.size();i++){
			System.out.println("ro: "+list.get(i).ro+" , angle: "+list.get(i).angle);
		}
	}
	
	public List<Hough> mergeLine(List<Hough> list){
		List<Hough> resList=new ArrayList<Hough>();
		int i=1;
		int ro=list.get(0).ro;
		int angle=list.get(0).angle;
		List<Hough> tmpList=new ArrayList<Hough>();
		tmpList.add(list.get(0));
		while(i<list.size()){
			int tmpRo=list.get(i).ro;
			int tmpAngle=list.get(i).angle;
			if(Math.abs(ro-tmpRo)<=10&&Math.abs(angle-tmpAngle)<=3){
				tmpList.add(list.get(i));
			}else{
				if(tmpList.size()<=3){
					for(int j=0;j<tmpList.size();j++){
						resList.add(tmpList.get(j));
					}
				}else{
					int avgRo=0;
					int avgAngle=0;
					
					for(int j=0;j<tmpList.size();j++){
						avgRo+=tmpList.get(j).ro;
						avgAngle+=tmpList.get(j).angle;
					}
					avgRo=avgRo/tmpList.size();
					avgAngle=avgAngle/tmpList.size();
					
					resList.add(new Hough(avgRo,avgAngle));
					resList.add(new Hough(avgRo+1,avgAngle));
					if(avgRo==0)
						resList.add(new Hough(avgRo+2,avgAngle));
					else resList.add(new Hough(avgRo-1,avgAngle));
				}
				tmpList.clear();
				tmpList.add(list.get(i));
				ro=list.get(i).ro;
				angle=list.get(i).angle;
			}
			i++;
		}
		if(tmpList.size()>0){
			if(tmpList.size()<=3){
				for(int j=0;j<tmpList.size();j++){
					resList.add(tmpList.get(j));
				}
			}else{
				int avgRo=0;
				int avgAngle=0;
				
				for(int j=0;j<tmpList.size();j++){
					avgRo+=tmpList.get(j).ro;
					avgAngle+=tmpList.get(j).angle;
				}
				avgRo=avgRo/tmpList.size();
				avgAngle=avgAngle/tmpList.size();
				
				resList.add(new Hough(avgRo,avgAngle));
				resList.add(new Hough(avgRo+1,avgAngle));
				if(avgRo==0)
					resList.add(new Hough(avgRo+2,avgAngle));
				else resList.add(new Hough(avgRo-1,avgAngle));
			}
			tmpList.clear();
		}
		return resList;
	}
	public void houghTransformLine(BufferedImage image) throws IOException{
		int width=image.getWidth();
		int height=image.getHeight();
		int[][] data=new int[height][width];
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				int pixel=image.getRGB(i, j);
				data[j][i]=pixel&0xff;
			}
		}
		
		int ro=(int)Math.sqrt(height*height+width*width);
		int theta=90;
		int[][] hist=new int[ro][theta+1];
		
		for(int k=0;k<=theta;k++){
			for(int i=0;i<width;i++){
				for(int j=0;j<height;j++){
					if(data[j][i]!=0){
						//此处存在问题
						double segma=k*Math.PI/(theta*2);
						segma=k*Math.PI/(theta*2);
						int rho=(int)(i*Math.cos(segma))+(int)(j*Math.sin(segma));
						hist[rho][k]++;
					}
				}
			}
		}
		
		List<Hough> tmpPeeks=peakHough(hist,70);
		printHoughList(tmpPeeks);
		List<Hough> peeks=mergeLine(tmpPeeks);
		System.out.println();
		System.out.println();
		System.out.println();
		printHoughList(peeks);
		for(int k=0;k<peeks.size();k++){
			double resTheta=peeks.get(k).angle*Math.PI/(theta*2);
			resTheta=peeks.get(k).angle*Math.PI/(theta*2);
			for(int i=0;i<width;i++){
				for(int j=0;j<height;j++){
					int rho=(int)(i*Math.cos(resTheta)+j*Math.sin(resTheta));
					if(data[j][i]!=0&&rho==peeks.get(k).ro){
						data[j][i]=setRed();
					}else{
						if(data[j][i]>255||data[j][i]<0){
							//System.out.println("+++++out: "+data[j][i]);
						}
						data[j][i]=setColor(data[j][i]);
					}
				}
			}
		}
		
		mat2Image("./image/houghapple_2.png",data);
	}
	
	public void houghTransformCircle(){}
	
	
	public void houghTransformCircleNoR(){
		
	}
	
	public int[][] createHoughMat(BufferedImage image,int startSeta,int endSeta){
		int n=10;
		int m=10;
		int[][] mat=new int[n][m];
		return mat;
	}
	public List<Hough> peakHough(int[][] mat,int num){
		List<Hough> resList=new ArrayList<Hough>();
		int width=mat[0].length;
		int height=mat.length;
		int max=0;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(max<mat[i][j]){
					max=mat[i][j];
				}
			}
		}
		
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(mat[i][j]>((double)max*num/100.0)){
					resList.add(new Hough(i,j));
				}
			}
		}
		return resList;
	}
	
	public BufferedImage drawHoughLine(){
		BufferedImage bi=null;
		return bi;
	}
	
	public BufferedImage drawHoughCircle(){
		BufferedImage bi=null;
		return bi;
	}
	
	
	public int setRed(){
		return 0xff0000;
	}
	
	public int setColor(int num){
		return (255<<24)|(num<<16)|(num<<8)|num;
		//return new Color(num&0xff0000>>16,num&0xff00>>8,num&0xff).getRGB();
	}
	public void mat2Image(String fileName,int[][] mat) throws IOException{
		int width=mat[0].length;
		int height=mat.length;
		BufferedImage bi=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				bi.setRGB(i, j, mat[j][i]);
			}
		}
		
		BaseImage.createImage(bi,fileName);
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HoughTransform myObj=new HoughTransform();
		BufferedImage image = BaseImage.readImage("./image/apple1.png");
		myObj.houghTransformLine(image);
		
	}

}
