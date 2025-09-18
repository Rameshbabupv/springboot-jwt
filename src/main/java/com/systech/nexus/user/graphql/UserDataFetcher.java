package com.systech.nexus.user.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.systech.nexus.common.annotation.Loggable;
import com.systech.nexus.user.domain.User;
import com.systech.nexus.user.service.UserService;
import graphql.execution.DataFetcherResult;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GraphQL Data Fetcher for User entity operations with JWT-based security.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-01-17) - Initial implementation with basic CRUD operations
 * v1.1 (2025-09-18) - Added JWT authentication and role-based access control
 * v1.2 (2025-09-18) - Removed unused JwtTokenUtil dependency
 *
 * For complete change history: git log --follow UserDataFetcher.java
 *
 * Features:
 * - Complete CRUD operations via GraphQL with JWT authentication
 * - Role-based access control (admin, manager, user levels)
 * - Comprehensive error handling with detailed GraphQL errors
 * - Input validation and sanitization
 * - Custom data fetchers for timestamp formatting
 * - AOP logging integration for audit trails
 *
 * Security Model:
 * - Queries (read): Requires any authenticated user (nexus-user+)
 * - Create/Update: Requires manager or admin role (nexus-manager+)
 * - Delete: Requires admin role only (nexus-admin)
 *
 * All operations return DataFetcherResult to handle both success and error cases
 * gracefully in the GraphQL response format.
 *
 * @author Claude Code Assistant
 * @version 1.2
 * @since 1.0
 */
@DgsComponent
public class UserDataFetcher {

    private final UserService userService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public UserDataFetcher(UserService userService) {
        this.userService = userService;
    }

    // QUERIES (require authentication - any valid user can read)

    @DgsQuery
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "GraphQL query: get all users")
    public DataFetcherResult<List<User>> users() {
        try {
            List<User> users = userService.getAllUsers();
            return DataFetcherResult.<List<User>>newResult()
                .data(users)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to fetch users: " + e.getMessage())
                .build();
            return DataFetcherResult.<List<User>>newResult()
                .error(error)
                .build();
        }
    }

    @DgsQuery
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "GraphQL query: get user by ID")
    public DataFetcherResult<User> user(@InputArgument String id) {
        try {
            Long userId = Long.parseLong(id);
            Optional<User> user = userService.getUserById(userId);

            if (user.isPresent()) {
                return DataFetcherResult.<User>newResult()
                    .data(user.get())
                    .build();
            } else {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("User not found with id: " + id)
                    .build();
                return DataFetcherResult.<User>newResult()
                    .error(error)
                    .build();
            }
        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid user ID format: " + id)
                .build();
            return DataFetcherResult.<User>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to fetch user: " + e.getMessage())
                .build();
            return DataFetcherResult.<User>newResult()
                .error(error)
                .build();
        }
    }

    @DgsQuery
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "GraphQL query: get user by username")
    public DataFetcherResult<User> userByUsername(@InputArgument String username) {
        try {
            Optional<User> user = userService.getUserByUsername(username);

            if (user.isPresent()) {
                return DataFetcherResult.<User>newResult()
                    .data(user.get())
                    .build();
            } else {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("User not found with username: " + username)
                    .build();
                return DataFetcherResult.<User>newResult()
                    .error(error)
                    .build();
            }
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to fetch user by username: " + e.getMessage())
                .build();
            return DataFetcherResult.<User>newResult()
                .error(error)
                .build();
        }
    }

    // MUTATIONS (require elevated privileges)

    @DgsMutation
    @PreAuthorize("hasAnyRole('nexus-manager', 'nexus-admin')")
    @Loggable(description = "GraphQL mutation: create user")
    public DataFetcherResult<User> createUser(@InputArgument Map<String, Object> input) {
        try {
            String username = (String) input.get("username");
            String email = (String) input.get("email");
            String firstName = (String) input.get("firstName");
            String lastName = (String) input.get("lastName");

            // Basic validation
            if (username == null || username.trim().isEmpty()) {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Username is required")
                    .build();
                return DataFetcherResult.<User>newResult()
                    .error(error)
                    .build();
            }

            if (email == null || email.trim().isEmpty()) {
                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Email is required")
                    .build();
                return DataFetcherResult.<User>newResult()
                    .error(error)
                    .build();
            }

            User newUser = userService.createUser(username, email, firstName, lastName);
            return DataFetcherResult.<User>newResult()
                .data(newUser)
                .build();

        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to create user: " + e.getMessage())
                .build();
            return DataFetcherResult.<User>newResult()
                .error(error)
                .build();
        }
    }

    @DgsMutation
    @PreAuthorize("hasAnyRole('nexus-manager', 'nexus-admin')")
    @Loggable(description = "GraphQL mutation: update user")
    public DataFetcherResult<User> updateUser(@InputArgument String id, @InputArgument Map<String, Object> input) {
        try {
            Long userId = Long.parseLong(id);
            String username = (String) input.get("username");
            String email = (String) input.get("email");
            String firstName = (String) input.get("firstName");
            String lastName = (String) input.get("lastName");

            User updatedUser = userService.updateUser(userId, username, email, firstName, lastName);
            return DataFetcherResult.<User>newResult()
                .data(updatedUser)
                .build();

        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid user ID format: " + id)
                .build();
            return DataFetcherResult.<User>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to update user: " + e.getMessage())
                .build();
            return DataFetcherResult.<User>newResult()
                .error(error)
                .build();
        }
    }

    @DgsMutation
    @PreAuthorize("hasRole('nexus-admin')")
    @Loggable(description = "GraphQL mutation: delete user")
    public DataFetcherResult<Boolean> deleteUser(@InputArgument String id) {
        try {
            Long userId = Long.parseLong(id);
            boolean deleted = userService.deleteUser(userId);
            return DataFetcherResult.<Boolean>newResult()
                .data(deleted)
                .build();

        } catch (NumberFormatException e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Invalid user ID format: " + id)
                .build();
            return DataFetcherResult.<Boolean>newResult()
                .error(error)
                .build();
        } catch (Exception e) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Failed to delete user: " + e.getMessage())
                .build();
            return DataFetcherResult.<Boolean>newResult()
                .error(error)
                .build();
        }
    }

    // DATA FETCHERS FOR CUSTOM FIELDS

    @DgsData(parentType = "User", field = "createdAt")
    public String createdAt(DataFetchingEnvironment dfe) {
        User user = dfe.getSource();
        return user.getCreatedAt().format(dateTimeFormatter);
    }

    @DgsData(parentType = "User", field = "updatedAt")
    public String updatedAt(DataFetchingEnvironment dfe) {
        User user = dfe.getSource();
        return user.getUpdatedAt().format(dateTimeFormatter);
    }
}