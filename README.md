# Schedule construction service

Service for showing PoC solutions, patterns, technologies usage.

[![Java version](https://img.shields.io/static/v1?label=Java&message=17&color=blue)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AlexOmarov_schedule_construction_service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=AlexOmarov_schedule_construction_service)

## Table of Contents

- [Features](#features)
- [Documentation](#documentation)
- [Deployment](#deployment)

## Features
* Rsocket / GRPC / HTTP / Kafka endpoints
* TLS for Rsocket
* Serialization to hessian
* R2dbc postgres with transaction support
* DLT topic
* Example of inner architecture
* Scheduling with distributed locking
* Caching
* Coroutines GRPC / RSOCKET / HTTP
* Tracing (for all endpoints), metrics, logging in json

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

## Deployment

Service can be deployed locally or using docker. Required tools:
* [Java 17 SDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Gradle >= 8](https://gradle.org/install/)

Also, there should be several side systems. Following is the list of that with related config's env names (default in brackets):
- Postgres (v. >= 15):
    - host: POSTGRES_HOST (localhost)
    - port: POSTGRES_PORT (5432)
    - name: POSTGRES_DATABASE (schedule_construction_service)
    - schema: POSTGRES_DATABASE_SСHEMA (public)
    - user: POSTGRES_USER_NAME (schedule_construction_service)
    - password: POSTGRES_PASSWORD (schedule_construction_service)
- Kafka (confluent 7.3, kafka 3.3):
    - brokers: KAFKA_BROKERS (localhost:9092)
    - stub-topic: STUB_TOPIC (stub)
- Other systems:
    - other-service-host: OTHER_SERVICE_HOST (localhost:9090)
  
В стандартной конфигурации сервис держит открытым для http соединений порт 8080 и порт 9090 для grpc соединений.
Настройку можно изменить, добавив `GRPC_PORT` и `HTTP_PORT` системные переменные.

При развертке на прод контуре должно быть развернуто не менее двух экземпляров сервиса, и STUB_TOPIC
очередь должна иметь не менее двух партиций. Время жизни сообщений - два месяца или более.

`Liveness` и `readiness` API доступны по `actuator/health/liveness` и `actuator/health/readiness` путям.  
Уровень логирования можно менять через системную переменную `logging.level.root` (info, debug, etc.).
Если нет необходимости смотреть ВСЕ debug логи, можно ограничить debug уровень конкретными пакетами,
установив переменную `logging.level.<PACKAGE_TO_LOG>`.

Метрики для Prometheus доступны по адресу `actuator/prometheus`.

### Локальная развертка

Необходимо установить системную переменную spring.profiles.active=dev.
Приложение будет доступно по `11000` порту (http) и `12000` порту (grpc)

Можно разворачивать либо конфигурацией из IntellijIDEA, либо вручную командой в терминале
```bash
$ .\gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Локальный Docker

Необходимо использовать `docker-compose-local.yml` чтобы собрать образ и стартовать контейнер.
В стандартной конфигурации сервис будет использовать `application-dev.yml` файл свойств.


## Публикация

Для каждой новой версии сервиса (можно завязать выкат на релиз, можно ка каждый коммит в мастер и т.д.)
необходимо проводить публикацию API модуля в репозитории пакетов maven.
Для этого используется задача publish
```
.\gradlew publish
```
Для ее корректной работы необходимо указать несколько системных переменных:
- CI_ARTIFACT_REPO_HOST - хост nexus репозитория
- CI_ARTIFACT_REPO_NAME - логин для авторизации в nexus
- CI_ARTIFACT_REPO_TOKEN - пароль для авторизации в nexus

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

Когда проект собирается с использованием `build` задачи gradle detekt и ktlint проверки проходят автоматически,
и detekt xml отчет формируется по путям `schedule_construction_app/build/report/detekt`
и `schedule_construction_api/build/report/detekt`. Также есть возможность запускать проверки вручную командой
```
.\gradlew detekt
```

Для получения процента покрытия необходимо:
```
.\gradlew test jacocoTestReport coverage
.\gradlew sonar -D"sonar.host.url"="https://sonarcloud.io" -D"sonar.login"="YOUR_LOGIN" -D"sonar.projectKey"="KEY" -D"sonar.organization"="ORG" 
```
Отчет по покрытию будет сгенерирован в `schedule_construction_app/build/report/jacoco`
Кроме того, при вызове sonar с помощью gradle задачи сгенерированный detekt отчет будет добавлен к анализу.

## API

### Web endpoints
All the service's web endpoints specification can be found in swagger page /swagger-ui.html
### Esb events
All the service's events specification can be found on [events folder](docs/api/esb)
