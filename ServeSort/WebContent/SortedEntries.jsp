<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sortet Entries</title>
</head>
<body>
<h3>Here are the words in lexicographical order!</h3>
	<table>
		<!-- create a table entry with a row for each word in the sorted array 
		(the Servlet set the sorted array as an attribute called "sortedWords") -->
		<c:forEach items="${sortedWords}" var="word">
			<tr>
				<td>${word}
			</tr>
		</c:forEach>
	</table>
</body>
</html>