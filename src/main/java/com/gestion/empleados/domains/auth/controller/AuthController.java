package com.gestion.empleados.domains.auth.controller;

import com.gestion.empleados.domains.auth.dto.response.TokenResponse;
import com.gestion.empleados.domains.auth.service.AuthService;
import com.gestion.empleados.domains.user.dto.request.UserDTOin;
import com.gestion.empleados.domains.user.dto.response.UserDTO;
import com.gestion.empleados.domains.user.model.User;
import com.gestion.empleados.shared.config.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth Endpoints")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDTOin dto) {
        authService.register(dto, User.UserRole.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/users")
    @Operation(summary = "Da de alta un usuario común", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Void> createUser(@RequestBody UserDTOin dto) {
        authService.register(dto, User.UserRole.USER);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users")
    @Operation(summary = "Lista usuarios comunes", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(authService.getUsers());
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Actualiza un usuario común", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTOin dto) {
        return ResponseEntity.ok(authService.updateUser(id, dto));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Elimina un usuario común", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserDTOin dto) {
        UserDTO authenticatedUser = authService.authenticate(dto);
        final String accessToken = JwtUtil.buildToken(
                authenticatedUser.getEmail(),
                authenticatedUser.getId(),
                authenticatedUser.getRole()
        );
        return ResponseEntity.ok(new TokenResponse(accessToken, authenticatedUser));
    }

}
