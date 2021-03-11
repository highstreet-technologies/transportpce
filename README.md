# TransportPCE with ONAP

## Prerequisites

  * OpenJDK 11
  * Maven 3.6
  * docker
  * docker-compose (for integration test)

## build

 * build artifacts
```
mvn clean install -DskipTests
```
  * build docker image
```
mvn clean install -f distribution/odltrpce-alpine-standalone
```
 * build gui
```
mvn clean install -f gui
```
  * build docker image
```
mvn clean install -f distribution/gui
```

This creates a docker image 'odl/transportpce' with the latest tag.

## run standalone

```
docker run -d odl/transportpce
```


## run integration test

```
cd integration/onap-ext3
```
edit ```.env``` file
```
docker-compose up -d
../bin/integration.py test 3
```

more details [here](integration/README.md)


