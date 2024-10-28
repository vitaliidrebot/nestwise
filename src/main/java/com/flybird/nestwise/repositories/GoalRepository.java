package com.flybird.nestwise.repositories;

import com.flybird.nestwise.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g WHERE g.createdBy = :userId AND g.parent IS NULL")
    List<Goal> findByCreatedBy(@Param("userId") Long userId);
}

