package com.effectivemobile.socialmedia.service.role.impl;

import com.effectivemobile.socialmedia.model.Role;
import com.effectivemobile.socialmedia.repository.RoleRepository;
import com.effectivemobile.socialmedia.service.role.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(Role role) {

        return roleRepository.save(role);
    }

    @Override
    public void delete(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public List<Role> findAll() {

        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {

        return roleRepository.findById(id);
    }

    @Override
    public Role findRoleByName(String name) {

        return roleRepository.findByName(name).get();
    }
}
