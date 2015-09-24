package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * A very basic REST client that sends the list to the RESTSortServer specified
 * by the file "config.json", checks that the server has sorted the list and
 * outputs the result to standart out.
 * 
 * @author edi
 *
 */
public class JSONPOSTClient {
	public static void main(String[] args) {

		// The url of the RESTSortServer specified in the config file
		URL url = null;

		// The wordlist specified in the config file
		JSONArray wordArray = null;

		// Parse the config file "config.json" that contains a json object with
		// the attributes "Server" and "List".
		// Server points to the URL of the RESTSortServer (usually
		// http://localhost:8080/RestSortServer/post)
		// List is a jsonArray that contains the worldlist you wish to get into
		// lexicographical order.
		try (BufferedReader reader = new BufferedReader(new FileReader("config.json"))) {

			// Construct a string with the contents of the config file
			StringBuilder stringConfigBuffer = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringConfigBuffer.append(line);
			}

			// Parse the string into a JSONObject
			JSONObject jsonConfig = new JSONObject(stringConfigBuffer.toString());

			// Read the attributes "Server" and "List" from the JSONObject
			url = new URL(jsonConfig.getString("Server"));
			wordArray = jsonConfig.getJSONArray("List");
		} catch (FileNotFoundException e) {
			System.err.println(
					"The file \"config.json\" doesn not exist in working directory. Please make sure to create a propper config file and re run.");
		} catch (IOException e) {
			System.err.println(
					"Something went wrong while reading the file \"config.json\". Please make sure it exists and can be read by the java virtual machine and re run.");
		} catch (JSONException e) {
			System.err.println(
					"The config file \"config.json\" could not be parsed properly. Please make sure the file contains a json Object with the attributes \"Server\" pointing to the URL of the RESTSortServer and \"List\" containin a jsonArray of Strings you wish to get sorted.");
		}

		// if something went wrong, abort
		if (url == null || wordArray == null)
			return;

		// else send the wordlist to the speciefied URL and check that the
		// response list is sorted
		try {

			// connect to the rest-json server
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			connection.connect();

			// send the List JSONArray
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			// System.out.println(wordArray.toString());
			out.write(wordArray.toString());
			out.close();

			// receive the response List
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder stringBuffer = new StringBuilder();
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
			}
			in.close();

			// check that the response is a JSONArray
			JSONArray responseArray = new JSONArray(stringBuffer.toString());

			// check that the array is in ascending lexicographical order
			boolean ordered = true;
			if (responseArray.length() > 0) {
				String last = responseArray.getString(0);
				for (int i = 1; i < responseArray.length(); i++) {
					String current = responseArray.getString(i);
					if (last.compareTo(current) > 0) {
						ordered = false;
						break;
					}
					last = current;
				}
			}

			// output the result
			System.out.println("The request list: " + wordArray.toString() + ".");
			System.out.println("The response list: " + responseArray.toString() + ".");
			if (ordered)
				System.out.println("The list was sorted properly.");
			else
				System.err.println("The list has not been sorted properly.");

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not open connection to \"" + url.toString()
					+ "\". Please make sure the url is correct and the server is running.");
		} catch (JSONException e) {
			System.err.println("The response was not parsable into a JSONArray.");
		}

	}
}
