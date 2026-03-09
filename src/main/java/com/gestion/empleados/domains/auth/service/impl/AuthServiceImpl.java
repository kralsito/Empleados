package com.gestion.empleados.domains.auth.service.impl;


import com.gestion.empleados.domains.auth.error.AuthError;
import com.gestion.empleados.domains.auth.service.AuthService;
import com.gestion.empleados.domains.user.dto.request.UserDTOin;
import com.gestion.empleados.domains.user.dto.response.UserDTO;
import com.gestion.empleados.domains.user.mapper.UserMapper;
import com.gestion.empleados.domains.user.model.User;
import com.gestion.empleados.domains.user.repository.UserRepository;
import com.gestion.empleados.shared.exception.custom.UnauthorizedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void register(UserDTOin dto) {
        User user = UserMapper.MAPPER.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    public UserDTO authenticate(UserDTOin dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
            User user = userRepository.findByEmail(dto.getEmail()).orElseThrow();
            return new UserDTO(user.getId(), user.getEmail());
        }catch (Exception ex){
            throw new UnauthorizedException(AuthError.AUTH_ERROR);
        }
    }
}
