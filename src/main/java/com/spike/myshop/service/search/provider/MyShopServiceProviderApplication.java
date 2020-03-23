package com.spike.myshop.service.search.provider;

import com.alibaba.dubbo.container.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "com.spike.myshop")
@EnableHystrix
@EnableHystrixDashboard
@MapperScan(basePackages = "com.spike.myshop.service.search.provider.mapper")
public class MyShopServiceProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyShopServiceProviderApplication.class, args);
        Main.main(args);
    }
}
