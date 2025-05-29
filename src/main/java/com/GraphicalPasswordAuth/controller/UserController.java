package com.GraphicalPasswordAuth.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GraphicalPasswordAuth.model.User;
import com.GraphicalPasswordAuth.services.UserService;

/**
 * REST controller for handling user-related operations such as signup, login,
 * password reset, and retrieving encrypted icon sets for graphical password authentication.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user with an icon-based password.
     *
     * @param user The user details submitted in the request body.
     * @return ResponseEntity containing the registered user or an error message.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            User saved = userService.registerUser(user);
            System.out.println("Received user: " + user.getName() + ", " + user.getEmail() + ", " + user.getIconPassword());
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /**
     * Authenticates a user using their email and icon-based password.
     *
     * @param email The user's email address.
     * @param iconPassword The user's graphical password.
     * @return A success message or a 401 Unauthorized response.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String iconPassword) {
        boolean success = userService.loginUser(email, iconPassword);
        return success 
            ? ResponseEntity.ok("Login successful") 
            : ResponseEntity.status(401).body("Invalid credentials");
    }

    /**
     * Initiates the password reset process by generating a reset token.
     *
     * @param payload A JSON payload containing the user's email address.
     * @return A generic response indicating a reset email has been sent (if applicable).
     */
    @PostMapping("/request-form")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        userService.generatePasswordResetToken(email);
        return ResponseEntity.ok("If email exists, password reset link sent.");
    }

    /**
     * Resets the user's password using a secure token and a new icon-based password.
     *
     * @param payload A JSON payload with the reset token and new graphical password.
     * @return A success or failure response depending on the validity of the token.
     * @throws NoSuchAlgorithmException If the encryption algorithm is unsupported.
     */
    @PostMapping("/reset-form")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) throws NoSuchAlgorithmException {
        String token = payload.get("token");
        String newIconPassword = payload.get("newIconPassword");
        boolean result = userService.resetPassword(token, newIconPassword);
        return result
            ? ResponseEntity.ok("Password reset successful.")
            : ResponseEntity.status(400).body("Invalid or expired token.");
    }

    /**
     * Retrieves the encrypted icon set for a given user's email.
     * Used to render the graphical login grid.
     *
     * @param email The user's email address.
     * @return The encrypted icon set or a 404 error if not found.
     */
    @GetMapping("/encrypted-icons")
    public ResponseEntity<?> getEncryptedIcons(@RequestParam String email) {
        return userService.getEncryptedIconsByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(404).body("User not found or no icon set"));
    }
}
