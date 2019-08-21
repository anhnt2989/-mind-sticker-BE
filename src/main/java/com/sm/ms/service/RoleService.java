package com.sm.ms.service;

import com.sm.ms.model.Role;
import com.sm.ms.model.RoleName;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(RoleName roleName);
}
