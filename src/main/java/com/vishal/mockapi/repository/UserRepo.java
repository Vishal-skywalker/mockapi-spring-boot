package com.vishal.mockapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vishal.mockapi.entity.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity, Long> {
    List<UserEntity> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.email = :email, u.name = :name WHERE u.id = :id")
    void updateUserById(@Param("id") Long id, @Param("email") String email, @Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = :password WHERE u.id = :id")
    void updateUserPassword(@Param("id") Long id, @Param("password") String password);
}
