package com.project.lovable_clone.repository;

import com.project.lovable_clone.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
             select p from Project p
             where p.deletedAt is null
             order by p.updatedAt desc
            """)
    List<Project> findAllAccessibleByUser(@Param("userId") Long userId);

    @Query("""
            select p from Project p
            where p.id = :projectId and
            p.deletedAt is null
            """)
    Optional<Project> findAccessibleProjectById(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
