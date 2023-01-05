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

    @CacheEvict(value = "publications", key="#p?.author?.username")
    Publication save(Publication p);
    Set<Publication> findByAuthor_UsernameAndDatetimeGreaterThan(String username, Timestamp timestamp);
    @Cacheable(value = "publications", key="#userName")
    Set<Publication> findByAuthor_Username(String userName);

}
