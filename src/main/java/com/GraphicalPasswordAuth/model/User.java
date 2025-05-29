package com.GraphicalPasswordAuth.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Represents a user entity for the Graphical Password Authentication system.
 * 
 * Each user has a unique email and an icon-based (graphical) password,
 * along with supporting fields for encryption, password reset functionality, and security.
 */
@Entity
@Table(name = "user_data")
public class User {

    /**
     * The unique identifier for the user (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's email address (must be unique and not null).
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The user's name.
     */
    private String name;

    /**
     * The icon-based graphical password for authentication.
     */
    @Column(name = "icon_password", nullable = false)
    private String iconPassword;

    /**
     * The salt used for encrypting the icon password.
     */
    @Column(name = "salt", nullable = false)
    private String salt;

    /**
     * Encrypted representation of the icon codes used in the password grid.
     */
    @Column(name = "encrypted_icon_codes", columnDefinition = "TEXT")
    private String encryptedIconCodes;

    /**
     * Password reset token (if generated).
     */
    private String resetToken;

    /**
     * Expiration date/time for the reset token.
     */
    private LocalDateTime resetTokenExpiry;

    // Getters and Setters

    /**
     * Gets the user's ID.
     *
     * @return the user ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     *
     * @param id the user ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's email.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's icon password.
     *
     * @return the icon-based password.
     */
    public String getIconPassword() {
        return iconPassword;
    }

    /**
     * Sets the user's icon password.
     *
     * @param iconPassword the icon-based password.
     */
    public void setIconPassword(String iconPassword) {
        this.iconPassword = iconPassword;
    }

    /**
     * Gets the salt used for encryption.
     *
     * @return the salt string.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the salt used for encryption.
     *
     * @param salt the salt string.
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Gets the encrypted icon codes.
     *
     * @return the encrypted icon codes.
     */
    public String getEncryptedIconCodes() {
        return encryptedIconCodes;
    }

    /**
     * Sets the encrypted icon codes.
     *
     * @param encryptedIconCodes the encrypted icon codes.
     */
    public void setEncryptedIconCodes(String encryptedIconCodes) {
        this.encryptedIconCodes = encryptedIconCodes;
    }

    /**
     * Gets the password reset token.
     *
     * @return the reset token.
     */
    public String getResetToken() {
        return resetToken;
    }

    /**
     * Sets the password reset token.
     *
     * @param resetToken the reset token.
     */
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    /**
     * Gets the expiration time for the password reset token.
     *
     * @return the token expiry time.
     */
    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    /**
     * Sets the expiration time for the password reset token.
     *
     * @param resetTokenExpiry the token expiry time.
     */
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
