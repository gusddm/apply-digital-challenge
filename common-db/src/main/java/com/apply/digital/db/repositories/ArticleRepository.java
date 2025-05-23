package com.apply.digital.db.repositories;

import com.apply.digital.db.entities.AlgoliaArticleEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ArticleRepository extends JpaRepository<AlgoliaArticleEntity, Long>,
        JpaSpecificationExecutor<AlgoliaArticleEntity> {

    boolean existsByObjectId(String objectId);

    @Transactional
    @Modifying
    @Query("UPDATE AlgoliaArticleEntity a set active = false WHERE a.objectId = :objectId")
    void deleteByObjectId(@Param("objectId") String objectId);

    @Nonnull
    @EntityGraph(attributePaths = {"tags"}) //Prevents LazyInitialization issues
    Page<AlgoliaArticleEntity> findAll(Specification<AlgoliaArticleEntity> spec, @Nonnull Pageable pageable);
}
