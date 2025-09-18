package ru.itmentor.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.itmentor.spring.boot_security.demo.model.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository 
public class RoleDAOImpl implements RoleDAO {
    

    @PersistenceContext 
    private EntityManager entityManager;
    

    @Override
    public List<Role> getAllRoles() {
        return entityManager.createQuery("select role from Role role", Role.class).getResultList();
    }
    

    @Override
    public Role getRoleById(Long id) {
        return entityManager.find(Role.class, id);
    }
    

    @Override
    public void saveRole(Role role) {
        entityManager.persist(role);
    }
    

    @Override
    public Role findByName(String name) {
        try {
            return entityManager.createQuery("select role from Role role where role.name=:name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}

