package codevenger.timera.authorization;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.os.AsyncTask;
import android.util.Log;

public class OAuthFlickrService extends AsyncTask<String, Void, Void> {
	public static String authorizationUrl = null;
	public static OAuthService service = null;
	public static Token requestToken = null;
	public static Token accessToken = null;
	public static Verifier verifier = null;
	private static final String PROTECTED_RESOURCE_URL = "http://api.flickr.com/services/rest/";
	String apiKey = "3f67e9341d6b434434b11f126bd13084";
	String apiSecret = "0912521631d6c370";
	String callback = "oauths://Flickr";
	public static String photos;
	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		if (params[0].equals("GetAuthorizationUrl")) {
			service = new ServiceBuilder().provider(FlickrApi.class)
					.apiKey(apiKey).apiSecret(apiSecret).callback(callback)
					.build();
			Log.d("timera", "service new complete");
			requestToken = service.getRequestToken();
			Log.d("timera", "requestToken");
			OAuthFlickrService.authorizationUrl = service
					.getAuthorizationUrl(requestToken);
		} else {
			Token accessToken = service.getAccessToken(requestToken, verifier);
			OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
			request.addQuerystringParameter("method", "flickr.people.getPhotos");
			request.addQuerystringParameter("user_id", "105673877@N08");
			request.addQuerystringParameter("format", "json");
			request.addQuerystringParameter("api_key",
					"3f67e9341d6b434434b11f126bd13084");
			service.signRequest(accessToken, request);
			Response response = request.send();
			photos = response.getBody();
			Log.d("timera", photos);
		}
		return null;
	}
}
