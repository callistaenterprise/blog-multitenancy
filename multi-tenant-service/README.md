# Multi Tenant Service

## Running the Multi Tenant Service

Build the Multi Tenant Service executable:

```
mvn package
```

then start it as an simple java application:

```
java -jar target/multi-tenant-service-0-SNAPSHOT.jar
```
or via maven
```
mvn spring-boot:run
```

## Testing the Multi Tenant Service

Insert some test data for different tenants:

```
curl -H "X-TENANT-ID: tenant1" -H "Content-Type: application/se.callista.blog.service.api.product.v1_0+json" -X POST -d '{"name":"Product 1"}' localhost:8080/products
curl -H "X-TENANT-ID: tenant2" -H "Content-Type: application/se.callista.blog.service.api.product.v1_0+json" -X POST -d '{"name":"Product 2"}' localhost:8080/products
```

Then query for the data, and verify that the data is properly isolated between tenants:

```
curl -H "X-TENANT-ID: tenant1" localhost:8080/products
curl -H "X-TENANT-ID: tenant2" localhost:8080/products
```

## Configuration

Change default port value and other settings in src/main/resources/application.yml.
