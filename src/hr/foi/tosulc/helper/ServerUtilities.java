package hr.foi.tosulc.helper;

import static hr.foi.tosulc.helper.CommonUtilities.SERVER_URL_FOR_REGISTRATING;
import static hr.foi.tosulc.helper.CommonUtilities.SERVER_URL_FOR_SUBSCRIBE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;

public class ServerUtilities {

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	public static String register(final Context context, final String regId) {
		String result = "";
		String serverUrl = SERVER_URL_FOR_REGISTRATING;
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		try {
			result = post(serverUrl, params);

			if (!result.equals("registered") && !result.equals("already_here")) {
				result = "server_problem";
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String subsribeToSponsor(final Context context,
			final String regId, final String nameOfSponsor) {
		String result = "";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		params.put("nameOfSponsor", nameOfSponsor);

		try {
			result = post(SERVER_URL_FOR_SUBSCRIBE, params);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static String post(String endpoint, Map<String, String> params)
			throws IOException {
		String res = "";
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		//Log.v(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			//Log.e("URL", "> " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(5000);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			Reader reader = new InputStreamReader(new BufferedInputStream(
					(InputStream) conn.getContent()));
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				res += line;
			}
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} catch (SocketTimeoutException e) {
			return "connection_timedout";
		} catch (ConnectTimeoutException e) {
			return "connection_timedout";
		} catch (IOException e) {
			return "connection_timedout";
		} finally {
			if (conn != null) {
				conn.disconnect();

			}
		}
		return res;
	}
}
