package codevenger.timera.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

	private int selected;
	private String pathA, pathB;
	private Bitmap background, foreground, cropped;
	private int screenWidth, screenHeight;
	private boolean running;
	private Thread renderThread;
	private SurfaceHolder holder;
	private int fgAlpha;
	private float preX, preY;
	private List<Path> paths;
	private List<Point> points;

	public MixView(Context context, String imgA, String imgB) {
		super(context);
		this.pathA = imgA;
		this.pathB = imgB;
		fgAlpha = 200;
		paths = new ArrayList<Path>();
		points = new ArrayList<Point>();
		holder = getHolder();
		holder.addCallback(this);
	}

	public void render(Canvas canvas) {
		Paint fgPaint = new Paint();
		fgPaint.setAlpha(fgAlpha);
		canvas.drawBitmap(background, new Rect(0, 0, background.getWidth(),
				background.getHeight()), new Rect(0, 0, screenWidth,
				screenHeight), null);
		canvas.drawBitmap(cropped,
				new Rect(0, 0, cropped.getWidth(), cropped.getHeight()),
				new Rect(0, 0, screenWidth, screenHeight), fgPaint);
	}

	private Bitmap cropBitmap() {
		Bitmap croppedBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(100);
		Canvas bitmapCanvas = new Canvas(croppedBitmap);
		bitmapCanvas.drawBitmap(foreground, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		synchronized (paths) {
			for (Path path : paths) {
				bitmapCanvas.drawPath(path, paint);
			}
		}

		return croppedBitmap;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			paths.add(new Path());
			paths.get(paths.size() - 1).moveTo(x, y);
			points = new ArrayList<Point>();
			points.add(new Point((int) x, (int) y));
			break;
		case MotionEvent.ACTION_MOVE:
			paths.get(paths.size() - 1).quadTo(preX, preY, x, y);
			points.add(new Point((int) x, (int) y));
			break;
		case MotionEvent.ACTION_UP:
			paths.get(paths.size() - 1).quadTo(preX, preY, x, y);
			points.add(new Point((int) x, (int) y));
			// blur here
			points = null;
			break;
		}
		preX = x;
		preY = y;
		cropped = cropBitmap();
		return true;
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		screenWidth = getWidth();
		screenHeight = getHeight();
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pathA),
				screenWidth, screenHeight, false);
		foreground = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pathB),
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

	public void changeSelected(int selected) {
		this.selected = selected;
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

}
