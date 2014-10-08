# Redlink Java SDK Development

## Unit Tests

To properly run the test suite there are few requisites need to be in place:

* At [my.redlink.io][my], create an app 'test' including a dataset with the name 'test' too.
* Copy the api key into the src/test/resources/api.key file.
* Run the test with Maven or any compatible IDE.

Test are skipped by default, so then you need to explictly enable them:

    mvn test -DskipTests=false

