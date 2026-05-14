package com.gestion.empleados.domains.user.dto.response;

import com.gestion.empleados.domains.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private User.UserRole role;
}