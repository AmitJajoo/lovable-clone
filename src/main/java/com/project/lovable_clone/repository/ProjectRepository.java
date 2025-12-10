package com.project.lovable_clone.repository;

import com.project.lovable_clone.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            select p from Project p
            where p.deletedAt is null
            and p.owner.id = :userId order by p.updatedAt desc
           """)
    List<Project> findAllAccessibleByUser(@Param("userId") Long userId);
}
