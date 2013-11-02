package codevenger.timera.imageprocessing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class ImageProcess {
	public static Thread[] threads = new Thread[10];
	public static double smoothFunc(double x, double s) {
		return 1 / (1 + Math.exp(-((x - 0.5) / s)));
	}
	
	public static Bitmap guassianBlurMask(Bitmap src) {
		int width = src.getWidth();
        int height = src.getHeight();
        int blurValue = 1;
         
	    BlurMaskFilter blurMaskFilter;
	    Paint paintBlur = new Paint();
	      
	    Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas canvas = new Canvas(dest); 
	      
	    //Create background in White
	    Bitmap alpha = src.extractAlpha();
	    paintBlur.setColor(0xFFFFFFFF);
	    canvas.drawBitmap(alpha, 0, 0, paintBlur);
	      
	    //Create outer blur, in White
	    blurMaskFilter = new BlurMaskFilter(blurValue, BlurMaskFilter.Blur.OUTER);
	    paintBlur.setMaskFilter(blurMaskFilter);
	    canvas.drawBitmap(alpha, 0, 0, paintBlur);
	      
	    //Create inner blur
	    blurMaskFilter = new BlurMaskFilter(blurValue, BlurMaskFilter.Blur.INNER);
	    paintBlur.setMaskFilter(blurMaskFilter);
	    canvas.drawBitmap(src, 0, 0, paintBlur);
	     
	    return dest;
	}
	
	public static Bitmap gaussianBlur(Bitmap src, List<Point> path, int radius) {
		// Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap result = src.copy(src.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = result.getWidth();
        int h = result.getHeight();

        int[] pix = new int[w * h];
        //Log.e("pix", w + " " + h + " " + pix.length);
        result.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        //Log.e("pix", w + " " + h + " " + pix.length);
        result.setPixels(pix, 0, w, 0, 0, w, h);
        pix = null;

        return result;
	}
	
	@SuppressLint("UseValueOf")
	public static Bitmap pathGaussianBlur(Bitmap src, List<Point> path, int radius) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap result = src.copy(src.getConfig(), true);
		//Set<Integer> hash = new HashSet<Integer>();
		//int r = 5;
		
		Log.d(null, "GO!");
		Log.d(null, "SIZE = " + path.size());
		//Point prev = null;
		int lx, ly, rx, ry;
		lx = width - 1;
		ly = height - 1;
		rx = ry = 0;
		for(int i = 0;i < path.size(); ++i) {
			Point p = path.get(i);
			//double r = Math.sqrt(radius * radius);
			int cx = Math.max(p.x - radius, 0);
			int cy = Math.max(p.y - radius, 0);
			int dx = Math.min(p.x + radius, width-1);
			int dy = Math.min(p.y + radius, height-1);
			lx = Math.min(cx, lx);
			ly = Math.min(cy, ly);
			rx = Math.max(dx, rx);
			ry = Math.max(dy, ry);
			/*
			for(int j = cx; j < dx; ++j)
				for(int k = cy;k < dy; ++k) {
					double dist = Math.sqrt((j - p.x) * (j - p.x) + (k - p.y) * (k - p.y));
					double ratio = dist / radius;
					if(ratio > 1)	continue;
					//if(hash.contains(new Integer(k * width + j)))	continue;
					//hash.add(new Integer(k * width + j));
					int pix = src.getPixel(j, k);
					int A = (int) (Color.alpha(pix) * (ratio * ratio));
					int R = Color.red(pix);
					int G = Color.green(pix);
					int B = Color.blue(pix);
					result.setPixel(j, k, Color.argb(A, R, G, B));
				}
			*/
		}
		int mx = (lx + rx) / 2;
		int my = (ly + ry) / 2;
		double rds = Math.sqrt((rx - lx) * (rx - lx) / 4 + (ry - ly) * (ry - ly) / 4);
		for(int i = lx;i < rx; ++i)
			for(int j = ly;j < ry;++j) {
				double dist = Math.sqrt((i - mx) * (i - mx) + (j - my) * (j - my));
				double ratio = dist / rds;
				if(ratio > 0.7)	continue;
				int pix = src.getPixel(i, j);
				int A = (int) (Color.alpha(pix) * (ratio * ratio));
				int R = Color.red(pix);
				int G = Color.green(pix);
				int B = Color.blue(pix);
				result.setPixel(i, j, Color.argb(A, R, G, B));
			}
		Log.d(null, "OAO!");
		return result;
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
		
		for(int y = 0;y < height; ++y)
			for(int x = 0;x < width; ++x) {
				int pix = src.getPixel(x, y);
				int A = Color.alpha(pix);
				int R = Color.red(pix);
				int G = Color.green(pix);
				int B = (int) (Color.blue(pix) * smoothFunc(Color.blue(pix) + 1, 256));
				result.setPixel(x, y, Color.argb(A, R, G, B));
				
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
		
		for(int y = 0;y < height; ++y)
			for(int x = 0;x < width; ++x) {
				double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
				double ratio = dist / r;
				int pix = src.getPixel(x, y);
				double dr = 1 - ratio * ratio;
				int A = Color.alpha(pix);
				int R = (int) (Color.red(pix) * dr);
				int G = (int) (Color.green(pix) * dr);
				int B = (int) (Color.blue(pix) * smoothFunc(Color.blue(pix) + 1, 256) * dr);
				result.setPixel(x, y, Color.argb(A,  R, G, B));
			}
		
		return result;
	}
}
