```shell
docker run -d -p 9090:9090 --name grpc-container grpc-java:v1

grpcurl -proto hello.proto -d '{"message": "Hello"}' 
grpc-java-whjkce3kva-uc.a.run.app:443 HelloWorldService/hello

grpcurl \
    -proto hello.proto \
    -d '{"message": "Hello"}' \
    grpc-java-whjkce3kva-uc.a.run.app:443 \
    HelloWorld.hello
```

>{
"message": "Response from gRPC server"
}

><os.detected.classifier>windows-x86_64</os.detected.classifier>

