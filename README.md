# RESTJsonSort

Sample program to demonstrate a REST+JSON Server. The client sends a JSON array of strings to the server, who sorts it and sends it back. The client then checks that the returned list is sorted and a permutation of the sent list.

##Requirements:

###Oracle Java 7:

`sudo apt-get add-repository ppa:webupd8team/java`

`sudo apt-get install oracle-java7-installer`

###Apache Tomcat 7:

`sudo apt-get install tomcat7`

###Apache Maven 3:

If you wish to alter the source code, you can use maven (v 3) to recreate the war (server) and jar (client) file.

`sudo apt-get install maven3`


##How to run:

Download and copy the server war file into the "webapps" (usually /var/lib/tomcat7/webapps) folder 
of your tomcat7 installation:

`wget https://github.com/eschleining/RESTJsonSort/releases/download/v1.1/RESTSortServer.war`

`sudo cp RESTSortServer.war /var/lib/tomcat7/webapps/`

To check if everything is installed properly open a browser window with your tomcat address and the suffix `RESTSortServer/verify` (e.g. in my installation the address is: `http://localhost:8080/RESTSortServer/verify`). If you see the text `The service is running properly.` then the server is behaving as expected.

To run the client it is enough to downlad the "config.json" file and the client jar:

`wget https://github.com/eschleining/RESTJsonSort/releases/download/v1.1/config.json`

`wget https://github.com/eschleining/RESTJsonSort/releases/download/v1.1/RESTSortClient.jar`

You might have to change the "Server" attribute of the file "config.json" if your tomcat listens to another address than "http://localhost:8080/" before running the client jar. If you wish to get another list sorted than the default `["harry", "ron", "hermione"]` feel free to change the `"List"` property of the config.json just make sure to specify a JSON Array. Then execute the client jar from the same working directory that the "config.json" is in:

`java -jar RESTSortClient.jar`

##How to build:

First install all required libraries into your local maven repo by executing `mvn install` from the root directory of this git repository.

Then in the root directory execute `mvn package` and maven will build the Helper,Client and Server components (the executables will be stored inside the "target" directory of the corresponding components).

If you have configured the manager app for tomcat, you can also use maven to deploy the sort server by defining a server with the id "TomcatServer" in your maven settings.xml, that contains the credentials for a tomcat user with the "manager-script" role (the user must be defined in tomcats "tomcat-users.xml" file with the role "manager-script").

For more information about the tomcat manager app, read [this](http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html#Configuring_Manager_Application_Access). If you seek instructions to configure maven, read [this](http://maven.apache.org/ref/3.3.3/maven-settings/settings.html). 
After you have configured tomcat and maven, you can use the commands `mvn tomcat7:deploy,redeploy,undeploy` from within the RESTSortServer directory.
