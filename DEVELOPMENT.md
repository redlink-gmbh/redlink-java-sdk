# Redlink Java SDK Development

## Unit Tests

To properly run the test suite there are few requisites need to be in place:

* At [my.redlink.io][my], create an app `test`, including `dbpedia`, `freebase` and a custom dataset named `test`.
* Copy the api key into the `src/test/resources/api.key` file.
* Run the test suite with Maven or any compatible IDE: `mvn test`
