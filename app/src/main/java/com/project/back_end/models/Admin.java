package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Entity
@Table(name = "admins")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
