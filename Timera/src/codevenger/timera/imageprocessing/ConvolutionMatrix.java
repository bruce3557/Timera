package codevenger.timera.imageprocessing;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

public class ConvolutionMatrix {
    public static final int SIZE = 3;
    
    public double[][] Matrix;
    public double Factor = 1;
    public double Offset = 1;
 
    public ConvolutionMatrix(int size) {
        Matrix = new double[size][size];
    }
 
    public void setAll(double value) {
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                Matrix[x][y] = value;
            }
        }
    }
 
    public void applyConfig(double[][] config) {
        for(int x = 0; x < SIZE; ++x) {
            for(int y = 0; y < SIZE; ++y) {
                Matrix[x][y] = config[x][y];
            }
        }
    }
 
    public static Bitmap computeConvolution3x3(Bitmap src, ConvolutionMatrix matrix) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
 
        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[SIZE][SIZE];
 
        for(int y = 0; y < height - 2; ++y) {
            for(int x = 0; x < width - 2; ++x) {
 
                // get pixel matrix
                for(int i = 0; i < SIZE; ++i) {
                    for(int j = 0; j < SIZE; ++j) {
                        pixels[i][j] = src.getPixel(x + i, y + j);
                    }
                }
 
                // get alpha of center pixel
                A = Color.alpha(pixels[1][1]);
 
                // init color sum
                sumR = sumG = sumB = 0;
 
                // get sum of RGB on matrix
                for(int i = 0; i < SIZE; ++i) {
                    for(int j = 0; j < SIZE; ++j) {
                        sumR += (Color.red(pixels[i][j]) * matrix.Matrix[i][j]);
                        sumG += (Color.green(pixels[i][j]) * matrix.Matrix[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * matrix.Matrix[i][j]);
                    }
                }
 
                // get final Red
                R = (int)(sumR / matrix.Factor + matrix.Offset);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }
 
                // get final Green
                G = (int)(sumG / matrix.Factor + matrix.Offset);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }
 
                // get final Blue
                B = (int)(sumB / matrix.Factor + matrix.Offset);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }
 
                // apply new pixel
                result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }
 
        // final image
        return result;
    }

	public static Bitmap computePathConvolution(Bitmap src,
			ConvolutionMatrix matrix, List<Point> path, int radius) {
		int width = src.getWidth();
        int height = src.getHeight();
       // Log.d(null, "width: " + width + ", height: " + height);
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
 
        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[radius * 2 + 1][radius * 2 + 1];
        /*
        for(int y = 0; y < height - 2; ++y) {
            for(int x = 0; x < width - 2; ++x) {
            	int pix = src.getPixel(x, y);
            	A = Color.alpha(pix);
            	R = Color.red(pix);
            	G = Color.green(pix);
            	B = Color.blue(pix);
            	result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }
        */
        int rs = 200;
        for(Point p : path) {
        	int cx = p.x;
        	int cy = p.y;
        	
        	for(int y = -radius - rs;y < radius + rs;++y) {
        		if(y > -radius + rs || y < radius - rs)	continue;
        		for(int x = -radius;x < radius;++x) {
        			if(x > -radius + rs || x < radius - rs)	continue;
        			int nx = cx + x;
        			int ny = cy + y;
        			if( (x) * (x) + (y) * (y) > (radius + rs) * (radius + rs) )	continue;
        			//Log.d(null, "nx = " + nx + ", ny = " + ny);
        			int pix = src.getPixel(nx, ny);
        			
        			A = Color.alpha(pix) / 3;
        			sumR = sumG = sumB = 0;
        			
        			for(int i=-1;i<2;++i)
        				for(int j=-1;j<2;++j) {
        					pix = src.getPixel(nx + i, ny + j);
        					sumR += (Color.red(pix) * matrix.Matrix[i + 1][j + 1]);
                            sumG += (Color.green(pix) * matrix.Matrix[i + 1][j + 1]);
                            sumB += (Color.blue(pix) * matrix.Matrix[i + 1][j + 1]);
        				}
        			// get final Red
                    R = (int)(sumR / matrix.Factor + matrix.Offset);
                    if(R < 0) { R = 0; }
                    else if(R > 255) { R = 255; }
     
                    // get final Green
                    G = (int)(sumG / matrix.Factor + matrix.Offset);
                    if(G < 0) { G = 0; }
                    else if(G > 255) { G = 255; }
     
                    // get final Blue
                    B = (int)(sumB / matrix.Factor + matrix.Offset);
                    if(B < 0) { B = 0; }
                    else if(B > 255) { B = 255; }
     
                    // apply new pixel
                    result.setPixel(nx + 1, ny + 1, Color.argb(A, R, G, B));
        		}
        	}
        }
        
		return result;
	}
}
