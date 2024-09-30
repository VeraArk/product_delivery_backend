package org.product_delivery_backend.service;

import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.entity.Role;
import org.product_delivery_backend.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleUser(){
        return roleRepository.findByTitle("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("No role found"));
    }
}
