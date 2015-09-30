package client;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import client.SortClient;
import helper.Helper;

public class SortClientTest {

	SortClient empty;
	String uri, array, locale;

	@Before
	public void before() {
		empty = new SortClient();
		uri = "http://localhost:8080/testuri";
		array = "[\"testarray\"]";
		locale = "de";
	}

	@Test
	public void testSortClient() throws JSONException {
		assertEquals(new JSONArray(Helper.DEFAULT_REQUEST_ARRAY).toString(), empty.getRequestArray().toString());
		assertNotNull(empty.getUri());
		assertEquals(new Locale(Helper.DEFAULT_LOCALE), empty.getLocale());
	}

	@Test
	public void testSetConfigFromInputStream() throws IOException, JSONException, URISyntaxException {

		String config = "{" + "\"" + Helper.URI_PARAMETER_NAME + "\":\"" + uri + "\"," + "\""
				+ Helper.REQUEST_ARRAY_PARAMETER_NAME + "\":" + array + ",\"" + Helper.LOCALE_PARAMETER_NAME + "\":\""
				+ locale + "\"}";
		empty.setConfigFromInputStream(new ByteArrayInputStream(config.getBytes()));

		assertEquals(new Locale(locale), empty.getLocale());
		assertEquals(new URI(uri), empty.getUri());
		assertEquals(new JSONArray(array).toString(), empty.getRequestArray().toString());
	}

	@Test
	public void testSetConfigFromCommandLine()
			throws ParseException, FileNotFoundException, URISyntaxException, JSONException, IOException {
		String[] args = new String[] { "-l", locale, "-a", array, "-u", uri };
		CommandLine commandLine = new DefaultParser().parse(SortClient.options, args);
		empty.setConfigFromCommandLine(commandLine);

		assertEquals(new Locale(locale), empty.getLocale());
		assertEquals(new URI(uri), empty.getUri());
		assertEquals(new JSONArray(array).toString(), empty.getRequestArray().toString());
	}
}