curl http://localhost:8080/api/health | jq .
Output
```bash
 % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    15    0    15    0     0   1537      0 --:--:-- --:--:-- --:--:--  1666
{
  "status": "UP"
}
```

  - Hello API: http://localhost:8080/api/hello
  - Health Check: http://localhost:8080/api/health
  - H2 Console: http://localhost:8080/h2-console

 ok let us create hello world graphQL , if you need documents use REF mcp server, also please update your memory in serena

# Gradle 

# Things in todolist
1. keycloak
2. Create graph tesing , with user example 
3. Run springboot in dev mode 
4. implemant AOP 


mvn spring-boot:run -Dspring-boot.run.profiles=dev

 # Simple hello query
  curl -X POST http://localhost:8080/graphql \
    -H "Content-Type: application/json" \
    -d '{"query":"{ hello }"}'
  # Returns: {"data":{"hello":"Hello, World!"}}

  # Custom greeting query  
  curl -X POST http://localhost:8080/graphql \
    -H "Content-Type: application/json" \
    -d '{"query":"{ customGreeting(name: \"Claude\") }"}'
  # Returns: {"data":{"customGreeting":"Hello, Claude!"}}


# Graphql multiquery testing

  {
    hello
    customGreeting(name: "GraphQL")
  }
  ```output
{
  "data": {
    "hello": "Hello, World!",
    "customGreeting": "Hello, GraphQL!"
  }
}
  ```

Testing CURD using graphql
1. CREATE User

  mutation {
    createUser(input: {
      username: "johndoe"
      email: "john@example.com"
      firstName: "John"
      lastName: "Doe"
    }) {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  mutation {
    createUser(input: {
      username: "janesmith"
      email: "jane@example.com"
      firstName: "Jane"
      lastName: "Smith"
    }) {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  2. QUERY Users

  Get all users:
  query {
    users {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  Get user by ID:
  query {
    user(id: "1") {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  Get user by username:
  query {
    userByUsername(username: "johndoe") {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  3. UPDATE User

  mutation {
    updateUser(id: "1", input: {
      firstName: "Johnny"
      lastName: "Smith"
    }) {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  mutation {
    updateUser(id: "1", input: {
      email: "johnny.smith@example.com"
      firstName: "Jonathan"
    }) {
      id
      username
      email
      firstName
      lastName
      createdAt
      updatedAt
    }
  }

  4. DELETE User

  mutation {
    deleteUser(id: "2")
  }




Please tell me your role: 
1. are you updating your memory in serena mcp 
2. do you remember that we can use REF mcp for documentations 
3. are you desiging and working on Monolithic Modular frame work 
4. are you following TDD , RED, Green and BLUE rule
5. Changelog 


Questions:
1. where do we keep changelog about the class. I usually keep on the top of the class about the change log. so that we know what changes the class has gone thorugh . Please let me know your thinking and think hard 



mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn clean compile

curl http://localhost:8080/api/hello/public
curl -m 5 http://localhost:8080/actuator/health
curl -m 5 http://localhost:8090/realms/systech/.well-known/openid_configuration | jq '.issuer'
curl -v http://localhost:8090/realms/systech/.well-known/openid_configuration
can 


mvn test -Dtest="com.systech.nexus.company.*"
mvn compile -q
