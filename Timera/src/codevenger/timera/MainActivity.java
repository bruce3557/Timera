package codevenger.timera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import codevenger.timera.utility.BitmapTools;

public class MainActivity extends Activity implements OnClickListener {

	private static final int REQUEST_IMAGE_A = 0;
	private static final int REQUEST_IMAGE_B = 1;

	private ImageView imgA, imgB;
	private Button confirm;
	private String pathA, pathB;
	private boolean pickA, pickB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imgA = (ImageView) findViewById(R.id.main_pick_a);
		imgB = (ImageView) findViewById(R.id.main_pick_b);
		confirm = (Button) findViewById(R.id.main_confirm);

		imgA.setOnClickListener(this);
		imgB.setOnClickListener(this);
		confirm.setOnClickListener(this);
		confirm.setVisibility(View.INVISIBLE);

		pickA = false;
		pickB = false;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.main_pick_a:
			intent.setClass(MainActivity.this, PickActivity.class);
			startActivityForResult(intent, REQUEST_IMAGE_A);
			break;
		case R.id.main_pick_b:
			intent.setClass(MainActivity.this, PickActivity.class);
			intent.putExtra(CameraActivity.DATA_OVERLAY, pathA);
			startActivityForResult(intent, REQUEST_IMAGE_B);
			break;
		case R.id.main_confirm:
			intent.setClass(MainActivity.this, MixActivity.class);
			intent.putExtra(MixActivity.DATA_PATH_A, pathA);
			intent.putExtra(MixActivity.DATA_PATH_B, pathB);
			startActivity(intent);
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_IMAGE_A:
				pathA = data.getStringExtra(PickActivity.DATA_PATH);
				imgA.setImageBitmap(BitmapTools.decodeSampledBitmapFromFile(
						pathA, 200, 200));
				pickA = true;
				break;
			case REQUEST_IMAGE_B:
				pathB = data.getStringExtra(PickActivity.DATA_PATH);
				imgB.setImageBitmap(BitmapTools.decodeSampledBitmapFromFile(
						pathB, 200, 200));
				pickB = true;
				break;
			default:
				break;
			}
		}
		if (pickA && pickB) {
			confirm.setVisibility(View.VISIBLE);
		}
	}
}
