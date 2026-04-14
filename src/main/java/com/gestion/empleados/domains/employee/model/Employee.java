package com.gestion.empleados.domains.employee.model;

import com.gestion.empleados.domains.role.model.Role;
import com.gestion.empleados.domains.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}