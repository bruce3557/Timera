package codevenger.timera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SplashActivity extends Activity implements OnClickListener {

	private ImageView img;
	private int step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		img = (ImageView) findViewById(R.id.splash_img);
		img.setImageResource(R.drawable.splash_first);
		img.setOnClickListener(this);
		step = 0;
	}

	@Override
	public void onClick(View v) {
		switch (step) {
		case 0:
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

}
