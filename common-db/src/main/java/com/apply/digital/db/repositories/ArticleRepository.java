package com.apply.digital.db.repositories;

import com.apply.digital.db.entities.AlgoliaArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
}
