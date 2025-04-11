package com.apply.digital.db.repositories;

import com.apply.digital.db.entities.AlgoliaTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<AlgoliaTagEntity, Long> {
    Optional<AlgoliaTagEntity> findByName(String name);
}
