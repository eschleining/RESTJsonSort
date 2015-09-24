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
