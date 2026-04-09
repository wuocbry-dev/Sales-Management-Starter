package com.yourcompany.salesmanagement.module.user.repository;

import com.yourcompany.salesmanagement.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("""
            select distinct u
            from User u
            left join fetch u.roles r
            left join fetch r.permissions p
            where u.id = :id
            """)
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") Long id);
}

