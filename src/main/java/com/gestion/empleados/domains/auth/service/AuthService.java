package com.gestion.empleados.domains.auth.service;

import com.gestion.empleados.domains.user.dto.request.UserDTOin;
import com.gestion.empleados.domains.user.dto.response.UserDTO;

public interface AuthService {

    void register(UserDTOin dto);
    UserDTO authenticate(UserDTOin dto);
}
