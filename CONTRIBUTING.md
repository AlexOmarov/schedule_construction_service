## Contributing

New branch with story slack (`SCHED-...`) as a name should be created for each new task.

Each contribution to master branch MUST include following steps:

1. Changelog update with number of each task written under the new version
2. New unit tests for each new functionality (or changed class)
3. Increment version of service (highly recommended to use [Semver standard](https://semver.org/))
4. Updates of README (if some serious changes have been made)
5. Updates of [class diagram](docs/diagram) (if new classes have been created or old integrations have been changed)
6. Updates of [data model](docs/db) (if database has been changed)
7. Updates of api specs in swagger (if web endpoints have been changed)
8. Updates of [events specs](docs/api/esb) (if esb events have been changed)
9. Updates of [Workflows schemas](docs/diagram) (if service scenarios have been changed)
