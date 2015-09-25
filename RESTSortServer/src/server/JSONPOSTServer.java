package server;

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

import helper.Helper;

/**
 * The Server object which contains a method to check if everything is
 * installed properly (verifyRESTService()) and the method which receives a
 * JSONArray, sorts it and sends it back to the client.
 * 
 * @author Eduard Schleining
 *
 */

@Path("/")
public class JSONPOSTServer {

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

			List<String> stringList = Helper.getStringList(jsonArray);
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