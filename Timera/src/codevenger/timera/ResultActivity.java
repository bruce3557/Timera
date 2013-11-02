package codevenger.timera;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class ResultActivity extends Activity {

	private Button save, flickr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		save = (Button) findViewById(R.id.result_save);
		flickr = (Button) findViewById(R.id.result_flickr);
	}

}
