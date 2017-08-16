# Tomcat JNDI with Scheduler and Apache CXF Web Service
----------

This application provides the following functionality:

* Demonstrates the configuration and use of JNDI with an embedded Tomcat server.

* RESTful web service that performs JNDI lookups.
 
	* http://localhost:8080/direct
	* http://localhost:8080/factoryBean
	* http://localhost:8080/directFromUtil
	* http://localhost:8080/factoryBeanFromUtil

* Scheduled task that performs JNDI lookups.

* (Apache CXF) SOAP web service that performs JNDI lookups.
 
	* http://localhost:8080/services/SampleManager?wsdl
	
	** echo
	** direct
	** factoryBean
	** directFromBridge
	** factoryBeanFromBridge

	* Please note the direct and directFromBridge operations currently return the following errors, respectively, at runtime:
	
	** Name [java:comp/env/jdbc/myDataSource] is not bound in this Context. Unable to find [java:comp].
	** NamingException: javax.naming.NameNotFoundException: Name [java:comp/env/jdbc/myDataSource] is not bound in this Context. Unable to find [java:comp].
 

## Development
----------

### Requirements
* [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or later

* IDE that supports gradle
	* Recommend [Spring Tool Suite](https://spring.io/tools)
	* Buildship Gradle Integration plugin (can be installed via the [Eclipse Oxygen](http://download.eclipse.org/releases/oxygen) release site)

* [Git](http://git-scm.com/download/win) and/or a visual Git client for Windows (SourceTree, TortoiseGit, etc)

### Development information
This is a [Spring Boot](https://projects.spring.io/spring-boot/) Java application that is managed with gradle. You should be able to import this project as a gradle or spring boot application in your IDE.

### Running in Windows
* Option 1: use your IDE to run the application.

* Option 2: start the application using gradle. 

```
./gradlew bootrun
```

### Running in Linux
* start the application via the command line. 

** start the application

```
java -jar ${APP_NAME}
```
  

## Building
----------

### Building
Run the command below to build an executable jar.

```
./gradlew clean build
```


## Misc.
----------

### Property file(s)
The application expects there to be a property file called "application.properties" that is located in the same directory the application is located.  If the property file it is not present, then the application will used the bundled property file. 

### Log file
The logging file properties can be set in the application's property file.

### Testing
* run the unit tests on the command line using gradle. 

```
./gradlew clean test
```

* please note all the Apache CXF SOAP web service tests that perform JNDI lookups are currently failing:

```
:test

sample.tomcat.jndi.web.SampleManagerTests > direct FAILED
    org.springframework.ws.soap.client.SoapFaultClientException at SampleManagerTests.java:73

sample.tomcat.jndi.web.SampleManagerTests > directFromBridge FAILED
    java.lang.AssertionError at SampleManagerTests.java:90

sample.tomcat.jndi.web.SampleManagerTests > factoryBean FAILED
    org.springframework.ws.soap.client.SoapFaultClientException at SampleManagerTests.java:65

sample.tomcat.jndi.web.SampleManagerTests > factoryBeanFromBridge FAILED
    org.springframework.ws.soap.client.SoapFaultClientException at SampleManagerTests.java:81

10 tests completed, 4 failed
:test FAILED
```
 

### Credit(s)
* The core functionality of this application is borrowed from the following example:
[spring-boot-sample-tomcat-jndi](https://github.com/wilkinsona/spring-boot-sample-tomcat-jndi)

* The SpringContextBridge functionality is borrowed from the following example:
[Using Spring managed Bean in non-managed object](https://blog.jdriven.com/2015/03/using-spring-managed-bean-in-non-managed-object/)
