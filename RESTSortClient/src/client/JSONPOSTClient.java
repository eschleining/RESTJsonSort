package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import helper.Helper;

/**
 * A very basic REST client that sends the list to the RESTSortServer specified
 * by the file "config.json", checks that the response list is sorted and a
 * permutation of the request list and outputs the result to standard out.
 * 
 * @author Eduard Schleining
 *
 */
public class JSONPOSTClient {
	URL url = null;
	JSONArray requestArray = null, responseArray = null;

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public JSONArray getRequestArray() {
		return requestArray;
	}

	public void setRequestArray(JSONArray requestArray) {
		this.requestArray = requestArray;

		// reset the response array
		this.responseArray = null;
	}

	/**
	 * Posts the wordArray object to the server specified by the url, receives
	 * the response list and sets the responseArray field before returning it.
	 * 
	 * @return if the field responseArray is not null, returns the field
	 *         responseArray, else does a post request with the requestList,
	 *         sets the responseArray field to the received result and returns
	 *         the value of the responseArray afterwards
	 * 
	 * @throws IOException
	 *             if a connection to the url is not possible, has no output or
	 *             no input Stream.
	 * 
	 * @throws JSONException
	 *             if the input stream cannot be parsed into a JSONArray object
	 */
	public JSONArray getResponseArray() throws IOException, JSONException {

		// if responseArray is already set, return it
		if (responseArray != null)
			return responseArray;

		// else send the wordlist to the specified URL and receive the
		// responseArray

		// connect to the rest-json server
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		connection.connect();

		// send the List JSONArray
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		// System.out.println(wordArray.toString());
		out.write(requestArray.toString());
		out.close();

		// receive the response List
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuilder stringBuffer = new StringBuilder();
		while ((line = in.readLine()) != null) {
			stringBuffer.append(line);
		}
		in.close();

		// check that the response is a JSONArray and set the responseArray
		// field
		responseArray = new JSONArray(stringBuffer.toString());

		return responseArray;
	}

	public JSONPOSTClient(URL url, JSONArray wordArray) {
		this.url = url;
		this.requestArray = wordArray;
	}

	/**
	 * Creates a client object with the requestArray and Server url specified in
	 * the file.
	 * 
	 * @param filename
	 *            the filename of the config file. The file must be in json
	 *            format and contain a string attribute "Server" which specifies
	 *            the URL and an array attribute "List" that specifies the
	 *            wordArray, this client will send to the server in order to get
	 *            it sorted.
	 * 
	 * @return A JSONPOSTClient Object where the doPost() method can be invoked
	 *         to receive the sorted list, null if an exception occurred.
	 * 
	 * @throws FileNotFoundException
	 *             if the file with filename does not exist
	 * 
	 * @throws IOException
	 *             if a FileInputStream cannot be opened on the file with
	 *             filename
	 * 
	 * @throws JSONException
	 *             if the file with filename has no doensn't contain json
	 */
	public static JSONPOSTClient createClientFromConfigFile(String filename)
			throws FileNotFoundException, IOException, JSONException {
		// The url of the RESTSortServer specified in the config file
		URL url = null;

		// The wordlist specified in the config file
		JSONArray wordArray = null;

		// Parse the config file
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		// Construct a string with the contents of the config file
		StringBuilder stringConfigBuffer = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringConfigBuffer.append(line);
		}
		reader.close();

		// Parse the string into a JSONObject
		JSONObject jsonConfig = new JSONObject(stringConfigBuffer.toString());

		// Read the attributes "Server" and "List" from the JSONObject
		url = new URL(jsonConfig.getString("Server"));
		wordArray = jsonConfig.getJSONArray("List");

