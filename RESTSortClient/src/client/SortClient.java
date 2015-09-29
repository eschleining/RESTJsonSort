package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import helper.Helper;

/**
 * A very basic REST client that sends the List to the RESTSortServer specified
 * by the file "config.json", checks that the response list is sorted and a
 * permutation of the request list and outputs the result to standard out.
 * 
 * @author Eduard Schleining
 *
 */
public class SortClient {

	private static final String SERVER_CONFIG_ATTRIBUTE = "uri";

	private static final String DEFAULT_URI = "http://localhost:8080/RESTSortServer/sort";

	private URI uri = null;
	private JSONArray requestArray = null, responseArray = null;
	private Locale locale = null;

	public URI getUri() {
		return uri;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setUri(URI uri) {
		this.uri = uri;
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
	 * Posts the requestArray object to the server specified by the url,
	 * receives the response an sets the responseArray field before returning
	 * it.
	 * 
	 * @return if the field responseArray is not null, returns the field
	 *         responseArray, else does a get request with the requestArray and locale parameters,
	 *         sets the responseArray field to the received result and returns
	 *         the value of the responseArray afterwards
	 */
	public JSONArray getResponseArray() {

		// if responseArray is already set, return it
		if (responseArray != null)
			return responseArray;

		Client client = Client.create(new DefaultClientConfig());
		WebResource resource = client.resource(uri);
		resource.accept(MediaType.APPLICATION_JSON);
		responseArray = resource.queryParam(Helper.LOCALE_PARAMETER_NAME, locale.toString())
				.queryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME, requestArray.toString()).get(JSONArray.class);

		return responseArray;
	}

	/**
	 * Creates a Sort client object with default values for uri,locale and
	 * requestArray
	 * 
	 */
	public SortClient() {
		try {
			this.uri = new URI(DEFAULT_URI);
			this.requestArray = new JSONArray(Helper.DEFAULT_REQUEST_ARRAY);
			this.locale = new Locale(Helper.DEFAULT_LOCALE);
		} catch (URISyntaxException e) {
			System.err.println("The default URI(" + DEFAULT_URI + ") is not parsable.");
		} catch (JSONException e) {
			System.err.println("The default array (" + Helper.DEFAULT_REQUEST_ARRAY + ") is not parsable.");
		}
	}

	/**
	 * Creates a JSON object from a file that should contain the attributes "uri", "array" and
	 * "locale", specifying which uri the array and locale will be send to.
	 * 
	 * @param filename
	 *            the filename of the config file. The file must be in json
	 *            format and should contain a string attribute "Server" which
	 *            specifies the URL and an array attribute "List" that specifies
	 *            the wordArray, this client will send to the server in order to
	 *            get it sorted.
	 * 
	 * @return A JSONPOSTClient Object where the getResponseArray() method can
	 *         be invoked to receive the sorted list, null if an exception
	 *         occurred.
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
	public static JSONObject readConfigFromFile(String filename)
			throws FileNotFoundException, IOException, JSONException {

		// Parse the config file
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		// Construct a string with the contents of the config file
		StringBuilder stringConfigBuffer = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringConfigBuffer.append(line);
		}
		reader.close();

		// Parse the string into a JSONObject and return it
		JSONObject jsonConfig = new JSONObject(stringConfigBuffer.toString());
		return jsonConfig;
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
		Collator comparator = Collator.getInstance(Locale.ENGLISH);

		// loop through the array and compare all elements pairwise.
		for (int i = 1; i < responseArray.length(); i++) {
			String current = responseArray.getString(i);

			// this elements (current, last) are not in order, return false
			if (comparator.compare(last, current) > 0)
				return false;

			// update the last pointer to the current index before increasing
			// the index
			last = current;
		}

		// no unordered elements found. Return true
		return true;
	}

	/**
	 * Checks if the responseArray is a permutation of requestArray.
	 * 
	 * @return true if requestArray contains the same elements as responseArray
	 *         and vice versa.
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
	 * Creates a SortClient object as specified in a config file or by command
	 * line arguments, calls its getResponseArray() method and its check methods
	 * and outputs the results.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		// create command line options
		Options options = new Options();
		options.addOption("u", "uri", true,
				"An URI pointing to the RESTSortServer to send the wordList to (default \"" + DEFAULT_URI + "\").");
		options.addOption("a", Helper.REQUEST_ARRAY_PARAMETER_NAME, true,
				"A JSON array of words that will be sent to the RESTSortServer (default \""
						+ Helper.DEFAULT_REQUEST_ARRAY + "\").");
		options.addOption("c", "config", true,
				"A file that contains a JSON object with the attributes \"uri\" which points to a RESTSortServer, \""
						+ Helper.LOCALE_PARAMETER_NAME + "\" that is the locale to be used when sorting and \""
						+ Helper.REQUEST_ARRAY_PARAMETER_NAME
						+ "\" which is an array of words that will be sent to the server to get sorted.");
		options.addOption("l", Helper.LOCALE_PARAMETER_NAME, true, "The locale that is used to sort the array.");
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

		// create a client with the default uri,locale and requestArray
		SortClient client = new SortClient();

		// parse the config file and set the clients url,locale and requestArray
		// accordingly
		if (commandLine.hasOption('c')) {
			String configFileName = commandLine.getOptionValue('c');
			try {
				JSONObject configObject = readConfigFromFile(configFileName);
				if (configObject.has(SERVER_CONFIG_ATTRIBUTE))
					client.setUri(new URI(configObject.getString(SERVER_CONFIG_ATTRIBUTE)));
				if (configObject.has(Helper.REQUEST_ARRAY_PARAMETER_NAME))
					client.setRequestArray(configObject.getJSONArray(Helper.REQUEST_ARRAY_PARAMETER_NAME));
				if (configObject.has(Helper.LOCALE_PARAMETER_NAME))
					client.setLocale(new Locale(configObject.getString(Helper.LOCALE_PARAMETER_NAME)));
			} catch (FileNotFoundException e) {
				System.err.println("The config file \"" + configFileName + "\" cannot be found.");
				return;
			} catch (IOException e) {
				System.err.println("The config file \"" + configFileName + "\" cannot be read.");
				return;
			} catch (JSONException e) {
				System.err.println("The config file \"" + configFileName + "\" doesn't contain valid JSON code.");
				return;
			} catch (URISyntaxException e) {
				System.err.println("The config file \"" + configFileName + "\" doesn't contain a valid URI.");
				return;
			}
		}

		// parse the command line arguments and set the clients uri,locale and
		// requestArray accordingly
		if (commandLine.hasOption('u'))
			try {
				client.setUri(new URI(commandLine.getOptionValue('u')));
			} catch (URISyntaxException e) {
				System.err.println("The server argument parameter must point to a valid URL.");
				return;
			}

		if (commandLine.hasOption('a'))
			try {
				client.setRequestArray(new JSONArray(commandLine.getOptionValue('a')));
			} catch (JSONException e) {
				System.err.println("The list parameter must be a valid JSON array.");
				return;
			}

		if (commandLine.hasOption('l'))
			client.setLocale(new Locale(commandLine.getOptionValue('l')));

		// get the request and response arrays
		JSONArray requestArray = client.getRequestArray();
		JSONArray responseArray = client.getResponseArray();

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