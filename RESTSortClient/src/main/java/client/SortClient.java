package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
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
import com.sun.jersey.api.client.ClientResponse;
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
	 * Executes a GET request with the requestArray object to the server
	 * specified by the url, receives the response an sets the responseArray
	 * field before returning it.
	 * 
	 * @return if the field responseArray is not null, returns the field
	 *         responseArray, else does a get request with the requestArray and
	 *         locale parameters, sets the responseArray field to the received
	 *         result and returns the value of the responseArray afterwards
	 * 
	 * @throws JSONException
	 *             if the response cannot be parsed as a JSON array
	 */
	public JSONArray getResponseArray() throws JSONException {

		// if responseArray is already set, return it
		if (responseArray != null)
			return responseArray;

		Client client = Client.create(new DefaultClientConfig());
		WebResource resource = client.resource(uri).path(Helper.SORT_PATH);
		resource.accept(MediaType.APPLICATION_JSON);
		String encoding = "UTF-8";
		try {
			String arrayParam = URLEncoder.encode(requestArray.toString(), encoding);
			String localeParam = URLEncoder.encode(locale.toString(), encoding);
			ClientResponse response = resource.queryParam(Helper.LOCALE_PARAMETER_NAME, localeParam)
					.queryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME, arrayParam).get(ClientResponse.class);
			responseArray = new JSONArray(response.getEntity(String.class));
			return responseArray;
		} catch (UnsupportedEncodingException e) {
			System.err.println(encoding + " encoding not supported:" + e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a Sort client object with default values for uri,locale and
	 * requestArray
	 * 
	 */
	public SortClient() {
		try {
			this.uri = new URI(Helper.DEFAULT_URI);
			this.requestArray = new JSONArray(Helper.DEFAULT_REQUEST_ARRAY);
			this.locale = new Locale(Helper.DEFAULT_LOCALE);
		} catch (URISyntaxException e) {
			System.err.println("The default URI(" + Helper.DEFAULT_URI + ") is not parsable.");
		} catch (JSONException e) {
			System.err.println("The default array (" + Helper.DEFAULT_REQUEST_ARRAY + ") is not parsable.");
		}
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

	// Strings used for command line options and to parse config
	private static final String URI_OPTION_SHORT = "u";
	private static final String URI_OPTION_LONG = Helper.URI_PARAMETER_NAME;
	private static final String URI_OPTION_DESCRIPTION = "An URI pointing to the RESTSortServer to send the wordList to (default \""
			+ Helper.DEFAULT_URI + "\").";

	private static final String ARRAY_OPTION_SHORT = "a";
	private static final String ARRAY_OPTION_LONG = Helper.REQUEST_ARRAY_PARAMETER_NAME;
	private static final String ARRAY_OPTION_DESCRIPTION = "A JSON array of words that will be sent to the RESTSortServer (default \""
			+ Helper.DEFAULT_REQUEST_ARRAY + "\").";

	private static final String CONFIG_OPTION_SHORT = "c";
	private static final String CONFIG_OPTION_LONG = "config";
	private static final String CONFIG_OPTION_DESCRIPTION = "A file that contains a JSON object with the attributes \"uri\" which points to a RESTSortServer, \""
			+ Helper.LOCALE_PARAMETER_NAME + "\" that is the locale to be used when sorting and \""
			+ Helper.REQUEST_ARRAY_PARAMETER_NAME
			+ "\" which is an array of words that will be sent to the server to get sorted.";

	private static final String LOCLAE_OPTION_SHORT = "l";
	private static final String LOCALE_OPTION_LONG = Helper.LOCALE_PARAMETER_NAME;
	private static final String LOCALE_OPTION_DESCRIPTION = "The locale that is used to sort the array.";

	private static final String HELP_OPTION_SHORT = "h";
	private static final String HELP_OTION_LONG = "help";
	private static final String HELP_OPTION_DESCRIPTION = "Print this help message.";

	// create command line options
	public static final Options options = new Options()
			.addOption(URI_OPTION_SHORT, URI_OPTION_LONG, true, URI_OPTION_DESCRIPTION)
			.addOption(ARRAY_OPTION_SHORT, ARRAY_OPTION_LONG, true, ARRAY_OPTION_DESCRIPTION)
			.addOption(CONFIG_OPTION_SHORT, CONFIG_OPTION_LONG, true, CONFIG_OPTION_DESCRIPTION)
			.addOption(LOCLAE_OPTION_SHORT, LOCALE_OPTION_LONG, true, LOCALE_OPTION_DESCRIPTION)
			.addOption(HELP_OPTION_SHORT, HELP_OTION_LONG, false, HELP_OPTION_DESCRIPTION);

	/**
	 * Sets the fields of this to the values specified by the commandLine
	 * parameter.
	 * 
	 * @param commandLine
	 *            the commandLine to read the values for "uri (-u --uri <arg>)",
	 *            "locale (-l --locale <arg>)", "-c --config <arg>" and
	 *            "array (-a --array <arg>)" from.
	 * 
	 * @throws URISyntaxException
	 *             if the uri parameter is given but its argument is not a valid
	 *             URI
	 * 
	 * @throws JSONException
	 *             if the array parameter is given but not a valid JSON array
	 * 
	 * @throws IOException
	 *             if the config parameter is given but the Input stream for the
	 *             config file cannot be read
	 * 
	 * @throws FileNotFoundException
	 *             if the config parameter is given but the config file cannot
	 *             be found
	 */
	public void setConfigFromCommandLine(CommandLine commandLine)
			throws URISyntaxException, JSONException, FileNotFoundException, IOException {
		if (commandLine.hasOption(URI_OPTION_SHORT))
			setUri(new URI(commandLine.getOptionValue(URI_OPTION_SHORT)));
		if (commandLine.hasOption(ARRAY_OPTION_SHORT))
			setRequestArray(new JSONArray(commandLine.getOptionValue(ARRAY_OPTION_SHORT)));
		if (commandLine.hasOption(LOCLAE_OPTION_SHORT))
			setLocale(new Locale(commandLine.getOptionValue(LOCLAE_OPTION_SHORT)));
		if (commandLine.hasOption(CONFIG_OPTION_SHORT)) {
			String configFileName = commandLine.getOptionValue(CONFIG_OPTION_SHORT);
			setConfigFromInputStream(new FileInputStream(configFileName));
		}
	}

	/**
	 * Sets the fields of this to the values in the input stream.
	 * 
	 * @param inputStream
	 *            input stream of that contains a jsonObject. The stream must be
	 *            in json format and should contain a string attribute "uri"
	 *            which specifies the URI and an array attribute "array" that
	 *            specifies the wordArray, this client will send to the server
	 *            in order to get it sorted, additionally it can have a "locale"
	 *            field with a valid locale string.
	 * 
	 * @throws IOException
	 *             if the InputStream cannot be read
	 * 
	 * @throws JSONException
	 *             if the InputStream doensn't contain json
	 * 
	 * @throws URISyntaxException
	 *             if the InputStream contains a "uri" field which is no valid
	 *             URI
	 */
	public void setConfigFromInputStream(InputStream inputStream)
			throws IOException, JSONException, URISyntaxException {

		// Parse the config file
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		// Construct a string with the contents of the config file
		StringBuilder stringConfigBuffer = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringConfigBuffer.append(line);
		}
		reader.close();

		// Parse the string into a JSONObject and return it
		JSONObject jsonConfig = new JSONObject(stringConfigBuffer.toString());
		if (jsonConfig.has(URI_OPTION_LONG))
			setUri(new URI(jsonConfig.getString(URI_OPTION_LONG)));
		if (jsonConfig.has(ARRAY_OPTION_LONG))
			setRequestArray(jsonConfig.getJSONArray(ARRAY_OPTION_LONG));
		if (jsonConfig.has(LOCALE_OPTION_LONG))
			setLocale(new Locale(jsonConfig.getString(LOCALE_OPTION_LONG)));
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
		if (commandLine.hasOption(HELP_OPTION_SHORT)) {
			helpFormatter.printHelp("java -jar RESTSortClient.jar", options);
			return;
		}

		// create a client with the default uri,locale and requestArray
		SortClient client = new SortClient();

		// parse the command line arguments and set the clients uri,locale and
		// requestArray accordingly
		try {
			client.setConfigFromCommandLine(commandLine);
		} catch (URISyntaxException e) {
			System.err.println("The uri argument parameter must point to a valid URI.");
			return;
		} catch (JSONException e) {
			System.err.println("The list parameter must be a valid JSON array.");
			return;
		} catch (FileNotFoundException e) {
			System.err.println("The config file cannot be found.");
			return;
		} catch (IOException e) {
			System.err.println("The config file cannot be read.");
			return;
		}

		// get the request and response arrays
		JSONArray requestArray = client.getRequestArray();
		JSONArray responseArray = null;
		try {
			responseArray = client.getResponseArray();
		} catch (JSONException e1) {
			System.err.println("Response was not an JSON array.");
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