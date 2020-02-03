[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fergusmacd_events-server&metric=alert_status)](https://sonarcloud.io/dashboard?id=fergusmacd_events-server)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=fergusmacd/events-server)](https://dependabot.com) [![CircleCI](https://circleci.com/gh/fergusmacd/events-server/tree/master.svg?style=svg)](https://circleci.com/gh/fergusmacd/events-server/tree/master)
[![CircleCI](https://circleci.com/gh/fergusmacd/events-server.svg?style=svg)](https://circleci.com/gh/fergusmacd/events-server)
# Events Server
Repo that contains back end code for events builder


## PreRequisites
You will need the following installed:
* <a href="https://stedolan.github.io/jq/">JQ</a>
* Java 8+
* Maven 3+
* lombox plugin if using Intellij

## Spring Boot Features
The spring boot features leveraged are:
* RestControllers with persistence in H2
* Generated Swagger UI
* HATEOAS JSON responses
* Response Controller for logging version and header details
* Example test cases for controllers
* Spring Actuator
* Jaeger Tracing, with root span

## Maven Features
For details see the pom.xml. The maven features leveraged are:
* Site generation
* License management, and license header updates with The Apache Software License, Version 2.0
* Standard maven project information
* Maven project reports
    1. Jacoco - code coverage
    2. CPD - duplicate code
    3. PMD - coding rules)
    4. SpotBugs - bug detection
    5. Gitlog - user changes
    6. Changelog - files that changed by commit
    7. File Activity - number of changes by file
    8. Developer Activity - changes by developer
    9. SureFire Report - junit test results
    10. Checkstyle - stylistic deviation
    11. Dependency Analysis - third party dependencies
    12. Dependency Check - OWASP CVE check
    13. Tag List - TODO and Fixme
    14. Licenses - third party license list
* Release Management

## Building
The project can be built with the following targets
```bash
mvn clean install
# to generate the site and reports
mvn site install -P reporting
```

## Docker build
mvn clean package dockerfile:build

## Running and Testing
To run the project and test the responses
```bash
# Start the server
mvn clean spring-boot:run

### Curl commands, in another window

## Check EmployeeController
# Return all employees
curl -v http://localhost:8000/employees | jq

# Return employee #1
curl -v http://localhost:8000/employees/1 | jq

# Create new employee
curl -v -X POST localhost:8000/employees -H 'Content-Type:application/json' -d '{"name": "Samwise Gamgee", "role": "ring bearer"}'

# Replace/update employee
curl -v -X PUT localhost:8000/employees/3 -H 'Content-Type:application/json' -d '{"name": "Samwise Gamgee", "role": "ring bear"}'

# Delete employee
curl -v -X DELETE localhost:8000/employees/3

## Check ResponseController

# See all request information
curl -H "Accept-Charset: utf-8" -v localhost:8000/headers

# Get caller address
curl -H "Accept-Charset: utf-8" -v localhost:8000/print-caller-address

# See version info - from git.properties
curl -v localhost:8000/version

## Swagger Info

# View complete swagger json
curl -v localhost:8000/v3/api-docs | jq
```

To view the swagger UI go to http://localhost:8000/swagger-ui.html

## Spring Actuator V2
Spring actuator v2 disables most endpoints by default. The application.yaml overrides some
of the endpoints. They can be queried in the following ways
```bash

## Spring Actuator

##
# See what endpoints are available
curl -v http://localhost:8000/actuator | jq

# Get the app status (up or down)
curl -v localhost:8000/actuator/health

# Get the app info - returns build information
curl -v localhost:8000/actuator/info

# Get the app metrics
curl -v localhost:8000/actuator/metrics
# Returns a list of names that can be queried, e.g.
curl -v localhost:8000/actuator/metrics/jvm.memory.max

# List of beans
curl http://localhost:8000/actuator/beans -i -X GET

# Environment variables
curl -v localhost:8000/actuator/env

```

## Updating the alpine image
Run the following commands inside the demo-app container
```bash
apk add busybox-extras
apk add -u busybox
```

## Useful Links

## Other useful Spring Boot resources
https://start.spring.io/
https://spring.io/guides/tutorials/bookmarks/
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html
https://www.tutorialspoint.com/spring_boot/spring_boot_rest_controller_unit_test.htm
https://www.baeldung.com/spring-boot-app-as-a-service
Hateoas examples
https://github.com/spring-projects/spring-hateoas-examples
Swagger config
[Spring Docs](https://springdoc.github.io/springdoc-openapi-demos/faq.html)
Actuator
https://docs.spring.io/spring-boot/docs/2.0.x/actuator-api/html/
https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-endpoints-enabling-endpoints
https://www.baeldung.com/spring-boot-actuators
Git commit plugin
https://dzone.com/articles/maven-git-commit-id-plugin
[Spring Boot Validation](https://www.baeldung.com/spring-boot-bean-validation)
[Chaos Monkey](https://www.baeldung.com/spring-boot-chaos-monkey)

### Logback
https://logback.qos.ch/manual/configuration.html
