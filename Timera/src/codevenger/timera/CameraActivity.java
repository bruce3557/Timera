package codevenger.timera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import codevenger.timera.utility.BitmapTools;
import codevenger.timera.utility.PathTools;
import codevenger.timera.view.CameraView;

import codevenger.timera.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CameraActivity extends Activity implements OnClickListener,
		OnTouchListener, OnSeekBarChangeListener {

	public static final String DATA_PATH = "path";
	public static final String DATA_OVERLAY = "overlay";

	private int screenWidth, screenHeight;
	private RelativeLayout cameraLayout;
	private RelativeLayout controlLayout;
	private RelativeLayout overlayLayout;
	private SeekBar alpha, zoom;
	private Button shoot;
	private ImageView overlayImage;
	private Camera camera;
	private CameraView cameraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		camera = Camera.open();
		cameraView = new CameraView(this, camera);
		cameraLayout = (RelativeLayout) findViewById(R.id.camera_layout);
		controlLayout = (RelativeLayout) findViewById(R.id.camera_control);
		overlayLayout = (RelativeLayout) findViewById(R.id.camera_overlay);
		alpha = (SeekBar) findViewById(R.id.camera_alpha);
		zoom = (SeekBar) findViewById(R.id.camera_zoom);
		shoot = (Button) controlLayout.findViewById(R.id.camera_shoot);
		overlayImage = (ImageView) overlayLayout
				.findViewById(R.id.camera_image);

		cameraView.setOnTouchListener(this);
		cameraLayout.addView(cameraView);
		controlLayout.bringToFront();
		overlayLayout.bringToFront();
		alpha.setMax(100);
		alpha.setProgress(40);
		alpha.setOnSeekBarChangeListener(this);
		zoom.setMax(camera.getParameters().getMaxZoom());
		zoom.setProgress(0);
		zoom.setOnSeekBarChangeListener(this);
		shoot.setOnClickListener(this);

		String overlayPath = getIntent().getStringExtra(DATA_OVERLAY);
		if (overlayPath != null) {
			Bitmap overlay = BitmapFactory.decodeFile(overlayPath);
			overlayImage.setImageBitmap(Bitmap.createScaledBitmap(overlay,
					screenWidth, screenHeight, false));
			overlayImage.setAlpha(0.4f);
		} else {
			overlayImage.setVisibility(View.INVISIBLE);
			alpha.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		camera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				camera.stopPreview();
				try {
					File file = PathTools.getNewFile();
					Bitmap bitmap = BitmapTools.rotate(
							BitmapFactory.decodeByteArray(data, 0, data.length),
							90);
					FileOutputStream outputStream = new FileOutputStream(file);
					bitmap.compress(CompressFormat.JPEG, 100, outputStream);
					outputStream.close();

					Intent intent = new Intent();
					intent.putExtra(DATA_PATH, file.getAbsolutePath());
					setResult(RESULT_OK, intent);
					finish();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v != cameraView) {
			return false;
		} else {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int x = (int) (event.getY() / cameraView.getHeight() * 2000 - 1000);
				int y = (int) (1000 - event.getX() / cameraView.getWidth()
						* 2000);
				Camera.Parameters paras = camera.getParameters();
				List<Area> areas = new ArrayList<Area>();
				areas.add(new Area(new Rect(x - 50, y - 50, x + 50, y + 50),
						1000));
				paras.setFocusAreas(areas);
				paras.setMeteringAreas(areas);
				camera.setParameters(paras);
				camera.autoFocus(null);
			}
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		camera.release();
		super.onDestroy();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.camera_alpha:
			overlayImage.setAlpha((float) progress / 100);
			break;
		case R.id.camera_zoom:
			Camera.Parameters paras = camera.getParameters();
			paras.setZoom(progress);
			camera.setParameters(paras);
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

}
