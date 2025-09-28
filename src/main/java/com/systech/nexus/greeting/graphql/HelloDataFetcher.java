package com.systech.nexus.greeting.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.systech.nexus.greeting.service.HelloService;
import com.systech.nexus.common.annotation.Loggable;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class HelloDataFetcher {

    // Public GraphQL queries removed - all GraphQL operations now require authentication
    // Hello and custom greeting functionality moved to REST API at /api/public/hello

}