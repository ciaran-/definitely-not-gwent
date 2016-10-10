# definitely-not-gwent
A framework for producing online CCGs

## Dependencies
* Scala
* sbt
* PostgreSQL

## Running the Sample
1. Update src/main/resources/application.conf to point the app to your Postgres db location (if you're using Postgres.app, this will be localhost and db is your User name).
2. Execute CREATE_TABLES on your db
3. `sbt run`
