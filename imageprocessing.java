/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.digital_watermarking;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author VISHAL
 */
public class imageprocessing {

    static int height=0,width=0,block=32,col=0,row=0,size=1024,N=32,k=2;
    static int key = 1;
    static double result[][]= new double[size][size];
    static double temp[][] = new double[block][block];
    //static double watermark[][] = new double[block][block];
    static boolean embedding =false, extraction = false;
    static File Host,Watermark;
    
    public imageprocessing(File H)
    {
        extraction = false;
        int pixels[][] = fileToPixels(H);
        double newpixels[][] = inttodouble(pixels);
        System.out.println("size of image "+pixels.length+" "+pixels[0].length);
        spiltimage(newpixels);
        System.out.println("image processing done");
         int newresult[][] = doubletoint(result);
        BufferedImage resultimage = pixelsToImage(newresult);
         System.out.println("Saving image to project folder");
        try {
           // BufferedImage bi = getMyImage();  // retrieve image
            File outputfile = new File("saved1.jpg");
            ImageIO.write(resultimage, "jpg", outputfile);
            System.out.println("Watermarking done");
        } catch (Exception e) {
            // handle exception
            System.out.println("error occur during image to file ");
        }
        
    }
    public imageprocessing(File H,File W) {
        //imageprocessing obj =  new imageprocessing();
        embedding = true;
        Host = H;
        Watermark = W;
        int pixels[][] = fileToPixels(Host);
        double newpixels[][] = inttodouble(pixels);
        System.out.println("size of image "+pixels.length+" "+pixels[0].length);
        spiltimage(newpixels);
        System.out.println("image processing done");
        key = 1;
        int newresult[][] = doubletoint(result);
        BufferedImage resultimage = pixelsToImage(newresult);
         System.out.println("Saving image to project folder");
        try {
           // BufferedImage bi = getMyImage();  // retrieve image
            File outputfile = new File("saved.jpg");
            ImageIO.write(resultimage, "jpg", outputfile);
            System.out.println("Watermarking done");
        } catch (Exception e) {
            // handle exception
            System.out.println("error occur during image to file ");
        }
        
        
    } 
    
    private void extraction()
    {
        temp = DCT.DCT2(temp); //dct
        Matrix image = new Matrix(temp);
        // 	B_W^*= U ∗   ∗   S_W^* ∗   V^(*T)
        SingularValueDecomposition obj = new SingularValueDecomposition(image); // step1	B=U*S*V^T
        Matrix sb = obj.getS();   //S
        Matrix ub = obj.getU();
        Matrix vb = obj.getV();
        
        double keyu[][] = readarray('u'); // key for extracting water mark ,.,,
        Matrix Mkeyu = new Matrix(keyu);
        double keys[][] = readarray('s');
        Matrix Mkeys = new Matrix(keys);
        double keyv[][] = readarray('v');
        Matrix Mkeyv = new Matrix(keyv);
        
        // step 2   	 S_1^* = U w  ∗  S_W^* ∗  V_w^T

       Matrix s1 = Mkeyu.solve(sb);
       s1 = s1.solve(Mkeyv);
       
       
       // step 3
       
       Matrix w1 = sb.minus(Mkeys);
      
       
       //step 4 
        key++;
    }
    
