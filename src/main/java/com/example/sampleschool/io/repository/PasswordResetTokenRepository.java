package com.example.sampleschool.io.repository;

import com.example.sampleschool.io.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long>{
    PasswordResetTokenEntity findByToken(String token);
}
