package com.example.grpc_test_server.service;

import com.adarsh.HealthCheck.HealthCheckRequest;
import com.adarsh.HealthCheck.HealthCheckResponse;
import com.adarsh.HealthCheck.HealthCheckResponse.ServingStatus;
import com.adarsh.HealthGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HealthCheckServiceImpl extends HealthGrpc.HealthImplBase {

  @Override
  public void check(HealthCheckRequest request,
      StreamObserver<HealthCheckResponse> responseObserver) {
    System.out.println(request.getService());
    HealthCheckResponse healthCheckResponse = HealthCheckResponse.newBuilder()
        .setStatus(ServingStatus.SERVING)
        .build();
    responseObserver.onNext(healthCheckResponse);
    responseObserver.onCompleted();
  }
}
