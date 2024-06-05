### Technical Task - Raidiam ###

This README provides an overview of the project, including its structure, requirements, and instructions for running the tests. It also highlights the technologies used and the overall approach taken to complete the challenge.

I hope you can easily navigate through this project. It follows a simple structure that I describe in more detail below in this readme.

Best regards,
Pedro Pantaleão - pedrohbps@gmail.com

### Requirements ###

* Java 8

* Maven

### Stack Used ###

* Testing Frameworks: RestAssured, JUnit
* Code Utilities: Lombok
* Build Tool: Maven
* Programming Language: Java

### Project Structure ###

* Common: Common functions that can be used in most test classes.
* Models: Data models that can be used in the project.
* Persistence: DB interface;
* Runners: Runners for the test classes.
* Api: The directory of the tests.
* Utils: Classes for configurations and other thing

### Running this project ###

In some cases, depending on your local settings, it may be necessary to run the project with administrator permissions by adding "sudo" before the commands below.

```
mvn clean test -Dtest=ApiRunner -Denvironment=local
```

### To-Dos ###

* Create a Dockerfile for containerized execution.
* Develop a Makefile for streamlined build and test tasks.
* Refactor the consent workflow into a dedicated class.
* Eliminate hardcoded values and implement configuration management.

### Observations ###

* A persistence folder with interfaces for database connection is included, demonstrating familiarity with database integration in API testing automation.
* Hardcoded variables are present, highlighting the need for proper configuration management in a real-world project.
* A Jenkinsfile is provided for CI/CD integration, but further review and refinement are recommended