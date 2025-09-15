package ru.itmentor.spring.boot_security.demo.dao;

import ru.itmentor.spring.boot_security.demo.model.Role;
import java.util.List;

public interface RoleDAO {

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    void saveRole(Role role);

    Role findByName(String name);
}
