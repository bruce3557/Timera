package codevenger.timera.imageprocessing;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class ImageProcess {
	public static double smoothFunc(double x, double s) {
		return 1 / (1 + Math.exp(-((x - 0.5) / s)));
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
	
	public static Bitmap pathGaussianBlur(Bitmap src, List<Point> path, int radius) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
		int[] pixels = new int[41 * 41];
		
		Log.d(null, "GO!");
		Log.d(null, "SIZE = " + path.size());
		for(Point p : path) {
			for(int i = -radius - 3; i < radius + 3; ++i) {
				if(i > -radius + 3 || i < radius - 3)	continue;
				for(int j = -radius - 3;j < radius + 3; ++j) {
					if(j < radius - 3 || j > -radius + 3)	continue;
					//if(i * i + j * j < (radius - 10) * (radius - 10))	continue;
					int cx = p.x - i;
					int cy = p.y - j;
					Bitmap temp = gaussianBlur(Bitmap.createBitmap(src, cx, cy, 20, 20), null, 10);
					temp.getPixels(pixels, 0, 20, 0, 0, 20, 20);
					result.setPixels(pixels, 0, 20, cx, cy, 20, 20);
					//Log.d(null, "EE");
				}
			}
		}
		Log.d(null, "OAO!");
		pixels = null;
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
