package com.gestion.empleados.domains.auth.service;

import com.gestion.empleados.domains.user.dto.request.UserDTOin;
import com.gestion.empleados.domains.user.dto.response.UserDTO;
import com.gestion.empleados.domains.user.model.User;

public interface AuthService {

    void register(UserDTOin dto, User.UserRole role);
    UserDTO authenticate(UserDTOin dto);
}
