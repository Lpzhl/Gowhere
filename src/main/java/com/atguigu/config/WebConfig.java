package com.atguigu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //在这个例子中，所有 /resources/** 的 URL 请求都会被映射到文件系统上的 E:/java/xuniResoure/ 目录
        registry.addResourceHandler("/resources/**")   //
                .addResourceLocations("file:///E:/java/xuniResoure/"); //所访问的文件资源
    }
}