    private double[][] readarray(char value)
    {
       double board[][] = null;
        try
        (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\VISHAL\\Documents\\NetBeansProjects\\digital_watermarking\\"+value+"\\"+key+".txt"));)
        {String line = "";
int row = 0;
while((line = reader.readLine()) != null)
{
   String[] cols = line.split(","); //note that if you have used space as separator you have to split on " "
   int col = 0;
   for(String  c : cols)
   {
      board[row][col] = Integer.parseInt(c);
      col++;
   }
   row++;
}
reader.close();
    } catch (IOException e) {

}
    return board;
    }
    
    private void storearray(double array[][],char value,int key)
    {
      //  System.out.print(array.length);
       StringBuilder builder = new StringBuilder();
for(int i = 0; i < array.length; i++)//for each row
{
   for(int j = 0; j < array.length; j++)//for each column
   {
      builder.append(array[i][j]+"");//append to the output string
      if(j < array.length - 1)//if this is not the last row element
         builder.append(",");//then add comma (if you don't like commas you can use spaces)
   }
   builder.append("\n");//append new line at the end of the row
}
try
(BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\VISHAL\\Documents\\NetBeansProjects\\digital_watermarking\\"+value+"\\"+key+".txt"));){
writer.write(builder.toString());//save the string representation of the board
writer.close();
// System.out.print("hi done bhai");
    }
    catch (IOException e) {

}
}
    private double[][] sfc(double[][] a)
    {
       double b[]= new double[block*block];
       int n  = 32;
       int t1,t2,t3,t4;
       
       for(int i=0;i<block;i++)
       {
           for(int j=0;j<block;j++)
           {
               if(i<=j && rem(j-i,2)== 0)
               {
                   t1=2*i+(j-i)*n-(j-i)*(j-i-1)/2; 
                  // System.out.print(t1+" ");
                    b[t1] = a[i][j]; //b(t1)=a(i,j);
               }
               else if (i<=j && rem(j-i,2)!=0)        //~=0)
               {
                   t2=2*i+(j-i-1)*n-(j-i-1)*(j-i-2)/2;
                   b[t2]=a[i][j]; // b(t2)=a(i,j);       
               }
               else if(i>j && rem(i-j,2)==0)
               {
                   t3=(n^2+n)/2+2*j+(i-j-1)*n-(i-j-1)*(i-j-2)/2-n; 
                    b[t3] = a[i][j]; // b(t3)=a(i,j);
               }
               else if(i>j && rem(i-j,2) != 0) //~=0
                {
                     t4=(n^2+n)/2+2*j+(i-j)*n-(i-j)*(i-j-1)/2-1-n;
                     b[t4] = a[i][j]; //b(t4)=a(i,j);   
                } 
               
               }
           }
       
      double c[][] = construct2DArray(b,block,block);
      
       
       return c;
    }
     public double[][] construct2DArray(double[] original, int m, int n) {
        if(m*n!=original.length) return new double[][]{};
        double result[][]=new double[m][n];
        int i=0,j=0;
        for(double val:original){
            result[i][j]=val;
            j=(j+1)%n;
            if(j==0)
                i++;
        }
        return result;       
    }
    public int rem(int a, int b)
    {
        return a%b;
    }
     public void embeddingprocess()
    {
        
        temp = DCT.DCT2(temp); //dct
        Matrix image = new Matrix(temp);  // image 
       // System.out.print(image.getColumnDimension());
       // step 2  B=U*S*V^T
        SingularValueDecomposition obj = new SingularValueDecomposition(image); //	B=U*S*V^T
        Matrix s = obj.getS();   //S
        Matrix u1 = obj.getU();
        Matrix v1 = obj.getV();
       // System.out.print(s.getColumnDimension());
        storearray(s.getArray(),'s',key); //storing S value to file;
        
        // watermark processing
        int watermarkpixels[][] = fileToPixels(Watermark);  
        double watermark[][] = inttodouble(watermarkpixels);
        double watermarksfc[][] = sfc(watermark);
        
        
       
        //step 3 S1 = S + α ∗ W     
        Matrix watermarkmatrix = new Matrix(watermark);
        Matrix watermarksfcmatrix = new Matrix(watermarksfc);
        //watermarksfcmatrix.transpose();
        watermarksfcmatrix.times(1); //sclar multiply   
       // Matrix s1 = s.plus(watermarkmatrix);
        Matrix s1 = s.plus(watermarksfcmatrix);
        
        // step 4  S1 = Uw *  Sw * Vw^T
      SingularValueDecomposition obj2 = new SingularValueDecomposition(s1);  //S1=U_w *  S_W * V_W^T
      Matrix sw = obj2.getS(); 
      Matrix Uw = obj2.getU();
      Matrix Vw = obj2.getV();
      storearray(Uw.getArray(),'u',key);
      storearray(Vw.getArray(),'v',key);
      //step 5 Bw = U ∗ Sw * V^T
      Matrix result1 = u1.solve(sw); // Bw = U ∗ Sw * V^T
     result1 = v1.solve(result1);
      double temp2[][] = result1.getArray();
      
      //step 6 inverse DCT
        temp = DCT.IDCT2(temp); //inverse dct */
        key++;
        
    }
    public double[][] inttodouble(int pixels[][])
    {
        int row = pixels.length;
        int col = pixels[0].length;
       // System.out.print(col+" "+ row);
        double result[][] = new double[row][col];
        
      for(int i =0;i<row;i++)
      {
          for(int j=0;j<col;j++)
          {
              result[i][j] = pixels[i][j];
          }
      }
        return result;  
    }
    
    public int[][] doubletoint(double pixels[][])
    {
        int row = pixels.length;
        int col = pixels[0].length;
        
        int result[][] = new int[row][col];
        
       for(int i =0;i<row;i++)
       {
           for(int j=0;j<col;j++)
           {
               result[i][j] = (int)Math.round(pixels[i][j]);
           }
       }
        return result;  
    }
    
   
   
    public void spiltimage(double pixels[][]){
        while(row < size){
        temp = splitblock(pixels,col,row); //operation apply here
       if(embedding == true && extraction == false )
       {
           embeddingprocess();
       }
       
       if(extraction == true && embedding == false)
       {
           extraction();
       }
       
        updateresult(temp,col,row);
        col = col + block;
        if(col >= size)
        {
          row = row + block;
          col = 0;
        }
        }
    }

    static double[][] splitblock(double pixels[][],int col,int row)
    {
      for(int i=0;i<block;i++)
      {
        for(int j=0;j<block;j++)
        {
          temp[i][j] = pixels[row+i][col+j];
        }
      }
     return temp;

    }

    static void updateresult(double temp[][],int col,int row)
    {
  
      for(int i=row,n=0;i<block || n<block;i++,n++)
      {
        for(int j=col,m=0;j<block || m<block;j++,m++)
        {
           result[i][j] = temp[n][m];
        }
  
      }
      
    }
    public File getFile(){
        // create a GUI window to pick the text to evaluate
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showOpenDialog(null);
        File f = null;
        chooser.grabFocus();
        if (retval == JFileChooser.APPROVE_OPTION)
           f = chooser.getSelectedFile();
        return f;
    }
    public int[][] fileToPixels(File f){
	    int[][] result = null;
    	BufferedImage img;
	    try {
	        img = ImageIO.read(f);
	    } catch (Exception e) {
	        System.out.println("Incorrect File ");
	        return result;
	    }
	    if (img.getType() == BufferedImage.TYPE_INT_RGB) {
	        result = imageToPixels(img);
	    } else {
	        BufferedImage tmpImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
	        tmpImage.createGraphics().drawImage(img, 0, 0, null);
	        result = imageToPixels(tmpImage);
	    } 
	    return result;
    }
    public int[][] imageToPixels(BufferedImage image) throws IllegalArgumentException {
        if (image == null) {
            throw new IllegalArgumentException();
        }    
        width = image.getWidth();
        height = image.getHeight();
        int[][] pixels = new int[height][width];
        for (int row = 0; row < height; row++) {
            image.getRGB(0, row, width, 1, pixels[row], 0, width);
        }
        return pixels;
    }
     
    public BufferedImage pixelsToImage(int[][] pixels) throws IllegalArgumentException {
        if (pixels == null) {
            throw new IllegalArgumentException();
        }
        int width = pixels[0].length;
        int height = pixels.length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            image.setRGB(0, row, width, 1, pixels[row], 0, width);
        }
        return image;
    }
}
