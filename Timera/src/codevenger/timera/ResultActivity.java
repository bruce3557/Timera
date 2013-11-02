package codevenger.timera;

import codevenger.timera.imageprocessing.ImageProcess;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ResultActivity extends Activity {

	public static final String DATA_PATH = "path";

	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		image = (ImageView) findViewById(R.id.result_image);
		image.setVisibility(View.INVISIBLE);

		new AsyncTask<Void, Void, Void>() {

			ProgressDialog dialog;
			Bitmap bitmap;

			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(ResultActivity.this);
				dialog.setTitle("Processing");
				dialog.setCancelable(false);
				dialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra(
						DATA_PATH));
				bitmap = ImageProcess.gaussianBlur(bitmap, null, 50);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				image.setImageBitmap(bitmap);
				image.setVisibility(View.VISIBLE);
				dialog.dismiss();
			}

		}.execute();

	}
}
