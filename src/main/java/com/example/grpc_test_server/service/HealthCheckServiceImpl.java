package com.example.grpc_test_server.service;

import com.adarsh.HealthGrpc;
import com.adarsh.HeathCheck.HealthCheckRequest;
import com.adarsh.HeathCheck.HealthCheckResponse;
import com.adarsh.HeathCheck.HealthCheckResponse.ServingStatus;
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
