# Multi Tenant Management

## Overview  

This app provides a simple rest interface for dynamically adding tenants
and allocate them to shards, creating new shards as necessary.

## Running the Multi Tenant Management Service

Build the Multi Tenant Management executable:

```
mvn package
```

then start it as an simple java application:

```
java -jar target/multi-tenant-management-0-SNAPSHOT.jar
```
or via maven
```
mvn spring-boot:run
```

## Testing the Multi Tenant Management Service

Set up some different tenants:

```
curl -X POST "localhost:8088/tenants?tenantId=tenant1"
curl -X POST "localhost:8088/tenants?tenantId=tenant2"
curl -X POST "localhost:8088/tenants?tenantId=tenant3"
```

Since the `multitenancy.shard.max-tenants` is set to `2`, this will create 2 shards, the first
one hosting tenant1 and tenant2, and the second hosting tenant3.

## Configuration

Change default port value and other settings in src/main/resources/application.yml.
