package com.systech.nexus.greeting.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.systech.nexus.greeting.service.HelloService;
import com.systech.nexus.common.annotation.Loggable;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class HelloDataFetcher {

    @Autowired
    private HelloService helloService;

    @DgsQuery
    @Loggable(description = "GraphQL hello query")
    public String hello() {
        return helloService.getHelloMessage().getMessage();
    }

    @DgsQuery
    @Loggable(description = "GraphQL custom greeting query")
    public String customGreeting(String name) {
        return helloService.getCustomGreeting(name).getMessage();
    }
}