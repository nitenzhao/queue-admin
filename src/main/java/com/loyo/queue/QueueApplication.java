package com.loyo.queue;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.loyo.queue")
@EnableSwagger2Doc
@EnableCaching
public class QueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueApplication.class, args);
    }

}
