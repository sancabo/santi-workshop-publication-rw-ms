package com.devsancabo.www.publicationsrw.repository;

import com.devsancabo.www.publicationsrw.entity.Publication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @CacheEvict(value = "publications", key="#p?.author?.username")
    Publication save(Publication p);
    List<Publication> findByAuthor_UsernameAndDatetimeGreaterThan(String username, Timestamp timestamp, Pageable pageable);
    @Cacheable(value = "publications", key="#userName")
    List<Publication> findByAuthor_Username(String userName, Pageable pageable);

}
