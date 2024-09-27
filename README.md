```shell
docker run -d -p 9090:9090 --name grpc-container grpc-java:v1

grpcurl -proto hello.proto -d '{"message": "Hello"}' 
grpc-java-whjkce3kva-uc.a.run.app:443 HelloWorldService/hello

ENDPOINT=$(\
  gcloud run services list \
  --project=${PROJECT} \
  --region=${REGION} \
  --platform=managed \
  --format="value(status.address.url)" \
  --filter="metadata.name=grpc-java-v2") 
ENDPOINT=${ENDPOINT#https://} && echo ${ENDPOINT}
```

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

>{
"message": "Response from gRPC server"
}

><os.detected.classifier>windows-x86_64</os.detected.classifier>

```
grpcurl --plaintext -d '{"service": "healthy and serving"}' localhost:9090 Health/Check
{
"status": "SERVING"
}

grpcurl --plaintext -d '{"message": "How are you?"}' localhost:9090 HelloWorldService/hello
{
"message": "Response from gRPC server"
}
```