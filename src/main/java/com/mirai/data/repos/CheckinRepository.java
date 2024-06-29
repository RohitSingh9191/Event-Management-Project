package com.mirai.data.repos;

import com.mirai.data.entities.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Integer> {
    Checkin getByUserIdAndStatus(Integer id, String status);

    Checkin getByUserId(Integer id);
}
