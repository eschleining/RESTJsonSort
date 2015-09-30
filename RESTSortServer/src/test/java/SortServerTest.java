package test.java;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import helper.Helper;
import server.SortServer;

public class SortServerTest {

	public static final String EMPTY = "[]", ONE = "[a]", SORTED = "[a,b,c]", UNSORTED = "[c,b,a]",
			SORTED_UNSAFE = "[a,A,ä,Ä,ö,Ö,ü,Ü]", UNSORTED_UNSAFE = "[Ü,ü,Ö,ö,Ä,ä,A,a]";
	public JSONArray empty, one, sorted, unsorted, sorted_unsafe, unsorted_unsafe;

	public Locale locale;
	public SortServer server;

	@Before
	public void before() throws JSONException {

		empty = new JSONArray(EMPTY);
		one = new JSONArray(ONE);
		sorted = new JSONArray(SORTED);
		unsorted = new JSONArray(UNSORTED);
		sorted_unsafe = new JSONArray(SORTED_UNSAFE);
		unsorted_unsafe = new JSONArray(UNSORTED_UNSAFE);
		locale = new Locale(Helper.DEFAULT_LOCALE);
		server = new SortServer();
	}

	@Test
	public void testSortJSONStringArray() {
		assertEquals(sorted.toString(), server.sortJSONStringArray(sorted, locale).getEntity().toString());
		assertEquals(sorted.toString(), server.sortJSONStringArray(unsorted, locale).getEntity().toString());
		assertEquals(empty.toString(), server.sortJSONStringArray(empty, locale).getEntity().toString());
		assertEquals(one.toString(), server.sortJSONStringArray(one, locale).getEntity().toString());
		assertEquals(sorted_unsafe.toString(),
				server.sortJSONStringArray(sorted_unsafe, locale).getEntity().toString());
		assertEquals(sorted_unsafe.toString(),
				server.sortJSONStringArray(unsorted_unsafe, locale).getEntity().toString());
	}

}
