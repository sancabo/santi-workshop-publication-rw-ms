package com.devsancabo.www.publicationsrw.repository;

import com.devsancabo.www.publicationsrw.entity.Publication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Set;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @CacheEvict(value = "publications", key="#p?.author?.id")
    Publication save(Publication p);
    Set<Publication> findByAuthorIdAndDatetimeGreaterThan(Long id, Timestamp timestamp);
    @Cacheable(value = "publications", key="#id")
    Set<Publication> findByAuthorId(Long id);
}
