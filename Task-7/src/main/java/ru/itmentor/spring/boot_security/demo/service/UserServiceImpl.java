package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.dao.UserDAO;
import ru.itmentor.spring.boot_security.demo.model.User;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        userDAO.saveUser(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userDAO.deleteUser(id);
    }

    @Override
    public User getUserById(Long id) {
        return userDAO.getUserById(id);
    }

    @Transactional
    @Override
    public void updateUser(Long id, User updatedUser) {
        userDAO.updateUser(id, updatedUser);
    }

    @Override
    public User findByUsername(String username) {
        System.out.println("Поиск пользователя по логину: " + username);
        User user = userDAO.findByUsername(username);
        if (user != null) {
            System.out.println("Пользователь найден: " + user.getUsername());
        } else {
            System.out.println("Пользователь НЕ найден: " + username);
        }
        return user;
    }
}