package com.predictry.oasis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.predictry.oasis.domain.Task;

/**
 * CRUD Repository for <code>Task</code>.
 * 
 * @author jocki
 *
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
