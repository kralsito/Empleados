package com.gestion.empleados.domains.role.repository;

import com.gestion.empleados.domains.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    List<Role> findAllByUserId(Long userId);
    Optional<Role> findByIdAndUserId(Long id, Long userId);
}
