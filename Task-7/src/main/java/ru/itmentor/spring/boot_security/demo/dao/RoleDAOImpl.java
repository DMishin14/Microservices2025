package ru.itmentor.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.itmentor.spring.boot_security.demo.model.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Класс RoleDAOImpl - это реализация интерфейса RoleDAO
 * 
 * Что делает этот класс:
 * 1. Реализует все методы из интерфейса RoleDAO
 * 2. Использует EntityManager для работы с базой данных
 * 3. Выполняет SQL-запросы для работы с ролями
 */
@Repository // Аннотация говорит Spring, что это компонент для работы с данными
public class RoleDAOImpl implements RoleDAO {
    
    // EntityManager - это наш инструмент для работы с базой данных
    @PersistenceContext // Spring автоматически создает и внедряет EntityManager
    private EntityManager entityManager;
    
    /**
     * Получить все роли из базы данных
     * JPQL запрос "select role from Role role" означает "выбери все роли из таблицы roles"
     */
    @Override
    public List<Role> getAllRoles() {
        return entityManager.createQuery("select role from Role role", Role.class).getResultList();
    }
    
    /**
     * Получить роль по ID
     * entityManager.find() - это самый простой способ найти объект по ID
     */
    @Override
    public Role getRoleById(Long id) {
        return entityManager.find(Role.class, id);
    }
    
    /**
     * Сохранить новую роль
     * entityManager.persist() - сохраняет новый объект в базе данных
     */
    @Override
    public void saveRole(Role role) {
        entityManager.persist(role);
    }
    
    /**
     * Найти роль по названию
     * Используем JPQL запрос с параметром :name
     * getSingleResult() возвращает один результат или выбрасывает исключение
     */
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
