package com.sm.ms;

import com.sm.ms.model.Role;
import com.sm.ms.model.RoleName;
import com.sm.ms.repository.RoleRepository;
import com.sm.ms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DataSeedingListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName) == null) {
                roleRepository.save(new Role(roleName));
            }
        }
    }
}
