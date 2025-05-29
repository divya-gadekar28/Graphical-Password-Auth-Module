package com.GraphicalPasswordAuth.services;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GraphicalPasswordAuth.model.User;
import com.GraphicalPasswordAuth.repository.UserRepository;

/**
 * Service class for handling core business logic related to user management,
 * including registration, login, password reset, and icon-based authentication.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    final String password = "fixedSecretKey123!";
    final String salt = "uniqueSaltValue";

    /**
     * Registers a new user by verifying the encrypted icon grid,
     * hashing the icon-based password with a salt, and storing it in the database.
     *
     * @param user the user object received from frontend
     * @return the saved {@link User} entity
     */
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        try {
            if (user.getEncryptedIconCodes() == null || user.getEncryptedIconCodes().isEmpty()) {
                throw new RuntimeException("Encrypted icon set required.");
            }

            // Decrypt encrypted icons and verify password
            String decryptedIconPassword = decryptEncryptedIconCodes(user.getEncryptedIconCodes());

            if (!decryptedIconPassword.equals(user.getIconPassword())) {
                throw new RuntimeException("Encrypted icon set does not match the icon password.");
            }

            String salt = generateSalt();
            String hashedPattern = hashPattern(user.getIconPassword(), salt);
            user.setSalt(salt);
            user.setIconPassword(hashedPattern);

            return userRepository.save(user);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing icon password", e);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting encryptedIconCodes", e);
        }
    }

    /**
     * Validates login credentials by comparing the hashed icon password.
     *
     * @param email the user's email
     * @param iconPasswordInput the icon-based password input from the user
     * @return true if credentials are valid, false otherwise
     */
    public boolean loginUser(String email, String iconPasswordInput) {
        return userRepository.findByEmail(email).map(user -> {
            try {
                String hashedInput = hashPattern(iconPasswordInput, user.getSalt());
                return hashedInput.equals(user.getIconPassword());
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }).orElse(false);
    }

    /**
     * Generates a secure reset token and sends a password reset email to the user.
     *
     * @param email the user's email address
     */
    public void generatePasswordResetToken(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = generateSecureToken();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
            userRepository.save(user);

            String resetLink = "http://localhost:8080/resetPassword.html?token=" + token;
            System.out.println("Reset link: " + resetLink);
            emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
        });
    }

    /**
     * Resets the user's password using the provided token and new icon-based password.
     *
     * @param token the password reset token
     * @param newIconPassword the new icon-based password
     * @return true if reset is successful, false otherwise
     * @throws NoSuchAlgorithmException if hashing fails
     */
    public boolean resetPassword(String token, String newIconPassword) throws NoSuchAlgorithmException {
        Optional<User> optionalUser = userRepository.findByResetToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getResetTokenExpiry() != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
                String salt = generateSalt();
                String hashed = hashPattern(newIconPassword, salt);
                user.setSalt(salt);
                user.setIconPassword(hashed);
                user.setResetToken(null);
                user.setResetTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the encrypted icon set for the specified user's email.
     *
     * @param email the user's email
     * @return Optional containing encrypted icon string or empty if not found
     */
    public Optional<String> getEncryptedIconsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getEncryptedIconCodes);
    }

    /**
     * Generates a random salt value for password hashing.
     *
     * @return base64-encoded salt string
     */
    private String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] saltBytes = new byte[16];
        sr.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Hashes the icon password using SHA-256 and a provided salt.
     *
     * @param pattern the plain icon password
     * @param salt the salt
     * @return base64-encoded hashed password
     * @throws NoSuchAlgorithmException if SHA-256 is not available
     */
    private String hashPattern(String pattern, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String combined = pattern + salt;
        byte[] hashedBytes = md.digest(combined.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    /**
     * Generates a secure random token for password reset.
     *
     * @return base64-url-encoded token string
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Decrypts the encrypted icon code string using AES-GCM.
     *
     * @param encryptedBase64 the base64-encoded encrypted string
     * @return decrypted plain string
     * @throws Exception if decryption fails
     */
    public String decryptEncryptedIconCodes(String encryptedBase64) throws Exception {
        final String password = "fixedSecretKey123!";
        final String salt = "uniqueSaltValue";

        byte[] keyBytes = deriveKeyFromPassword(password, salt);

        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

        byte[] iv = new byte[12];
        byte[] ciphertext = new byte[decoded.length - 12];
        System.arraycopy(decoded, 0, iv, 0, 12);
        System.arraycopy(decoded, 12, ciphertext, 0, ciphertext.length);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
        byte[] decrypted = cipher.doFinal(ciphertext);

        return new String(decrypted);
    }

    /**
     * Derives a 256-bit AES key from password and salt using PBKDF2.
     *
     * @param password the password string
     * @param salt the salt string
     * @return derived key bytes
     * @throws Exception if key derivation fails
     */
    private byte[] deriveKeyFromPassword(String password, String salt) throws Exception {
        javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(),
                salt.getBytes(),
                100000,
                256);
        return factory.generateSecret(spec).getEncoded();
    }
}
