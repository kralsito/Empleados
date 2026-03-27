package com.gestion.empleados.domains.employee.model;

import com.gestion.empleados.domains.role.model.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
public class
Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
}
