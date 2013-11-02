package codevenger.timera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Verifier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import codevenger.timera.authorization.OAuthFlickrService;
import codevenger.timera.authorization.PhotoInform;

public class FlickrActivity extends Activity {

	public static final String DATA_PATH = "path";
	String url;
	String verifier;
	private static final int EDIT = 1;
	private PhotoInform newPhoto;
	public int totalNum;
	public ArrayList<String> urlList = new ArrayList<String>();
	public ArrayList<ImageView> imageList = new ArrayList<ImageView>();
	public ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
	public ArrayList<PhotoInform> photoList = new ArrayList<PhotoInform>();
	Bitmap fileToBeSaved = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flickr);
		OAuthFlickrService flickrService = new OAuthFlickrService();
		try {
			flickrService.execute("GetAuthorizationUrl").get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url = OAuthFlickrService.authorizationUrl;
		Log.d("timera", url);
		Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putString("url", url);
		intent.putExtras(b);
		intent.setClass(FlickrActivity.this, FlickrWebActivity.class);
		startActivityForResult(intent, EDIT);
	}

	public void upadteUI() {
		GridView gridview = (GridView) findViewById(R.id.gridView);
		ImageAdapter ia = new ImageAdapter(this);
		gridview.setAdapter(ia);
		DownloadImageTask downloadTask = new DownloadImageTask();
		try {
			downloadTask.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("timera", "Mom i am here");
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				File dir = new File(Environment.getExternalStorageDirectory()
						.toString() + "/Timera");
				if (!dir.exists() || !dir.isDirectory()) {
					dir.mkdir();
				}
				DownloadSingleImageTask newTask = new DownloadSingleImageTask();
				DownloadImageTask downloadTask = new DownloadImageTask();
				String originUrl = urlList.get(position).substring(0, 57)
						.concat("_b").concat(".jpg");
				try {
					// downloadTask.execute().get();
					newTask.execute(originUrl).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				FileOutputStream fos = null;
				String fileName = originUrl.substring(35, 46).concat(".jpg");
				try {
					Log.d("Timera",
							dir.getAbsolutePath().concat("/").concat(fileName));
					fos = new FileOutputStream(dir.getAbsolutePath()
							.concat("/").concat(fileName));

					if (fileToBeSaved == null) {
						Log.d("Timera", "H");
					}
					if (fileToBeSaved.compress(Bitmap.CompressFormat.JPEG, 100,
							fos)) {
						Log.d("Timera", "GAME");
					}
					Log.d("Timera", fos.toString());
					fos.flush();
					fos.close();

					Intent intent = new Intent();
					intent.putExtra(DATA_PATH, dir.getAbsolutePath()
							.concat("/").concat(fileName));
					setResult(RESULT_OK, intent);
					finish();
					// MediaStore.Images.Media.insertImage(getContentResolver(),
					// b, "Screen", "screen");
				} catch (FileNotFoundException e) {
					Log.d("Timera", "I");
					e.printStackTrace();
				} catch (Exception e) {
					Log.d("Timera", "J");
					e.printStackTrace();
				}
				Toast.makeText(FlickrActivity.this,
						urlList.get(position).substring(0, 56).concat(".jpg"),
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return urlList.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			String url = null;
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageBitmap(bitmapList.get(position));
			return imageView;
		}

		// references to our images
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		private ProgressDialog pd;
		private Context context = FlickrActivity.this;

		/*
		 * public DownloadImageTask(ImageView bmImage) { this.bmImage = bmImage;
		 * }
		 */
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(context, "downloading", "please wait");
		}

		protected Bitmap doInBackground(String... urls) {
			Bitmap mIcon11 = null;
			for (String s : urlList) {
				try {
					InputStream in = new java.net.URL(s).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
					bitmapList.add(mIcon11);
				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			pd.dismiss();
		}
	}

	private class DownloadSingleImageTask extends
			AsyncTask<String, Void, Bitmap> {
		private ProgressDialog pd;
		private Context context = FlickrActivity.this;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(context, "downloading", "please wait");
		}

		protected Bitmap doInBackground(String... urls) {
			Bitmap mIcon11 = null;
			try {
				Log.d("Error", urls[0]);
				InputStream in = new java.net.URL(urls[0]).openStream();
				fileToBeSaved = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {

			pd.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EDIT:
			OAuthFlickrService.verifier = new Verifier(data.getExtras()
					.getString("verifier"));
			OAuthFlickrService getAccessToken = new OAuthFlickrService();
			try {
				getAccessToken.execute("GetAccessToken").get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String photos = OAuthFlickrService.photos;
			photos = photos.substring(14, photos.length() - 1);
			try {
				JSONObject allObject = new JSONObject(photos);
				JSONObject photosObject = allObject.getJSONObject("photos");
				JSONArray photoArray = photosObject.getJSONArray("photo");
				String id, owner, title, server, secret;
				int farm;
				JSONObject oneObject;
				for (int i = 0; i < photoArray.length(); i++) {
					oneObject = photoArray.getJSONObject(i);
					id = oneObject.getString("id");
					owner = oneObject.getString("owner");
					title = oneObject.getString("title");
					server = oneObject.getString("server");
					farm = oneObject.getInt("farm");
					secret = oneObject.getString("secret");
					newPhoto = new PhotoInform(id, owner, title, server,
							secret, farm);
					photoList.add(newPhoto);
					urlList.add(newPhoto.getPhotoUrl("m"));
					Log.d("timera111111", newPhoto.getPhotoUrl());
				}

			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			upadteUI();
		}
	}
}
