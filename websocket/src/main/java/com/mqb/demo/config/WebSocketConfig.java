package com.mqb.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 采用springboot内置容器启动时，需要配置一个bean
 * @author mqb
 * @create 2020-08-20 14:45
 */
@Configuration
public class WebSocketConfig {

    /**
     * 这个bean会自动注册使用@ServerEndPoint注解声明的websocket endpoint
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
