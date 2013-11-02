package codevenger.timera.authorization;

import org.json.JSONException;
import org.json.JSONObject;
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
	public static String user_id = null;
	public static final String PROTECTED_RESOURCE_URL = "http://api.flickr.com/services/rest/";
	public static boolean isAthourizeUrl = false;
	public static boolean isAthourizeToken = false;
	String apiKey = "3f67e9341d6b434434b11f126bd13084";
	String apiSecret = "0912521631d6c370";
	String callback = "oauths://Flickr";
	

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		if (params[0].equals("GetAuthorizationUrl")) {
			service = new ServiceBuilder().provider(FlickrApi.class)
					.apiKey(apiKey).apiSecret(apiSecret).callback(callback)
					.build();
			Log.d("timera", "service new complete");
			requestToken = service.getRequestToken();
			OAuthFlickrService.authorizationUrl = service
					.getAuthorizationUrl(requestToken);
			isAthourizeUrl = true;
		} else {
			accessToken = service.getAccessToken(requestToken, verifier);
			Log.d("Timera", "service new complete");
				OAuthRequest request = new OAuthRequest(Verb.GET,
						PROTECTED_RESOURCE_URL);
				request.addQuerystringParameter("method", "flickr.test.login");
				request.addQuerystringParameter("format", "json");
				service.signRequest(accessToken, request);
				Response response = request.send();
				String temp = response.getBody();
				temp = temp.substring(14, temp.length() - 1);
				JSONObject allObject;
				try {
					allObject = new JSONObject(temp);
					JSONObject photosObject = allObject.getJSONObject("user");
					user_id = photosObject.getString("id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isAthourizeToken = true;
		}
		return null;
	}
}
