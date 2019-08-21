package com.sm.ms.service.impl;

import com.sm.ms.model.Role;
import com.sm.ms.model.RoleName;
import com.sm.ms.repository.RoleRepository;
import com.sm.ms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }
}
