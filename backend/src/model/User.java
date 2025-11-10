package model;

import java.time.Instant;

public class User {
    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final Instant createdAt;
    private final Instant updatedAt;

    public User(Long id, String email, String firstName, String lastName, String phone, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}