package test.java;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import helper.Helper;

public class HelperTest {

	public static final String EMPTY = "[]", ONE = "[a]", MORE = "[a,b,c,]";
	public JSONArray empty_JSON, one_JSON, more_JSON;
	public List<String> empty_JAVA, one_JAVA, more_JAVA;

	@Before
	public void before() throws JSONException {
		empty_JSON = new JSONArray(EMPTY);
		one_JSON = new JSONArray(ONE);
		more_JSON = new JSONArray(MORE);

		empty_JAVA = new ArrayList<>();
		one_JAVA = new ArrayList<String>(Arrays.asList(new String[] { "a" }));
		more_JAVA = new ArrayList<String>(Arrays.asList(new String[] { "a", "b", "c" }));
	}

	@Test
	public void testGetStringList() throws JSONException {
		assertEquals(empty_JAVA, Helper.getStringList(empty_JSON));
		assertEquals(one_JAVA, Helper.getStringList(one_JSON));
		assertEquals(more_JAVA, Helper.getStringList(more_JSON));
	}

}
