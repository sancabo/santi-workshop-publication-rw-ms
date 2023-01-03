package com.devsancabo.www.publicationsrw.repository;

import com.devsancabo.www.publicationsrw.entity.Author;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @CacheEvict(value = "authors", key = "#a?.username")
    Author save(Author a);
    @CacheEvict(value = "authors", key = "#a?.username")
    Author saveAndFlush(Author a);
    @Cacheable(value = "authors", key = "#username")
    Set<Author> findByUsername(String username);
}
