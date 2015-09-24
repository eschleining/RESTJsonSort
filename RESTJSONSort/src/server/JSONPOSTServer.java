package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.*;

import model.Words;

@Path("/")
public class JSONPOSTServer {
	/**
	 * @throws JSONException
	 * 
	 */
	public static String[] getJavaStringArray(JSONArray jsonArray) throws JSONException {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @throws JSONException
	 * 
	 */
	public static JSONArray getJsonStringArray(String[] javaArray) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < javaArray.length; i++) {
			jsonArray.put(javaArray[i]);
		}
		return jsonArray;
	}

	/**
	 * This method just indicates if the service runs properly and returns a
	 * success message.
	 * 
	 * @return Response message with status 200
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
	 * @param track
	 * @return
	 */
	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sortJSONStringArray(JSONArray jsonArray) {
		String[] javaArray;
		try {
			javaArray = getJavaStringArray(jsonArray);
			Arrays.sort(javaArray);
			return Response.ok().entity(getJsonStringArray(javaArray)).build();
		} catch (JSONException e) {
			return Response.notAcceptable(null).build();
		}

	}

}
