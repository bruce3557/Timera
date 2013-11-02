package codevenger.timera.authorization;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import android.os.AsyncTask;
import android.util.Log;

public class GetPhotoFlickrService extends AsyncTask<Void, Void, Void>{
	public static String photos;

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		OAuthRequest request = new OAuthRequest(Verb.GET,
				OAuthFlickrService.PROTECTED_RESOURCE_URL);
		Log.d("Timera", "!!!!");
		request.addQuerystringParameter("method", "flickr.people.getPhotos");
		request.addQuerystringParameter("user_id", OAuthFlickrService.user_id);
		Log.d("Timera", "!!!!ll");
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("api_key",
				"3f67e9341d6b434434b11f126bd13084");
		Log.d("Timera", "!!!!llww");
		//Log.d("Timera", OAuthFlickrService.accessToken.getToken());
		Log.d("Timera", "55566");
		OAuthFlickrService.service.signRequest(OAuthFlickrService.accessToken, request);
		Response response = request.send();
		photos = response.getBody();
		Log.d("timera", photos);
		return null;
	}

}
