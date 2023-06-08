package com.effectivemobile.socialmedia.service.role;

import com.effectivemobile.socialmedia.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role save(Role role);

    void delete(Role role);

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Role findRoleByName(String name);
}
