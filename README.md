# Java Tech Challenge Tammana

This application is an interview calendar API. It implements endpoints for 
1. creating entities candidates and interviewers, 
2. creating their slot availability, 
3. querying common availability slots.

**Notes:**

1. Candidate name is unique.
2. Interviewer name is unique.
3. Candidate and Interviewer can have the same name.
4. The entity must be created before creating their availability.
5. Possible to fetch the list of candidates and interviewers.
6. Possible to fetch a particular candidate or interviewer by name.
7. Candidate/Interviewer can be deleted by their name.
8. Slot availability will be deleted if a candidate/interviewer gets removed.

### Tools:

- Intellij IDEA
- Postman
- Java 11

### Tech Stack:

- Java 11
- Spring Boot
- Hibernate
- Swagger
- H2 Database (in-memory)
- JUnit
- Hamcrest
- Mockito


### Setup:

- To run the application, execute the following command. But make sure you have java installed in the system
  -  java -jar java-tech-challenge-tammana-1.0-SNAPSHOT.jar
- For source code, clone project using (_**git clone https://github.com/mithildobarkar/InterviewCalendarApi.git**_)
- Open terminal in the project folder
- Run the application with:
  - _mvn clean install_
  - _mvn spring-boot:run_
- Test the application with _mvn test_ -> run all tests
- Package the application with _mvn package_
- Test using Postman
- Test using Swagger Ui

### Endpoints:

Find the API documentation at _http://localhost:8080/swagger-ui/index.html_.

For testing the API using Postman import the file in the _postman_collections_ folder. 
