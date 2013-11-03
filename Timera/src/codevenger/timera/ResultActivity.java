package codevenger.timera;

import codevenger.timera.utility.BitmapTools;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends Activity {

	public static final String DATA_PATH = "path";

	private int screenWidth, screenHeight;
	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		String path = getIntent().getStringExtra(DATA_PATH);
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		image = (ImageView) findViewById(R.id.result_image);
		image.setImageBitmap(BitmapTools.decodeSampledBitmapFromFile(path,
				screenWidth, screenHeight));
	}

}
