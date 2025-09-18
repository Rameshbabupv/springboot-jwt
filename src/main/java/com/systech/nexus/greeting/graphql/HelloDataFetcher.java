package com.systech.nexus.greeting.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.systech.nexus.greeting.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class HelloDataFetcher {

    @Autowired
    private HelloService helloService;

    @DgsQuery
    public String hello() {
        return helloService.getHelloMessage().getMessage();
    }

    @DgsQuery
    public String customGreeting(String name) {
        return helloService.getCustomGreeting(name).getMessage();
    }
}