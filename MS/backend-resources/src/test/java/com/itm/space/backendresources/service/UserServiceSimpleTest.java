package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.exception.BackendResourcesException;
import com.itm.space.backendresources.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "keycloak.realm=test-realm"
})
class UserServiceSimpleTest {

    @MockBean
    private Keycloak keycloakClient;

    @MockBean
    private UserMapper userMapper;

    private UserService userService;

    private RealmResource realmResource;
    private UsersResource usersResource;
    private UserResource userResource;
    private org.keycloak.admin.client.resource.RoleMappingResource roleMappingResource;
    private MappingsRepresentation mappingsRepresentation;

    @Value("${keycloak.realm}")
    private String realm;

    @BeforeEach
    void setUp() {

        realmResource = mock(RealmResource.class);
        usersResource = mock(UsersResource.class);
        userResource = mock(UserResource.class);
        roleMappingResource = mock(org.keycloak.admin.client.resource.RoleMappingResource.class);
        mappingsRepresentation = mock(MappingsRepresentation.class);


        when(keycloakClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(any(String.class))).thenReturn(userResource);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.getAll()).thenReturn(mappingsRepresentation);


        userService = new UserServiceImpl(keycloakClient, userMapper);

        try {
            var field = UserServiceImpl.class.getDeclaredField("realm");
            field.setAccessible(true);
            field.set(userService, realm);
        } catch (Exception e) {
            fail("Не удалось установить realm: " + e.getMessage());
        }
    }

    @Test
    void createUser_WithValidRequest_ShouldCreateUserSuccessfully() {

        UserRequest userRequest = createUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );


        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(201);
        when(mockResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(mockResponse);


        assertDoesNotThrow(() -> userService.createUser(userRequest));


        verify(usersResource).create(any(UserRepresentation.class));
    }


    @Test
    void createUser_WithKeycloakError_ShouldThrowBackendResourcesException() {

        UserRequest userRequest = createUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );


        Response errorResponse = mock(Response.class);
        when(errorResponse.getStatus()).thenReturn(409);
        when(errorResponse.getStatusInfo()).thenReturn(Response.Status.CONFLICT);
        WebApplicationException keycloakException = new WebApplicationException(errorResponse);
        when(usersResource.create(any(UserRepresentation.class))).thenThrow(keycloakException);


        BackendResourcesException exception = assertThrows(
                BackendResourcesException.class,
                () -> userService.createUser(userRequest)
        );


        assertNotNull(exception.getMessage());
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserResponse() {

        UUID userId = UUID.randomUUID();


        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("testuser");
        userRepresentation.setEmail("test@example.com");
        userRepresentation.setFirstName("John");
        userRepresentation.setLastName("Doe");

        List<RoleRepresentation> roles = List.of(
                createRoleRepresentation("ROLE_USER"),
                createRoleRepresentation("ROLE_MODERATOR")
        );

        List<GroupRepresentation> groups = List.of(
                createGroupRepresentation("default-group"),
                createGroupRepresentation("admin-group")
        );

        UserResponse expectedResponse = createUserResponse(
                "John",
                "Doe",
                "test@example.com",
                List.of("ROLE_USER", "ROLE_MODERATOR"),
                List.of("default-group", "admin-group")
        );

        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(mappingsRepresentation.getRealmMappings()).thenReturn(roles);
        when(userResource.groups()).thenReturn(groups);
        when(userMapper.userRepresentationToUserResponse(userRepresentation, roles, groups))
                .thenReturn(expectedResponse);

        UserResponse result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(userResource).toRepresentation();
        verify(userResource).roles();
        verify(roleMappingResource).getAll();
        verify(mappingsRepresentation).getRealmMappings();
        verify(userResource).groups();
        verify(userMapper).userRepresentationToUserResponse(userRepresentation, roles, groups);
    }

    @Test
    void getUserById_WithError_ShouldThrowBackendResourcesException() {

        UUID userId = UUID.randomUUID();

        when(userResource.toRepresentation()).thenThrow(new RuntimeException("Пользователь не найден"));

        BackendResourcesException exception = assertThrows(
                BackendResourcesException.class,
                () -> userService.getUserById(userId)
        );

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }


    private UserRequest createUserRequest(String username, String email, String password, String firstName, String lastName) {
        try {
            // Создаем через рефлексию, так как Lombok может не работать в тестах
            Constructor<UserRequest> constructor = UserRequest.class.getDeclaredConstructor(
                    String.class, String.class, String.class, String.class, String.class);
            return constructor.newInstance(username, email, password, firstName, lastName);
        } catch (Exception ex) {
            fail("Не удалось создать UserRequest: " + ex.getMessage());
            return null;
        }
    }

    private UserResponse createUserResponse(String firstName, String lastName, String email, List<String> roles, List<String> groups) {
        try {
            // Создаем через рефлексию, так как Lombok может не работать в тестах
            Constructor<UserResponse> constructor = UserResponse.class.getDeclaredConstructor(
                    String.class, String.class, String.class, List.class, List.class);
            return constructor.newInstance(firstName, lastName, email, roles, groups);
        } catch (Exception ex) {
            fail("Не удалось создать UserResponse: " + ex.getMessage());
            return null;
        }
    }

    private RoleRepresentation createRoleRepresentation(String roleName) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        return role;
    }

    private GroupRepresentation createGroupRepresentation(String groupName) {
        GroupRepresentation group = new GroupRepresentation();
        group.setName(groupName);
        return group;
    }
}
