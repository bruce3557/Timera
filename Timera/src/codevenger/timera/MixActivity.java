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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MixActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {

	public static final String DATA_PATH_A = "pathA";
	public static final String DATA_PATH_B = "pathB";

	private MixView mixView;
	private RelativeLayout mixLayout;
	private RelativeLayout controlLayout;
	private Button imgA, imgB, confirm;
	private Button modeFilter, modeAlpha, modeErase, undo;
	private SeekBar seekBarAlpha, seekBarStroke;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mix);

		mixView = new MixView(this, getIntent().getStringExtra(DATA_PATH_A),
				getIntent().getStringExtra(DATA_PATH_B));

		mixLayout = (RelativeLayout) findViewById(R.id.mix_layout);
		controlLayout = (RelativeLayout) findViewById(R.id.mix_control);
		imgA = (Button) controlLayout.findViewById(R.id.mix_img_a);
		imgB = (Button) controlLayout.findViewById(R.id.mix_img_b);
		confirm = (Button) controlLayout.findViewById(R.id.mix_confirm);
		modeFilter = (Button) controlLayout.findViewById(R.id.mix_filter);
		modeAlpha = (Button) controlLayout.findViewById(R.id.mix_alpha);
		modeErase = (Button) controlLayout.findViewById(R.id.mix_erase);
		undo = (Button) controlLayout.findViewById(R.id.mix_undo);
		seekBarAlpha = (SeekBar) controlLayout
				.findViewById(R.id.mix_seekbar_alpha);
		seekBarAlpha.setVisibility(View.INVISIBLE);
		seekBarStroke = (SeekBar) controlLayout
				.findViewById(R.id.mix_seekbar_stroke);
		seekBarStroke.setVisibility(View.INVISIBLE);
		mixLayout.addView(mixView);
		controlLayout.bringToFront();

		imgA.setOnClickListener(this);
		imgB.setOnClickListener(this);
		confirm.setOnClickListener(this);
		modeFilter.setOnClickListener(this);
		modeAlpha.setOnClickListener(this);
		modeErase.setOnClickListener(this);
		undo.setOnClickListener(this);
		seekBarAlpha.setProgress(200);
		seekBarAlpha.setOnSeekBarChangeListener(this);
		seekBarStroke.setProgress(100);
		seekBarStroke.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mix_img_a:
			mixView.changeSelected(MixView.SELECT_A);
			modeErase.setVisibility(View.INVISIBLE);
			break;
		case R.id.mix_img_b:
			mixView.changeSelected(MixView.SELECT_B);
			modeErase.setVisibility(View.VISIBLE);
			break;
		case R.id.mix_confirm:
			Bitmap output = Bitmap.createBitmap(mixView.getWidth(),
					mixView.getHeight(), Config.ARGB_8888);
			Canvas outputCanvas = new Canvas(output);
			mixView.render(outputCanvas);
			File file = PathTools.getNewMixedFile();
			try {
				FileOutputStream outputStream = new FileOutputStream(file);
				output.compress(CompressFormat.JPEG, 100, outputStream);
				outputStream.close();
				Intent intent = new Intent(MixActivity.this,
						ResultActivity.class);
				intent.putExtra(ResultActivity.DATA_PATH,
						file.getAbsolutePath());
				startActivity(intent);
				finish();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.mix_filter:
			mixView.setMode(MixView.MODE_FILTER);
			seekBarAlpha.setVisibility(View.INVISIBLE);
			seekBarStroke.setVisibility(View.INVISIBLE);
			break;
		case R.id.mix_alpha:
			mixView.setMode(MixView.MODE_ALPHA);
			seekBarAlpha.setVisibility(View.VISIBLE);
			seekBarStroke.setVisibility(View.INVISIBLE);
			break;
		case R.id.mix_erase:
			mixView.setMode(MixView.MODE_ERASE);
			seekBarAlpha.setVisibility(View.INVISIBLE);
			seekBarStroke.setVisibility(View.VISIBLE);
			break;
		case R.id.mix_undo:
			mixView.undo();
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
		case R.id.mix_seekbar_stroke:
			mixView.setStrokeWidth(progress + 20);
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

}
