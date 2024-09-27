> HelloWorld Proto

```protobuf
syntax = "proto3";

option java_package = "com.adarsh";
option java_multiple_files = true;

message HelloWorldRequest {
  string message = 1;
}

message HelloWorldResponse {
  string message = 1;
}

service HelloWorldService {
  rpc hello(HelloWorldRequest) returns (HelloWorldResponse);
}
```

> HealthCheck Proto

```protobuf
syntax = "proto3";

option java_package = "com.adarsh";
option java_multiple_files = true;

message HealthCheckRequest {
  string service = 1;
}

message HealthCheckResponse {
  enum ServingStatus {
    UNKNOWN = 0;
    SERVING = 1;
    NOT_SERVING = 2;
    SERVICE_UNKNOWN = 3;  // Used only by the Watch method.
  }
  ServingStatus status = 1;
}

service Health {
  rpc Check(HealthCheckRequest) returns (HealthCheckResponse);

  rpc Watch(HealthCheckRequest) returns (stream HealthCheckResponse);
}
```

> pom.xml

```xml
  <properties>
    <java.version>21</java.version>
    <protobuf.version>3.23.4</protobuf.version>
    <protobuf-plugin.version>0.6.1</protobuf-plugin.version>
    <grpc.version>1.58.0</grpc.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
      <groupId>net.devh</groupId>
      <artifactId>grpc-server-spring-boot-starter</artifactId>
      <version>3.1.0.RELEASE</version>
    </dependency>

    <dependency>
      <groupId>io.grpc</groupId>
      <artifactId>grpc-stub</artifactId>
      <version>${grpc.version}</version>
    </dependency>

    <dependency>
      <groupId>io.grpc</groupId>
      <artifactId>grpc-protobuf</artifactId>
      <version>${grpc.version}</version>
    </dependency>

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.15.4</version>
    </dependency>

    <dependency>
      <!-- Java 9+ compatibility - Do NOT update to 2.0.0 -->
      <groupId>jakarta.annotation</groupId>
      <artifactId>jakarta.annotation-api</artifactId>
      <version>1.3.5</version>
      <optional>true</optional>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>

  </dependencies>

  <build>

    <extensions>
      <extension>
        <groupId>kr.motd.maven</groupId>
        <artifactId>os-maven-plugin</artifactId>
        <version>1.7.0</version>
      </extension>
    </extensions>

    <plugins>
      <plugin>
        <groupId>org.xolstice.maven.plugins</groupId>
        <artifactId>protobuf-maven-plugin</artifactId>
        <version>0.6.1</version>
        <configuration>
          <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
          <!--					<protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:osx-x86_64</protocArtifact>-->
          <!--					<os.detected.classifier>windows-x86_64</os.detected.classifier>-->
          <pluginId>grpc-java</pluginId>
          <!--					<pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:osx-x86_64</pluginArtifact>-->
          <!--					<os.detected.classifier>windows-x86_64</os.detected.classifier>-->
          <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>compile</goal>
              <goal>compile-custom</goal>
            </goals>
          </execution>
        </executions>
      </plugin>

    </plugins>
```

> Build Docker image and check the service locally

```shell
docker build -t grpc-java:v3 .

docker run -d -p 9090:9090 --name grpc-container grpc-java:v1

grpcurl --plaintext -d '{"service": "healthy and serving"}' localhost:9090 Health/Check

grpcurl --plaintext -d '{"message": "How are you?"}' localhost:9090 HelloWorldService/hello
```

> Tag Docker image and push to the Artifact Registry or Docker Hub

```shell
docker tag grpc-java:v3 asia.gcr.io/devops-hq/grpc/grpc-java:v3
docker push asia.gcr.io/devops-hq/grpc/grpc-java:v3
```

> Deploy the image in Cloud Run and get the endpoint 

```shell
ENDPOINT=$(\
  gcloud run services list \
  --project=${PROJECT} \
  --region=${REGION} \
  --platform=managed \
  --format="value(status.address.url)" \
  --filter="metadata.name=grpc-java-v2") 
ENDPOINT=${ENDPOINT#https://} && echo ${ENDPOINT}

grpcurl -proto hello.proto -d '{"message": "Hello"}' 
grpc-java-whjkce3kva-uc.a.run.app:443 HelloWorldService/hello
```

> gRPC calls made to the server

```shell
grpcurl \
    -proto src/main/proto/hello.proto \
    -d '{"message": "Hello"}' \
    grpc-java-whjkce3kva-uc.a.run.app:443 \
    HelloWorldService.hello

grpcurl \
    -proto src/main/proto/hello.proto \
    -d '{"message": "Hello"}' \
    grpc-java-v2-whjkce3kva-em.a.run.app:443 \
    HelloWorldService.hello

grpcurl \
    -proto src/main/proto/healthCheck.proto \
    -d '{"service": "healthy and serving"}' \
    grpc-java-v2-whjkce3kva-em.a.run.app:443 \
    Health.Check
```

```shell
for i in {1..5000}; do
  grpcurl -proto src/main/proto/healthCheck.proto -d '{"service": "healthy and serving"}' grpc-java-v2-whjkce3kva-em.a.run.app:443 Health.Check
  grpcurl -proto src/main/proto/hello.proto -d '{"message": "Hello"}' grpc-java-v2-whjkce3kva-em.a.run.app:443 HelloWorldService.hello
done
```