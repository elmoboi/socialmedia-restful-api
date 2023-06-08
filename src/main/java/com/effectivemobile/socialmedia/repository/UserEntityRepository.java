package com.effectivemobile.socialmedia.repository;

import com.effectivemobile.socialmedia.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findFirstByEmail(String email);

    UserEntity findByEmail(String email);

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findById(Long id);

    UserEntity findUserEntitiesByUserName(String userName);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
