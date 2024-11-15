package com.mirai.data.repos;

import com.mirai.data.entities.Users;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    Page<Users> findAll(Specification<Users> spec, Pageable pageable);

    Users findByEmailAndStatusNot(String email, String status);

    List<Users> findByStatus(String status);

    List<Users> findByStatusAndType(String confirmed, String speaker);
}
