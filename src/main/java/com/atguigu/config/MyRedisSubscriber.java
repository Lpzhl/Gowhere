package com.atguigu.config;

import org.springframework.stereotype.Component;

@Component
public class MyRedisSubscriber {

        public void receiveMessage(String message) {
            // 处理接收到的消息
            System.out.println("接收到消息: " + message);
        }
    }