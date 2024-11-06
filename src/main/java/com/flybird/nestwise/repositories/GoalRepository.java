package com.flybird.nestwise.repositories;

import com.flybird.nestwise.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.parent IS NULL")
    List<Goal> findByUserId(@Param("userId") Long userId);
}

