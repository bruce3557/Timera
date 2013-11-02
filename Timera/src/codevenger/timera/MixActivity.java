package codevenger.timera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import codevenger.timera.utility.PathTools;
import codevenger.timera.view.MixView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MixActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {

	public static final String DATA_PATH_A = "pathA";
	public static final String DATA_PATH_B = "pathB";

	private MixView mixView;
	private RelativeLayout mixLayout;
	private RelativeLayout controlLayout;
	private RelativeLayout resultLayout;
	private LinearLayout filterLayout, alphaLayout, strokeLayout;
	private Button imgA, imgB, imgC, confirm;
	private Button modeFilter, modeAlpha, modeErase, undo;
	private Button filterNone, filterGray, filterYellow, filterLomo;
	private Button stroke1, stroke2, stroke3, stroke4, stroke5;
	private Button save, flickr;
	private ImageView bottomShadow;
	private SeekBar seekBarAlpha;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mix);

		mixView = new MixView(this, getIntent().getStringExtra(DATA_PATH_A),
				getIntent().getStringExtra(DATA_PATH_B));
		mixView.setPadding(0, 100, 0, 100);

		mixLayout = (RelativeLayout) findViewById(R.id.mix_layout);
		controlLayout = (RelativeLayout) findViewById(R.id.mix_control);
		resultLayout = (RelativeLayout) findViewById(R.id.mix_result);
		resultLayout.setVisibility(View.INVISIBLE);
		filterLayout = (LinearLayout) findViewById(R.id.mix_filter_layout);
		filterLayout.bringToFront();
		alphaLayout = (LinearLayout) findViewById(R.id.mix_alpha_layout);
		alphaLayout.bringToFront();
		alphaLayout.setVisibility(View.INVISIBLE);
		strokeLayout = (LinearLayout) findViewById(R.id.mix_stroke_layout);
		strokeLayout.bringToFront();
		strokeLayout.setVisibility(View.INVISIBLE);
		imgA = (Button) controlLayout.findViewById(R.id.mix_img_a);
		imgB = (Button) controlLayout.findViewById(R.id.mix_img_b);
		imgC = (Button) controlLayout.findViewById(R.id.mix_img_c);
		confirm = (Button) controlLayout.findViewById(R.id.mix_confirm);
		confirm.setVisibility(View.INVISIBLE);
		modeFilter = (Button) controlLayout.findViewById(R.id.mix_filter);
		modeAlpha = (Button) controlLayout.findViewById(R.id.mix_alpha);
		modeErase = (Button) controlLayout.findViewById(R.id.mix_erase);
		undo = (Button) controlLayout.findViewById(R.id.mix_undo);
		filterNone = (Button) controlLayout.findViewById(R.id.mix_filter_none);
		filterGray = (Button) controlLayout.findViewById(R.id.mix_filter_gray);
		filterYellow = (Button) controlLayout
				.findViewById(R.id.mix_filter_yellow);
		filterLomo = (Button) controlLayout.findViewById(R.id.mix_filter_lomo);
		stroke1 = (Button) controlLayout.findViewById(R.id.mix_stroke_1);
		stroke2 = (Button) controlLayout.findViewById(R.id.mix_stroke_2);
		stroke3 = (Button) controlLayout.findViewById(R.id.mix_stroke_3);
		stroke4 = (Button) controlLayout.findViewById(R.id.mix_stroke_4);
		stroke5 = (Button) controlLayout.findViewById(R.id.mix_stroke_5);
		save = (Button) resultLayout.findViewById(R.id.result_save);
		flickr = (Button) resultLayout.findViewById(R.id.result_flickr);
		bottomShadow = (ImageView) controlLayout
				.findViewById(R.id.mix_edit_shadow);
		seekBarAlpha = (SeekBar) controlLayout
				.findViewById(R.id.mix_seekbar_alpha);
		mixLayout.addView(mixView);
		controlLayout.bringToFront();

		imgA.setOnClickListener(this);
		imgB.setOnClickListener(this);
		imgC.setOnClickListener(this);
		confirm.setOnClickListener(this);
		modeFilter.setOnClickListener(this);
		modeAlpha.setOnClickListener(this);
		modeErase.setOnClickListener(this);
		undo.setOnClickListener(this);
		filterNone.setOnClickListener(this);
		filterGray.setOnClickListener(this);
		filterYellow.setOnClickListener(this);
		filterLomo.setOnClickListener(this);
		stroke1.setOnClickListener(this);
		stroke2.setOnClickListener(this);
		stroke3.setOnClickListener(this);
		stroke4.setOnClickListener(this);
		stroke5.setOnClickListener(this);
		save.setOnClickListener(this);
		flickr.setOnClickListener(this);
		seekBarAlpha.setProgress(200);
		seekBarAlpha.setOnSeekBarChangeListener(this);

		setMode(MixView.MODE_FILTER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mix_img_a:
			selectImage(0);
			setMode(MixView.MODE_FILTER);
			break;
		case R.id.mix_img_b:
			selectImage(1);
			break;
		case R.id.mix_img_c:
			selectImage(2);
			setMode(MixView.MODE_SCALE);
			break;
		case R.id.mix_confirm:
			resultLayout.setVisibility(View.VISIBLE);
			resultLayout.bringToFront();
			break;
		case R.id.result_save:
			Bitmap output = Bitmap.createBitmap(mixView.getWidth(),
					mixView.getHeight(), Config.ARGB_8888);
			Canvas outputCanvas = new Canvas(output);
			mixView.render(outputCanvas);
			File file = PathTools.getNewMixedFile();
			try {
				FileOutputStream outputStream = new FileOutputStream(file);
				output.compress(CompressFormat.JPEG, 100, outputStream);
				outputStream.close();
				Toast.makeText(MixActivity.this, "Saved.", Toast.LENGTH_SHORT)
						.show();
				finish();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.result_flickr:
			finish();
			break;
		case R.id.mix_filter:
			setMode(MixView.MODE_FILTER);
			break;
		case R.id.mix_alpha:
			setMode(MixView.MODE_ALPHA);
			break;
		case R.id.mix_erase:
			setMode(MixView.MODE_ERASE);
			break;
		case R.id.mix_undo:
			mixView.undo();
			break;
		case R.id.mix_filter_none:
			break;
		case R.id.mix_filter_gray:
			mixView.toGray();
			break;
		case R.id.mix_filter_yellow:
			mixView.toYellow();
			break;
		case R.id.mix_filter_lomo:
			mixView.toLomo();
			break;
		case R.id.mix_stroke_1:
			setStroke(0);
			break;
		case R.id.mix_stroke_2:
			setStroke(1);
			break;
		case R.id.mix_stroke_3:
			setStroke(2);
			break;
		case R.id.mix_stroke_4:
			setStroke(3);
			break;
		case R.id.mix_stroke_5:
			setStroke(4);
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.mix_seekbar_alpha:
			mixView.setForegroundAlpha(progress);
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	private void selectImage(int img) {
		switch (img) {
		case 0:
			mixView.changeSelected(MixView.SELECT_A);
			modeFilter.setVisibility(View.VISIBLE);
			modeAlpha.setVisibility(View.INVISIBLE);
			modeErase.setVisibility(View.INVISIBLE);
			undo.setVisibility(View.VISIBLE);
			imgA.setBackgroundResource(R.drawable.mix_select_press_a);
			imgB.setBackgroundResource(R.drawable.mix_select_b);
			imgC.setBackgroundResource(R.drawable.mix_select_c);
			confirm.setVisibility(View.INVISIBLE);
			break;
		case 1:
			mixView.changeSelected(MixView.SELECT_B);
			modeFilter.setVisibility(View.VISIBLE);
			modeAlpha.setVisibility(View.VISIBLE);
			modeErase.setVisibility(View.VISIBLE);
			undo.setVisibility(View.VISIBLE);
			imgA.setBackgroundResource(R.drawable.mix_select_a);
			imgB.setBackgroundResource(R.drawable.mix_select_press_b);
			imgC.setBackgroundResource(R.drawable.mix_select_c);
			confirm.setVisibility(View.INVISIBLE);
			break;
		case 2:
			mixView.changeSelected(MixView.SELECT_C);
			modeFilter.setVisibility(View.INVISIBLE);
			modeAlpha.setVisibility(View.INVISIBLE);
			modeErase.setVisibility(View.INVISIBLE);
			undo.setVisibility(View.INVISIBLE);
			imgA.setBackgroundResource(R.drawable.mix_select_a);
			imgB.setBackgroundResource(R.drawable.mix_select_b);
			imgC.setBackgroundResource(R.drawable.mix_select_press_c);
			confirm.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void setMode(int mode) {
		switch (mode) {
		case MixView.MODE_FILTER:
			mixView.setMode(MixView.MODE_FILTER);
			filterLayout.setVisibility(View.VISIBLE);
			alphaLayout.setVisibility(View.INVISIBLE);
			strokeLayout.setVisibility(View.INVISIBLE);
			modeFilter.setBackgroundResource(R.drawable.mix_press_fliter);
			modeAlpha.setBackgroundResource(R.drawable.mix_alpha);
			modeErase.setBackgroundResource(R.drawable.mix_erase);
			bottomShadow.setVisibility(View.VISIBLE);
			break;
		case MixView.MODE_ALPHA:
			mixView.setMode(MixView.MODE_ALPHA);
			filterLayout.setVisibility(View.INVISIBLE);
			alphaLayout.setVisibility(View.VISIBLE);
			strokeLayout.setVisibility(View.INVISIBLE);
			modeFilter.setBackgroundResource(R.drawable.mix_fliter);
			modeAlpha.setBackgroundResource(R.drawable.mix_press_alpha);
			modeErase.setBackgroundResource(R.drawable.mix_erase);
			bottomShadow.setVisibility(View.VISIBLE);
			break;
		case MixView.MODE_ERASE:
			mixView.setMode(MixView.MODE_ERASE);
			filterLayout.setVisibility(View.INVISIBLE);
			alphaLayout.setVisibility(View.INVISIBLE);
			strokeLayout.setVisibility(View.VISIBLE);
			modeFilter.setBackgroundResource(R.drawable.mix_fliter);
			modeAlpha.setBackgroundResource(R.drawable.mix_alpha);
			modeErase.setBackgroundResource(R.drawable.mix_press_erase);
			bottomShadow.setVisibility(View.INVISIBLE);
			break;
		case MixView.MODE_SCALE:
			mixView.setMode(MixView.MODE_SCALE);
			filterLayout.setVisibility(View.INVISIBLE);
			alphaLayout.setVisibility(View.INVISIBLE);
			strokeLayout.setVisibility(View.INVISIBLE);
			bottomShadow.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}

	private void setStroke(int level) {
		mixView.setStrokeWidth(20 + level * 70);
	}

}
