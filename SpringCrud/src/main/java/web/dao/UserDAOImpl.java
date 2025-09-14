package web.dao;

import org.springframework.stereotype.Repository;
import web.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select user from User user", (User.class)).getResultList();
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
        User upUser = getUserById(id);
        upUser.setName(updateUser.getName());
        upUser.setSurname(updateUser.getSurname());
        upUser.setAge(updateUser.getAge());
        entityManager.merge(upUser);
    }

    @Override
    public void deleteUser(Long id) {
        entityManager.createQuery("delete from User user where user.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }
}