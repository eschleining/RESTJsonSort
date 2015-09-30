package server;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import helper.Helper;

public class SortServerIT {

	public static final int OK = 200, NOT_FOUND = 404;

	public static final String[] unsafe_string_array = new String[] { "a", "A", "ä", "Ä", "ö", "Ö", "ü", "Ü", "!", "\"",
			"§", "$", "%", "&", "/", "(", ")", "=", "?", "`" },
			unsafe_string_array_sorted = new String[] { "!", "?", "/", "`", "\"", "(", ")", "§", "$", "&", "%", "=",
					"a", "A", "ä", "Ä", "ö", "Ö", "ü", "Ü" };
	public static final String EMPTY = "[]", SORTED = "[a,b,c]", UNSORTED = "[c,b,a]", FAIL_LOCALE = "not_a_locale",
			FAIL_ARRAY = "not_an_array", UNSAFE_ARRAY = new JSONArray(Arrays.asList(unsafe_string_array)).toString(),
			UNSAFE_ARRAY_SORTED = new JSONArray(Arrays.asList(unsafe_string_array_sorted)).toString();

	public WebResource empty, verify, sort, no_locale, no_array, sorted, unsorted, fail_locale, fail_array,
			unsafe_array, unsafe_encoded_array;

	public static final Client client = Client.create(new DefaultClientConfig());

	@Before
	public void before() throws UnsupportedEncodingException, JSONException {
		empty = client.resource(Helper.DEFAULT_URI);
		verify = empty.path(Helper.VERIFY_PATH);
		sort = empty.path(Helper.SORT_PATH);
		no_locale = sort.queryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME, UNSORTED);
		no_array = sort.queryParam(Helper.LOCALE_PARAMETER_NAME, Helper.DEFAULT_LOCALE);
		fail_locale = sort.queryParam(Helper.LOCALE_PARAMETER_NAME, FAIL_LOCALE);
		fail_array = sort.queryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME, FAIL_ARRAY);
		unsafe_array = sort.queryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME, new JSONArray(UNSAFE_ARRAY).toString());
		unsafe_encoded_array = sort.queryParam(Helper.REQUEST_ARRAY_PARAMETER_NAME,
				URLEncoder.encode(UNSAFE_ARRAY, "UTF-8"));
	}

	@Test
	public void testVerifyRESTService() {
		ClientResponse response = verify.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals("The service is running properly.", response.getEntity(String.class));
	}

	@Test
	public void testSortJSONStringArray() throws ClientHandlerException, UniformInterfaceException, JSONException {
		ClientResponse response;

		response = sort.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals(new JSONArray(EMPTY).toString(), response.getEntity(JSONArray.class).toString());

		response = no_locale.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals(new JSONArray(SORTED).toString(), response.getEntity(JSONArray.class).toString());

		response = no_array.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals(new JSONArray(EMPTY).toString(), response.getEntity(JSONArray.class).toString());

		response = fail_locale.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals(new JSONArray(EMPTY).toString(), response.getEntity(JSONArray.class).toString());

		response = fail_array.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(NOT_FOUND, response.getStatus());

		response = unsafe_array.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals(new JSONArray(UNSAFE_ARRAY_SORTED).toString(), response.getEntity(JSONArray.class).toString());

		response = unsafe_encoded_array.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK, response.getStatus());
		assertEquals(new JSONArray(UNSAFE_ARRAY_SORTED).toString(), response.getEntity(JSONArray.class).toString());
	}

}
