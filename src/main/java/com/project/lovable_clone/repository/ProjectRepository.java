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
             AND EXISTS (
              SELECT 1 from ProjectMember pm
              WHERE pm.id.userId = :userId
              AND pm.id.projectId = p.id
             )
             order by p.updatedAt desc
            """)
    List<Project> findAllAccessibleByUser(@Param("userId") Long userId);

    @Query("""
            select p from Project p
            where p.id = :projectId and
            p.deletedAt is null
            AND EXISTS (
              SELECT 1 from ProjectMember pm
              WHERE pm.id.userId = :userId
              AND pm.id.projectId = p.id
            )
            """)
    Optional<Project> findAccessibleProjectById(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
