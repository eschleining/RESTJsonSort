package server;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import helper.Helper;

/**
 * The Server object which contains a method to check if everything is installed
 * properly (verifyRESTService()) and the method which receives a JSONArray,
 * sorts it and sends it back to the client.
 * 
 * @author Eduard Schleining
 *
 */

@Path("/")
public class SortServer {

	/**
	 * Indicates if the service runs properly and returns a success message.
	 * 
	 * @return response message with status 200
	 */
	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService() {
		String result = "The service is running properly.";

		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}

	/**
	 * Sorts the requestArray with a Collator derived from locale by converting
	 * the array to a List and invoking Collections.sort(List,Collator) and
	 * returns the resulting list.
	 * 
	 * @param requestArray
	 *            the JSONArray to sort
	 * @param locale
	 *            the Locale the Collator will be constructed from
	 * @return a JSONArray that has been sorted with a collator derived from
	 *         locale
	 * @throws JSONException
	 *             if Helper.getStringlist(requestArray) cannot be invoked
	 */
	private static JSONArray sort(JSONArray requestArray, Locale locale) throws JSONException {
		Collator collator = Collator.getInstance(locale);
		List<String> list = Helper.getStringList(requestArray);
		Collections.sort(list, collator);
		return new JSONArray(list);
	}

	// /**
	// *
	// * POST service method, receives a JSON object of the form {"locale" :
	// * String, "array" : String[]}, parses its parameters into a locale and an
	// * JSON array, sorts the array with the corresponding locale collator and
	// * returns it. If array or locale are not specified, default values from
	// the
	// * Helper class are used.
	// *
	// * @param requestObject
	// * the JSONObject sent by the client
	// *
	// * @return Response with status 200 (ok) and the sorted array in JSON
	// format
	// * or Response with status 400 (invalid data) and no payload if the
	// * client data cannot be processed.
	// */
	// @POST
	// @Path("/sort")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response sortJSONStringArray(JSONObject requestObject) {
	// try {
	// // create default parameters
	// JSONArray requestArray = new JSONArray(Helper.DEFAULT_REQUEST_ARRAY);
	// Locale locale = new Locale(Helper.DEFAULT_LOCALE);
	//
	// // override default parameters with requestObject
	// if (requestObject.has(Helper.REQUEST_ARRAY_PARAMETER_NAME))
	// requestArray =
	// requestObject.getJSONArray(Helper.REQUEST_ARRAY_PARAMETER_NAME);
	// if (requestObject.has(Helper.LOCALE_PARAMETER_NAME))
	// locale = new
	// Locale(requestObject.getString(Helper.LOCALE_PARAMETER_NAME));
	//
	// // sort and respond with ok()
	// JSONArray responsePayload = sort(requestArray, locale);
	// return Response.ok().entity(responsePayload).build();
	// } catch (JSONException e) {
	// // Send a response with status 400 (invalid data) in case the
	// // jsonArray cannot be processed properly.
	// return Response.status(400).build();
	// }
	//
	// }

	/**
	 * GET service method, receives a JSON array and a locale string, sorts the
	 * array with the corresponding locale collator and returns it. If array or
	 * locale are not specified, default values from the Helper class are used.
	 *
	 * @param requestArray
	 *            the JSONArray to be sorted
	 * @param locale
	 *            the locale string sent by the client
	 * @return Response with status 200 (ok) and the sorted array in JSON format
	 *         or Response with status 400 (invalid data) and no payload if the
	 *         client data cannot be processed.
	 */
	@GET
	@Path(Helper.SORT_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sortJSONStringArray(
			@DefaultValue(Helper.DEFAULT_REQUEST_ARRAY) @QueryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME) JSONArray requestArray,
			@DefaultValue(Helper.DEFAULT_LOCALE) @QueryParam(Helper.LOCALE_PARAMETER_NAME) Locale locale) {
		try {
			JSONArray sorted = sort(requestArray, locale);
			return Response.ok().entity(sorted).build();
		} catch (JSONException e) {
			return Response.status(400).build();
		}
	}

}