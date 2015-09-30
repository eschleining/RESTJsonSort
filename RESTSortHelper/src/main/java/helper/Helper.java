package helper;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

/**
 * Class that wraps methods useful for both Server and Client
 * 
 * @author Eduard Schleining
 *
 */
public class Helper {

	// strings used by server and client
	public static final String REQUEST_ARRAY_PARAMETER_NAME = "array";
	public static final String LOCALE_PARAMETER_NAME = "locale";
	public static final String URI_PARAMETER_NAME = "uri";

	public static final String DEFAULT_REQUEST_ARRAY = "[]";
	public static final String DEFAULT_LOCALE = "en_US";
	public static final String SORT_PATH = "/sort";
	public static final String VERIFY_PATH="/verify";
	
	public static final String DEFAULT_URI = "http://localhost:8080/RESTSortServer";

	/**
	 * Helper method that converts a JSONArray Object to a List of Strings.
	 * 
	 * @param jsonArray
	 *            the JSONArray Object to be read.
	 * 
	 * @returns a List<String> that contains the same elements as the jsonArray
	 *          Object.
	 * 
	 * @throws JSONException
	 *             if the getString(int) method cannot be invoked on the
	 *             jsonArray Object.
	 * 
	 */
	public static List<String> getStringList(JSONArray jsonArray) throws JSONException {
		ArrayList<String> list = new ArrayList<>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}
		return list;
	}

}
