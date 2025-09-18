package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "MODERATOR")
    void createUser_ShouldCallServiceAndReturnOk() throws Exception {

        UserRequest userRequest = new UserRequest(
                "testuser",
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );

        mvc.perform(requestWithContent(
                        MockMvcRequestBuilders.post("/api/users"), userRequest))

                .andExpect(status().isOk()); // Ожидаем статус 200

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getUserById_ShouldReturnUserResponse() throws Exception {

        UUID userId = UUID.randomUUID();
        UserResponse expectedResponse = new UserResponse(
                "John",
                "Doe",
                "test@example.com",
                List.of("ROLE_USER"),
                List.of("default-group")
        );

        when(userService.getUserById(userId)).thenReturn(expectedResponse);

        mvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk()) //200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "test@example.com",
                            "roles": ["ROLE_USER"],
                            "groups": ["default-group"]
                        }
                        """));

        verify(userService).getUserById(eq(userId));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "MODERATOR")
    void hello_ShouldReturnCurrentUsername() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/users/hello")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().string("testuser"));
    }

    @Test
    void createUser_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {

        UserRequest userRequest = new UserRequest(
                "testuser",
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );

        mvc.perform(requestWithContent(
                        MockMvcRequestBuilders.post("/api/users"), userRequest))

                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {

        UserRequest userRequest = new UserRequest(
                "testuser",
                "invalid-email",
                "password123",
                "John",
                "Doe"
        );

        mvc.perform(requestWithContent(
                        MockMvcRequestBuilders.post("/api/users"), userRequest))

                .andExpect(status().isBadRequest());

        // Проверяем, что сервис НЕ был вызван из-за ошибки валидации
        verify(userService, never()).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void createUser_WithShortUsername_ShouldReturnBadRequest() throws Exception {

        UserRequest userRequest = new UserRequest(
                "a",
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );

        mvc.perform(requestWithContent(
                        MockMvcRequestBuilders.post("/api/users"), userRequest))

                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void createUser_WithShortPassword_ShouldReturnBadRequest() throws Exception {

        UserRequest userRequest = new UserRequest(
                "testuser",
                "test@example.com",
                "123",
                "John",
                "Doe"
        );

        mvc.perform(requestWithContent(
                        MockMvcRequestBuilders.post("/api/users"), userRequest))

                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequest.class));
    }
}
