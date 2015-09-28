# RESTJsonSort

Sample program to demonstrate a REST+JSON Server. The client sends a JSON array of strings to the server, who sorts it and sends it back. The client then checks that the returned list is sorted and a permutation of the sent list.

##Requirements:

###Oracle Java 7 JDK:
Download and install Oracle Java SE [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) or if you have apt, install it like follows:
```bash
sudo apt-get add-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
```
###Apache Tomcat 7:
Download and install Apache tomcat 7 [here](http://tomcat.apache.org/download-70.cgi) or if you have apt, install it like follows:
```bash
sudo apt-get update
sudo apt-get install tomcat7
```

###Apache Maven 3:
If you wish to alter the source code, you can use maven 3 to recreate the war (server) and jar (client) file.
Download and intall Apache maven 3 [here](http://maven.apache.org/download.cgi) or if you have apt, install it like follows:
```bash
sudo add-apt-repository ppa:andrei-pozolotin/maven3
sudo apt-get update
sudo apt-get install maven3
```

##How to run:

###Server:

Download the file `RESTSortServer.war` [here](https://github.com/eschleining/RESTJsonSort/releases/latest) and copy it into the webapps folder of your tomcat7 installation (usually `/var/lib/tomcat7/webapps`).

To check if everything is installed properly open a browser window with your tomcat address and the suffix `RESTSortServer/verify` (e.g. in my installation the address is: `http://localhost:8080/RESTSortServer/verify` ). If you see the text `The service is running properly.` then the server is behaving as expected.

###Client:

Download the files `RESTSortClient.jar` and `config.json` [here](https://github.com/eschleining/RESTJsonSort/releases/latest)

You might have to change the `"Server"` attribute in the file `config.json` if your tomcat listens to another address than `http://localhost:8080/` before running the client. 

If you wish to get another list sorted than the default `["harry", "ron", "hermione"]` feel free to change the `"List"` property in `config.json` just make sure to specify a JSON Array. Then execute the client jar from the same working directory that the config file is in:
```bash
java -jar RESTSortClient.jar
```

##How to build:

First install all required libraries into your local maven repo by executing `mvn install` from the root directory of this git repository.

Then in the root directory execute `mvn package` and maven will build the Helper,Client and Server components (the executables will be stored inside the "target" directory of the corresponding components).

If you have configured the manager app for tomcat, you can also use maven to deploy the sort server by defining a server with the id `TomcatServer` in your maven `settings.xml`, that contains the credentials for a tomcat user with the `manager-script` role (the user must be defined in tomcats `tomcat-users.xml` file with the role `manager-script`).

In `tomcat-users.xml` (usually under `/var/lib/tomcat7/conf`) add the following:
```xml
<tomcat-users>
...
  <role rolename="manager-script"/>
  <user username="your username" password="your secret password" roles="manager-script"/>
</tomcat-users>
```


In your maven `settings.xml` (usually under `/etc/maven` or `~/.m2`):
```xml
<servers>
...
		<server>
			<id>TomcatServer</id>
			<username>your username</username>
			<password>your secret password</password>
		</server>
</servers>
```

Should your tomcat manager listen to another address than `http://localhost:8080/manager/text` you will have to add the `url` tag to the `pom.xml` in the `RESTSortServer` directory:
```xml
<plugin>
				<groupId>org.apache.tomcat.maven</groupId>
				<artifactId>tomcat7-maven-plugin</artifactId>
				<version>2.2</version>
				<configuration>
					<server>TomcatServer</server>
					<path>/RESTSortServer</path>
					<url>add your managers url here</url>
				</configuration>
</plugin>
```

For more information about the tomcat manager app, read [this](http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html#Configuring_Manager_Application_Access). If you seek instructions to configure maven, read [this](http://maven.apache.org/ref/3.3.3/maven-settings/settings.html). 
After you have configured tomcat and maven, you can use the commands `mvn tomcat7:deploy,redeploy,undeploy` from within the RESTSortServer directory.
