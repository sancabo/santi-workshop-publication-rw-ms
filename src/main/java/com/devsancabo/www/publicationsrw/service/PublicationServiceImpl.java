package com.devsancabo.www.publicationsrw.service;

import com.devsancabo.www.LoremIpsum;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateRequestDTO;
import com.devsancabo.www.publicationsrw.dto.PublicationCreateResponseDTO;
import com.devsancabo.www.publicationsrw.entity.Author;
import com.devsancabo.www.publicationsrw.entity.Publication;
import com.devsancabo.www.publicationsrw.repository.AuthorRepository;
import com.devsancabo.www.publicationsrw.repository.PublicationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Service
public class PublicationServiceImpl implements PublicationService {
    private final Logger logger = LoggerFactory.getLogger(PublicationServiceImpl.class);
    private final PublicationRepository publicationRepository;
    private final AuthorRepository authorRepository;


    @Autowired
    public PublicationServiceImpl(final PublicationRepository publicationRepository,
                                  final AuthorRepository authorRepository){
        this.publicationRepository = publicationRepository;
        this.authorRepository = authorRepository;
    }


    @Override
    public List<Publication> search(String username, String date, Integer pageNumber, Integer pageSize) {
        if(Objects.isNull(date)) {
            return publicationRepository.findByAuthor_Username(username, PageRequest.of(pageNumber, pageSize));
        }
        else {
            var datetime = LocalDateTime.parse(date);
            return publicationRepository.findByAuthor_UsernameAndDatetimeGreaterThan(
                    username, Timestamp.from(datetime.toInstant(ZoneOffset.of("-03:00"))),
                    PageRequest.of(pageNumber, pageSize));
        }

    }

    @Override
    @Transactional
    public PublicationCreateResponseDTO create(PublicationCreateRequestDTO dto) {
        var detachedAuthors = authorRepository.findByUsername(dto.author().username());
        Author detachedAuthor;
        if(detachedAuthors.isEmpty()){
            logger.info("Creating new user");
            var author = buildAuthor(dto);
            //I want to ensure the author is found when I next call findByUsername.
            detachedAuthor = authorRepository.saveAndFlush(author);
        } else {
            logger.info("Using existing User");
            detachedAuthor = detachedAuthors.iterator().next();
        }
        var publication = buildPublication(detachedAuthor);
        var createdPublication = publicationRepository.save(publication);
        return PublicationCreateResponseDTO.builder()
                .content(createdPublication.getContent())
                .datetime(createdPublication.getDatetime().toString())
                .id(createdPublication.getId())
                .author(dto.author()).build();
    }

    private Author buildAuthor(PublicationCreateRequestDTO dto) {
        var author = new Author();
        String username = dto.author().username();
        author.setEmail(username + "@gmail.com");
        author.setPassword("password");
        author.setUsername(username);
        return author;
    }

    private Publication buildPublication(Author detachedAuthor) {
        var publication = new Publication();
        publication.setContent(LoremIpsum.getRandomSentence(25));
        publication.setAuthor(detachedAuthor);
        publication.setDatetime(Timestamp.from(Instant.now()));
        return publication;
    }
}
