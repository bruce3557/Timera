package codevenger.timera.view;

import java.util.ArrayList;
import java.util.List;

import codevenger.timera.imageprocessing.ImageProcess;
import codevenger.timera.utility.BitmapTools;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class MixView extends SurfaceView implements SurfaceHolder.Callback,
		OnTouchListener {

	public static final int SELECT_A = 0;
	public static final int SELECT_B = 1;
	public static final int SELECT_C = 2;
	public static final int MODE_SCALE = 0;
	public static final int MODE_FILTER = 1;
	public static final int MODE_ALPHA = 2;
	public static final int MODE_ERASE = 3;
	public static final int FILTER_NONE = 0;
	public static final int FILTER_GRAY = 1;
	public static final int FILTER_YELLOW = 2;
	public static final int FILTER_LOMO = 3;
	private static final int PADDING_TOP = 46;
	private static final int PADDING_BOTTOM = 60;

	private Context context;
	private int selected, mode, filter;
	private String pathA, pathB;
	private Bitmap background, foreground, cropped;
	private int screenWidth, screenHeight;
	private boolean running;
	private Thread renderThread;
	private SurfaceHolder holder;
	private int fgAlpha;
	private float preX, preY;
	private List<Path> paths;
	private List<Integer> strokeWidths;
	private int strokeWidth;
	private List<Point> points;

	public MixView(Context context, String imgA, String imgB) {
		super(context);
		this.context = context;
		this.pathA = imgA;
		this.pathB = imgB;
		mode = MODE_SCALE;
		fgAlpha = 200;
		strokeWidth = 100;
		paths = new ArrayList<Path>();
		strokeWidths = new ArrayList<Integer>();
		points = new ArrayList<Point>();
		holder = getHolder();
		holder.addCallback(this);
	}

	public void render(Canvas canvas) {
		Paint fgPaint = new Paint();
		fgPaint.setAlpha(fgAlpha);
		canvas.drawBitmap(background, new Rect(0, 0, background.getWidth(),
				background.getHeight()), new Rect(0, PADDING_TOP, screenWidth,
				screenHeight), null);
		canvas.drawBitmap(cropped,
				new Rect(0, 0, cropped.getWidth(), cropped.getHeight()),
				new Rect(0, PADDING_TOP, screenWidth, screenHeight), fgPaint);
	}

	public void render(Canvas canvas, int alpha) {
		Paint fgPaint = new Paint();
		fgPaint.setAlpha(alpha);
		canvas.drawBitmap(background, new Rect(0, 0, background.getWidth(),
				background.getHeight()), new Rect(0, PADDING_TOP, screenWidth,
				screenHeight), null);
		canvas.drawBitmap(cropped,
				new Rect(0, 0, cropped.getWidth(), cropped.getHeight()),
				new Rect(0, PADDING_TOP, screenWidth, screenHeight), fgPaint);
	}

	private Bitmap cropBitmap() {
		Bitmap croppedBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		Canvas bitmapCanvas = new Canvas(croppedBitmap);
		bitmapCanvas.drawBitmap(foreground, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		synchronized (paths) {
			for (int i = 0; i < paths.size(); ++i) {
				paint.setStrokeWidth(strokeWidths.get(i));
				bitmapCanvas.drawPath(paths.get(i), paint);
			}
		}

		return croppedBitmap;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (mode) {
		case MODE_ERASE:
			onTouchErase(event);
			break;
		default:
			break;
		}
		return true;
	}

	public void onTouchErase(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			paths.add(new Path());
			paths.get(paths.size() - 1).moveTo(x, y);
			strokeWidths.add(strokeWidth);
			points = new ArrayList<Point>();
			points.add(new Point((int) x, (int) y));
			break;
		case MotionEvent.ACTION_MOVE:
			paths.get(paths.size() - 1).quadTo(preX, preY, x, y);
			points.add(new Point((int) x, (int) y));
			cropped = cropBitmap();
			break;
		case MotionEvent.ACTION_UP:
			paths.get(paths.size() - 1).quadTo(preX, preY, x, y);
			points.add(new Point((int) x, (int) y));
			// blur here
			Bitmap temp = Bitmap.createBitmap(screenWidth, screenHeight,
					Config.ARGB_8888);
			Canvas tc = new Canvas(temp);
			render(tc, 255);
			cropped = ImageProcess.pathGaussianBlur(temp, points, 100);
			points = null;
			break;
		default:
			break;
		}
		preX = x;
		preY = y;
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		screenWidth = getWidth();
		screenHeight = getHeight() - PADDING_TOP - PADDING_BOTTOM;
		background = Bitmap.createScaledBitmap(
				BitmapTools.decodeSampledBitmapFromFile(pathA, 720, 1280),
				screenWidth, screenHeight, false);
		foreground = Bitmap.createScaledBitmap(
				BitmapTools.decodeSampledBitmapFromFile(pathB, 720, 1280),
				screenWidth, screenHeight, false);
		cropped = cropBitmap();

		running = true;
		renderThread = new Thread(new RenderThread());
		renderThread.start();

		setOnTouchListener(this);
	}

	public void undo() {
		if (paths.size() > 0) {
			paths.remove(paths.size() - 1);
			strokeWidths.remove(strokeWidths.size() - 1);
		}
		cropped = cropBitmap();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// nothing to do here
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		renderThread.interrupt();
	}

	public void setForegroundAlpha(int alpha) {
		fgAlpha = alpha;
	}

	public void setStrokeWidth(int width) {
		strokeWidth = width;
	}

	public void changeSelected(int selected) {
		this.selected = selected;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	private class RenderThread implements Runnable {

		@Override
		public void run() {
			Canvas canvas = null;
			while (running) {
				try {
					canvas = holder.lockCanvas();
					render(canvas);
					holder.unlockCanvasAndPost(canvas);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}

	public void toGray() {
		background = ImageProcess.toGrayscale(background);
	}

	public void toYellow() {
		background = ImageProcess.yellowEffect(background);
	}

	public void toLomo() {
		background = ImageProcess.lomoEffect(background);
	}

}
