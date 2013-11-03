package codevenger.timera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import codevenger.timera.utility.BitmapTools;

public class ResultActivity extends Activity implements OnClickListener {

	public static final String DATA_PATH = "path";

	private int screenWidth, screenHeight;
	private ImageView image;
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		path = getIntent().getStringExtra(DATA_PATH);
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		image = (ImageView) findViewById(R.id.result_image);
		image.setImageBitmap(BitmapTools.decodeSampledBitmapFromFile(path,
				screenWidth, screenHeight));
		image.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
		try {
			startActivity(Intent.createChooser(share, "Share photo"));
		} catch (Exception e) {

		}
	}

}
