package com.mmall.practice.mdcstarter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class Controller {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/doTest", method = RequestMethod.GET)
    public String doTest(@RequestParam("name") String name) throws InterruptedException {
        log.info("入参 name={}",name);
        testTrace();
        log.info("调用结束 name={}",name);
        return "Hello,"+name;
    }
    private void testTrace(){
        log.info("这是一行info日志");
        log.error("这是一行error日志");
        testTrace2();
    }
    private void testTrace2(){
        log.info("这也是一行info日志");
        userService.insertUser();
    }
}
