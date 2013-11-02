package codevenger.timera;

import codevenger.timera.utility.UriTools;
import codevenger.timera.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PickActivity extends Activity implements OnClickListener {

	public static final String DATA_PATH = "path";
	public static final int REQUEST_GALLERY = 0;
	public static final int REQUEST_CAMERA = 1;
	public static final int REQUEST_FLICKR = 2;
	public static final int REQUEST_NEARBY = 3;

	private Button gallery, camera, flickr, nearby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick);

		gallery = (Button) findViewById(R.id.pick_gallery);
		camera = (Button) findViewById(R.id.pick_camera);
		flickr = (Button) findViewById(R.id.pick_flickr);
		nearby = (Button) findViewById(R.id.pick_nearby);

		gallery.setOnClickListener(this);
		camera.setOnClickListener(this);
		flickr.setOnClickListener(this);
		nearby.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.pick_gallery:
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(intent, REQUEST_GALLERY);
			break;
		case R.id.pick_camera:
			intent.setClass(PickActivity.this, CameraActivity.class);
			intent.putExtra(CameraActivity.DATA_OVERLAY, getIntent()
					.getStringExtra(CameraActivity.DATA_OVERLAY));
			startActivityForResult(intent, REQUEST_CAMERA);
			break;
		case R.id.pick_flickr:
			intent.setClass(PickActivity.this, FlickrActivity.class);
			startActivityForResult(intent, REQUEST_FLICKR);
			break;
		case R.id.pick_nearby:
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Intent intent = new Intent();
			switch (requestCode) {
			case REQUEST_GALLERY:
				intent.putExtra(
						DATA_PATH,
						UriTools.getPathFromURI(PickActivity.this,
								data.getData()));
				break;
			case REQUEST_CAMERA:
				intent.putExtra(DATA_PATH,
						data.getStringExtra(CameraActivity.DATA_PATH));
				break;
			case REQUEST_FLICKR:
				intent.putExtra(DATA_PATH,
						data.getStringExtra(FlickrActivity.DATA_PATH));
				break;
			}
			setResult(RESULT_OK, intent);
			finish();
		}
	}

}
