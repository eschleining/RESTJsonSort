# RESTJsonSort

Sample program to demonstrate a RESTJson Server. The client sends a json array of strings to the server, who sorts it and sends it back. The client then checks that the returned list is sorted and a permutation of the sent list.

##Requirements:

###Oracle Java 7:

sudo apt-get add-repository ppa:webupd8team/java

sudo apt-get install oracle-java7-installer

###Tomcat 7 application server:

sudo apt-get install tomcat7

###Apache Maven 3:

If you wish to alter the source code, you can use maven (v 3) to recreate the war (server) and jar (client) file.

sudo apt-get install maven3


##How to run:

Download and copy the server war file into the "webapps" (usually /var/lib/tomcat7/webapps) folder 
of your tomcat7 installation:

wget https://github.com/eschleining/RESTJsonSort/releases/download/v1.0/Server-1.0.war

sudo cp Server-1.0.war /var/lib/tomcat7/webapps/

To check if everything is installed properly open a browser window with your tomcat address and the suffix "RESTSortServer/verify" 
(e.g. in my installation the address is: "http://localhost:8080/RESTSortServer/verify"). If you see
the text "The service is running properly." then the server is behaving as expected.

To run the client it is enough to downlad the "config.json" file and the client jar. Then execute the client jar from the same working directory that the "config.json" is in:

wget https://github.com/eschleining/RESTJsonSort/releases/download/v1.0/config.json

wget https://github.com/eschleining/RESTJsonSort/releases/download/v1.0/Client-1.0-jar-with-dependencies.jar

java -jar Client-1.0-jar-with-dependencies.jar

You might have to change the "Server" attribute of the file "config.json" if your tomcat listens to another address than "http://localhost:8080/" before running the client jar. If you wish to get another list sorted than the default (["harry", "ron", "hermione"]) feel free to change the "List" property of the config.json just make sure to specify a json Array.

##How to build:

If you change the source code you can use maven to recreate the war and jar files, in the root directory execute

mvn package

and maven will build the Helper,Client and Server components (the executables will be stored inside the "target" directory of the corresponding components).
