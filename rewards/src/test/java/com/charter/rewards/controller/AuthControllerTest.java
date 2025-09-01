
package com.charter.rewards.controller;

import com.charter.rewards.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test 1: Successful Authentication
    @Test
    @DisplayName("Should generate JWT token when valid username and password are provided")
    void testGenerateToken_Success() throws Exception {
        AuthController.AuthRequest request = new AuthController.AuthRequest();
        request.setCustName("Jack");
        request.setPhoneNo("9978543210");

        // Mock AuthenticationManager to succeed
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("Jack", "9978543210")
        )).thenReturn(null);

        // Mock JWT token generation
        when(jwtUtil.generateToken("Jack")).thenReturn("mock-jwt-token");

        String result = authController.generateToken(request);

        assertEquals("mock-jwt-token", result);
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken("Jack", "9978543210"));
        verify(jwtUtil, times(1)).generateToken("Jack");
    }

    // ✅ Test 2: Invalid Username/Password
    @Test
    @DisplayName("Should throw exception when invalid username or password is provided")
    void testGenerateToken_InvalidCredentials() {
        AuthController.AuthRequest request = new AuthController.AuthRequest();
        request.setCustName("Jack");
        request.setPhoneNo("wrongpassword");

        // Mock AuthenticationManager to throw BadCredentialsException
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("Jack", "wrongpassword")
        )).thenThrow(new BadCredentialsException("Invalid credentials"));

        Exception exception = assertThrows(Exception.class, () -> {
            authController.generateToken(request);
        });

        assertEquals("Invalid username or password", exception.getMessage());
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken("Jack", "wrongpassword"));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    // ✅ Test 3: Unexpected Error from AuthenticationManager
    @Test
    @DisplayName("Should propagate unexpected error from AuthenticationManager")
    void testGenerateToken_UnexpectedError() {
        AuthController.AuthRequest request = new AuthController.AuthRequest();
        request.setCustName("Jack");
        request.setPhoneNo("9978543210");

        // Mock unexpected RuntimeException
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("Jack", "9978543210")
        )).thenThrow(new RuntimeException("Unexpected error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authController.generateToken(request);
        });

        assertEquals("Unexpected error", exception.getMessage()); // Now matches reality
        verify(jwtUtil, never()).generateToken(anyString());
    }
}


