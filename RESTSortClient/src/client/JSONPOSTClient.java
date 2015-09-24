package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import helper.Helper;

/**
 * A very basic REST client that sends the list to the RESTSortServer specified
 * by the file "config.json", checks that the server has sorted the list and
 * outputs the result to standard out.
 * 
 * @author Eduard Schleining
 *
 */
public class JSONPOSTClient {
	URL url;
	JSONArray wordArray;

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public JSONArray getWordArray() {
		return wordArray;
	}

	public void setWordArray(JSONArray wordArray) {
		this.wordArray = wordArray;
	}

	public JSONPOSTClient(URL url, JSONArray wordArray) {
		super();
		this.url = url;
		this.wordArray = wordArray;
	}

	/**
	 * Creates a client object with the word array and Server url specified in
	 * the file.
	 * 
	 * @param filename
	 *            the filename of the config file. The file must be in json
	 *            format and contain a String attribute "Server" which specifies
	 *            the URL and a and Array attribute "List" that specifies the
	 *            wordlist, this client will send to the Server in order to get
	 *            it sorted.
	 * 
	 * @return A JSONPOSTClient Object where the doPost() method can be invoked
	 *         to receive the sorted list, null if an exception occured.
	 */
	private static JSONPOSTClient createClientFromConfigFile(String filename) {
		// The url of the RESTSortServer specified in the config file
		URL url = null;

		// The wordlist specified in the config file
		JSONArray wordArray = null;

		// Parse the config file
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

			// create the resulting JSONPostClient object and return it.
			JSONPOSTClient client = new JSONPOSTClient(url, wordArray);
			return client;
		} catch (FileNotFoundException e) {
			System.err.println("The file \"" + filename
					+ "\" does not exist in working directory. Please make sure to create a propper config file and re run.");
			return null;
		} catch (IOException e) {
			System.err.println("Something went wrong while reading the file \"" + filename
					+ "\". Please make sure it exists and can be read by the java virtual machine and re run.");
			return null;
		} catch (JSONException e) {
			System.err.println("The config file \"" + filename
					+ "\" could not be parsed properly. Please make sure the file contains a json Object with the attributes \"Server\" pointing to the URL of the RESTSortServer and \"List\" containin a jsonArray of Strings you wish to get sorted.");
			return null;
		}
	}

	/**
	 * Posts the wordArray object to the server sepcified by the url, receives
	 * the response list, checks that it it in order (sorted) and prints some
	 * outputs.
	 * 
	 * Should url or wordArray be null, this method will print an error message
	 * and return.
	 */
	public void doPost() {
		if (url == null) {
			System.err.println("No url specified. Exiting.");
			return;
		}

		if (wordArray == null) {
			System.err.println("No word array speified. Exiting.");
			return;
		}

		// else send the wordlist to the specified URL and check that the
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

			// Check if the received list is a permutation of the sent list
			List<String> responseList = Helper.getStringList(responseArray);
			List<String> requestList = Helper.getStringList(wordArray);
			boolean permutation = (responseList.containsAll(requestList) && requestList.containsAll(responseList));

			// output the result
			System.out.println("The request list: " + wordArray.toString() + ".");
			System.out.println("The response list: " + responseArray.toString() + ".");
			if (ordered && permutation)
				System.out.println("The list was sorted properly.");
			else
				System.err.println("The list has not been sorted properly or is not a permutation of the sent list.");

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not open connection to \"" + url.toString()
					+ "\". Please make sure the url is correct and the server is running.");
		} catch (JSONException e) {
			System.err.println("The response was not parsable into a JSONArray.");
		}

	}

	/**
	 * creates a JSONPOSTClient object as specified in the "config.json" file
	 * and invokes it's doPost() method.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		JSONPOSTClient client = createClientFromConfigFile("config.json");
		client.doPost();
	}
}
