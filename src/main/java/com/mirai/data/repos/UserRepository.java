package com.mirai.data.repos;

import com.mirai.data.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    Page<Users> findAll(Specification<Users> spec, Pageable pageable);

    Users findByEmailAndStatusNot(String email, String status);

    List<Users> findByStatus(String status);

}
