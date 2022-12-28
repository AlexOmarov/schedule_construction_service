# Schedule construction service

Service for storing, forming and managing academic schedule.

[![Java version](https://img.shields.io/static/v1?label=Java&message=17&color=blue)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)

## Table of Contents

- [Introduction](#introduction)
- [Documentation](#documentation)
- [Features](#features)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [API](#requirements)

## Introduction

Schedule construction service is responsible for managing academic schedule of the middle school classes.

## Documentation

All the service's documentation can be found in it's 
[docs folder](docs)
There you can find:

- Data model
- Class diagram
- Sequence diagram for all the business flows which are performed by service
- Description of all service integrations
- Descriptions of service API's, incoming and outcoming esb events
- Some additional diagrams for better understanding of underlying processes

## Features
 * Forming schedule for set of classes

## Requirements

The application can be run locally or in a docker container, 
the requirements for each setup are listed below.

### Local

* [Java 17 SDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Gradle >= 7](https://gradle.org/install/)

### Docker

* [Docker](https://www.docker.com/get-docker)

## Quick Start

Application will run by default on port `11000`

Configure the port by changing `server.port` in __application.yml__


### Run Local

Depending on which environment you want to launch the service you should choose
spring profile:
* To launch service in dev contour, user `dev` profile

You can run application either via Intellij launch configuration (preferred way) or
manually
```bash
$ .\gradlew bootRun --args='--spring.profiles.active=dev'
```

### Run Docker

Use `docker-compose-local.yml` to build the image and create a container.
Note, that by default container will run using `application-dev.yml`

### Run code quality assurance tasks

If you want to get total coverage and sonar analysis with local changes, then you should run following tasks:
```
.\gradlew test jacocoTestReport coverage
.\gradlew sonarqube -D"sonar.host.url"="https://sonarcloud.io" -D"sonar.login"="b9694e03ee5fbf20e87d643ef0efccc104332567"
```
Then, jacoco test report with coverage will be generated on local machine in build folder
and sonar analysis will take place on server and will be visible on sonarcloud instance.
Also, it is recommended to install [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) Intellij plugin for integration of code
quality analysis more native-like
Also, there is a possibility to configure jacoco coverage as a replace for common Idea coverage
analyzer (it's optional)

## API

### Web endpoints
All the service's web endpoints specification can be found in swagger page /swagger-ui.html
### Esb events
All the service's events specification can be found on [events folder](docs/api/esb)
