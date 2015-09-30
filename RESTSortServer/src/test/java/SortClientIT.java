import static org.junit.Assert.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import client.SortClient;

public class SortClientIT {

	public static final String EMPTY = "[]", ONE = "[a]", SORTED = "[a,b,c]", UNSORTED = "[c,b,a]",
			SORTED_UNSAFE = "[a,A,ä,Ä,ö,Ö,ü,Ü]", UNSORTED_UNSAFE = "[Ü,ü,Ö,ö,Ä,ä,A,a]";
	public JSONArray empty_array, one_array, sorted_array, unsorted_array, sorted_unsafe_array, unsorted_unsafe_array;
	public SortClient empty, one, sorted, unsorted, sorted_unsafe, unsorted_unsafe, fail_array;

	@Before
	public void before() throws JSONException {

		empty_array = new JSONArray(EMPTY);
		empty = new SortClient();
		empty.setRequestArray(empty_array);
		empty.getResponseArray();

		one_array = new JSONArray(ONE);
		one = new SortClient();
		one.setRequestArray(one_array);
		one.getResponseArray();

		sorted_array = new JSONArray(SORTED);
		sorted = new SortClient();
		sorted.setRequestArray(sorted_array);
		sorted.getResponseArray();

		unsorted_array = new JSONArray(UNSORTED);
		unsorted = new SortClient();
		unsorted.setRequestArray(unsorted_array);
		unsorted.getResponseArray();

		sorted_unsafe_array = new JSONArray(SORTED_UNSAFE);
		sorted_unsafe = new SortClient();
		sorted_unsafe.setRequestArray(sorted_unsafe_array);
		sorted_unsafe.getResponseArray();

		unsorted_unsafe_array = new JSONArray(UNSORTED_UNSAFE);
		unsorted_unsafe = new SortClient();
		unsorted_unsafe.setRequestArray(unsorted_unsafe_array);
		unsorted_unsafe.getResponseArray();
		
	}

	@Test
	public void testGetResponseArray() throws JSONException {
		assertEquals(empty_array.toString(), empty.getResponseArray().toString());
		assertEquals(one_array.toString(), one.getResponseArray().toString());
		assertEquals(sorted_array.toString(), sorted.getResponseArray().toString());
		assertEquals(sorted_array.toString(), unsorted.getResponseArray().toString());
		assertEquals(sorted_unsafe_array.toString(), sorted_unsafe.getResponseArray().toString());
		assertEquals(sorted_unsafe_array.toString(), unsorted_unsafe.getResponseArray().toString());
	}

	@Test
	public void testResponseIsSorted() throws JSONException {
		assertTrue(empty.responseIsSorted());
		assertTrue(one.responseIsSorted());
		assertTrue(sorted.responseIsSorted());
		assertTrue(unsorted.responseIsSorted());
		assertTrue(sorted_unsafe.responseIsSorted());
		assertTrue(unsorted_unsafe.responseIsSorted());
	}

	@Test
	public void testResponseIsPermutationOfRequest() throws JSONException {
		assertTrue(empty.responseIsPermutationOfRequest());
		assertTrue(one.responseIsPermutationOfRequest());
		assertTrue(sorted.responseIsPermutationOfRequest());
		assertTrue(unsorted.responseIsPermutationOfRequest());
		assertTrue(sorted_unsafe.responseIsPermutationOfRequest());
		assertTrue(unsorted_unsafe.responseIsPermutationOfRequest());
	}

}
