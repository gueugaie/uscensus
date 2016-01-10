US Census sample explorer
===

This project is a SpringBoot / AngularJS / Bootstrap project. Its goal is to provide a browser-based single page application for navigating a sample of the US Census Bureau data. More precisely, to provide a mean age aggregate on its population database, based on any available criterion.

This readme discusses : 

 - Implementation considerations, such as considered, but not implemented design choices
 - This sample app's structure and final architecture
 - The deployment instructions

#Deployment

##Requirements
Java8, maven 3.0.3, and a decent browser (IE11 works) are required to build and run the app.

If the maven repository is not yet loaded with recent Spring versions, the initial setup phase will download many many dependencies, so a broadband connection is needed.

Please ask for a pre-built package if necessary.

## Database installation
No step is required to obtain the database. It is packaged as an internal dependancy of the app, via an internal virtual maven repository.
The database is loaded directly from the classpath, and the sqlite runtime is embedded inside the JDBC driver for Windows, Linux and MacOS plateforms.


## Running from the IDE
This project is a maven project and should be runnable for any IDE such as Eclipse or IntelliJ. To run, please import a clone of this project, make sure the project and its Maven nature are detected by the IDE, then select and run the `UscensusApplication` class.

The tomcat default port of 8080 will be used.

The index page is http://localhost:8080/index.html

## Running from Maven
The `mvn spring-boot:run` command should start the app. 

The tomcat default port of 8080 will be used.

The index page is http://localhost:8080/index.html

## Packaging a runnable JAR
The standard `mvn package` command should create a runnable jar with all dependencies in the target directory. You might want to skip test using the `-Dmaven.test.skip=true` command line flag.

Running the app the is as simple as `java -jar uscensus-VERSION.jar`.


# Implementation considerations

## SQLite database limitations
SQLite is a cool multiplatform database engine and format, but not optimized for server-side concerns (esp. parallel access). Given the limited nature of this sample, we stuck to SQLite, but would have been better served in terms of lightness (SQLite for Java brings with it all native implementations of the engine), speed and embedability with, say Derby of H2.

## DB Access performance
The limited size of the dataset (a few MBs), and the predictability of our usage pattern could advocate for a full in-memory caching of either the dataset, or all its possible results. Considering our mostly "full-table-scan" use-case, this would be a necessary perf boost for any kind of serious usage.

A possible implementation of our main service interface using these principles is offered in the unit tests (`UscensusApplicationTests`), but the standard "on demand SQL generation" implementation of the service is used in the main project.

A caching layer is a possibility (Spring `@Cacheable` annotation to the rescue), but was not factored in (it would involve other decisions out of this test's scope, such as max size, eviction policy, TTL, ...).

## Extensibility
Should the "age" column we're averaging on be a static configuration parameter ? Should the 100 page size be a per-request parameter ? Should the "us_census_learn" table name be hardcoded ?

For this implementation, I decided not to hardcode anything on the server side (the main service interface accepts all this as variables). It is the frontend that actually performs : table and column names selection and paging decisions.

The server sides still performs on the fly db table / column names discovery to inform the frontend of all valid choices. 

## Security considerations
This pattern of extensibility offers the possibility for SQL Injection if not secured.
The use of `PreparedStatement`'s variable binding, the usual Java go-to solution for SQLInjection safety, is not of any help here, seeing we would need to bind elements from the `SELECT` and `FROM` clauses, which is not allowed by the API.

Well actualy, we're safe on this one, because :

  1. Every access to the business interface is secured by a validation of parameters against what actually exists in the database
  2. The DB is readOnly, as it is served from the classpath

I did not implement, time permitting, additional security checks for this, but could easily have added : AOP interception for the validation of table/column names. As the code stands, it is the responsibility of the caller of DAO interfaces to check the validity of its parameters. Although a simple service allows that, the fact that the caller should take care of validation is error prone.

## Production grade architecture
Time permitting again, the project should have been architectured a bit differently, namely :

1. Integral separation of DTO vs Domain objects, whereas here, the main Domain object has been replaced by the DTO version of it (we don't really need "Person" objects, only a 3-tuple data container)
2. All services described as interfaces
3. A clear distinction of domain vs business services
4. A coherent approach to exception handling in the Rest controllers (catching exception and refactoring them to front-end readable error codes)
5. Frontend label internationalisation
6. A fair amount of server side Logging
7. Yeah... ng and java tests too.

But for a < 8 hours project, something has to give.

## Alternatives to Spring boot ?
Yeah, I would have done a Node.js or Play! Scala version of it, if I could have had the full weekend to spend on it :).

#App architecture

## Backend Architecture

### Backend framework
The backend is a SpringBoot application. This is kind of a very heavyweight approach, but for prototyping, it's just too easy to consider going down at the servlet / JDBC API level.
This means the app uses an embedded Tomcat, serving a full blown Servlet compliant web app.

### Domain, DAO, DB connectivity

The domain object for the table is not materialized, only two classes are used at this level: a paging abstraction, `ResultPage`, and a portable data object `DataAverage`, that essentially is a 3-tuple, all in the `domain` package.

The DAO is described as an interface, to allow for AOP-style caching, but it essentially is a plain JDBC service allowing three calls :

1.  List table names from the db
2.  List column names for any table in the db
3.  Get a page of `DataAverage` instances

This is in the `dao` package.

Spring's JDBC template provides the whole DB abstraction (connection pooling, transaction handling at the service level, ...). See the `application.properties` file for more on this.

### Business services
Business services come in two flavors inside the `srv` package.

`ConfigurationService` was originally designed to hold a reference to static configuration data (such as the name of the table to operate on, the name of the column to average on). This part has been deleted in favor of a generic implementation on the DAO side.
The other use of this service is as a gateway to access the database description, and allow to :

1. Get the name of all tables, and all columns, available in the DB
2. Provide security level validation that a user request's matches the actual DB description, so that we may not be SQL injected.

`AverageService` is the main working service of our implementation, validating and performing user requests of data averages.

Spring takes care of transaction management at this level, courtesy of `@Service`

### DTO Objects
The `dto` package holds the classes that are used for our backend REST API. These are POJOs, enough said.
Once again, the reuse of domain objects inside the DTOs is a prototype shortcut, and a horrible one that I am aware of.

### REST controllers
The `rest` package exposes the business services, pretty straight-forwardly.

## Frontend architecture

The frontend is a simple angular/bootstrap app. Given it's simplicity there is not much more to say about it, it's a single module / single controler Angular App.

### Call patterns
The app's bootstrap is a call to the REST service for getting all available tables in the DB (it could have been injected inside the server side generated HTML, instead of coming through REST, yeah, but here it's generic). 

When a table is selected, another REST call is made to retreive the description of the table (i.e all column names).

When a column is selected, the first page of averaged data is fetched.

A pagination controller is used to navigate through the results.

### Style
Not much care has been given to the style, for time constraints essentially. Once the bootsrap framework is in place, it should be easy to add some rapidly, but still...

## Test
Only one test is proposed: one that compares the SQL aggregation (done on the DB side) to one made on the Java-side.
This incidently validates an obvious possible perf improvement (as was noted in the introduction), one based on the precomputation of all possible results just as the test does, only wrapped inside a `USCensusDAO` implementation.


Thanks for your time.