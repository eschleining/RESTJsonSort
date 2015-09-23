package server;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SortServlet
 */
@WebServlet("/")
public class SortServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SortServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Small routine that forwards the request to the unsorted jsp.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("UnsortedEntries.jsp").forward(request, response);
	}

	/**
	 * Takes the wordlist posted by the client from the unsorted jsp, 
	 * 1. splits the wordlist string by space into a String array, 
	 * 2. sorts the created array (lexicographical order)
	 * 3. sets the sorted array as a request attribute
	 * 4. forwards the request to the SortedEntries jsp where the results are listed 
	 * in ascending lexicographical order
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String words = request.getParameter("words");
		String[] wordArray = words.split(" ");
		Arrays.sort(wordArray);
		request.setAttribute("sortedWords", wordArray);
		request.getRequestDispatcher("SortedEntries.jsp").forward(request, response);
	}

}
