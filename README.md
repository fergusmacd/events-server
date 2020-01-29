[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=fergusmacd/events-server)](https://dependabot.com)
[![CircleCI](https://circleci.com/gh/fergusmacd/events-server.svg?style=svg)](https://circleci.com/gh/fergusmacd/events-server)

# Demo Project
This java project is for demonstrating various functionality of Spring Boot and
maven, built in circleCI. By default the server runs on port 8000 and can be started by
running a spring boot maven target.

## PreRequisites
You will need the following installed:
* [JQ](https://stedolan.github.io/jq/)
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

## Starting the Postgres Container
```bash
docker volume create --driver local \
    --opt type=none \
    --opt device=/some-absolute/path/postgres_data \
    --opt o=bind postgres_data
docker-compose -f docker-compose-core.yml build
POSTGRES_PASSWORD=some_password docker-compose  -e  -f docker-compose-core.yml up
```

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
curl -v localhost:8000/v2/api-docs | jq
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
## Spring Cloud Vault Integration
This assumes Vault is running with the following
APP_NAME=test
Mount path: `applications/${APP_NAME}/approle`
Role name: `${APP_NAME}-ro`
Role access rights:
            `applications/${APP_NAME}/approle/login` create for login
            `applications/${APP_NAME/*` read for secrets
Policy name: `${APP_NAME}-application-ro`
Vault with key value
```sh
vault kv put applications/${APP_NAME}/secrets/api-key fake-key=fake-key-value-vault
Success! Data written to: applications/${APP_NAME}/secrets/api-key
```
Start this way
```sh
mvn clean spring-boot:run -Dspring.cloud.vault.enabled=true
```

## Jaeger Distributed Tracing
This is turned on with the following dependencies
```bash
#     https://github.com/opentracing-contrib/java-spring-cloud

    <dependency>
      <groupId>io.opentracing.contrib</groupId>
      <artifactId>opentracing-spring-cloud-starter</artifactId>
      <version>0.3.1</version>
    </dependency>

#   https://github.com/opentracing-contrib/java-spring-jaeger

    <dependency>
      <groupId>io.opentracing.contrib</groupId>
      <artifactId>opentracing-spring-jaeger-cloud-starter</artifactId>
      <version>2.0.0</version>
    </dependency>
```
### Jaeger with Docker compose
Start the server up in docker-compose with the jaeger agents:
```bash

docker-compose -f docker-compose-jaeger.yml up
```

### Jaeger with Docker
If you want to go old school you can run the app and Jaeger standalone like this
 ```bash
# Create a local network, check what is there
docker network ls
docker network create jaeger_network
docker run --rm -it --name demo --network jaeger_network -p 8000:8000 mononoke/demo-app:latest
docker run --rm -it --name jaeger --network jaeger_network  -e COLLECTOR_ZIPKIN_HTTP_PORT=9411   -p 5775:5775/udp   -p 6831:6831/udp   -p 6832:6832/udp   -p 5778:5778   -p 16686:16686   -p 14268:14268   -p 9411:9411   jaegertracing/all-in-one:1.8
# note the old docker --link command is deprecated
```

Now, send some requests in, using some context spanning requests:
```bash
curl -v http://localhost:8000/chaining-employee
```

Open up the Jaeger console (http://localhost:16688/search), select the demo-app service and query for results. You will see
the incoming request, the re-direct and then the DB query.

* Couple of things to note. In the chaincontroller, the downstream request is hard coded to localhost
* If you run docker containers standalone, the application.yml uses jaeger as the host name, as per the container name


## Updating the alpine image
Run the following commands inside the demo-app container
```bash
apk add busybox-extras
apk add -u busybox
```

## Useful Links

### The code this is based on
https://spring.io/guides/tutorials/rest/

### Other useful Spring Boot resources
https://start.spring.io/
https://spring.io/guides/tutorials/bookmarks/
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html
https://www.tutorialspoint.com/spring_boot/spring_boot_rest_controller_unit_test.htm
https://www.baeldung.com/spring-boot-app-as-a-service
Hateoas examples
https://github.com/spring-projects/spring-hateoas-examples
Swagger config
https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
Actuator
https://docs.spring.io/spring-boot/docs/2.0.x/actuator-api/html/
https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-endpoints-enabling-endpoints
https://www.baeldung.com/spring-boot-actuators
Git commit plugin
https://dzone.com/articles/maven-git-commit-id-plugin
[Spring Boot Validation](https://www.baeldung.com/spring-boot-bean-validation)
[Chaos Monkey](https://www.baeldung.com/spring-boot-chaos-monkey)
[Test Containers for testing](https://www.baeldung.com/spring-boot-testcontainers-integration-test)


### Logback
https://logback.qos.ch/manual/configuration.html
