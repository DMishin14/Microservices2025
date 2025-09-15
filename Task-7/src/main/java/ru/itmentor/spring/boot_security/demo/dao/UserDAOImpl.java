package ru.itmentor.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.itmentor.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select user from User user", User.class).getResultList();
    }

    @Override
    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void updateUser(Long id, User updateUser) {

        User existingUser = getUserById(id);

        existingUser.setName(updateUser.getName());
        existingUser.setSurname(updateUser.getSurname());
        existingUser.setAge(updateUser.getAge());
        existingUser.setUsername(updateUser.getUsername());
        existingUser.setPassword(updateUser.getPassword());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setRoles(updateUser.getRoles());

        entityManager.merge(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        entityManager.createQuery("delete from User user where user.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public User findByUsername(String username) {
        try {
            return entityManager.createQuery("select user from User user where user.username=:username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}