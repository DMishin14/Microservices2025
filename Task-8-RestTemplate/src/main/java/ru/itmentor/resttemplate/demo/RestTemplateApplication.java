package ru.itmentor.resttemplate.demo;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.itmentor.resttemplate.demo.model.User;

import java.util.List;

public class RestTemplateApplication {

    private static final String API_BASE_URL = "http://94.198.50.185:7081/api/users";
    private static final String SESSION_COOKIE_HEADER = "Set-Cookie";
    
    private final RestTemplate restTemplate;
    private String sessionId;

    public RestTemplateApplication() {
        this.restTemplate = new RestTemplate();
    }

    public static void main(String[] args) {
        RestTemplateApplication app = new RestTemplateApplication();
        app.executeAllOperations();
    }

    public void executeAllOperations() {
        
        try {
            getAllUsers();

            createUser();

            updateUser();

            deleteUser();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllUsers() {
        
        ResponseEntity<User[]> response = restTemplate.getForEntity(API_BASE_URL, User[].class);

        List<String> cookies = response.getHeaders().get(SESSION_COOKIE_HEADER);
        if (cookies != null && !cookies.isEmpty()) {
            // Extract JSESSIONID from cookie
            String cookie = cookies.get(0);
            if (cookie.contains("JSESSIONID=")) {
                sessionId = cookie.substring(cookie.indexOf("JSESSIONID="), cookie.indexOf(";"));
                System.out.println("Session ID saved: " + sessionId);
            }
        }
        
        User[] users = response.getBody();
        System.out.println("Users received: " + (users != null ? users.length : 0));
        
        if (users != null) {
            for (User user : users) {
                System.out.println("  " + user);
            }
        }
    }

    private void createUser() {
        
        User newUser = new User(3L, "James", "Brown", (byte) 25);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (sessionId != null) {
            headers.set("Cookie", sessionId);
        }
        
        HttpEntity<User> request = new HttpEntity<>(newUser, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(API_BASE_URL, request, String.class);
        
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        String responseBody = response.getBody();
        if (responseBody != null && responseBody.length() > 0) {
            System.out.println("First part of code received: " + responseBody);
        }
    }

    private void updateUser() {
        System.out.println("\n3. Updating user with id=3...");
        
        User updatedUser = new User(3L, "Thomas", "Shelby", (byte) 25);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (sessionId != null) {
            headers.set("Cookie", sessionId);
        }
        
        HttpEntity<User> request = new HttpEntity<>(updatedUser, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            API_BASE_URL, 
            HttpMethod.PUT, 
            request, 
            String.class
        );
        
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        String responseBody = response.getBody();
        if (responseBody != null && responseBody.length() > 0) {
            System.out.println("Second part of code received: " + responseBody);
        }
    }

    private void deleteUser() {
        System.out.println("\n4. Deleting user with id=3...");
        
        HttpHeaders headers = new HttpHeaders();
        if (sessionId != null) {
            headers.set("Cookie", sessionId);
        }
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        String deleteUrl = API_BASE_URL + "/3";
        
        ResponseEntity<String> response = restTemplate.exchange(
            deleteUrl, 
            HttpMethod.DELETE, 
            request, 
            String.class
        );
        
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());

        String responseBody = response.getBody();
        if (responseBody != null && responseBody.length() > 0) {
            System.out.println("Third part of code received: " + responseBody);
        }
    }
}