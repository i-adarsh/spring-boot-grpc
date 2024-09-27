package com.example.grpc_test_server.service;

import com.adarsh.HelloWorldRequest;
import com.adarsh.HelloWorldResponse;
import com.adarsh.HelloWorldServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloWorldServiceImpl  extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

  @Override
  public void hello(HelloWorldRequest request,
      StreamObserver<HelloWorldResponse> responseObserver) {
    String message = request.getMessage();
    System.out.println("gRPC message: " + message);
    HelloWorldResponse helloWorldResponse = HelloWorldResponse.newBuilder()
        .setMessage("Response from gRPC server")
        .build();
    responseObserver.onNext(helloWorldResponse);
    responseObserver.onCompleted();
  }
}
