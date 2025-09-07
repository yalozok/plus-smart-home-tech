package ru.yandex.grpc.analyzer.configuration;

import org.springframework.context.annotation.Configuration;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Configuration
public class GrpcConfig {
    
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;
    
    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub() {
        return hubRouterStub;
    }
}
