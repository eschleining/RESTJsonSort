# RESTJsonSort

Sample program to demonstrate a RESTJson Server. The client sends a json array of strings to the server, who sorts it and sends it back. The client then checks that the returned list is sorted and a permutation of the sent list.

Requirements:

Oracle Java 7:
sudo apt-get add-repository ppa:webupd8team/java 
sudo apt-get install oracle-java7-installer

Tomcat 7 application server:
sudo apt-get install tomcat7


How to run:

Copy the RESTSortServer.war file into the "webapps" (usually /var/lib/tomcat7/webapps) folder 
of your tomcat7 installation:
sudo cp RESTSortServer.war /var/lib/tomcat7/webapps/

To check if everything is installed properly open a browser window with your tomcat address and the suffix "RESTSortServer/verify" 
(e.g. in my installation the address is: "http://localhost:8080/RESTSortServer/verify"). If you see
the text "The service is running properly." then the server is behaving as expected.

You might have to change the "Server" attribute of the file "config.json" if your tomcat listens to another address than 
"http://localhost:8080/" before running the client jar. If you wish to get another list sorted than the default (["harry", "ron", "hermione"])
feel free to change the "List" property of the config.json just make sure to specify a json Array.

To run the client it is enough to execute the RESTSortClient.jar from the same working directory that the "config.json" is in:
java -jar RESTSortClient.jar