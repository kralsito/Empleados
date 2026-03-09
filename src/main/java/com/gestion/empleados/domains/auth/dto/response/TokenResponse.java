package com.gestion.empleados.domains.auth.dto.response;

import com.gestion.empleados.domains.user.dto.response.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private UserDTO user;
}
