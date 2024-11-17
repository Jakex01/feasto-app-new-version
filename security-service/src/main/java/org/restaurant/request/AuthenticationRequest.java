package org.restaurant.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    @Size(min = 2, max = 50, message = "Email must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must not contain special characters")
    private String email;
    @NotNull(message = "Password is mandatory")
    @Size(min = 8, max=50, message = "Password must be at least 8 characters long")
    private String password;
}
