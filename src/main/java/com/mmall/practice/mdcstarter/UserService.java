package com.mmall.practice.mdcstarter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    @Async("MyExecutor")
    public void insertUser(){
        log.info("我是子线程---");
    }
}
