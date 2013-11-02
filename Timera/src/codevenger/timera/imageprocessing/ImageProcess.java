package codevenger.timera.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageProcess {
	public static double smoothFunc(double x, double s) {
		return 1 / (1 + Math.exp(-((x - 0.5) / s)));
	}
	
	public static Bitmap toGrayscale(Bitmap src) {        
	    int width, height;
	    height = src.getHeight();
	    width = src.getWidth();    

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(src, 0, 0, paint);
	    return bmpGrayscale;
	}
	
	public static Bitmap yellowEffect(Bitmap src) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
		for(int y = 0;y < height - 1; ++y)
			for(int x = 0;x < width - 1; ++x) {
				int pix = src.getPixel(x, y);
				int A = Color.alpha(pix);
				int R = Color.red(pix);
				int G = Color.green(pix);
				int B = (int) (Color.blue(pix) * smoothFunc(Color.blue(pix) + 1, 256));
				result.setPixel(x+1, y+1, Color.argb(A, R, G, B));
				
			}
		return result;
	}
	
	public static Bitmap lomoEffect(Bitmap src) {
		int width = src.getWidth();
		int height = src.getHeight();
		int cx = width / 2;
		int cy = height / 2;
		double r = Math.sqrt( width * width / 4 + height * height / 4);
		Bitmap result = yellowEffect(src);
		
		for(int y = 0;y < height - 1; ++y)
			for(int x = 0;x < width - 1; ++x) {
				double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
				double ratio = dist / r;
				int pix = src.getPixel(x, y);
				int A = Color.alpha(pix);
				int R = (int) (Color.red(pix) * (1 - ratio * ratio));
				int G = (int) (Color.green(pix) * (1 - ratio * ratio));
				int B = (int) (Color.blue(pix) * (1 - ratio * ratio));
				result.setPixel(x, y, Color.argb(A,  R, G, B));
			}
		
		return result;
	}
}
