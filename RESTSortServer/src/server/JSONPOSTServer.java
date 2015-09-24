package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

@Path("/")
public class JSONPOSTServer {

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
	 * 
	 * Main service method, receives a jsonArray, sorts it and sends the sorted
	 * input back to the client.
	 * 
	 * @param jsonArray
	 *            the JSONArray sent by the client
	 * 
	 * @return Response with status 200 (ok) and the sorted array in JSON format
	 *         or Response with status 400 (invalid data) and no payload if the
	 *         client data cannot be processed.
	 */
	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sortJSONStringArray(JSONArray jsonArray) {
		try {

			List<String> stringList = getStringList(jsonArray);
			Collections.sort(stringList);

			JSONArray responsePayload = new JSONArray(stringList);

			return Response.ok().entity(responsePayload).build();
		} catch (JSONException e) {
			// Send a response with status 400 (invalid data) in case the
			// jsonArray cannot be processed properly.
			return Response.status(400).build();
		}

	}

}