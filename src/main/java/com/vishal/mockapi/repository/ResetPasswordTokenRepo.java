package com.vishal.mockapi.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vishal.mockapi.entity.ResetPasswordToken;

public interface ResetPasswordTokenRepo extends CrudRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findByToken(String token);

}
