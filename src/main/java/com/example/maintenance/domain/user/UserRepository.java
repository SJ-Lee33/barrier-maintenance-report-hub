package com.example.maintenance.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByNameAndPhone(String name, String phone);

	Optional<User> findByEmail(String email);
}