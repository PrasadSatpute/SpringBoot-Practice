package com.lokayukta.cims.dto;
import com.lokayukta.cims.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4-50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotNull(message = "Role is required")
    private User.Role role;

    private User.Section assignedSection;

    @Min(value = 1, message = "Table number must be between 1-18")
    @Max(value = 18, message = "Table number must be between 1-18")
    private Integer tableNo;
}