		// create the resulting JSONPostClient object and return it.
		JSONPOSTClient client = new JSONPOSTClient(url, wordArray);
		return client;
	}

	/**
	 * Checks whether the reponseArray is sorted or not. Returns true if it is,
	 * false otherwise.
	 * 
	 * @return true if all elements in the responseArray are in lexicographical
	 *         order or it is empty, false otherwise.
	 * 
	 * @throws JSONException
	 *             if the getString(int) method cannot be invoked on the
	 *             jsonArray parameter.
	 */
	public boolean responseIsSorted() throws JSONException {

		// if the array is empty or has only one element, it is sorted
		if (responseArray.length() <= 1)
			return true;

		// initialize the last pointer to the first position of the array
		String last = responseArray.getString(0);

		// loop through the array and compare all elements pairwise.
		for (int i = 1; i < responseArray.length(); i++) {
			String current = responseArray.getString(i);

			// this elements (current, last) are not in order, return false
			if (last.compareTo(current) > 0)
				return false;

			// update the last pointer to the current index before increasing
			// the index
			last = current;
		}

		// no unordered elements found. Return true
		return true;
	}

	/**
	 * Checks if the jsonArray is a permutation of the wordArray field.
	 * 
	 * @param jsonArray
	 *            the array to check against the wordArray field.
	 * 
	 * @return true if jsonArray contains the same elements as wordList and vice
	 *         versa.
	 * 
	 * @throws JSONException
	 *             if the Helper.getStringList(JSONArray) throws the exception
	 *             with either the request- or responseArray as an argument.
	 */
	public boolean responseIsPermutationOfRequest() throws JSONException {

		// initialize List objects
		List<String> requestList = Helper.getStringList(requestArray);
		List<String> responseList = Helper.getStringList(responseArray);

		// loop through the requestList (requestArray) and remove all found
		// elements from responseList (responseArray)
		for (String word : requestList) {

			// if this word is not found in responseArray, it is no
			// permutation of requestArray, return false
			if (!responseList.remove(word))
				return false;
		}

		// if responseList still contains elements after all requestList
		// elements have been removed, it is no permutation, else it is.
		return responseList.isEmpty();
	}

	/**
	 * Creates a JSONPOSTClient object as specified in a config file or by
	 * command line arguments, calls its getResponseArray() method and its check
	 * methods and outputs the results.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		// create command line options
		Options options = new Options();
		options.addOption("s", "server", true, "A URL pointing to the server to send the wordList to.");
		options.addOption("l", "list", true, "A JSON array of words that will be sent to the RESTSortServer.");
		options.addOption("c", "config", true,
				"A file that contains a JSON object with the attributes \"Server\" which points to a RESTSortServer and \"List\" which is an array of words that will be sent to the server to get sorted.");
		options.addOption("h", "help", false, "Print this help message.");

		// create the Help message
		HelpFormatter helpFormatter = new HelpFormatter();

		// Read the command line options
		CommandLine commandLine = null;
		try {
			commandLine = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.err.println("Arguments could not be parsed.");
			return;
		}

		// just print the help message
		if (commandLine.hasOption('h')) {
			helpFormatter.printHelp("java -jar RESTSortClient.jar", options);
			return;
		}

		// create a client object without command line arguments by parsing the
		// config file
		JSONPOSTClient client = null;
		String configFileName = commandLine.hasOption('c') ? commandLine.getOptionValue('c') : "config.json";
		if (!commandLine.hasOption("s") || !commandLine.hasOption("l"))
			try {
				client = createClientFromConfigFile(configFileName);
			} catch (FileNotFoundException e) {
				System.err.println("The config file \"" + configFileName + "\" cannot be found.");
				return;
			} catch (IOException e) {
				System.err.println("The config file \"" + configFileName + "\" cannot be read.");
				return;
			} catch (JSONException e) {
				System.err.println("The config file \"" + configFileName + "\" doesn't contain valid JSON code.");
				return;
			}

		// create a client object by parsing the command line argumnts
		else {
			try {
				URL url = new URL(commandLine.getOptionValue('s'));
				JSONArray requestArray = new JSONArray(commandLine.getOptionValue('l'));
				client = new JSONPOSTClient(url, requestArray);
			} catch (MalformedURLException e) {
				System.err.println("The server argument parameter must point to a valid URL.");
				return;
			} catch (JSONException e) {
				System.err.println("The list parameter must be a valid JSON array.");
				return;
			}
		}

		// get the request and response arrays
		JSONArray requestArray = client.getRequestArray();
		JSONArray responseArray = null;
		try {
			responseArray = client.getResponseArray();
		} catch (IOException e) {
			System.err.println("The server didn't behave as expected.");
			return;
		} catch (JSONException e) {
			System.err.println("The server didn't send a JSON array.");
			return;
		}

		// output the result
		System.out.println("The request list: " + requestArray.toString() + ".");
		System.out.println("The response list: " + responseArray.toString() + ".");

		// call the check methods to see if the responseArray is sorted and a
		// permutation of the requestArray
		try {

			if (client.responseIsSorted() && client.responseIsPermutationOfRequest())
				System.out.println("The list was sorted properly.");
			else
				System.err.println("The list has not been sorted properly or is not a permutation of the sent list.");

		} catch (JSONException e) {
			System.err.println("One of the arrays is not an array of Strings.");
			return;
		}

	}
}