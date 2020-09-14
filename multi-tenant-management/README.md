# Multi Tenant Management

## Overview  

This app provides a simple rest interface for dynamically adding tenants.

## Running the Multi Tenant Management Service

Build the Multi Tenant Management executable:

```
mvn package
```

then start it as an simple java application:

```
java -jar  target/multi-tenant-management-0-SNAPSHOT.jar
```
or via maven
```
mvn spring-boot:run
```

## Testing the Multi Tenant Management Service

Set up some different tenants:

```
curl -X POST "localhost:8088/tenants?tenantId=tenant1&db=tenant1&password=secret"
curl -X POST "localhost:8088/tenants?tenantId=tenant2&db=tenant2&password=secret"
```

## Configuration

Change default port value and other settings in src/main/resources/application.yml.
